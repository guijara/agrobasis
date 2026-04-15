# Decision Records

Esta pasta contém os registros de decisões arquiteturais, estruturais e de domínio do AgroBasis.

Esses registros existem para preservar o contexto das escolhas mais importantes do projeto, evitando que decisões relevantes se percam ao longo da evolução do sistema.

## Objetivo

Nem toda decisão importante fica óbvia apenas observando o código final.

Muitas escolhas do projeto dependem de:
- restrições do contexto;
- simplificações conscientes;
- decisões incrementais;
- limitações assumidas;
- e trade-offs de arquitetura ou domínio.

O papel desta pasta é registrar essas decisões de forma clara.

## Quando registrar uma decisão

Uma decisão deve ser registrada quando ela:
- altera a direção arquitetural do projeto;
- define uma convenção estrutural importante;
- resolve uma dúvida de modelagem de domínio;
- estabelece um limite explícito de escopo;
- ou representa um trade-off relevante para a evolução futura.

## Exemplos esperados no AgroBasis

Entre as decisões que fazem sentido nesta pasta estão:
- adoção inicial de monólito modular;
- organization como fronteira multitenant;
- commodity como enum;
- fluxo de aprovação de vínculo organizacional;
- sincronização manual de mercado;
- freight por organização + fazenda + commodity;
- commercial adjustment por organização + fazenda + commodity.

## Estrutura esperada de um decision record

Cada registro deve deixar claro:
- contexto;
- decisão tomada;
- justificativa;
- consequências.

## Função no processo

Os decision records ajudam a:
- reduzir retrabalho;
- preservar o raciocínio arquitetural;
- facilitar onboarding;
- melhorar a consistência das próximas specs e plans.
