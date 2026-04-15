# Visão geral do sistema

## Papel desta documentação

Este documento apresenta a visão geral da arquitetura do AgroBasis, distinguindo:

- a arquitetura atualmente implementada;
- a arquitetura alvo do produto;
- a direção evolutiva prevista para o sistema.

Essa separação é necessária porque o projeto já possui uma modelagem arquitetural mais ampla do que aquilo que está implementado no estado atual do repositório.

## Visão geral

O AgroBasis é uma plataforma de suporte à decisão para comercialização de commodities agrícolas.

Sua arquitetura foi concebida para transformar dados externos de mercado e dados internos da organização em informação econômica e, futuramente, em inteligência analítica mais avançada.

A arquitetura geral do produto considera os seguintes blocos principais:

- Frontend
- API Gateway
- Core Service
- Intelligence Service
- Message Broker
- Main Database
- Cache Database
- integrações externas de mercado, logística e mensageria

Essa visão mais ampla já está modelada na documentação de engenharia do projeto e representa a arquitetura alvo do sistema. :contentReference[oaicite:2]{index=2} :contentReference[oaicite:3]{index=3}

## Arquitetura atualmente implementada

No estado atual do projeto, a implementação efetiva está concentrada no `core-service`, estruturado como um monólito modular.

Esse núcleo já contém:

- organização;
- identidade;
- estrutura produtiva;
- mercado;
- custos;
- pricing;
- segurança;
- integração externa inicial de mercado;
- análise atual do pricing.

Além disso, o ambiente local já utiliza infraestrutura de apoio com PostgreSQL e Redis provisionado, embora nem todos os papéis futuros do Redis e da mensageria já estejam ativos no fluxo implementado.

## Arquitetura alvo

A arquitetura alvo do AgroBasis é distribuída e especializada por responsabilidade.

### Frontend
Responsável pela experiência de uso, dashboards, filtros e visualizações orientadas à decisão.

### API Gateway
Responsável por centralizar o tráfego externo, proteger os serviços internos e atuar como sentinela de comunicação.

### Core Service
Responsável pelo núcleo de regras de negócio, identidade, organização, estrutura produtiva, mercado, custos e pricing determinístico.

### Intelligence Service
Responsável pela camada analítica especializada, incluindo simulações, cenários e processamento estatístico intensivo.

### Message Broker
Responsável pela comunicação assíncrona entre serviços e pela desacoplagem de tarefas pesadas ou reativas.

### Main Database
Responsável pelo armazenamento relacional principal do sistema.

### Cache Database
Responsável por dados voláteis, aceleração de leitura e apoio a padrões futuros de cache e mensageria.

## Estratégia evolutiva

A implementação do AgroBasis não está tentando construir toda essa arquitetura distribuída desde o início.

A estratégia adotada é incremental:

1. consolidar o domínio central e os fluxos reais no `core-service`;
2. endurecer segurança e isolamento entre organizações;
3. amadurecer o pricing e sua camada analítica;
4. adaptar o projeto ao processo orientado por especificação;
5. só então abrir a próxima fronteira arquitetural, especialmente o `intelligence-service`.

## Persistência e dados

A modelagem de dados do produto foi concebida para sustentar:

- isolamento multitenant por organização;
- custos internos sigilosos;
- dados estruturais de produção;
- séries temporais de mercado e frete;
- histórico de simulação;
- gatilhos e notificações.

Essa visão de persistência é mais ampla do que o estado já implementado no banco atual, mas representa a direção arquitetural do sistema. :contentReference[oaicite:4]{index=4}

## Observação importante

A documentação de arquitetura do AgroBasis deve sempre distinguir:

- o que já existe no código;
- o que já foi modelado como arquitetura alvo;
- e o que ainda é direção futura.

Essa separação evita confusão entre roadmap, engenharia e estado real de implementação.
