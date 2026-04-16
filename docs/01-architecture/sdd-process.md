# Processo de desenvolvimento orientado por especificação

## Objetivo

Este documento define como o AgroBasis utiliza desenvolvimento orientado por especificação no fluxo real do projeto.

O objetivo desse processo não é criar burocracia. O objetivo é reduzir ambiguidade, melhorar a qualidade das decisões, aumentar a rastreabilidade das mudanças e tornar a implementação mais previsível para humanos e agentes de IA.

## Princípios do processo

O processo de SDD do AgroBasis segue estes princípios:

- documentação viva como fonte principal de contexto;
- mudanças relevantes devem ser definidas antes da implementação;
- artefatos curtos, objetivos e úteis;
- separação clara entre produto, arquitetura, evolução, mudança específica e decisão;
- implementação guiada por contexto, não por impulso;
- validação explícita com testes e critérios de encerramento.

## Artefatos do processo

### 1. Vision
Arquivo principal:
- `docs/00-product/vision.md`

Descreve:
- o que é o produto;
- qual problema ele resolve;
- para quem existe;
- qual valor pretende entregar;
- o que está dentro e fora de sua natureza principal.

### 2. Roadmap
Arquivo principal:
- `docs/00-product/roadmap.md`

Descreve:
- a direção macro de evolução do produto;
- as etapas principais;
- a lógica de amadurecimento do sistema.

### 3. Arquitetura
Arquivos principais:
- `docs/01-architecture/system-overview.md`
- `docs/01-architecture/current-architecture.md`
- `docs/01-architecture/target-architecture.md`
- `docs/01-architecture/data-architecture.md`

Descrevem:
- a visão geral do sistema;
- o estado atual implementado;
- a arquitetura alvo;
- a arquitetura de dados.

### 4. Stages
Arquivos principais:
- `docs/02-stages/stage-a.md`
- `docs/02-stages/stage-b.md`
- `docs/02-stages/stage-c.md`

Descrevem:
- o papel de cada macroetapa;
- o que foi consolidado;
- o resultado técnico e arquitetural de cada fase.

### 5. Specs
Pasta principal:
- `docs/03-specs/`

Descrevem:
- o que uma mudança relevante deve fazer;
- por que ela existe;
- qual problema resolve;
- quais regras devem ser respeitadas;
- o que está fora do escopo;
- qual é o critério de encerramento.

### 6. Plans
Pasta principal:
- `docs/04-plans/`

Descrevem:
- como uma spec será implementada tecnicamente;
- quais módulos serão afetados;
- quais artefatos serão criados ou alterados;
- qual ordem de implementação seguir;
- como a mudança será validada.

### 7. Decision Records
Pasta principal:
- `docs/05-decisions/`

Descrevem:
- decisões importantes de arquitetura, domínio e direção técnica;
- o contexto da decisão;
- a escolha feita;
- sua justificativa;
- suas consequências.

### 8. Templates
Pasta principal:
- `docs/06-templates/`

Contém os modelos base para:
- spec;
- plan;
- decision;
- stage;
- tasks.

### 9. Tasks
Pasta principal:
- `docs/07-tasks/`

Descrevem:
- os passos operacionais de implementação de uma mudança;
- a decomposição concreta da execução;
- o checklist de realização técnica e validação.

## Fluxo padrão de mudança

Toda mudança relevante deve, preferencialmente, seguir este fluxo:

1. entender o contexto da mudança;
2. localizar sua posição no produto e na arquitetura;
3. criar ou atualizar uma spec;
4. criar ou atualizar um plan, quando necessário;
5. registrar decision record, se houver decisão relevante;
6. criar tasks, quando a mudança justificar decomposição operacional;
7. implementar;
8. validar com testes;
9. atualizar documentação relacionada;
10. registrar encerramento da mudança.

## Quando criar uma spec

Criar spec quando a mudança envolver:

- novo fluxo de negócio;
- mudança de domínio;
- integração externa;
- alteração importante de segurança;
- mudança arquitetural;
- mudança de cálculo;
- alteração de contrato de API;
- expansão relevante de módulo.

## Quando criar um plan

Criar plan quando a mudança:

- afetar vários arquivos ou camadas;
- envolver migration;
- alterar domínio, persistência e API ao mesmo tempo;
- tocar segurança;
- tiver impacto estrutural;
- exigir uma ordem técnica mais clara para reduzir ambiguidade.

## Quando criar tasks

Criar tasks quando:

- a mudança for média ou grande;
- houver vários passos concretos;
- a implementação precisar ser mais determinística;
- o trabalho for executado com apoio de agente;
- houver risco de esquecer partes importantes da entrega.

## Quando registrar uma decisão

Registrar decision record quando a mudança:

- definir uma convenção estrutural importante;
- alterar a direção da arquitetura;
- resolver uma dúvida relevante de modelagem;
- fixar um limite de escopo importante;
- introduzir um trade-off que deva ser preservado.

## Quando não usar o fluxo completo

Nem toda mudança precisa passar por todos os artefatos.

Mudanças pequenas, como:
- correção pontual de bug;
- renomeação local;
- limpeza pequena de código;
- ajuste simples sem impacto de domínio ou contrato;

podem ser implementadas sem criar spec, plan e tasks formais, desde que não comprometam a coerência do sistema.

## Relação entre os artefatos

### Spec
Responde:
- o que deve ser feito?
- por que isso existe?
- quais regras e limites a mudança possui?

### Plan
Responde:
- como isso será implementado agora?

### Tasks
Respondem:
- quais passos concretos devem ser executados?

### Decision
Responde:
- qual escolha importante foi feita e por quê?

## Papel das skills

As skills em `.ai/skills/` existem para orientar a atuação do agente no projeto.

Elas devem ser usadas para padronizar:

- implementação;
- testes;
- documentação;
- naming;
- mensagens;
- revisão de acoplamento;
- criação de artefatos do processo.

As skills não substituem spec ou plan. Elas orientam como o trabalho deve ser conduzido.

## Regra prática de uso das skills

Sempre que uma skill for importante para a qualidade da saída, ela deve ser mencionada explicitamente durante a solicitação.

Exemplos:
- usar skill de workflow de implementação;
- usar skill de integração de testes;
- usar skill de nomenclatura e mensagens;
- usar skill de revisão de acoplamento;
- usar skill de escrita de artefatos de SDD.

## Documentação viva e documentação derivada

A documentação em markdown dentro de `docs/` é a fonte viva principal do projeto.

A pasta `docs/engineering/` contém material consolidado, apresentável ou exportável, mas não deve ser tratada como a fonte principal de manutenção do conhecimento.

## Documentação da raiz e documentação local de módulo

### Docs da raiz
Servem para:
- produto;
- arquitetura;
- etapas;
- specs;
- plans;
- decisions;
- templates;
- tasks.

### Docs locais de módulo
Servem para:
- contexto local do módulo;
- entidades centrais;
- responsabilidades;
- limites;
- dependências principais.

## Critério de qualidade do processo

O processo está funcionando corretamente quando:

- mudanças relevantes não começam direto no código;
- a documentação reduz ambiguidade em vez de só registrar história;
- as decisões importantes ficam rastreáveis;
- as implementações seguem padrão;
- os testes validam o que a spec propunha;
- o conhecimento do projeto deixa de depender apenas de memória informal.

## Observação final

O SDD do AgroBasis foi adotado de forma leve e incremental.

A intenção não é transformar o projeto em um processo pesado, e sim construir uma base documental e operacional que permita:

- evolução mais previsível;
- melhor uso de agentes de IA;
- maior clareza arquitetural;
- menor retrabalho nas próximas macrofases do sistema.
