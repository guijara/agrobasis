# Ajuste comercial por organização, fazenda e commodity

## Status
- accepted

## Contexto
Após a introdução do frete, o pricing ainda precisava de uma camada comercial simples que permitisse aproximar o cálculo da lógica de decisão real da operação, sem introduzir prematuramente toda a complexidade comercial do agronegócio.

## Decisão
O ajuste comercial será modelado como um dado interno da organização, associado a:

- organização;
- fazenda;
- commodity.

Esse dado será representado por um `CommercialAdjustmentProfile`, com `adjustmentPerTon` em BRL/TON.

Na fase atual, esse ajuste é tratado como um abatimento fixo por tonelada.

## Justificativa
Essa decisão foi tomada para:

- adicionar uma camada comercial ao pricing sem inflar o domínio;
- manter simetria com a modelagem do frete;
- preservar o cálculo como determinístico e explicável;
- tornar o preço final mais próximo da leitura comercial da organização.

## Consequências

### Positivas
- o pricing passa a retornar preço comercial final;
- o sistema ganha uma camada adicional de leitura econômica útil;
- a modelagem continua simples e controlada;
- a memória de cálculo permanece clara.

### Negativas ou trade-offs
- o ajuste comercial ainda não representa múltiplos tipos de desconto ou composição comercial mais rica;
- não há modelagem percentual, temporal ou contextual mais avançada;
- o termo “ajuste comercial” exige documentação clara para evitar interpretação excessivamente ampla.

## Observações
No estado atual do projeto, o ajuste comercial é sempre um abatimento fixo por tonelada. Isso pode evoluir futuramente, mas não faz parte do escopo atual.
