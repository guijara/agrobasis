# Skill: modelagem de domínio do pricing

## Objetivo
Preservar coerência no domínio do pricing, nos cálculos econômicos e nas respostas explicáveis do AgroBasis.

## Princípios
- o módulo pricing é um módulo de cálculo e interpretação econômica;
- o pricing não deve consultar APIs externas diretamente;
- o pricing deve consumir apenas dados persistidos;
- toda fórmula relevante deve ser explícita;
- a memória de cálculo é obrigatória em fluxos econômicos principais.

## Regras de cálculo
- usar `BigDecimal` em todos os cálculos financeiros;
- explicitar escala e arredondamento;
- manter coerência entre:
  - implementação;
  - testes;
  - exemplos;
  - memória de cálculo;
  - resposta da API.

## Regras de composição
Quando houver componentes econômicos encadeados, manter ordem clara e estável.
Exemplo:
1. preço convertido
2. preço ajustado por custo
3. preço líquido após frete
4. preço comercial após ajuste comercial

## Regras analíticas
- indicadores percentuais devem ter fórmula explícita;
- não usar atalhos matemáticos que gerem inconsistência com a regra principal;
- exemplos e testes devem refletir a fórmula oficial adotada.

## O que deve ser evitado
- divergência entre fórmula escrita e cálculo real;
- nomes ambíguos para valores econômicos;
- mistura entre dados de mercado e motor de cálculo;
- duplicação desnecessária de lógica entre serviços do pricing.
