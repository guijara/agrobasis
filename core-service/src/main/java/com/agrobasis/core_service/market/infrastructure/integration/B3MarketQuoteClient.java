package com.agrobasis.core_service.market.infrastructure.integration;

import com.agrobasis.core_service.farm.domain.Commodity;
import com.agrobasis.core_service.market.domain.Currency;
import com.agrobasis.core_service.market.domain.Unit;
import com.agrobasis.core_service.market.domain.exception.ExternalMarketIntegrationException;
import com.agrobasis.core_service.market.domain.exception.InvalidExternalMarketDataException;
import com.agrobasis.core_service.market.domain.exception.UnsupportedExternalCommodityException;
import com.agrobasis.core_service.market.infrastructure.integration.config.MarketIntegrationProperties;
import com.agrobasis.core_service.market.infrastructure.integration.dto.ExternalCommodityQuoteData;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class B3MarketQuoteClient implements CommodityMarketQuoteClient {

    private static final String SOURCE = "B3";
    private static final DateTimeFormatter B3_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final Map<Commodity, B3CommodityContract> CONTRACTS = Map.of(
            Commodity.SOYBEAN, new B3CommodityContract("SJC", Currency.USD),
            Commodity.CORN, new B3CommodityContract("CCM", Currency.BRL)
    );

    private final RestClient restClient;
    private final MarketIntegrationProperties properties;

    public B3MarketQuoteClient(MarketIntegrationProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getConnectTimeout());
        requestFactory.setReadTimeout(properties.getReadTimeout());
        this.restClient = RestClient.builder()
                .baseUrl(properties.getB3BaseUrl())
                .requestFactory(requestFactory)
                .build();
        this.properties = properties;
    }

    @Override
    public ExternalCommodityQuoteData fetchLatestCommodityQuote(Commodity commodity) {
        B3CommodityContract contract = CONTRACTS.get(commodity);
        if (contract == null) {
            throw new UnsupportedExternalCommodityException("Commodity não suportada pela integração B3 nesta fase.");
        }

        try {
            byte[] zipBytes = restClient.get()
                    .uri(properties.getB3HistoricalQuotePath(), LocalDate.now().getYear())
                    .retrieve()
                    .body(byte[].class);

            return readLatestQuoteFromZip(commodity, contract, zipBytes)
                    .orElseThrow(() -> new InvalidExternalMarketDataException("Arquivo histórico da B3 sem cotação válida para a commodity informada."));
        } catch (RestClientException ex) {
            throw new ExternalMarketIntegrationException("Falha ao buscar cotação externa na B3.", ex);
        } catch (IOException ex) {
            throw new ExternalMarketIntegrationException("Falha ao ler arquivo histórico da B3.", ex);
        } catch (RuntimeException ex) {
            if (ex instanceof ExternalMarketIntegrationException
                    || ex instanceof InvalidExternalMarketDataException
                    || ex instanceof UnsupportedExternalCommodityException) {
                throw ex;
            }
            throw new ExternalMarketIntegrationException("Resposta externa de cotação da B3 ilegível.", ex);
        }
    }

    private Optional<ExternalCommodityQuoteData> readLatestQuoteFromZip(
            Commodity commodity,
            B3CommodityContract contract,
            byte[] zipBytes
    ) throws IOException {
        if (zipBytes == null || zipBytes.length == 0) {
            throw new InvalidExternalMarketDataException("Arquivo histórico da B3 vazio.");
        }

        ExternalCommodityQuoteData latestQuote = null;
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream, StandardCharsets.ISO_8859_1));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!isQuoteLineForContract(line, contract.tickerPrefix())) {
                        continue;
                    }

                    ExternalCommodityQuoteData quote = toQuoteData(commodity, contract, line);
                    if (latestQuote == null || quote.quotedAt().isAfter(latestQuote.quotedAt())) {
                        latestQuote = quote;
                    }
                }
            }
        }
        return Optional.ofNullable(latestQuote);
    }

    private boolean isQuoteLineForContract(String line, String tickerPrefix) {
        return line != null
                && line.length() >= 121
                && line.startsWith("01")
                && line.substring(12, 24).trim().startsWith(tickerPrefix);
    }

    private ExternalCommodityQuoteData toQuoteData(Commodity commodity, B3CommodityContract contract, String line) {
        LocalDate quotedDate = LocalDate.parse(line.substring(2, 10), B3_DATE_FORMATTER);
        BigDecimal price = new BigDecimal(line.substring(108, 121).trim())
                .movePointLeft(2);

        // B.2 normalizes commodity quotes to the pricing unit used internally.
        return new ExternalCommodityQuoteData(
                commodity,
                price,
                contract.currency(),
                Unit.TON,
                SOURCE,
                LocalDateTime.of(quotedDate, LocalTime.MIDNIGHT)
        );
    }

    private record B3CommodityContract(String tickerPrefix, Currency currency) {
    }
}
