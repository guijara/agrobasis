# C.1 — Análise atual do pricing

## Status
- done

## Contexto
Após a consolidação do pricing determinístico na Etapa B, o sistema já era capaz de calcular o valor econômico atual de uma commodity para uma organização, considerando:

- cotação de mercado;
- câmbio;
- custo;
- frete;
- ajuste comercial.

Esse cálculo já era rastreável e explicável, mas ainda estava orientado principalmente à resposta econômica final.

Havia necessidade de evoluir o sistema para uma camada inicial de leitura analítica, permitindo que o AgroBasis respondesse não apenas qual é o valor atual, mas também como esse valor é composto e qual é o peso relativo de cada componente do cálculo.

## Objetivo
Criar uma visão analítica do pricing atual, derivada do cálculo já existente, sem introduzir um novo motor de cálculo e sem antecipar a futura camada de inteligência especializada.

## Escopo
Esta spec inclui:

- criação de um endpoint analítico para o pricing atual;
- reutilização do `PricingService` como base do cálculo;
- criação de uma resposta analítica específica;
- composição estruturada do pricing;
- resumo de impacto econômico;
- indicadores percentuais derivados do `convertedPrice`;
- tenant enforcement no fluxo analítico;
- testes de aplicação e integração do novo fluxo.

## Fora de escopo
Esta spec não inclui:

- simulação determinística de cenários;
- Monte Carlo;
- intelligence-service;
- sensibilidade avançada;
- comparação entre fazendas;
- ranking entre commodities;
- agregações históricas;
- alertas;
- recomendação automática.

## Regras de negócio
- a análise deve ser derivada do pricing atual já calculado;
- o `PricingAnalysisService` não deve criar um motor paralelo de cálculo;
- o fluxo analítico deve reutilizar o `PricingService`;
- o endpoint deve receber:
  - `organizationId`
  - `farmId`
  - `commodity`
- o fluxo deve aplicar tenant enforcement;
- os indicadores percentuais devem usar `convertedPrice` como base;
- os percentuais devem ser calculados com:
  - `scale = 2`
  - `RoundingMode.HALF_UP`

## Indicadores obrigatórios
A análise deve retornar exatamente estes indicadores:

- `cost_share_of_converted_price`
- `freight_share_of_converted_price`
- `commercial_adjustment_share_of_converted_price`
- `commercial_price_retention`
- `total_reduction_share_of_converted_price`

## Impacto no sistema
Módulos e áreas afetadas:

- `pricing`
- segurança do fluxo analítico
- testes de integração e backbone flow

## Estrutura esperada
A solução foi estruturada com:

- `PricingAnalysisController`
- `PricingAnalysisService`
- `CurrentPricingAnalysisResponse`
- `PricingCompositionResponse`
- `PricingImpactSummaryResponse`
- `PricingIndicatorResponse`

## Estratégia de testes
A mudança foi validada com:

- `PricingAnalysisServiceTest`
- `SecurityHttpIT`
- `CoreBackboneFlowIT`

Os testes validam:
- cálculo correto dos indicadores;
- composição e impacto;
- tenant enforcement;
- acesso sem token;
- fluxo ponta a ponta do endpoint analítico.

## Critério de encerramento
A spec foi considerada concluída quando:

- o endpoint analítico foi implementado;
- a análise passou a ser derivada do `PricingService`;
- os indicadores obrigatórios ficaram corretos;
- a matemática foi padronizada pela fórmula literal;
- os testes unitários ficaram verdes;
- os testes de integração do fluxo ficaram verdes.

## Observações
Durante a implementação, foi necessário corrigir uma inconsistência entre os exemplos iniciais e a fórmula oficial dos indicadores percentuais.

A regra final adotada foi:
- cálculo literal sobre `convertedPrice`;
- arredondamento com duas casas;
- coerência entre implementação, testes e resposta da API.

Essa spec representa a primeira camada analítica do AgroBasis ainda totalmente dentro do `core-service`.
