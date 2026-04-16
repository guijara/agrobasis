# Tarefas — Nome da mudança

## Contexto
Descreva brevemente a mudança à qual estas tarefas pertencem.

## Spec relacionada
- caminho da spec

## Plan relacionado
- caminho do plan

## Regra de execução
Estas tarefas devem ser executadas respeitando a ordem e as dependências descritas neste documento.

Quando uma seção depender de outra, a seção dependente não deve começar antes da conclusão da seção anterior correspondente.

## Dependências entre blocos

- Bloco 2 depende da conclusão do Bloco 1.
- Bloco 3 depende da conclusão do Bloco 2.
- Bloco 4 depende da conclusão do Bloco 2 e, quando aplicável, do Bloco 3.
- Bloco 5 depende da conclusão dos blocos anteriores relevantes.

Ajuste essas dependências conforme a necessidade da mudança.

## Tarefas

### Bloco 1 — Estrutura inicial
**Dependência:** nenhuma

- [ ] tarefa
- [ ] tarefa
- [ ] tarefa

### Bloco 2 — Núcleo da regra
**Dependência:** Bloco 1 concluído

- [ ] tarefa
- [ ] tarefa
- [ ] tarefa

### Bloco 3 — Orquestração e integração
**Dependência:** Bloco 2 concluído

- [ ] tarefa
- [ ] tarefa
- [ ] tarefa

### Bloco 4 — API, segurança e compatibilidade
**Dependência:** Bloco 2 e Bloco 3 concluídos, quando aplicável

- [ ] tarefa
- [ ] tarefa
- [ ] tarefa

### Bloco 5 — Testes e validação final
**Dependência:** todos os blocos anteriores relevantes concluídos

- [ ] criar ou ajustar testes unitários
- [ ] criar ou ajustar testes de integração
- [ ] rodar testes relevantes
- [ ] verificar se o comportamento externo foi preservado
- [ ] atualizar documentação, se necessário

## Observações de execução
Registre aqui cuidados importantes sobre a ordem ou sobre riscos de implementação.

## Critério de encerramento
As tasks podem ser consideradas concluídas quando:
- todas as tarefas aplicáveis estiverem executadas;
- as dependências tiverem sido respeitadas;
- os testes esperados estiverem verdes;
- a mudança estiver coerente com a spec e o plan.
