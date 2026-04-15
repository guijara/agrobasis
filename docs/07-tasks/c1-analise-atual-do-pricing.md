# Tarefas — C.1 Análise atual do pricing

## Contexto
Estas tarefas organizam a implementação da primeira camada analítica do pricing do AgroBasis, derivada do cálculo econômico já existente.

## Spec relacionada
- `docs/03-specs/done/c1-analise-atual-do-pricing.md`

## Plan relacionado
- `docs/04-plans/plan-c1-analise-atual-do-pricing.md`

## Tarefas

### 1. Estrutura analítica do pricing
- [ ] criar `PricingCompositionResponse`
- [ ] criar `PricingImpactSummaryResponse`
- [ ] criar `PricingIndicatorResponse`
- [ ] criar `CurrentPricingAnalysisResponse`
- [ ] criar `PricingAnalysisUnavailableException`

### 2. Serviço analítico
- [ ] criar `PricingAnalysisService`
- [ ] fazer o service depender de `PricingService`
- [ ] reutilizar `calculateCurrentPrice(...)` como base do fluxo
- [ ] derivar composição, resumo de impacto e indicadores a partir do response atual

### 3. Indicadores obrigatórios
- [ ] implementar `cost_share_of_converted_price`
- [ ] implementar `freight_share_of_converted_price`
- [ ] implementar `commercial_adjustment_share_of_converted_price`
- [ ] implementar `commercial_price_retention`
- [ ] implementar `total_reduction_share_of_converted_price`
- [ ] padronizar cálculo percentual com `scale = 2` e `RoundingMode.HALF_UP`

### 4. API e segurança
- [ ] criar `PricingAnalysisController`
- [ ] expor `GET /api/pricing/analysis/current`
- [ ] receber `organizationId`, `farmId` e `commodity`
- [ ] receber `@AuthenticationPrincipal AuthenticatedUser`
- [ ] aplicar `tenantAccessValidator.assertOrganizationAccess(...)`
- [ ] garantir que o endpoint siga o padrão autenticado de segurança

### 5. Testes de aplicação
- [ ] criar `PricingAnalysisServiceTest`
- [ ] validar composição da resposta
- [ ] validar resumo de impacto
- [ ] validar os 5 indicadores obrigatórios
- [ ] validar a matemática percentual adotada

### 6. Testes de integração
- [ ] atualizar `SecurityHttpIT` para o endpoint analítico
- [ ] validar 401 sem token
- [ ] validar 403 para tenant incorreto, quando aplicável
- [ ] atualizar `CoreBackboneFlowIT` com chamada ao endpoint analítico
- [ ] validar os principais indicadores no fluxo ponta a ponta

### 7. Validação final
- [ ] rodar `PricingAnalysisServiceTest`
- [ ] rodar `SecurityHttpIT`
- [ ] rodar `CoreBackboneFlowIT`
- [ ] verificar coerência entre fórmula, testes e resposta
- [ ] corrigir divergências entre implementação e exemplo, se houver

## Critério de encerramento
As tasks podem ser consideradas concluídas quando:
- o endpoint analítico estiver implementado;
- o serviço analítico reutilizar o `PricingService`;
- os indicadores obrigatórios estiverem corretos;
- os testes relevantes estiverem verdes;
- a resposta estiver coerente com a spec e o plan.
