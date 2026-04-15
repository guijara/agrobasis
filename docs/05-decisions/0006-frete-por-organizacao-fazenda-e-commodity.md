# Frete por organização, fazenda e commodity

## Status
- accepted

## Contexto
O pricing precisava evoluir para ficar mais próximo da realidade operacional. O frete surgiu como o primeiro componente econômico natural a ser incorporado depois do custo base.

Era necessário decidir onde esse dado viveria e qual seria sua granularidade de modelagem.

## Decisão
O frete será modelado como um dado interno da organização, associado a:

- organização;
- fazenda;
- commodity.

Esse dado será representado por um `FreightProfile`, com `freightPerTon` em BRL/TON.

## Justificativa
Essa decisão foi tomada para:

- refletir melhor a origem física da operação;
- manter coerência com o modelo multitenant;
- evitar modelagem logística complexa cedo demais;
- permitir que o pricing evolua sem depender ainda de rotas, destino ou transportadora.

## Consequências

### Positivas
- o cálculo econômico fica mais realista;
- a modelagem continua simples;
- a relação entre tenant, origem produtiva e produto permanece explícita;
- o `pricing` passa a retornar preço líquido preliminar.

### Negativas ou trade-offs
- o modelo ainda não representa rota, destino ou variação temporal do frete;
- o sistema assume frete simplificado por perfil;
- futuras evoluções logísticas podem exigir uma modelagem mais rica.

## Observações
A unidade adotada no estágio atual é BRL por tonelada. Essa decisão foi tomada para manter consistência com o restante do cálculo econômico atual.
