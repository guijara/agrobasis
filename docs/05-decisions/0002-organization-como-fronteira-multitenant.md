# Organization como fronteira multitenant

## Status
- accepted

## Contexto
O AgroBasis precisa garantir isolamento entre empresas, pois dados como custos, fretes, ajustes comerciais, usuários e resultados econômicos não podem ser compartilhados entre organizações diferentes.

A modelagem do sistema exigia uma entidade central que representasse a fronteira lógica de dados do tenant.

## Decisão
A entidade `Organization` será tratada como a fronteira lógica principal do modelo multitenant do sistema.

Os dados relevantes da aplicação devem pertencer, direta ou indiretamente, a uma organização.

## Justificativa
Essa decisão foi tomada para:

- tornar explícita a fronteira de isolamento entre empresas;
- simplificar a aplicação do tenant enforcement;
- alinhar o domínio organizacional com a segurança da aplicação;
- garantir coerência entre identidade, estrutura produtiva, custos e pricing.

## Consequências

### Positivas
- o tenant do sistema fica modelado de forma explícita;
- fica mais simples aplicar filtros e validações por organização;
- os fluxos sensíveis passam a ter uma referência clara de pertencimento;
- a segurança e a modelagem de domínio permanecem coerentes.

### Negativas ou trade-offs
- vários fluxos precisam carregar ou validar `organizationId`;
- em alguns casos, isso aumenta a quantidade de validações por tenant;
- no futuro, parte desses fluxos pode migrar para inferência automática pelo contexto autenticado.

## Observações
No estado atual do projeto, ainda existem endpoints e fluxos em que o `organizationId` é recebido externamente e validado contra o principal autenticado. Há intenção futura de reduzir esse acoplamento em alguns casos, inferindo a organização diretamente do contexto de autenticação.
