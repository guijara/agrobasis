# Plano técnico — C.1 Análise atual do pricing

## Spec relacionada
- `docs/03-specs/done/c1-analise-atual-do-pricing.md`

## Objetivo do plano
Estruturar tecnicamente a implementação da primeira camada analítica do pricing, reutilizando o cálculo já existente e evitando a criação de um motor paralelo.

## Módulos afetados
- `pricing`
- segurança do fluxo analítico
- testes de integração

## Artefatos a criar ou alterar
### Novos artefatos
- `PricingAnalysisController`
- `PricingAnalysisService`
- `CurrentPricingAnalysisResponse`
- `PricingCompositionResponse`
- `PricingImpactSummaryResponse`
- `PricingIndicatorResponse`
- `PricingAnalysisUnavailableException`

### Artefatos a ajustar
- `SecurityConfig`
- `CoreBackboneFlowIT`
- `SecurityHttpIT`

## Estratégia de implementação

### 1. Reaproveitar o núcleo do pricing atual
A análise não deve recalcular o pricing por um caminho independente.

O `PricingAnalysisService` deve depender de `PricingService` e chamar o cálculo atual já consolidado.

### 2. Criar a camada analítica
A partir do `CurrentPricingResponse`, a implementação deve derivar:

- composição do pricing;
- resumo de impacto;
- indicadores percentuais.

### 3. Criar endpoint analítico
Criar um endpoint HTTP específico para análise atual do pricing:

- `GET /api/pricing/analysis/current`

Parâmetros esperados:
- `organizationId`
- `farmId`
- `commodity`

### 4. Aplicar segurança e tenant enforcement
O endpoint analítico deve:
- exigir autenticação;
- receber `@AuthenticationPrincipal`;
- validar acesso organizacional com `tenantAccessValidator`.

### 5. Implementar os indicadores obrigatórios
Os indicadores devem ser calculados com base em `convertedPrice`, usando:

- `scale = 2`
- `RoundingMode.HALF_UP`

Indicadores esperados:
- `cost_share_of_converted_price`
- `freight_share_of_converted_price`
- `commercial_adjustment_share_of_converted_price`
- `commercial_price_retention`
- `total_reduction_share_of_converted_price`

### 6. Padronizar a matemática dos percentuais
Os percentuais devem seguir a fórmula literal:

- `part * 100 / convertedPrice`

Evitar:
- soma de percentuais já arredondados como regra principal;
- retenção calculada por complemento (`100 - reduçãoTotal`) quando isso divergir da fórmula oficial.

### 7. Validar com testes
A implementação deve ser validada com:
- teste de serviço analítico;
- teste HTTP de segurança;
- backbone flow atualizado.

## Impactos esperados
Ao final da implementação, o sistema deve:
- expor uma análise atual do pricing;
- mostrar composição do cálculo;
- mostrar impactos absolutos;
- mostrar indicadores percentuais;
- manter segurança e tenant enforcement;
- preservar coerência com o pricing já existente.

## Estratégia de validação

### Testes de aplicação
- `PricingAnalysisServiceTest`

### Testes HTTP
- `SecurityHttpIT`

### Fluxo ponta a ponta
- `CoreBackboneFlowIT`

### Validações esperadas
- indicadores corretos;
- tenant enforcement aplicado;
- 401 sem token;
- 403 em acesso indevido;
- resposta analítica coerente com o cálculo atual.

## Riscos e cuidados
- não duplicar o motor de cálculo do pricing;
- não divergir entre fórmula, teste e response;
- não introduzir inconsistência matemática por arredondamento indireto;
- não deixar o endpoint analítico sem a mesma proteção de tenant do pricing atual.

## Critério de encerramento
O plano é considerado concluído quando:

- o endpoint analítico existe;
- o serviço analítico reutiliza o `PricingService`;
- os DTOs analíticos estão implementados;
- os 5 indicadores obrigatórios estão corretos;
- os testes relevantes estão verdes;
- a resposta analítica está coerente com o domínio atual do pricing.
