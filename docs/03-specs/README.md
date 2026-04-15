# Specs

Esta pasta contém as especificações das mudanças relevantes do AgroBasis.

As specs descrevem o que deve ser implementado, por que a mudança existe, qual problema ela resolve, quais regras precisam ser respeitadas e quais limites fazem parte do seu escopo.

## Organização

### `active/`
Specs em andamento, em discussão ou ainda não concluídas.

Essa pasta concentra mudanças que ainda estão em aberto, seja porque:
- estão sendo debatidas;
- estão sendo planejadas;
- estão em implementação;
- ou ainda não foram validadas completamente.

### `done/`
Specs concluídas.

Uma spec deve ser movida para `done/` quando:
- sua implementação estiver concluída;
- os testes esperados estiverem validados;
- e o escopo definido tiver sido efetivamente encerrado.

## Papel das specs

As specs são o artefato central do processo orientado por especificação do projeto.

Mudanças relevantes do sistema devem, preferencialmente, nascer aqui antes da implementação, principalmente quando envolverem:
- novos fluxos de negócio;
- mudanças de domínio;
- alterações arquiteturais;
- integrações externas;
- cálculos sensíveis;
- segurança;
- contratos de API.

## O que uma spec não é

Uma spec não é:
- um log de implementação;
- um conjunto de commits;
- uma ata de conversa;
- nem apenas uma lista de tarefas.

Ela deve descrever de forma clara:
- contexto;
- objetivo;
- escopo;
- regras;
- não-objetivos;
- critérios de encerramento.

## Relação com os plans

A spec define **o que deve ser feito**.

O plan define **como isso será implementado**.

Por isso, uma mudança relevante pode gerar:
- uma spec;
- um plan derivado da spec;
- e, se necessário, registros de decisão.
