# Visão geral do sistema

## Estrutura atual

O AgroBasis está sendo desenvolvido, neste estágio, com base em uma arquitetura centrada em um núcleo de domínio executável e incremental.

O sistema foi inicialmente estruturado como um monólito modular no `core-service`, permitindo consolidar primeiro a modelagem central, a segurança, os cálculos econômicos e os fluxos principais do produto antes da abertura de novas fronteiras arquiteturais.

## Componentes principais

### `core-service`
É o núcleo funcional atual do sistema.

Ele concentra:
- organização;
- identidade;
- estrutura produtiva;
- mercado;
- custos;
- pricing;
- segurança;
- integração externa de mercado;
- análise atual do pricing.

### `docs`
Concentra a documentação viva do projeto em formato orientado por especificação.

### `.ai`
Concentra recursos de suporte ao uso de agentes, como skills e instruções especializadas.

## Módulos atuais do core-service

- `organization`
- `identity`
- `farm`
- `market`
- `cost`
- `pricing`
- `shared`

## Direção futura

A evolução futura do sistema prevê a criação de um `intelligence-service`, responsável pela camada especializada de simulação, cenários e análise probabilística.

Esse serviço não faz parte do núcleo atual do sistema e deve ser introduzido apenas depois da adaptação do projeto ao processo de desenvolvimento orientado por especificação.

## Princípios atuais

A evolução arquitetural do AgroBasis segue estes princípios:

- crescimento incremental;
- clareza de fronteiras de domínio;
- priorização do fluxo real antes da complexidade distribuída;
- rastreabilidade de cálculo;
- isolamento organizacional;
- documentação viva;
- preparação progressiva para expansão futura.
