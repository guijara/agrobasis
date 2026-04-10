# 🌾 AgroBasis

> Plataforma em desenvolvimento para transformar dados agrícolas, custos e mercado em informação útil para decisão no agronegócio.

O **AgroBasis** é um sistema voltado à gestão agrícola e análise econômica, construído para organizar a operação da empresa e aproximar dados internos da realidade do mercado.

Hoje, o projeto já une três frentes importantes:

- **estrutura da empresa e da produção**, com organizações, usuários, fazendas e talhões;
- **dados de mercado**, como cotações e câmbio;
- **cálculo econômico**, combinando mercado e custos para gerar pricing ajustado.

A proposta do sistema é crescer com uma base sólida, segura e escalável, começando pelo essencial e evoluindo para integrações, análises mais ricas e inteligência aplicada ao negócio.

---

## 🏗️ Arquitetura

O AgroBasis está sendo desenvolvido como um **monólito modular**, priorizando clareza de domínio, simplicidade de evolução e baixo acoplamento entre responsabilidades.

### Estrutura atual

- **Infraestrutura local**
  - PostgreSQL
  - Redis preparado para usos futuros
  - Docker para ambiente isolado

- **Core Service**
  - núcleo principal do sistema
  - concentra os módulos de domínio e as regras de negócio

- **Intelligence Service** *(em roadmap)*
  - futuro módulo analítico para simulações e cenários

---

## 🧩 Módulos atuais

### `organization`
Define a organização como núcleo do sistema e base do modelo **multi-tenant**.

### `identity`
Cuida de usuários, papéis de acesso e segurança.  
Já possui autenticação com JWT, hash de senha com BCrypt e fluxo de solicitação/aprovação de vínculo com organização.

### `farm`
Modela a estrutura produtiva básica da empresa:
- fazenda
- talhão
- commodity

### `market`
Armazena referências externas de mercado:
- cotações de commodities
- taxas de câmbio

### `cost`
Armazena custos internos de referência por:
- organização
- commodity

### `pricing`
Combina mercado e custo para retornar:
- preço convertido
- custo por tonelada
- preço ajustado
- memória de cálculo

---

## 🔐 Segurança

A segurança do backend já começou a ser consolidada com:

- **Spring Security**
- **JWT com duração de 12 horas**
- **senhas com BCrypt**
- **principal customizado**
- **controle de acesso por role**
- **isolamento inicial por organização**

### Modelo de acesso atual

1. o usuário cria uma conta;
2. a conta nasce sem acesso efetivo a uma organização;
3. o usuário solicita vínculo com uma organização;
4. a organização aprova ou rejeita a solicitação;
5. após aprovação, o usuário passa a operar no tenant correspondente.

---

## 🚀 O que o sistema já faz

A base atual do backend já permite executar um fluxo real como:

1. criar organização;
2. criar usuário;
3. criar fazenda;
4. criar talhão com commodity;
5. registrar cotação;
6. registrar câmbio;
7. registrar custo;
8. calcular o pricing ajustado.

---

## 🛠️ Tecnologias

### Backend
- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- Spring Validation
- SpringDoc OpenAPI / Swagger

### Banco e infraestrutura
- PostgreSQL
- Flyway
- Docker
- Docker Compose
- Redis

### Testes
- JUnit 5
- Mockito
- Spring Boot Test
- AssertJ

---

## 🧪 Testes

O projeto prioriza testes que validem comportamento real do sistema, incluindo:

- testes de aplicação;
- testes de persistência;
- testes de integração;
- testes de segurança e tenant enforcement.

---

## 📚 Documentação

A documentação do projeto está sendo construída junto com a evolução do sistema, registrando:

- decisões de arquitetura;
- modelagem de domínio;
- responsabilidades dos módulos;
- evolução técnica por etapas.

### Swagger

Com a aplicação em execução, a documentação da API pode ser acessada em:

`http://localhost:8080/swagger-ui.html`

---

## ⚙️ Como executar localmente

### Pré-requisitos
- Docker e Docker Compose
- Java 21+
- Maven

### 1. Clone o repositório
~~~bash
git clone https://github.com/seu-usuario/agrobasis.git
cd agrobasis
~~~

### 2. Suba a infraestrutura
~~~bash
docker compose up -d
~~~

### 3. Execute a aplicação
~~~bash
./mvnw spring-boot:run
~~~

### 4. Acesse a API
`http://localhost:8080`

---

## 🗺️ Próximos passos

Os próximos passos do projeto estão focados em:

- aprofundar segurança e acesso;
- integrar APIs externas de mercado;
- evoluir o pricing;
- amadurecer o domínio de custos;
- preparar a base para inteligência analítica.

---

## 🌱 Status

O AgroBasis está em desenvolvimento ativo e já possui uma base funcional consistente de domínio, segurança e cálculo econômico.

O foco atual é evoluir essa base com estabilidade, clareza arquitetural e valor real para o negócio.
