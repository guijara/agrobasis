package com.agrobasis.core_service.market.infrastructure.integration;

import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.exception.ExternalMarketIntegrationException;
import com.agrobasis.core_service.market.domain.exception.InvalidExternalMarketDataException;
import com.agrobasis.core_service.market.domain.exception.UnsupportedExternalExchangeRatePairException;
import com.agrobasis.core_service.market.infrastructure.integration.config.MarketIntegrationProperties;
import com.agrobasis.core_service.market.infrastructure.integration.dto.ExternalExchangeRateData;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Component
public class BcbExchangeRateClient {

    private static final String SOURCE = "BCB PTAX";
    private static final DateTimeFormatter BCB_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private final RestClient restClient;

    public BcbExchangeRateClient(MarketIntegrationProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBcbBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    public ExternalExchangeRateData fetchLatestExchangeRate(Currency fromCurrency, Currency toCurrency) {
        if (!isSupportedPair(fromCurrency, toCurrency)) {
            throw new UnsupportedExternalExchangeRatePairException("Par cambial não suportado pela integração BCB nesta fase.");
        }

        try {
            BcbPtaxResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/olinda/servico/PTAX/versao/v1/odata/CotacaoMoedaPeriodo(moeda=@moeda,dataInicial=@dataInicial,dataFinalCotacao=@dataFinalCotacao)")
                            .queryParam("@moeda", "'USD'")
                            .queryParam("@dataInicial", "'" + LocalDate.now().minusDays(7).format(BCB_DATE_FORMATTER) + "'")
                            .queryParam("@dataFinalCotacao", "'" + LocalDate.now().format(BCB_DATE_FORMATTER) + "'")
                            .queryParam("$format", "json")
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            BcbPtaxQuote latestQuote = extractLatestQuote(response);
            BigDecimal usdBrlRate = latestQuote.cotacaoVenda();
            BigDecimal rate = fromCurrency == Currency.USD
                    ? usdBrlRate
                    : BigDecimal.ONE.divide(usdBrlRate, 6, RoundingMode.HALF_UP);

            return new ExternalExchangeRateData(
                    fromCurrency,
                    toCurrency,
                    rate,
                    SOURCE,
                    latestQuote.dataHoraCotacao()
            );
        } catch (RestClientException ex) {
            throw new ExternalMarketIntegrationException("Falha ao buscar câmbio externo no BCB.", ex);
        } catch (RuntimeException ex) {
            if (ex instanceof ExternalMarketIntegrationException
                    || ex instanceof InvalidExternalMarketDataException
                    || ex instanceof UnsupportedExternalExchangeRatePairException) {
                throw ex;
            }
            throw new ExternalMarketIntegrationException("Resposta externa de câmbio do BCB ilegível.", ex);
        }
    }

    private boolean isSupportedPair(Currency fromCurrency, Currency toCurrency) {
        // USD -> BRL is the primary B.2 flow; BRL -> USD is derived from the same PTAX quote.
        return fromCurrency == Currency.USD && toCurrency == Currency.BRL
                || fromCurrency == Currency.BRL && toCurrency == Currency.USD;
    }

    private BcbPtaxQuote extractLatestQuote(BcbPtaxResponse response) {
        List<BcbPtaxQuote> quotes = response == null ? List.of() : response.value();
        return quotes.stream()
                .filter(quote -> quote.cotacaoVenda() != null && quote.dataHoraCotacao() != null)
                .max(Comparator.comparing(BcbPtaxQuote::dataHoraCotacao))
                .orElseThrow(() -> new InvalidExternalMarketDataException("Resposta externa de câmbio do BCB sem cotação válida."));
    }

    private record BcbPtaxResponse(List<BcbPtaxQuote> value) {
    }

    private record BcbPtaxQuote(BigDecimal cotacaoVenda, LocalDateTime dataHoraCotacao) {
    }
}
