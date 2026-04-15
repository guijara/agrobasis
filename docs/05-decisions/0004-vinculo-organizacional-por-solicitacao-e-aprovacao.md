# Vínculo organizacional por solicitação e aprovação

## Status
- accepted

## Contexto
O sistema precisava permitir que usuários criassem conta sem automaticamente ganhar acesso a uma organização. Ao mesmo tempo, era necessário garantir que o pertencimento ao tenant fosse controlado por alguém com autoridade dentro da empresa.

## Decisão
O vínculo entre usuário e organização será controlado por um fluxo de solicitação e aprovação.

O fluxo adotado é:

1. o usuário cria sua conta;
2. a conta nasce sem acesso efetivo à organização;
3. o usuário cria uma solicitação de vínculo com uma organização;
4. um administrador da organização aprova ou rejeita essa solicitação;
5. somente após aprovação o usuário passa a pertencer ao tenant e pode operar normalmente.

## Justificativa
Essa decisão foi tomada para:

- separar identidade de pertencimento organizacional;
- evitar que o cadastro por si só conceda acesso indevido;
- alinhar segurança e domínio organizacional;
- permitir um fluxo controlado e auditável de entrada de usuários.

## Consequências

### Positivas
- maior segurança no controle de acesso organizacional;
- fluxo mais coerente com o conceito de tenant;
- diferenciação clara entre conta criada e acesso efetivo;
- base sólida para autenticação e autorização.

### Negativas ou trade-offs
- o fluxo de onboarding do usuário fica mais longo;
- o sistema precisa manter estados de acesso;
- foi necessário introduzir uma entidade adicional para solicitação de vínculo.

## Observações
Os estados de acesso atuais foram modelados para refletir esse fluxo de forma explícita, incluindo conta pendente, ativa e rejeitada.
