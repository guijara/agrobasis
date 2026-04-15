# Skill: workflow de commit

## Objetivo
Padronizar quando e como sugerir commits no AgroBasis durante a implementação de mudanças relevantes.

## Princípios
- commits devem refletir blocos coerentes de mudança;
- o agente pode sugerir commits, mas não deve presumir que o commit já foi executado;
- a sugestão de commit deve acontecer preferencialmente após validação adequada;
- a mensagem deve seguir Conventional Commits;
- a mensagem deve ser escrita em português;
- o agente deve explicar a escolha do tipo e do escopo do commit.

## Quando sugerir commit
Sugerir commit quando houver um bloco de trabalho claramente encerrado, como:
- fim de uma fase da adaptação ao SDD;
- conclusão de uma spec;
- conclusão de uma implementação relevante;
- criação de documentação estrutural importante;
- criação de artefatos base do processo.

## Quando não sugerir commit
Não sugerir commit quando:
- a mudança ainda estiver incompleta;
- os testes necessários ainda não tiverem sido executados;
- houver pendências relevantes não explicitadas;
- a alteração ainda for um rascunho instável.

## Estrutura esperada da sugestão
Ao sugerir um commit, sempre informar:

1. a mensagem sugerida;
2. o motivo da palavra-chave inicial;
3. o motivo do escopo escolhido;
4. o que esse commit representa no projeto.

## Convenções
### Tipo
Usar tipos compatíveis com Conventional Commits, como:
- `feat`
- `fix`
- `docs`
- `refactor`
- `test`
- `chore`

### Escopo
O escopo deve refletir a área principal alterada, por exemplo:
- `sdd`
- `arquitetura`
- `pricing`
- `market`
- `cost`
- `identity`

### Idioma
A descrição do commit deve ser em português.

## Regras de qualidade
- a mensagem deve ser curta, clara e específica;
- evitar escopos genéricos demais quando houver escopo melhor;
- evitar sugerir commits que misturem mudanças sem relação;
- evitar dizer que algo está concluído sem validação.

## Exemplo de saída esperada
Mensagem sugerida:
`docs(sdd): criar documentos iniciais da estrutura de especificação`

Explicação:
- `docs` porque a mudança afeta documentação versionada;
- `sdd` porque a mudança pertence à adaptação do projeto ao spec-driven development;
- a descrição indica a criação da base inicial dos artefatos do processo.
