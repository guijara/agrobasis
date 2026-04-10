# 🌾 AgroBasis (Ecossistema Néctar)

> Transformando o agronegócio através de dados estruturados, performance e inteligência agronômica.

O **AgroBasis** é um ecossistema SaaS (Software as a Service) focado na gestão agrícola inteligente. Projetado com base nos princípios de *Domain-Driven Design* (DDD) e arquitetura de microsserviços, o sistema oferece desde o controle estrutural e financeiro de fazendas até a fundação para integrações avançadas de predição climática e análise de solo.

O objetivo do projeto é fornecer uma plataforma robusta, escalável e de altíssima performance, resolvendo gargalos clássicos de sistemas agrícolas e entregando *insights* valiosos para a tomada de decisão no campo.

---

## 🏗️ Arquitetura do Sistema

O ecossistema foi desenhado para separar a burocracia administrativa de processamentos pesados de dados. Ele é composto por dois grandes pilares:

* **Core Service (Estrutural):** O coração administrativo da plataforma. Responsável por gerenciar o *Multi-tenancy* (Organizações e Usuários com controle de acesso - RBAC), além de toda a hierarquia espacial e temporal das propriedades rurais (Fazendas, Talhões e Safras).
* **Intelligence Service (Motor Agronômico - *Em Roadmap*):** Módulo focado na ingestão e análise de dados complexos, como integrações com APIs meteorológicas, processamento de índices vegetativos (NDVI via satélite) e telemetria de maquinários (IoT).

---

## 🚀 Destaques de Engenharia e Boas Práticas

Este projeto não foca apenas em entregar funcionalidades, mas na **excelência técnica** de como elas são construídas sob o capô:

* **Test-Driven Development (TDD Outside-In):** Desenvolvimento guiado por testes de integração de ponta a ponta, garantindo que o comportamento da API e as regras de negócio sejam validados antes mesmo da implementação final.
* **Alta Performance e Prevenção de N+1:** Uso estratégico de `@EntityGraph` e `JOIN FETCH` no Spring Data JPA para otimizar consultas relacionais complexas (Lazy Loading), evitando gargalos comuns em bancos de dados.
* **Fail-Fast & Exception Handling:** Tratamento global e padronizado de erros (`@ControllerAdvice`), garantindo respostas HTTP consistentes (padrão RFC) e amigáveis tanto para o Front-end quanto para a experiência do usuário.
* **Database Versioning:** Controle rigoroso de esquema de banco de dados utilizando Flyway, garantindo rastreabilidade, previsibilidade e segurança nas migrações entre diferentes ambientes.

---

## 🛠️ Tecnologias e Ferramentas

**Backend & Frameworks:**
* Java (JDK 17+)
* Spring Boot 3+ (Web, Data JPA, Validation)
* SpringDoc OpenAPI (Swagger) para documentação interativa da API

**Banco de Dados & Infraestrutura:**
* PostgreSQL (Banco de Dados Relacional)
* Flyway (Migrações e versionamento de esquema)
* Docker & Docker Compose (Containerização para ambiente de desenvolvimento isolado)

**Testes & Qualidade:**
* JUnit 5 & Mockito
* Spring Boot Test (Utilizando o moderno `RestClient` para testes de integração)
* AssertJ para asserções fluentes

---

## 📚 Documentação

Adotamos a filosofia de *Docs as Code*. Toda a documentação técnica e de negócio vive junto com o código-fonte:

* **Regras de Negócio e Engenharia:** Detalhes sobre a arquitetura, modelagem de banco de dados (DER), fluxos de permissão e decisões de design podem ser encontrados em nosso [Documento de Arquitetura](./docs/arquitetura.md).
* **Documentação da API (Swagger):** Com a aplicação rodando localmente, acesse `http://localhost:8080/swagger-ui.html` para visualizar, testar e interagir com todos os endpoints disponíveis.

---

## ⚙️ Como Executar o Projeto Localmente

**Pré-requisitos:**
* Docker e Docker Compose instalados.
* Java 17+ e Maven instalados.

**Passo a passo:**

**1. Clone o repositório:**
```bash
git clone [https://github.com/seu-usuario/agrobasis.git](https://github.com/seu-usuario/agrobasis.git)
```

**2. Suba a infraestrutura de banco de dados via Docker:**
```bash
docker-compose up -d
```

**3. Execute as migrações e inicie a aplicação:**
```bash
mvn spring-boot:run
```

**4. Acesse a aplicação:**
A API estará disponível e pronta para receber requisições em `http://localhost:8080`.
