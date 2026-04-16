# Módulo identity

## Papel

O módulo `identity` é responsável por identidade, autenticação, autorização e vínculo organizacional dos usuários no AgroBasis.

## Responsabilidades principais

O módulo concentra:

- cadastro de usuário;
- autenticação com JWT;
- autorização com papéis de acesso;
- principal autenticado no contexto de segurança;
- status de acesso do usuário;
- solicitação de vínculo organizacional;
- aprovação ou rejeição de vínculo.

## Conceitos centrais

### User
Representa a conta do usuário no sistema.

### UserRole
Representa o papel de acesso do usuário.

### UserAccessStatus
Representa o estado de acesso do usuário em relação à organização.

### OrganizationMembershipRequest
Representa a solicitação de vínculo entre usuário e organização.

## Regra central do módulo

O módulo diferencia explicitamente:

- existência da conta;
- permissão para autenticar;
- pertencimento efetivo à organização.

Essa separação é importante para garantir que criar uma conta não signifique automaticamente ganhar acesso ao tenant.

## Papel na segurança

O módulo `identity` sustenta:

- login;
- emissão de JWT;
- validação de papéis;
- tenant enforcement inicial;
- proteção dos fluxos HTTP.

## Observações

O módulo é uma das principais bases de segurança do sistema. Ele deve permanecer coerente com:
- o conceito de organization como tenant;
- o fluxo de aprovação organizacional;
- a política de autenticação e autorização do projeto.
