# Tarefas — C.2 Reorganização do núcleo econômico

## Contexto
Estas tarefas organizam a implementação da C.2, cujo objetivo é reorganizar o núcleo econômico do pricing para separar obtenção de dados, cálculo puro, contexto de referência e preparação inicial para cenários futuros.

## Spec relacionada
- `docs/03-specs/active/c2-preparacao-do-nucleo-economico-para-cenarios-e-intelligence.md`

## Plan relacionado
- `docs/04-plans/plan-c2-reorganizacao-do-nucleo-economico.md`

## Regra de execução
Estas tarefas devem ser executadas respeitando a ordem e as dependências descritas neste documento.

Não iniciar adaptação do `PricingService` antes da conclusão do núcleo puro de cálculo e do resolvedor de inputs atuais.

Não iniciar validação final antes da adaptação completa dos serviços e da preservação da compatibilidade externa.

## Dependências entre blocos

- Bloco 2 depende da conclusão do Bloco 1.
- Bloco 3 depende da conclusão do Bloco 2.
- Bloco 4 depende da conclusão do Bloco 2 e do Bloco 3.
- Bloco 5 depende da conclusão dos blocos anteriores relevantes.

## Tarefas

### Bloco 1 — Contratos internos do núcleo econômico
**Dependência:** nenhuma

- [ ] criar `PricingInput`
- [ ] definir em `PricingInput` os campos mínimos obrigatórios:
  - [ ] `commodity`
  - [ ] `farmId`
  - [ ] `marketPrice`
  - [ ] `marketCurrency`
  - [ ] `unit`
  - [ ] `exchangeRate`
  - [ ] `costPerTon`
  - [ ] `freightPerTon`
  - [ ] `adjustmentPerTon`
- [ ] criar `PricingResult`
- [ ] definir em `PricingResult` os campos mínimos obrigatórios:
  - [ ] `convertedPrice`
  - [ ] `adjustedPrice`
  - [ ] `netPrice`
  - [ ] `commercialPrice`
- [ ] criar `PricingReferenceContext`
- [ ] definir em `PricingReferenceContext` os campos mínimos obrigatórios:
  - [ ] `organizationId`
  - [ ] `farmId`
  - [ ] `commodity`
  - [ ] `marketQuoteSource`
  - [ ] `marketQuoteQuotedAt`
  - [ ] `exchangeRateSource`
  - [ ] `exchangeRateQuotedAt`
- [ ] criar `PricingScenarioRequest`
- [ ] definir em `PricingScenarioRequest` apenas campos opcionais de sobrescrita:
  - [ ] `marketPrice`
  - [ ] `exchangeRate`
  - [ ] `costPerTon`
  - [ ] `freightPerTon`
  - [ ] `adjustmentPerTon`

### Bloco 2 — Núcleo puro de cálculo
**Dependência:** Bloco 1 concluído

- [ ] criar `PricingCalculator`
- [ ] implementar no `PricingCalculator` o cálculo puro a partir de `PricingInput`
- [ ] preservar a ordem obrigatória do cálculo:
  - [ ] `convertedPrice = marketPrice × exchangeRate`
  - [ ] `adjustedPrice = convertedPrice - costPerTon`
  - [ ] `netPrice = adjustedPrice - freightPerTon`
  - [ ] `commercialPrice = netPrice - adjustmentPerTon`
- [ ] garantir que `PricingCalculator` não conheça:
  - [ ] repositories
  - [ ] controllers
  - [ ] contexto HTTP
  - [ ] integração externa
- [ ] definir se a memória de cálculo pura ficará em `PricingResult` ou será montada em camada superior
- [ ] manter coerência com o comportamento econômico atual

### Bloco 3 — Resolução dos dados atuais
**Dependência:** Bloco 2 concluído

- [ ] criar `CurrentPricingInputResolver`
- [ ] fazer o resolver consultar os repositories atuais necessários
- [ ] montar `PricingInput` com os dados persistidos atuais
- [ ] montar `PricingReferenceContext` com os metadados de origem do cálculo
- [ ] preservar as exceptions atuais em caso de ausência de:
  - [ ] cotação
  - [ ] câmbio
  - [ ] custo
  - [ ] frete
  - [ ] ajuste comercial
- [ ] garantir que o resolver não execute o cálculo econômico
- [ ] garantir que o resolver não monte response HTTP

### Bloco 4 — Reorganização dos serviços atuais
**Dependência:** Bloco 2 e Bloco 3 concluídos

- [ ] adaptar `PricingService` para atuar como orquestrador
- [ ] fazer `PricingService` usar `CurrentPricingInputResolver`
- [ ] fazer `PricingService` usar `PricingCalculator`
- [ ] manter o contrato atual de `CurrentPricingResponse`
- [ ] preservar o comportamento externo do endpoint atual de pricing
- [ ] adaptar `PricingAnalysisService` para continuar funcionando sobre a nova estrutura
- [ ] garantir que `PricingAnalysisService` não duplique o cálculo por outro caminho
- [ ] garantir compatibilidade com a análise atual já consolidada na C.1

### Bloco 5 — Testes e validação final
**Dependência:** Blocos 1, 2, 3 e 4 concluídos

- [ ] criar teste unitário para `PricingCalculator`
- [ ] validar no teste do `PricingCalculator`:
  - [ ] `convertedPrice`
  - [ ] `adjustedPrice`
  - [ ] `netPrice`
  - [ ] `commercialPrice`
- [ ] criar teste unitário para `CurrentPricingInputResolver`
- [ ] validar no teste do resolver:
  - [ ] montagem correta de `PricingInput`
  - [ ] montagem correta de `PricingReferenceContext`
  - [ ] lançamento correto das exceptions esperadas
- [ ] adaptar `PricingServiceTest`
- [ ] adaptar `PricingAnalysisServiceTest`
- [ ] executar `SecurityHttpIT`
- [ ] executar `CoreBackboneFlowIT`
- [ ] validar que o comportamento externo do pricing atual permaneceu correto
- [ ] validar que a análise atual do pricing permaneceu correta
- [ ] atualizar documentação, se necessário

## Observações de execução

- Não expandir `PricingScenarioRequest` além do mínimo necessário nesta etapa.
- Não criar execução real de cenário nesta fase se isso ampliar o escopo.
- Não alterar endpoint público sem necessidade explícita da spec.
- Não mover responsabilidade de cálculo para o resolver.
- Não manter cálculo econômico relevante duplicado em mais de uma camada.

## Critério de encerramento
As tasks podem ser consideradas concluídas quando:
- os contratos internos estiverem explícitos;
- o cálculo puro estiver isolado;
- a resolução de inputs atuais estiver isolada;
- `PricingService` estiver reorganizado como orquestrador;
- `PricingAnalysisService` continuar funcional;
- os testes relevantes estiverem verdes;
- o comportamento externo atual tiver sido preservado.
