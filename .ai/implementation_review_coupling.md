# Skill: revisão de acoplamento

## Objetivo
Avaliar se uma funcionalidade nova ou alterada introduziu acoplamento excessivo, indevido ou prematuro no AgroBasis.

## Quando aplicar
Aplicar quando houver:
- nova funcionalidade;
- novo módulo;
- nova integração;
- refatoração estrutural;
- expansão de domínio;
- criação de service compartilhado;
- dependência entre módulos.

## Pontos de revisão
Verificar se a mudança:

1. respeita a fronteira do módulo;
2. evita dependência desnecessária entre contextos distintos;
3. não move regra de domínio para camada errada;
4. não faz controller conhecer detalhes internos demais;
5. não faz pricing conhecer integração externa;
6. não faz módulo interno depender de detalhe de apresentação;
7. não cria abstração cedo demais sem necessidade clara.

## Sinais de acoplamento ruim
- controller acessando repository diretamente;
- service de um módulo operando regra principal de outro módulo;
- entity sendo usada como contrato público;
- módulo de cálculo conhecendo client externo;
- mesma regra replicada em múltiplos lugares;
- dependência circular implícita entre módulos.

## Saída esperada da revisão
A revisão deve informar:
- se o acoplamento atual está aceitável ou não;
- onde estão os pontos sensíveis;
- se há duplicação ou dependência indevida;
- qual ajuste simples melhoraria a separação de responsabilidades.

## O que deve ser evitado
- criticar abstrações úteis só por existirem;
- propor desacoplamento artificial sem ganho real;
- ignorar o estágio atual do projeto ao julgar a solução.
