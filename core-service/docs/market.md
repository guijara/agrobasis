# Módulo market

## Papel

O módulo `market` representa e armazena dados externos de referência econômica usados pelo sistema.

Seu foco é fornecer ao restante do AgroBasis referências confiáveis e persistidas de mercado, sem transferir ao pricing a responsabilidade de conversar diretamente com fontes externas.

## Responsabilidades principais

O módulo concentra:

- armazenamento de cotação de commodity;
- armazenamento de taxa de câmbio;
- sincronização manual com fontes externas;
- validação e normalização de dados externos;
- preservação de histórico de registros.

## Conceitos centrais

### MarketQuote
Representa uma cotação de commodity persistida no sistema.

### ExchangeRate
Representa uma taxa de câmbio persistida no sistema.

### MarketSyncService
Orquestra a sincronização manual com fontes externas e a persistência dos registros históricos.

## Papel no sistema

O módulo `market` não calcula o valor econômico da organização.

Seu papel é fornecer os dados externos de referência que serão usados por:

- `pricing`
- análises econômicas atuais
- futuras capacidades analíticas do produto

## Princípio importante

O pricing não deve consultar APIs externas diretamente.

A lógica adotada no AgroBasis é:

1. sincronizar;
2. validar;
3. persistir;
4. calcular a partir do banco.

## Observações

A sincronização atual é manual e administrativa. Essa decisão foi tomada para manter o fluxo simples, previsível e testável no estágio atual do projeto.
