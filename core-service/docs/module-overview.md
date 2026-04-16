# Visão geral dos módulos do core-service

## Papel do core-service

O `core-service` é o núcleo funcional atual do AgroBasis.

Ele concentra os fluxos principais de domínio, segurança, integração inicial de mercado, perfis econômicos internos e cálculo econômico determinístico do sistema.

## Organização modular atual

O serviço está organizado como monólito modular, permitindo consolidar o núcleo do produto antes da introdução de novos serviços especializados.

## Módulos principais

### `organization`
Define a organização como raiz lógica de tenant e contexto principal de pertencimento dos dados privados.

### `identity`
Concentra identidade, autenticação, autorização e vínculo organizacional dos usuários.

### `farm`
Concentra a estrutura produtiva física básica, incluindo fazenda, talhão e commodity principal.

### `market`
Concentra cotações, câmbio e sincronização inicial de dados externos com persistência histórica.

### `cost`
Concentra os perfis internos de custo, frete e ajuste comercial.

### `pricing`
Concentra o cálculo econômico determinístico e a primeira camada analítica do pricing.

### `shared`
Concentra componentes transversais reutilizáveis, como tratamento de erro, segurança auxiliar, documentação de API e elementos comuns.

## Relações entre módulos

- `identity` depende da noção de `organization` como tenant;
- `farm` depende de `organization`;
- `cost` depende de `organization`, `farm` e `commodity`;
- `market` fornece referências externas persistidas;
- `pricing` depende de `market` e `cost`.

## Princípio de organização

Cada módulo deve preservar sua responsabilidade principal e evitar conhecer detalhes internos demais dos demais contextos.

A evolução do `core-service` deve continuar priorizando:

- clareza de fronteiras;
- baixo acoplamento;
- crescimento incremental;
- coerência com o domínio;
- rastreabilidade do cálculo e das decisões.
