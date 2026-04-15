# Módulo pricing

## Papel
O módulo `pricing` transforma dados de mercado e perfis internos em informação econômica útil, rastreável e explicável.

## Responsabilidades principais
- calcular o preço convertido;
- calcular o preço ajustado por custo;
- calcular o preço líquido após frete;
- calcular o preço comercial após ajuste comercial;
- expor memória de cálculo;
- expor análise atual do pricing.

## Dependências principais
O módulo depende de:
- `market`
- `cost`

## Observação
O módulo `pricing` não consulta diretamente APIs externas. Ele consome apenas dados persistidos no sistema.
