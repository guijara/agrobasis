# Recursos de IA do projeto

Esta pasta reúne recursos de apoio ao uso de agentes no AgroBasis.

O objetivo desses arquivos é padronizar a forma como agentes de IA colaboram com o projeto, reduzindo ambiguidade e aumentando a consistência entre implementação, testes, documentação e arquitetura.

## Estrutura

### `skills/`
Contém skills operacionais e especializadas do projeto.

Essas skills funcionam como guias reutilizáveis para tarefas recorrentes, como:

- workflow de implementação;
- convenções de nomenclatura;
- modelagem de pricing;
- testes de integração;
- documentação;
- registro de decisões;
- criação de artefatos de SDD;
- revisão de acoplamento;
- workflow de commit.

## Papel das skills

As skills não substituem:
- pedido atual;
- spec;
- plan;
- decision.

Elas complementam esses artefatos, orientando como o trabalho deve ser conduzido dentro dos padrões do AgroBasis.

## Regra prática de uso

Quando uma skill for importante para a qualidade da saída, ela deve ser mencionada explicitamente durante a solicitação.

Exemplos:
- usar skill de workflow de implementação;
- usar skill de testes de integração;
- usar skill de nomenclatura e mensagens;
- usar skill de revisão de acoplamento;
- usar skill de criação de artefatos de SDD.

## Observação

A pasta `.ai/` faz parte da infraestrutura documental e operacional do projeto, mas não substitui a documentação viva em `docs/`.
