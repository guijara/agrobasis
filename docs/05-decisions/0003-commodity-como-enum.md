# Commodity como enum

## Status
- accepted

## Contexto
O sistema precisava representar as commodities monitoradas e utilizadas em módulos como `farm`, `market`, `cost` e `pricing`.

No escopo inicial do AgroBasis, o conjunto de commodities era limitado e bem definido, com foco principal em soja e milho.

## Decisão
`Commodity` será modelada como enum no estado atual do sistema.

## Justificativa
Essa decisão foi tomada para:

- simplificar a modelagem do domínio;
- evitar inconsistência semântica de cadastro;
- padronizar o uso das commodities nos módulos do sistema;
- reduzir complexidade desnecessária na fase inicial do projeto.

## Consequências

### Positivas
- menor complexidade estrutural;
- maior consistência entre módulos;
- menos risco de dados ambíguos ou inválidos;
- integração mais simples com pricing, cost e market.

### Negativas ou trade-offs
- adicionar novas commodities exige mudança de código;
- o modelo é menos flexível do que uma entidade de referência persistida;
- essa decisão pode precisar ser revista caso o escopo do produto cresça significativamente.

## Observações
A decisão é adequada ao escopo atual do projeto e não impede evolução futura para um modelo mais flexível, se o domínio exigir isso.
