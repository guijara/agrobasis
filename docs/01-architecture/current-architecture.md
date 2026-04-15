# Arquitetura atual

## Objetivo deste documento

Este documento descreve a arquitetura efetivamente implementada no estado atual do AgroBasis.

Seu papel é registrar o que já existe no código e na infraestrutura usada hoje, distinguindo esse estado da arquitetura alvo mais ampla já modelada para o produto.

## Visão geral

No estado atual, o AgroBasis está implementado principalmente como um **monólito modular** concentrado no `core-service`.

Essa escolha foi adotada para permitir a consolidação do domínio central, dos fluxos de segurança e do cálculo econômico antes da introdução de uma arquitetura distribuída mais complexa.

## Componentes atualmente presentes

### `core-service`
É o núcleo funcional atual do sistema.

Ele concentra os principais módulos e fluxos implementados, incluindo:

- `organization`
- `identity`
- `farm`
- `market`
- `cost`
- `pricing`
- `shared`

## Responsabilidades atuais do core-service

### Organização e multitenancy
O sistema já modela `Organization` como fronteira lógica principal de tenant.

### Identidade e acesso
O sistema já possui:

- cadastro de usuários;
- autenticação com JWT;
- autorização com Spring Security;
- principal customizado;
- solicitação e aprovação de vínculo organizacional;
- tenant enforcement inicial.

### Estrutura produtiva
O sistema já modela:

- fazenda;
- talhão;
- commodity principal do talhão.

### Mercado
O sistema já possui:

- persistência de cotação de commodity;
- persistência de taxa de câmbio;
- sincronização manual com fontes externas;
- histórico de registros persistidos.

### Custos e perfis internos
O sistema já possui:

- perfil de custo por organização e commodity;
- perfil de frete por organização, fazenda e commodity;
- perfil de ajuste comercial por organização, fazenda e commodity.

### Pricing
O sistema já calcula, de forma determinística e rastreável:

- preço convertido;
- preço ajustado por custo;
- preço líquido após frete;
- preço comercial após ajuste comercial;
- análise atual do pricing com composição e indicadores percentuais.

## Infraestrutura local atual

### PostgreSQL
É o banco de dados principal atualmente utilizado pelo sistema.

No estado atual, ele já suporta o fluxo funcional e o versionamento de esquema com Flyway.

### Redis
Está provisionado no ambiente local como parte da infraestrutura do projeto, mas ainda não atua como componente central do fluxo funcional atualmente implementado.

## Forma atual de integração externa

O sistema já realiza integração externa inicial com fontes de mercado, mas essa integração ocorre de forma controlada:

- via endpoints administrativos protegidos;
- com clients especializados;
- com validação e normalização dos dados;
- com persistência histórica no banco;
- sem acoplar diretamente o pricing às APIs externas.

## Limites da arquitetura atual

No estado atual, o sistema ainda **não implementa como fluxo central**:

- frontend produtivo consolidado;
- API Gateway ativo como sentinela externa;
- `intelligence-service`;
- processamento distribuído real para simulação;
- mensageria ativa entre serviços;
- alertas operacionais completos;
- estoque físico modelado no núcleo implementado;
- histórico de simulação persistido como parte do fluxo principal.

Esses elementos pertencem à arquitetura alvo do produto, mas ainda não fazem parte da implementação efetiva do repositório neste estágio.

## Papel desta arquitetura

A arquitetura atual existe para:

- consolidar o núcleo do domínio;
- estabilizar segurança e isolamento;
- amadurecer o cálculo econômico;
- permitir evolução incremental do produto;
- preparar a futura adoção de módulos especializados sem inflar prematuramente a complexidade operacional.
