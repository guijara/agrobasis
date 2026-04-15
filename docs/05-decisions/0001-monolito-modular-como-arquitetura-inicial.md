# Monólito modular como arquitetura inicial

## Status
- accepted

## Contexto
No início do projeto, o AgroBasis ainda estava em uma fase de consolidação do domínio principal. Havia necessidade de validar primeiro a modelagem central do sistema, os fluxos econômicos básicos e a estrutura de segurança, sem introduzir prematuramente a complexidade operacional de um sistema distribuído.

Ao mesmo tempo, o produto já possuía a expectativa futura de evoluir para uma arquitetura com mais de um serviço, especialmente com a criação de um módulo analítico especializado.

## Decisão
O sistema será estruturado inicialmente como um monólito modular no `core-service`.

Os módulos de domínio do núcleo atual permanecerão organizados de forma explícita dentro desse serviço, com separação clara de responsabilidades, mas sem divisão prematura em múltiplos serviços independentes.

## Justificativa
Essa decisão foi tomada para:

- consolidar primeiro a modelagem central do domínio;
- reduzir complexidade operacional na fase exploratória do projeto;
- facilitar testes, refatorações e evolução incremental;
- validar o fluxo real do sistema antes de distribuir responsabilidades entre serviços;
- preparar o caminho para futura extração de capacidades específicas, como inteligência analítica.

## Consequências

### Positivas
- menor complexidade de desenvolvimento inicial;
- menor custo de manutenção na fase atual;
- evolução mais rápida do núcleo do sistema;
- maior facilidade para validar domínio, segurança e pricing;
- melhor base para adoção progressiva de SDD.

### Negativas ou trade-offs
- nem toda fronteira lógica do sistema está refletida em serviços independentes;
- algumas responsabilidades futuras continuam convivendo no mesmo serviço;
- será necessário revisar essa decisão quando o `intelligence-service` for introduzido.

## Observações
Esta decisão não impede evolução futura para uma arquitetura mais distribuída. Ela apenas define a arquitetura inicial mais adequada ao estágio atual do AgroBasis.
