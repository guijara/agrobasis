# C.2 — Preparação do núcleo econômico para cenários e inteligência

## Status
- active

## Contexto

Após a consolidação da C.1, o AgroBasis já é capaz de:

- calcular o pricing atual de forma determinística;
- explicar a composição do cálculo;
- expor indicadores analíticos do valor atual;
- proteger o fluxo com autenticação e tenant enforcement.

No entanto, o núcleo econômico ainda está excessivamente concentrado no fluxo atual de aplicação do `PricingService`, que acumula responsabilidades de:

- buscar dados persistidos;
- validar contexto atual;
- calcular o resultado econômico;
- montar a resposta entregue ao cliente.

Esse desenho é suficiente para o estado atual do sistema, mas ainda não prepara adequadamente a próxima fronteira evolutiva do produto.

A próxima macrofase do AgroBasis exigirá:

- cenários determinísticos;
- reaproveitamento do cálculo com entradas explícitas;
- contratos estáveis entre o núcleo do `core-service` e a futura camada de inteligência;
- separação mais clara entre obtenção de dados, cálculo e apresentação da resposta.

A C.2 existe para preparar essa transição sem abrir ainda o `intelligence-service` e sem empurrar para o `core-service` responsabilidades que já pertencem claramente à futura camada analítica especializada.

## Objetivo

Reorganizar o núcleo econômico do pricing para torná-lo reutilizável, explícito e preparado para integração futura com cenários e inteligência, mantendo o cálculo determinístico no `core-service`.

## Escopo

Esta spec inclui:

- explicitar os insumos do cálculo econômico atual;
- explicitar o resultado puro do cálculo econômico;
- separar resolução de dados persistidos e cálculo;
- manter o cálculo determinístico no `core-service`;
- introduzir um contexto de referência do cálculo atual;
- preparar um contrato inicial de cenário determinístico;
- reorganizar o fluxo do `pricing` para que possa ser reutilizado futuramente pelo `intelligence-service`;
- manter compatibilidade com os fluxos atuais do sistema;
- preparar melhor a saída que será consumida pelo front-end e por futuras camadas analíticas.

## Fora de escopo

Esta spec não inclui:

- Monte Carlo;
- simulação probabilística;
- `intelligence-service`;
- mensageria entre serviços;
- filas;
- processamento assíncrono;
- análise estatística histórica;
- cálculo de volatilidade;
- recomendação automática;
- otimização;
- alertas;
- comparação entre múltiplos cenários em lote;
- persistência de cenários simulados.

## Problema que esta etapa resolve

A C.2 resolve o problema de acoplamento excessivo entre:

- obtenção de dados atuais;
- cálculo do pricing;
- montagem da resposta de aplicação.

Sem essa reorganização, o sistema corre risco de:

- duplicar o motor de cálculo em etapas futuras;
- acoplar a futura inteligência a detalhes do `core-service`;
- dificultar a introdução de cenários;
- misturar domínio econômico com orquestração de infraestrutura.

## Conceitos que devem ser introduzidos

### `PricingInput`
Objeto que representa todos os insumos necessários para executar um cálculo econômico determinístico.

Esse objeto deve tornar explícitos valores que hoje aparecem de forma implícita no fluxo atual de pricing.

### `PricingResult`
Objeto que representa o resultado puro do cálculo econômico.

Esse resultado deve ser desacoplado da forma como os dados foram obtidos e da forma como a resposta HTTP será montada.

### `PricingReferenceContext`
Objeto que representa o contexto de referência dos dados usados no cálculo.

Esse contexto deve transportar metadados relevantes de rastreabilidade, como fontes e datas das referências utilizadas.

### `PricingScenarioRequest`
Objeto que representa uma estrutura inicial de cenário determinístico com sobrescrita explícita de valores, desde que essa sobrescrita seja útil já no estado atual do cálculo.

Esse objeto deve ser preparado de forma controlada, como contrato inicial, sem obrigar a implementação imediata de simulação completa.

## Regras de negócio

- o cálculo econômico determinístico continua pertencendo ao `core-service`;
- o cálculo atual não deve ser duplicado em um motor paralelo;
- o núcleo de cálculo deve operar a partir de entradas explícitas;
- a resolução dos dados atuais deve ser separada do cálculo;
- o resultado puro do cálculo deve ser separado da resposta HTTP atual;
- o `PricingService` pode continuar existindo como serviço de aplicação/orquestração;
- o cálculo deve continuar usando `BigDecimal`;
- a ordem do cálculo econômico deve permanecer explícita e estável;
- a memória de cálculo deve permanecer coerente com o resultado final;
- a reorganização não deve quebrar os endpoints atuais de pricing;
- a reorganização deve preparar o sistema para cenários futuros sem antecipar o `intelligence-service`.

## Ordem econômica que deve ser preservada

O núcleo de cálculo deve continuar preservando, de forma explícita, esta sequência:

1. preço convertido
2. preço ajustado por custo
3. preço líquido após frete
4. preço comercial após ajuste comercial

## Arquitetura esperada

A C.2 deve introduzir ou consolidar as seguintes responsabilidades:

### `CurrentPricingInputResolver`
Responsável por buscar os dados persistidos atuais e montar um `PricingInput`.

Essa camada conhece os repositories e o estado atual do banco.

### `PricingCalculator`
Responsável por receber um `PricingInput` e produzir um `PricingResult`.

Essa camada não deve conhecer:
- repositories;
- controllers;
- integração externa;
- detalhes HTTP.

### `PricingService`
Deve continuar existindo como fachada de aplicação do fluxo atual.

Seu papel passa a ser:
- validar contexto atual;
- resolver os inputs atuais;
- acionar o `PricingCalculator`;
- montar a resposta atual do pricing.

### `PricingAnalysisService`
Deve continuar derivando a análise a partir do fluxo atual reorganizado, sem recriar a lógica de cálculo.

## Contratos que devem surgir

A C.2 deve produzir contratos estáveis que possam ser reutilizados futuramente pela camada de inteligência.

O mínimo esperado é:

- `PricingInput`
- `PricingResult`
- `PricingReferenceContext`

Opcionalmente, também:
- `PricingScenarioRequest`

## Cálculo que continua no core

Esta etapa confirma que ainda pertencem ao `core-service`:

- conversão de preço;
- desconto por custo;
- desconto por frete;
- desconto por ajuste comercial;
- memória de cálculo;
- composição e leitura analítica atual;
- qualquer recalculação puramente determinística baseada em insumos explícitos.

## Cálculo que não entra nesta etapa

Esta etapa não deve introduzir no `core-service`:

- lógica probabilística;
- iterações massivas;
- volatilidade histórica;
- Monte Carlo;
- geração de distribuições;
- análise de risco;
- cálculo assíncrono pesado;
- contratos de mensageria entre serviços.

## Impacto no sistema

Módulos e áreas afetadas:

- `pricing`
- possível reorganização de DTOs internos do cálculo
- manutenção de compatibilidade com respostas atuais
- testes de aplicação do pricing
- testes de integração do fluxo atual
- base arquitetural futura para integração com intelligence

## Estrutura esperada

A solução deve resultar, no mínimo, em:

- um contrato explícito de entrada do cálculo;
- um contrato explícito de saída do cálculo;
- um resolvedor dos dados atuais;
- um calculador desacoplado de persistência;
- adaptação do `PricingService` para orquestração;
- preservação do `PricingAnalysisService` sobre a nova estrutura;
- contexto de referência explícito;
- contrato inicial de cenário, se ele se mostrar útil já nesta etapa.

## Estratégia de testes

A implementação deve ser validada com:

- testes unitários do cálculo puro;
- testes unitários do resolvedor de inputs atuais;
- testes do `PricingService` adaptado;
- testes do `PricingAnalysisService`, garantindo compatibilidade com a reorganização;
- testes de integração dos endpoints atuais afetados;
- backbone flow, garantindo que o comportamento externo continue correto.

## Critério de encerramento

A C.2 pode ser considerada concluída quando:

- o núcleo do cálculo econômico estiver separado da obtenção de dados persistidos;
- `PricingInput` e `PricingResult` estiverem explícitos;
- `PricingReferenceContext` estiver implementado;
- `PricingScenarioRequest` estiver definido de forma útil, se aplicável;
- o `PricingService` estiver reorganizado como orquestrador do fluxo atual;
- o cálculo determinístico continuar correto;
- o fluxo analítico atual continuar funcionando;
- os testes relevantes estiverem verdes;
- a solução deixar clara a fronteira entre o que ainda pertence ao `core-service` e o que deve ficar para a futura camada de inteligência.

## Observações

A C.2 é uma etapa de fronteira.

Ela ainda pertence ao ciclo do `core-service`, mas sua principal função não é produzir inteligência nova. Sua função é preparar corretamente o núcleo econômico e os contratos que permitirão a próxima evolução do sistema sem acoplamento indevido nem duplicação de lógica.
