# Módulo market

## Papel
O módulo `market` representa e armazena dados externos de referência econômica.

## Responsabilidades principais
- armazenar cotações de commodity;
- armazenar taxas de câmbio;
- sincronizar dados externos de mercado;
- manter histórico de referências econômicas.

## Conceitos centrais
- `MarketQuote`
- `ExchangeRate`

## Observação
O módulo `market` não calcula valor da organização. Ele fornece dados de referência para o módulo `pricing`.
