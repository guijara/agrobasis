# Módulo farm

## Papel

O módulo `farm` representa a estrutura produtiva física básica da organização.

Ele modela a base espacial mínima necessária para que o sistema associe produção, custos e pricing a uma origem operacional concreta.

## Responsabilidades principais

O módulo concentra:

- cadastro de fazendas;
- cadastro de talhões;
- associação de commodity principal ao talhão.

## Conceitos centrais

### Farm
Representa a unidade produtiva macro da organização.

### Plot
Representa a subdivisão operacional da fazenda.

### Commodity
Representa a commodity principal associada ao talhão no escopo atual do sistema.

## Papel no domínio

A estrutura do módulo permite que o sistema relacione o cálculo econômico a uma origem produtiva concreta.

Esse papel se tornou especialmente importante com a introdução de:

- frete por fazenda e commodity;
- ajuste comercial por fazenda e commodity;
- consultas de pricing por fazenda.

## Limitações atuais

O módulo ainda não modela, de forma explícita:

- safra como entidade própria;
- produção temporal detalhada;
- histórico produtivo do talhão;
- múltiplas commodities simultâneas por talhão.

## Observações

O módulo `farm` fornece a estrutura produtiva mínima necessária para o estágio atual do AgroBasis. Evoluções futuras podem enriquecer esse contexto, mas a modelagem atual foi mantida simples de propósito para consolidar primeiro o núcleo econômico do sistema.
