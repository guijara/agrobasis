# Plans

Esta pasta contém os planos técnicos de implementação das mudanças relevantes do AgroBasis.

Os plans existem para descrever, de forma objetiva, como uma spec será executada no código e na estrutura do projeto.

## Papel dos plans

Enquanto a spec define:
- o problema;
- o objetivo;
- o escopo;
- as regras da mudança;

o plan define:
- a estratégia de implementação;
- os módulos afetados;
- os artefatos que serão criados ou alterados;
- a ordem da execução;
- os impactos esperados;
- e os critérios técnicos de validação.

## Quando criar um plan

Plans são especialmente úteis quando a mudança:
- afeta mais de um módulo;
- envolve migrations;
- altera contratos de API;
- modifica cálculo de pricing;
- toca segurança;
- exige refatoração estrutural;
- ou possui impacto técnico relevante.

## O que um plan não deve ser

Um plan não deve virar:
- uma spec duplicada;
- uma documentação excessivamente burocrática;
- ou uma lista detalhista de microtarefas irrelevantes.

Seu objetivo é orientar a implementação com clareza suficiente para reduzir ambiguidade e manter coerência técnica.

## Relação com specs e tasks

- **Spec**: define o que deve ser feito
- **Plan**: define como será implementado
- **Tasks**: desdobram a execução em passos menores, quando necessário.
