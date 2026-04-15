# Etapa A

## Nome
Espinha dorsal funcional do domínio.

## Objetivo
Estabelecer a base executável do sistema e consolidar o fluxo mínimo funcional do produto.

## Contexto
A Etapa A surgiu da necessidade de construir primeiro uma base funcional coerente do AgroBasis antes de aprofundar segurança, integração externa mais rica, inteligência analítica e operação em produção.

Em vez de tentar implementar grandes blocos isolados desde o início, a estratégia adotada foi consolidar uma espinha dorsal funcional do sistema, permitindo validar o domínio central de ponta a ponta com o menor nível possível de complexidade estrutural.

## Escopo da etapa
Esta etapa buscou consolidar:

- ambiente local com Docker e PostgreSQL;
- versionamento de banco com Flyway;
- estrutura modular inicial do `core-service`;
- modelagem inicial de `organization`, `identity`, `farm`, `market`, `cost` e `pricing`;
- endpoints essenciais;
- testes fundamentais de aplicação e persistência.

## O que ficou fora desta etapa
Esta etapa não teve como foco principal:

- segurança real com JWT e Spring Security;
- tenant enforcement aprofundado;
- integração externa real com mercado;
- frete e ajuste comercial no pricing;
- análise analítica do pricing;
- simulação probabilística;
- mensageria;
- alertas;
- frontend operacional;
- intelligence-service.

## Resultado esperado
Ao final da etapa, o sistema deveria ser capaz de executar um fluxo funcional mínimo, incluindo:

- criar organização;
- criar usuário;
- criar fazenda;
- criar talhão com commodity;
- registrar cotação de mercado;
- registrar taxa de câmbio;
- registrar perfil de custo;
- calcular o preço atual por commodity.

## Resultado consolidado
Ao final da Etapa A, o AgroBasis passou a ter uma base funcional real no `core-service`, estruturada como monólito modular, com os módulos de domínio centrais já organizados e testados.

Essa etapa consolidou:

- `Organization` como fronteira lógica do tenant;
- `User` e papéis como base da identidade;
- `Farm`, `Plot` e `Commodity` como estrutura produtiva básica;
- `MarketQuote` e `ExchangeRate` como referências econômicas externas persistidas;
- `CostProfile` como custo interno simplificado por organização e commodity;
- o primeiro fluxo funcional de `pricing`, com cotação, câmbio e memória de cálculo.

## Impacto arquitetural
A Etapa A consolidou a decisão de iniciar o AgroBasis como um monólito modular no `core-service`, priorizando validação de domínio, simplicidade de evolução e clareza estrutural antes da abertura de serviços especializados. :contentReference[oaicite:1]{index=1}

## Estratégia de validação
A etapa foi validada principalmente com:

- testes de aplicação;
- testes de persistência;
- validação dos fluxos essenciais de criação, consulta e atualização;
- validação do fluxo econômico mínimo do sistema.

A prioridade de testes esteve nos casos de uso de domínio e persistência, em vez de privilegiar controller tests isolados.

## Observações
A Etapa A não esgota a modelagem do domínio produtivo. Ela consolida apenas a estrutura mínima funcional necessária para que o sistema deixe de ser apenas uma ideia arquitetural e passe a operar como software executável.
