# Skill: workflow de implementação

## Objetivo
Padronizar a forma como mudanças relevantes devem ser implementadas, validadas e reportadas no projeto AgroBasis.

## Quando aplicar
Aplicar esta skill sempre que a mudança envolver:
- nova funcionalidade;
- alteração relevante de domínio;
- mudança de contrato de API;
- alteração de cálculo;
- integração externa;
- segurança;
- persistência;
- testes de integração;
- documentação relevante.

## Regras de execução
1. Antes de implementar, identificar claramente:
   - o objetivo da mudança;
   - o escopo;
   - os módulos afetados;
   - os testes esperados.

2. Durante a implementação:
   - manter coerência com a arquitetura atual;
   - reduzir ambiguidade técnica;
   - evitar duplicação de lógica;
   - preservar tenant enforcement e rastreabilidade quando aplicável.

3. Após implementar algo relevante:
   - resumir claramente o que foi feito;
   - citar os principais arquivos criados ou alterados;
   - informar correções ou ajustes feitos durante a implementação;
   - explicitar ressalvas, limitações ou pendências, se existirem.

## Validação obrigatória
Após mudanças relevantes, executar os testes apropriados ao escopo alterado.

Sempre que possível:
- rodar testes unitários do módulo afetado;
- rodar testes de integração do fluxo afetado;
- validar o backbone flow quando a mudança tocar comportamento de ponta a ponta.

## Relato obrigatório
Ao final da implementação, sempre informar:
- o que foi implementado;
- o que foi corrigido ou ajustado;
- quais testes foram executados;
- o resultado dos testes;
- o que ainda não foi validado, se houver algo.

## O que deve ser evitado
- declarar algo como concluído sem validação adequada;
- omitir falhas ou ressalvas;
- alterar comportamento sensível sem atualizar testes;
- modificar regras de domínio sem deixar isso explícito no resumo final.
