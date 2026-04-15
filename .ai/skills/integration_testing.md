# Skill: testes de integração

## Objetivo
Padronizar a criação, execução e manutenção de testes de integração úteis e previsíveis no AgroBasis.

## Quando aplicar
Aplicar quando a mudança afetar:
- fluxo HTTP;
- segurança;
- tenant enforcement;
- persistência com múltiplas entidades;
- backbone flow;
- integração entre módulos.

## Regras de teste
- preferir testes que validem comportamento real relevante;
- validar segurança HTTP quando a mudança tocar autenticação, autorização ou endpoints protegidos;
- validar tenant enforcement quando houver `organizationId` em fluxos sensíveis;
- validar backbone flow quando a mudança afetar o fluxo ponta a ponta do produto.

## Limpeza de banco
- limpar tabelas na ordem correta das dependências;
- remover entidades mais dependentes antes das entidades-raiz;
- evitar falhas por FK no setup ou teardown.

## Relato de validação
Sempre informar:
- quais testes foram executados;
- se passaram ou falharam;
- se houve falha por ambiente, deixar isso explícito;
- não mascarar problema de infraestrutura como sucesso funcional.

## O que deve ser evitado
- testes frágeis por ordem incorreta de limpeza;
- cenários redundantes sem ganho real;
- dependência de ambiente externo sem deixar isso claro;
- declarar fluxo validado sem rodar o IT correspondente.
