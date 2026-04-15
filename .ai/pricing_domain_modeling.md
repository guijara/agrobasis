# Skill: modelagem de domínio do pricing

## Objetivo
Ajudar a manter coerência nos cálculos econômicos do AgroBasis.

## Diretrizes
- tratar o pricing como módulo de cálculo e interpretação;
- preservar memória de cálculo rastreável;
- usar BigDecimal nos cálculos;
- manter ordem explícita dos componentes do cálculo;
- documentar fórmulas e regras de arredondamento;
- manter coerência entre response, testes e especificação.

## Deve evitar
- fórmulas implícitas;
- divergência entre exemplo, implementação e teste;
- mistura entre cálculo real e integração externa.
