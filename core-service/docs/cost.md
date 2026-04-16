# Módulo cost

## Papel

O módulo `cost` representa os perfis internos de referência econômica da organização.

Ele concentra os componentes internos que, combinados às referências de mercado, permitem transformar o valor bruto da commodity em um valor mais próximo da operação real.

## Responsabilidades principais

O módulo concentra:

- custo base por commodity;
- frete por organização, fazenda e commodity;
- ajuste comercial por organização, fazenda e commodity.

## Conceitos centrais

### CostProfile
Representa o custo base interno da organização por commodity.

### FreightProfile
Representa o frete simplificado por organização, fazenda e commodity.

### CommercialAdjustmentProfile
Representa o ajuste comercial simplificado por organização, fazenda e commodity.

## Papel no sistema

O módulo `cost` não realiza o cálculo final do pricing.

Seu papel é fornecer os perfis internos que serão usados pelo `pricing` para compor:

- preço ajustado;
- preço líquido;
- preço comercial.

## Princípios atuais do módulo

- modelagem simplificada;
- valores determinísticos;
- sem modelagem temporal completa por safra;
- coerência com organização como tenant;
- coerência com fazenda como origem operacional.

## Observações

O módulo foi modelado para ser útil desde cedo, sem antecipar toda a complexidade futura de custos operacionais, logística e comercialização.
