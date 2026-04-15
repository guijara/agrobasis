# Visão geral dos módulos do core-service

O `core-service` é o núcleo funcional atual do AgroBasis.

Ele concentra os principais fluxos de domínio, segurança, integração de mercado, custos e cálculo econômico do sistema.

## Módulos atuais

### `organization`
Responsável pela entidade organizacional central do sistema e pela fronteira lógica de tenant.

### `identity`
Responsável por usuários, papéis, autenticação, vínculo organizacional e regras de acesso.

### `farm`
Responsável pela estrutura produtiva básica da organização, incluindo fazendas, talhões e commodity principal.

### `market`
Responsável pelo armazenamento e sincronização de dados externos de referência econômica, como cotação e câmbio.

### `cost`
Responsável pelos perfis internos de custo, frete e ajuste comercial da organização.

### `pricing`
Responsável por transformar dados externos e internos em informação econômica útil, rastreável e explicável.

### `shared`
Responsável por elementos transversais do sistema, como tratamento global de erros, documentação de API, segurança auxiliar e componentes reutilizáveis.

## Objetivo desta documentação local

Os arquivos desta pasta existem para registrar o papel atual de cada módulo, seus limites e suas responsabilidades principais, sem substituir a documentação macro da raiz do projeto.
