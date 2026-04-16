# Módulo organization

## Papel

O módulo `organization` representa a entidade central do sistema do ponto de vista organizacional.

Ele define a empresa como raiz lógica dos dados privados e como fronteira principal de tenant dentro do AgroBasis.

## Responsabilidade principal

Sua responsabilidade é representar a organização como contexto de pertencimento dos principais dados internos do sistema.

Na prática, a organização é a base para:

- associação de usuários;
- associação de fazendas;
- associação de perfis internos de custo;
- associação de perfis de frete;
- associação de ajustes comerciais;
- validação de acesso a fluxos econômicos sensíveis.

## Papel no multitenancy

O AgroBasis adota, no estado atual, um modelo de isolamento lógico por organização.

Isso significa que `Organization` é tratada como a referência principal de tenant e participa da proteção dos fluxos mais sensíveis da aplicação.

## Relações principais

No estado atual do sistema, a organização se relaciona direta ou indiretamente com:

- usuários;
- fazendas;
- perfis de custo;
- perfis de frete;
- perfis de ajuste comercial.

## Observações

A organização não é apenas uma entidade cadastral. Ela representa o limite lógico de acesso e pertencimento de dados privados no sistema.

Em etapas futuras, parte dos fluxos que hoje ainda recebem `organizationId` externamente pode evoluir para maior inferência a partir do contexto autenticado, mas a organização continuará sendo a fronteira principal do tenant.
