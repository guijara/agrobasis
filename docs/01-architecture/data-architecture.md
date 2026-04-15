# Arquitetura de dados

## Objetivo deste documento

Este documento descreve a arquitetura de dados do AgroBasis em dois níveis:

- o modelo de dados já implementado no estado atual do sistema;
- a visão mais ampla de dados já modelada para a evolução futura do produto.

Seu papel é evitar confusão entre o banco real já suportado pelo `core-service` e a arquitetura de persistência mais ampla já prevista na documentação de engenharia.

## Princípios de dados do sistema

A arquitetura de dados do AgroBasis foi concebida com os seguintes princípios:

- isolamento lógico por organização;
- rastreabilidade de cálculo;
- suporte a histórico de referências econômicas;
- preservação de segredo de negócio;
- capacidade de evolução incremental do modelo;
- separação clara entre dados internos da organização e dados externos de mercado.

## Estado atual do modelo de dados

No estado atual do projeto, o banco já suporta o núcleo implementado do sistema.

### Núcleo organizacional
A entidade `Organization` já atua como raiz lógica do tenant no sistema.

O modelo atual já suporta associação de organização com:
- usuários;
- fazendas;
- talhões;
- perfis de custo;
- perfis de frete;
- perfis de ajuste comercial;
- fluxos sensíveis de pricing.

### Identidade e acesso
O modelo atual já suporta:
- usuários;
- papéis;
- status de acesso;
- solicitação de vínculo organizacional;
- aprovação e rejeição de vínculo.

### Estrutura produtiva
O modelo atual já suporta:
- fazenda;
- talhão;
- commodity principal associada ao talhão.

### Mercado
O modelo atual já suporta:
- cotação de commodity;
- taxa de câmbio;
- histórico de registros persistidos;
- sincronização externa manual com persistência local.

### Custos e perfis internos
O modelo atual já suporta:
- custo por organização e commodity;
- frete por organização, fazenda e commodity;
- ajuste comercial por organização, fazenda e commodity.

### Pricing
O modelo atual não persiste um “objeto de pricing” como entidade própria do fluxo principal.

O pricing atual é derivado a partir de:
- referências de mercado persistidas;
- perfis internos persistidos;
- regras determinísticas de cálculo.

## Visão ampliada de dados do produto

A documentação de engenharia do AgroBasis já modela uma visão mais ampla de persistência, que vai além do estado atual implementado.

Essa visão inclui conceitos como:

- custos internos sigilosos tratados com proteção especial;
- estoque físico;
- índices de qualidade;
- séries temporais de frete;
- dados históricos de mercado para análises futuras;
- histórico de simulação;
- alertas de preço.

## Distinção entre modelo atual e modelo ampliado

### Modelo atual
Corresponde ao que já está implementado e utilizado pelo `core-service`.

### Modelo ampliado
Corresponde ao conjunto de estruturas de dados já modeladas como parte da visão futura do produto, especialmente para suportar:

- inteligência analítica;
- simulações;
- monitoramento;
- notificações;
- comparação histórica mais rica.

## Isolamento multitenant

A arquitetura de dados do AgroBasis foi concebida com a organização como fronteira lógica principal.

Na prática:
- dados privados da empresa pertencem à organização;
- o tenant enforcement protege os fluxos sensíveis;
- o isolamento é garantido por associação lógica e validação de acesso.

A documentação de engenharia também considera, em visão mais ampla, estratégias de organização de dados com separação mais forte em nível estrutural, mas o estado atual do projeto ainda opera principalmente com isolamento lógico no domínio e na aplicação. :contentReference[oaicite:0]{index=0}

## Dados internos sensíveis

O produto foi concebido para tratar como sensíveis dados como:
- custos internos;
- margens;
- parâmetros econômicos próprios da organização.

No estado atual, o sistema já protege esse contexto principalmente por:
- autenticação;
- autorização;
- tenant enforcement;
- separação lógica por organização.

A arquitetura alvo de dados prevê ampliar esse cuidado em componentes futuros e em estruturas mais sofisticadas de persistência e segurança.

## Séries temporais e inteligência futura

A documentação de engenharia prevê uma camada de dados mais ampla para sustentar:
- histórico de mercado;
- histórico de frete;
- simulações;
- alertas;
- gatilhos;
- análises probabilísticas.

Esses elementos fazem parte da direção futura do produto e não devem ser confundidos com o conjunto mínimo de dados já implementado hoje.

## Regra de documentação

Sempre que houver evolução do modelo de dados, a documentação deve deixar claro:

- o que foi efetivamente implementado;
- o que continua sendo visão ampliada ou arquitetura alvo;
- quais entidades ou estruturas ainda não fazem parte do fluxo real do sistema.
