# Módulo pricing

## Papel

O módulo `pricing` transforma dados externos e perfis internos em informação econômica útil, rastreável e explicável.

Ele é o principal núcleo de cálculo econômico do AgroBasis no estado atual do sistema.

## Responsabilidades principais

O módulo concentra:

- cálculo do preço convertido;
- cálculo do preço ajustado por custo;
- cálculo do preço líquido após frete;
- cálculo do preço comercial após ajuste comercial;
- memória de cálculo detalhada;
- análise atual do pricing.

## Dependências principais

O módulo depende de dados persistidos em:

- `market`
- `cost`

## Princípio central

O módulo `pricing` não deve conversar diretamente com APIs externas.

Ele sempre deve operar sobre dados já persistidos no banco, mantendo o cálculo desacoplado da disponibilidade imediata das fontes externas.

## Camadas atuais do módulo

### Pricing determinístico
Camada responsável pelo cálculo econômico atual da commodity.

### Pricing analítico atual
Camada responsável pela composição, resumo de impacto e indicadores percentuais do cálculo já existente.

## Papel no produto

No estado atual do projeto, o módulo `pricing` já permite responder:

- qual é o valor econômico atual?
- qual é o preço líquido?
- qual é o preço comercial?
- como esse valor é composto?

## Observações

O módulo `pricing` ainda não incorpora:
- simulação;
- Monte Carlo;
- cenários probabilísticos;
- sensibilidade avançada;
- recomendações automáticas.

Essas capacidades pertencem a etapas futuras do produto e devem ser tratadas com cuidado para não inflar prematuramente o `core-service`.
