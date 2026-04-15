# Arquitetura alvo

## Objetivo deste documento

Este documento descreve a arquitetura alvo do AgroBasis, isto é, a estrutura mais ampla já modelada para o produto, mas ainda não totalmente implementada no estado atual do projeto.

Seu papel é registrar a direção arquitetural pretendida, preservando a distinção entre:

- visão futura do sistema;
- arquitetura atual implementada;
- etapas intermediárias de evolução.

## Visão geral

A arquitetura alvo do AgroBasis é uma arquitetura de serviços especializados, desenhada para suportar:

- processamento econômico determinístico;
- análise estatística intensiva;
- ingestão de dados externos;
- monitoramento contínuo;
- notificações e gatilhos;
- experiência orientada à decisão.

Essa arquitetura foi concebida para transformar dados de mercado, logística e operação interna em inteligência útil para o produtor ou gestor agrícola.

## Blocos principais da arquitetura alvo

### Frontend
Responsável pela interface principal do sistema.

Seu papel é:
- exibir dashboards;
- apresentar análises econômicas;
- permitir filtros por organização, fazenda e commodity;
- exibir visualizações orientadas à decisão;
- entregar experiência mobile-first e, futuramente, PWA.

### API Gateway
Responsável por funcionar como sentinela de comunicação entre o frontend e os serviços internos.

Seu papel previsto inclui:
- centralização de entrada HTTP;
- proteção dos serviços internos;
- balanceamento e roteamento;
- apoio à segurança de borda;
- desacoplamento entre cliente e serviços internos.

### Core Service
Responsável pelo núcleo de regras de negócio e pelo domínio transacional principal do sistema.

Seu papel inclui:
- identidade e acesso;
- organização e multitenancy;
- estrutura produtiva;
- mercado;
- custos;
- pricing determinístico;
- análise atual do pricing;
- persistência principal dos dados operacionais.

### Intelligence Service
Responsável pela camada de inteligência analítica especializada.

Seu papel previsto inclui:
- simulações;
- cenários;
- processamento estatístico;
- sensibilidade;
- curvas probabilísticas;
- apoio analítico a decisões futuras.

Esse serviço foi concebido como componente separado para evitar que processamento pesado comprometa a fluidez do restante da plataforma.

### Message Broker
Responsável pela comunicação assíncrona entre serviços.

Seu papel previsto inclui:
- desacoplamento entre `core-service` e `intelligence-service`;
- fila de processamento de simulações;
- suporte a tarefas demoradas;
- suporte futuro a alertas e notificações.

### Main Database
Responsável pelo armazenamento relacional principal do sistema.

Seu papel inclui:
- dados organizacionais;
- usuários;
- estrutura produtiva;
- custos;
- perfis internos;
- histórico de mercado;
- resultados relevantes persistidos.

### Cache Database
Responsável pelo armazenamento volátil e de apoio.

Seu papel previsto inclui:
- cache de dados de mercado;
- aceleração de leitura;
- apoio à comunicação assíncrona;
- redução de latência em fluxos sensíveis ao tempo.

## Fontes e sistemas externos previstos

A arquitetura alvo considera integração com:

- APIs de mercado;
- provedores de câmbio;
- fontes logísticas;
- serviços de mensageria;
- serviços externos de notificação.

## Princípios da arquitetura alvo

A arquitetura alvo do AgroBasis foi modelada com os seguintes princípios:

- separação de responsabilidades;
- resiliência;
- crescimento incremental;
- precisão econômica;
- rastreabilidade;
- desacoplamento entre processamento transacional e processamento analítico;
- suporte a evolução futura sem reescrita do núcleo.

## Relação com a implementação atual

A arquitetura alvo não deve ser confundida com o estado atual do código.

Ela funciona como:
- visão orientadora do produto;
- referência para as próximas macroetapas;
- base para evolução futura do `intelligence-service`;
- apoio à documentação de engenharia.

## Observação importante

Sempre que possível, novas evoluções devem ser avaliadas considerando se pertencem:

- ao estado atual do `core-service`;
- à preparação arquitetural;
- ou à arquitetura alvo ainda não implementada.

Essa distinção é essencial para evitar antecipação prematura de complexidade.
