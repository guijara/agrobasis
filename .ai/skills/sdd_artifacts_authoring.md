# Skill: criação de artefatos de SDD

## Objetivo
Padronizar a criação de specs, plans, tasks e decision records no AgroBasis.

## Princípios
- artefatos devem ser curtos, claros e úteis;
- cada artefato deve cumprir uma função específica;
- evitar duplicação entre spec, plan, tasks e decision.

## Spec
A spec deve descrever:
- contexto do problema;
- objetivo;
- escopo;
- fora de escopo;
- regras de negócio;
- impactos esperados;
- estratégia de testes;
- critério de encerramento.

A spec responde:
- o que deve ser feito?
- por que isso existe?
- quais limites a mudança possui?

## Plan
O plan deve descrever:
- módulos afetados;
- artefatos a criar ou alterar;
- ordem técnica de implementação;
- riscos e cuidados;
- estratégia de validação.

O plan responde:
- como isso será implementado agora?

## Tasks
As tasks devem ser:
- curtas;
- operacionais;
- diretamente executáveis;
- focadas em passos concretos.

Boa task:
- criar migration;
- criar entidade;
- ajustar service;
- criar testes;
- atualizar documentação.

Task ruim:
- “pensar melhor no sistema”;
- “ver se precisa mudar algo”.

## Decision record
O decision record deve conter:
- contexto;
- decisão;
- justificativa;
- consequências.

Ele responde:
- qual escolha importante foi feita e por quê?

## O que deve ser evitado
- spec virando plan;
- plan virando spec duplicada;
- tasks detalhadas demais;
- decision record usado como log de implementação.
