# Skill: testes de integração

## Objetivo
Ajudar a manter os testes de integração úteis, previsíveis e coerentes com o sistema.

## Diretrizes
- limpar o banco na ordem correta das dependências;
- validar fluxos reais quando isso agregar valor;
- usar testes HTTP para segurança quando necessário;
- validar tenant enforcement nos fluxos sensíveis;
- preferir testes que exercitem comportamento de ponta a ponta relevante.

## Deve evitar
- testes frágeis por limpeza incorreta;
- duplicação excessiva de cenários triviais;
- dependência desnecessária de ambiente externo não controlado.
