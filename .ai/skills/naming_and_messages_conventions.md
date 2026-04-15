# Skill: convenções de nomes e mensagens

## Objetivo
Padronizar nomes de DTOs, nomes de variáveis e mensagens de validação ou exceção no AgroBasis, com base no padrão já existente do projeto.

## Convenções de DTO
Usar os padrões já consolidados no projeto:

### Entrada de criação
- `SomethingCreateRequest`

### Entrada de atualização
- `SomethingUpdateRequest`

### Saída
- `SomethingResponse`

### Observação
Evitar:
- sufixo genérico `Dto`;
- nomes ambíguos como `Data`, `Payload` ou `Model` para contratos de API;
- misturar request e response no mesmo tipo.

## Convenções de nomes de variáveis
### Regras gerais
- usar nomes claros e consistentes com o domínio;
- seguir o padrão camelCase do projeto;
- preferir nomes completos a abreviações obscuras.

### Exemplos coerentes com o projeto
- `organizationId`
- `farmId`
- `commodity`
- `costPerTon`
- `freightPerTon`
- `adjustmentPerTon`
- `convertedPrice`
- `adjustedPrice`
- `netPrice`
- `commercialPrice`
- `savedUser`
- `savedFarm`
- `savedCostProfile`

### Regras de clareza
- IDs devem terminar com `Id`;
- valores monetários ou econômicos devem ter nome semântico;
- variáveis temporárias não devem esconder o papel do valor.

## Mensagens de validação
- escrever em português;
- ser claras e diretas;
- usar ponto final;
- refletir a regra real da entrada.

### Exemplos
- `O nome da fazenda é obrigatório.`
- `A área em hectares é obrigatória.`
- `O custo por tonelada não pode ser negativo.`
- `O ID da organização é obrigatório.`

## Mensagens de exceção
- escrever em português;
- ser curtas, específicas e consistentes;
- usar ponto final;
- evitar mensagens genéricas quando a regra puder ser explícita.

### Exemplos
- `Organização não encontrada.`
- `Perfil de custo não encontrado.`
- `Já existe uma solicitação pendente para esta organização.`
- `Usuário não possui permissão para aprovar esta solicitação.`

## Regras de consistência
- manter a mesma mensagem entre service e teste quando a mensagem fizer parte do contrato esperado;
- evitar alternar entre versões com e sem ponto final;
- evitar mistura de português e inglês em nomes de mensagem.

## O que deve ser evitado
- DTOs com nomes inconsistentes com o padrão já adotado;
- variáveis com siglas obscuras;
- mensagens vagas como `Erro ao processar operação`;
- mensagens sem ponto final, se o restante do projeto usa ponto final.
