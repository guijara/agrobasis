# Skill: mudanças no backend Java/Spring

## Objetivo
Padronizar alterações no core-service do AgroBasis, mantendo consistência entre domínio, API, persistência, segurança e testes.

## Estrutura esperada
Sempre que aplicável, manter separação entre:
- controller;
- service;
- repository;
- dto;
- entity;
- exception;
- migration;
- tests.

## Convenções gerais
- controllers apenas recebem requisição, validam acesso e delegam ao service;
- services concentram regras de negócio e orquestração;
- repositories não devem carregar regra de negócio;
- exceptions de domínio devem ficar em `domain/exception`;
- DTOs devem representar contrato de entrada e saída, não entidade JPA.

## Regras de implementação
- usar Bean Validation nos DTOs quando a validação pertence à entrada da API;
- reforçar no domínio quando a regra pertence à integridade do modelo;
- reforçar no banco quando a regra é estrutural ou deve ser garantida em persistência;
- aplicar tenant enforcement nos fluxos sensíveis;
- em fluxos por ID, preferir busca por `id + organizationId` quando houver risco de cruzamento entre tenants.

## Retorno de service
Sempre que aplicável:
- retornar DTO de resposta;
- evitar expor entidade diretamente;
- usar a entidade salva (`savedEntity`) quando o retorno depender do estado persistido.

## Segurança
Quando o fluxo exigir:
- validar `@AuthenticationPrincipal`;
- usar `tenantAccessValidator`;
- respeitar as regras já adotadas no `SecurityConfig`.

## O que deve ser evitado
- acoplamento direto entre controller e repository;
- lógica de negócio espalhada em controller;
- retorno de entidade em endpoints públicos;
- abstrações prematuras sem necessidade clara;
- duplicação de lógica entre services.
