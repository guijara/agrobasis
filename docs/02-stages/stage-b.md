# Etapa B

## Nome
Segurança, isolamento e robustez da base.

## Objetivo
Transformar a base funcional da Etapa A em uma base operacional mais segura, mais robusta e economicamente mais útil.

## Contexto
Depois da consolidação da espinha dorsal funcional, o projeto precisava endurecer sua base arquitetural.

A Etapa B surgiu para resolver quatro necessidades principais:

- autenticação e autorização reais;
- isolamento entre tenants;
- integração com dados externos reais de mercado;
- amadurecimento progressivo do cálculo econômico.

Por causa da amplitude desse escopo, a etapa foi dividida em subetapas.

## Subetapas
- B.1 — Segurança e Acesso
- B.2 — Integração externa de mercado
- B.3 — Evolução econômica do pricing com frete
- B.4 — Refinamento comercial do pricing

## Escopo da etapa
A Etapa B buscou consolidar:

- autenticação com JWT;
- autorização com Spring Security;
- principal customizado;
- fluxo de solicitação e aprovação de vínculo organizacional;
- tenant enforcement inicial;
- sincronização manual de dados de mercado;
- integração inicial com fontes externas;
- custo, frete e ajuste comercial como perfis internos;
- amadurecimento do pricing para preço comercial final;
- ampliação da cobertura de testes;
- reforço da coerência entre API, domínio, persistência e segurança.

## O que ficou fora desta etapa
A Etapa B não teve como foco principal:

- simulação determinística de cenários;
- simulação probabilística;
- intelligence-service;
- mensageria ativa entre serviços;
- alertas de negócio;
- frontend orientado à decisão;
- produção e operação em nuvem.

## Resultado esperado
Ao final da etapa, o sistema deveria:

- autenticar usuários com segurança real;
- impedir acesso entre tenants distintos;
- sincronizar dados externos de mercado;
- manter pricing desacoplado das APIs externas;
- calcular valor econômico mais próximo da operação real;
- preservar rastreabilidade do cálculo.

## Resultado consolidado
Ao final da Etapa B, o AgroBasis passou a contar com:

- autenticação com JWT de 12 horas;
- autorização com Spring Security;
- vínculo organizacional controlado por solicitação e aprovação;
- tenant enforcement inicial nos fluxos sensíveis;
- integração externa manual com mercado e câmbio;
- persistência histórica de referências econômicas;
- `CostProfile`, `FreightProfile` e `CommercialAdjustmentProfile`;
- `pricing` com:
  - preço convertido;
  - preço ajustado por custo;
  - preço líquido após frete;
  - preço comercial após ajuste comercial;
- memória de cálculo detalhada;
- backbone flow seguro e validado.

## Impacto arquitetural
A Etapa B consolidou a base operacional real do `core-service` e distinguiu melhor:

- o papel do `market` como repositório histórico de referências externas;
- o papel do `cost` como portador dos perfis internos da organização;
- o papel do `pricing` como motor determinístico de cálculo econômico.

Também consolidou a arquitetura de segurança e reforçou a fronteira organizacional do sistema. :contentReference[oaicite:2]{index=2} :contentReference[oaicite:3]{index=3}

## Estratégia de validação
A Etapa B foi validada por meio de:

- testes de aplicação;
- testes de persistência;
- testes HTTP de segurança;
- testes de tenant enforcement;
- atualização do backbone flow ponta a ponta.

Esses testes passaram a validar não só entidades isoladas, mas também o comportamento integrado do sistema.

## Observações
A Etapa B foi o ponto em que o AgroBasis deixou de ser apenas um sistema estrutural inicial e passou a se tornar uma base realmente utilizável para cálculo econômico agrícola, ainda sem abrir a camada analítica especializada futura.
