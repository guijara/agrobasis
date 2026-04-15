# Documentação do AgroBasis

Esta pasta concentra a documentação viva do projeto AgroBasis, organizada para apoiar a evolução do sistema com uma abordagem orientada por especificação.

A documentação aqui armazenada não tem apenas função descritiva. Ela também serve como base para análise, planejamento, implementação e validação das mudanças mais relevantes do projeto.

## Estrutura

### `engineering/`
Documentação de engenharia e apresentação do projeto.

Essa pasta pode conter materiais mais consolidados e apresentáveis, como documentos em PDF ou versões mais formais do projeto. Esses arquivos são úteis para exposição externa, mas não devem ser tratados como a fonte viva principal da evolução do sistema.

### `00-product/`
Documentos de visão geral do produto.

Aqui ficam materiais mais estáveis, como:
- visão do sistema;
- escopo;
- roadmap;
- glossário.

### `01-architecture/`
Documentos de arquitetura e estrutura transversal do sistema.

Aqui ficam descrições sobre:
- visão geral do sistema;
- organização modular;
- multitenancy;
- segurança;
- integrações;
- processo de desenvolvimento baseado em especificação.

### `02-stages/`
Documentação por etapa de evolução do projeto.

Essa pasta registra a visão técnica das etapas já implementadas ou em andamento, permitindo acompanhar o crescimento do sistema de forma incremental e coerente com o roadmap.

### `03-specs/`
Especificações funcionais e técnicas das mudanças relevantes do sistema.

As specs representam o artefato central do processo orientado por especificação. Toda mudança relevante deve nascer ou ser formalizada aqui antes de evoluir para implementação.

### `04-plans/`
Planos técnicos de implementação.

Os plans derivam das specs e descrevem como uma mudança será executada, incluindo estrutura técnica, módulos afetados, impactos e critérios de validação.

### `05-decisions/`
Registros de decisões arquiteturais e de domínio.

Essa pasta existe para documentar decisões importantes tomadas ao longo do projeto, incluindo seu contexto, motivação e consequências.

### `06-templates/`
Templates dos principais artefatos do processo.

Aqui ficam os modelos base para:
- spec;
- plan;
- decision record.

## Fonte principal de verdade

A documentação em markdown desta pasta é a fonte viva principal do processo de desenvolvimento.

Documentos exportados, PDFs ou materiais de apresentação podem coexistir no projeto, mas devem ser tratados como artefatos derivados ou consolidados, e não como o ponto principal de manutenção do conhecimento.

## Objetivo

A organização desta pasta busca manter o processo de desenvolvimento:
- claro;
- incremental;
- rastreável;
- compatível com a evolução arquitetural do AgroBasis.

O objetivo não é criar burocracia, mas sim melhorar a qualidade das decisões e reduzir ambiguidade antes da implementação.
