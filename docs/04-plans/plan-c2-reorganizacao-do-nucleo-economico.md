# Plano técnico — C.2 Reorganização do núcleo econômico

## Spec relacionada
- `docs/03-specs/active/c2-preparacao-do-nucleo-economico-para-cenarios-e-intelligence.md`

## Objetivo do plano
Reorganizar o núcleo econômico do pricing para separar:

- obtenção de dados atuais persistidos;
- cálculo econômico determinístico;
- contexto de referência do cálculo;
- preparação inicial para cenários futuros.

O foco é tornar o núcleo do cálculo reutilizável, explícito e preparado para integração futura com a camada de inteligência, sem alterar o comportamento externo esperado do sistema.

## Módulos afetados
- `pricing`
- integração indireta com `market`
- integração indireta com `cost`
- testes de aplicação do pricing
- testes de integração do fluxo atual

## Regra de implementação
A implementação deve seguir esta sequência obrigatória:

1. explicitar contratos internos;
2. extrair o cálculo puro;
3. extrair a resolução dos dados atuais;
4. adaptar o `PricingService` para orquestração;
5. manter compatibilidade com o fluxo analítico atual;
6. validar com testes.

Não inverter essa ordem.

## Artefatos a criar

### Contratos internos do pricing
Criar:

- `PricingInput`
- `PricingResult`
- `PricingReferenceContext`

### Contrato inicial de cenário
Criar:
- `PricingScenarioRequest`

Esse artefato deve ser criado apenas como contrato inicial de cenário determinístico explícito, sem obrigar implementação completa de simulação nesta etapa.

### Camadas internas do fluxo
Criar:

- `PricingCalculator`
- `CurrentPricingInputResolver`

## Papel exato de cada artefato

### `PricingInput`
Representa os insumos necessários para o cálculo econômico determinístico.

Deve conter, no mínimo:

- `commodity`
- `farmId`
- `marketPrice`
- `marketCurrency`
- `unit`
- `exchangeRate`
- `costPerTon`
- `freightPerTon`
- `adjustmentPerTon`

Não incluir metadados de origem nesse objeto.

### `PricingResult`
Representa o resultado puro do cálculo econômico.

Deve conter, no mínimo:

- `convertedPrice`
- `adjustedPrice`
- `netPrice`
- `commercialPrice`

Se a memória de cálculo for incluída aqui, ela deve representar apenas o cálculo puro, sem misturar dados de origem ou detalhes HTTP.

### `PricingReferenceContext`
Representa o contexto de referência usado no cálculo.

Deve conter, no mínimo:

- `organizationId`
- `farmId`
- `commodity`
- `marketQuoteSource`
- `marketQuoteQuotedAt`
- `exchangeRateSource`
- `exchangeRateQuotedAt`

Se necessário, também pode carregar metadados adicionais de rastreabilidade, desde que continuem estritamente ligados à origem dos dados.

### `PricingScenarioRequest`
Representa uma estrutura inicial de sobrescrita determinística de valores.

Deve ser criado de forma mínima.

Nesta etapa, ele deve conter apenas campos opcionais de sobrescrita, como:

- `marketPrice`
- `exchangeRate`
- `costPerTon`
- `freightPerTon`
- `adjustmentPerTon`

Não adicionar lógica de execução de cenário nesta etapa se isso exigir ampliação indevida do escopo.

### `PricingCalculator`
Responsável exclusivamente pelo cálculo econômico.

Deve:

- receber um `PricingInput`;
- devolver um `PricingResult`;
- não conhecer repositories;
- não conhecer controllers;
- não conhecer contexto HTTP;
- não consultar integração externa;
- não montar response pública.

### `CurrentPricingInputResolver`
Responsável por montar o `PricingInput` e o `PricingReferenceContext` a partir dos dados atuais persistidos.

Deve:

- consultar os repositories necessários;
- validar ausência de dados da mesma forma que o fluxo atual;
- preservar as exceptions já consolidadas no domínio;
- não executar o cálculo econômico;
- não montar resposta HTTP.

## Artefatos a adaptar

### `PricingService`
Adaptar para que ele deixe de concentrar cálculo e obtenção de dados no mesmo método.

O fluxo obrigatório do `PricingService` deve passar a ser:

1. validar contexto de entrada, se aplicável;
2. chamar `CurrentPricingInputResolver`;
3. obter `PricingInput` e `PricingReferenceContext`;
4. chamar `PricingCalculator`;
5. montar o `CurrentPricingResponse` atual;
6. preservar o contrato atual do endpoint.

### `PricingAnalysisService`
Adaptar para continuar funcionando sobre a nova estrutura.

Regra:
- não duplicar cálculo;
- continuar derivando a análise a partir do fluxo atual reorganizado;
- preservar os indicadores e o comportamento da C.1.

## Estrutura recomendada

### Camada de contrato interno
Criar em `pricing/domain` ou `pricing/application/internal`, conforme o padrão do projeto, mas manter todos os contratos internos do núcleo econômico agrupados no mesmo contexto.

A recomendação principal é:
- não espalhar `PricingInput`, `PricingResult` e `PricingReferenceContext` em pacotes desconexos.

### Camada de cálculo
`PricingCalculator` deve ficar junto do núcleo do módulo `pricing`, em uma área claramente interna ao cálculo.

### Camada de resolução
`CurrentPricingInputResolver` deve ficar na camada de aplicação do `pricing`, pois depende de repositories e orquestra acesso aos dados atuais.

## Regras de preservação de comportamento

A C.2 não deve alterar o comportamento externo dos fluxos já existentes.

Isso significa que devem continuar corretos:

- endpoint atual de pricing;
- endpoint atual de análise do pricing;
- memória de cálculo do fluxo atual;
- exceptions já consolidadas;
- tenant enforcement dos controllers;
- resultados econômicos atuais.

## Ordem econômica obrigatória

O `PricingCalculator` deve preservar exatamente esta ordem:

1. `convertedPrice = marketPrice × exchangeRate`
2. `adjustedPrice = convertedPrice - costPerTon`
3. `netPrice = adjustedPrice - freightPerTon`
4. `commercialPrice = netPrice - adjustmentPerTon`

Não alterar a ordem nem introduzir novos componentes nesta etapa.

## O que não implementar nesta etapa

Não implementar nesta etapa:

- execução real de cenário por `PricingScenarioRequest`;
- simulação determinística completa;
- múltiplos cenários;
- persistência de cenário;
- Monte Carlo;
- cálculos probabilísticos;
- fila;
- mensageria;
- comunicação com futuro `intelligence-service`.

## Estratégia de implementação

### Passo 1 — criar contratos internos
Criar primeiro:
- `PricingInput`
- `PricingResult`
- `PricingReferenceContext`
- `PricingScenarioRequest`

### Passo 2 — criar núcleo puro de cálculo
Criar `PricingCalculator` usando apenas `PricingInput` e `PricingResult`.

### Passo 3 — criar resolvedor de inputs atuais
Criar `CurrentPricingInputResolver` com dependência dos repositories atuais.

Esse resolvedor deve ser o único novo ponto autorizado a conhecer a combinação atual de fontes persistidas do pricing.

### Passo 4 — adaptar `PricingService`
Fazer o `PricingService` usar:
- `CurrentPricingInputResolver`
- `PricingCalculator`

e continuar montando o `CurrentPricingResponse`.

### Passo 5 — adaptar `PricingAnalysisService`
Garantir que a análise continue compatível com a nova estrutura sem recalcular a lógica por outro caminho.

### Passo 6 — validar
Executar testes de unidade e integração relevantes.

## Estratégia de testes

### Novos testes esperados
Criar ou adaptar:

- teste unitário para `PricingCalculator`
- teste unitário para `CurrentPricingInputResolver`

### Testes a adaptar
Adaptar:

- `PricingServiceTest`
- `PricingAnalysisServiceTest`

### Testes de integração a manter verdes
Executar:

- `SecurityHttpIT`
- `CoreBackboneFlowIT`

Se o endpoint de pricing atual continuar o mesmo, o foco dos ITs é garantir que a reorganização interna não quebrou o comportamento externo.

## Validações obrigatórias

A implementação só pode ser considerada correta se estas condições forem verdadeiras:

- `PricingCalculator` não conhece repositories;
- `CurrentPricingInputResolver` não calcula preços;
- `PricingService` não concentra mais todo o cálculo internamente;
- `PricingAnalysisService` continua funcional;
- os resultados finais continuam idênticos aos do fluxo atual;
- os testes relevantes passam sem alteração indevida do contrato público.

## Riscos e cuidados

- não duplicar lógica entre `PricingService` e `PricingCalculator`;
- não mover para `PricingReferenceContext` informações que são do cálculo puro;
- não deixar `PricingScenarioRequest` crescer além do necessário;
- não quebrar a memória de cálculo atual;
- não mudar comportamento externo enquanto reorganiza a estrutura interna;
- não introduzir abstrações extras além das definidas neste plano.

## Critério de encerramento

O plano pode ser considerado concluído quando:

- os contratos internos do núcleo econômico estiverem explícitos;
- o cálculo puro estiver extraído em `PricingCalculator`;
- a resolução dos dados atuais estiver extraída em `CurrentPricingInputResolver`;
- `PricingService` estiver atuando como orquestrador;
- `PricingAnalysisService` continuar correto;
- os testes unitários e de integração relevantes estiverem verdes;
- a estrutura final deixar clara a fronteira entre núcleo determinístico e futura camada de inteligência.
