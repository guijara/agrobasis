# Skill: mudanças no backend Java/Spring

## Objetivo
Ajudar a manter coerência nas mudanças do `core-service`.

## Diretrizes
- respeitar a organização modular atual;
- separar controller, service, repository, dto e exception;
- usar validação na entrada quando fizer sentido;
- usar tenant enforcement nos fluxos sensíveis;
- preservar consistência entre domínio, persistência e contrato de API;
- priorizar testes de aplicação e integração úteis.

## Deve evitar
- duplicar lógica entre módulos;
- acoplar controller à persistência;
- introduzir abstrações desnecessárias cedo demais.
