# Plano Linear de Desenvolvimento — v1 (Markdown)

> Estrutura bottom-up, subindo camadas da arquitetura. Para cada tarefa: implementação seguida **imediatamente** pelos testes (unitário/integrado/e2e quando aplicável).

## 0) Fundação do Projeto

### 0.1 — Projeto & Build (Gradle + Spring Boot 3) — 4h
**Implementação**
- Criar app base e profiles (`dev`, `test`).
- Adicionar dependências: Web, Data JPA, Security, Validation, springdoc-openapi, MapStruct, Lombok, JUnit/Mockito, Testcontainers.

**Teste (Smoke)**
- `./gradlew clean test` passa.
- App inicia em `dev` (porta padrão) — verificação básica.

---

### 0.2 — Estrutura de pastas & guidelines — 3h
**Implementação**
- Pacotes: `domain`, `repository`, `service`, `web/controller`, `web/dto`, `mapper`, `config`, `exception`.
- Definir DoD e checklist de PR.
DOD -> [Build ok, Cobertura Ok, Teste passando, funcionalidade Ok]

**Teste**
- Build verde e checagem estática (lint/format).

---

## 1) Infra & Configuração

### 1.1 — Docker Compose (Postgres) — 4h
**Implementação**
- `docker-compose.yml` com Postgres 16.
- Configurar `application.yml`.

**Teste (Integração Infra)**
- Subir DB e validar conexão.

---

### 1.2 — Flyway baseline & migrações iniciais — 6h
**Implementação**
- Migrações para `users`, `categories`, `products` (índices e constraints).

**Teste (Integração + Testcontainers)**
- TC aplica migrações e valida unicidade/constraints.

---

## 2) Modelagem de Domínio (Entities + Repository)

### 2.1 — Category — 4h
**Implementação**: `Category(id, name unique not null, description)`

**Teste (Integração Repository)**
- CRUD e verificação de `unique name`.

---

### 2.2 — Product — 6h
**Implementação**: `Product(id, name, description, sku unique, price>0, stock≥0, active, category_id)`

**Teste (Integração Repository)**
- Validações de preço/estoque, índice `sku`, relação com Category.

---

### 2.3 — User — 4h
**Implementação**: `User(id, email unique, passwordHash, role enum{ADMIN, CLIENTE}, enabled, createdAt)`

**Teste (Integração Repository)**
- Unicidade de email, persistência de `role`, `enabled`.

---

### 2.4 — Cart/CartItem — 6h
**Implementação**: `Cart(id, user_id unique 1:1)` e `CartItem(id, cart_id, product_id, quantity≥1, unitPriceSnapshot)`

**Teste (Integração Repository)**
- Criar carrinho e itens, validar `quantity≥1` e relação com Product.

---

### 2.5 — Order/OrderItem — 6h
**Implementação**: `Order(id, user_id, status enum, total, timestamps)` / `OrderItem(order_id, product_id, quantity, unitPrice)`

**Teste (Integração Repository)**
- Criar pedido com itens e índices `order.user_id`.

---

## 3) DTOs & MapStruct

### 3.1 — Category DTO/Mapper — 3h
**Implementação**: requests/responses + mapper.

**Teste (Unitário Mapper)**
- Conversão fiel e campos obrigatórios.

---

### 3.2 — Product DTO/Mapper — 4h
**Implementação**
- DTOs e mapper.

**Teste (Unitário Mapper)**
- Mapeamento de `price`, `stock`, `active`, `categoryId`.

---

### 3.3 — User/Auth DTO/Mapper — 3h
**Implementação**
- DTOs de registro/login; mapper cuidado com `password`.

**Teste (Unitário Mapper)**
- Email/role mapeados corretamente.

---

### 3.4 — Cart & Order DTO/Mapper — 4h
**Implementação**
- DTOs para carrinho/pedido, itens e totais.

**Teste (Unitário Mapper)**
- Itens, totais, snapshot de preço.

---

## 4) Serviços (Regras) — Teste imediato após cada Service

### 4.1 — CategoryService — 4h
**Implementação**: CRUD + validações.

**Teste (Unitário Service)**: `name` não vazio, 400/404.

---

### 4.2 — ProductService — 6h
**Implementação**: CRUD, filtros/paginação; `price>0`, `stock≥0`, `sku` único.

**Teste (Unitário Service)**: casos válidos/ inválidos, ordenação/filtros.

---

### 4.3 — AuthService — 6h
**Implementação**: registro (BCrypt), login (JWT com `sub`, `role`, `exp`).

**Teste (Unitário Service)**: email único, senha complexa, token emitido.
**Teste (Integração Security)**: 200/401.

---

### 4.4 — CartService — 8h
**Implementação**: obter carrinho, adicionar (merge), atualizar/remover, totais, snapshot.

**Teste (Unitário Service)**: `quantity≥1`, merge correto, snapshot fixo.

---

### 4.5 — OrderService (checkout) — 12h
**Implementação**: valida estoque; transação ACID; decrementa estoque; pedido `CREATED`; total Σ; limpar carrinho; `409` em conflito.

**Teste (Unitário Service)**: sucesso/falha estoque; atomicidade (mocks).
**Teste (Integração Service+DB)**: com TC, decremento e carrinho limpo.

---

### 4.6 — PaymentService (mock) — 6h
**Implementação**: idempotente `CREATED→PAID`; inválidas `409`.

**Teste (Unitário Service)**: idempotência e transições.

---

### 4.7 — OrderQueryService — 4h
**Implementação**: paginação para cliente/ADMIN.

**Teste (Unitário Service)**: filtros e paginação.

---

## 5) API REST (Controllers) — Integração imediata

### 5.1 — Categories (público) — 4h
**Implementação**: `GET /api/categories`, `GET /api/categories/{id}`.

**Teste (Integração MockMvc+TC)**: paginação/200/404.

---

### 5.2 — Admin Categories — 4h
**Implementação**: `POST/PUT/DELETE /api/admin/categories/{id}`.

**Teste (Integração)**: 201/200/204, 400/404, autorização.

---

### 5.3 — Products (público) — 6h
**Implementação**: `GET /api/products` (filtros/ordenar/paginar), `GET /api/products/{id}`.

**Teste (Integração)**: filtros/ordenar/paginação, 200/404.

---

### 5.4 — Admin Products — 6h
**Implementação**: `POST/PUT/DELETE /api/admin/products`.

**Teste (Integração)**: validações preço/estoque/sku; autorização.

---

### 5.5 — Auth — 5h
**Implementação**: `POST /api/auth/register`, `POST /api/auth/login`.

**Teste (Integração)**: 201/400 e 200/401; token presente.

---

### 5.6 — Cart — 6h
**Implementação**: `GET /api/cart`, `POST /api/cart/items`, `PUT/DELETE /api/cart/items/{itemId}`.

**Teste (Integração)**: adicionar/atualizar/remover; totais; 400 para quantidade inválida.

---

### 5.7 — Orders — 8h
**Implementação**: `POST /api/orders/checkout`, `POST /api/orders/{orderId}/pay`, `GET /api/orders`, `GET /api/admin/orders`.

**Teste (Integração)**: checkout sucesso/`409`; `pay` idempotente; paginação.

---

## 6) Segurança & Autorização

### 6.1 — Config Security (stateless JWT) — 6h
**Implementação**: filtros, `PasswordEncoder(BCrypt)`, `@EnableMethodSecurity`, `@PreAuthorize`.

**Teste (Integração Security)**: negar sem token; permitir com role correta; logs sem segredos.

---

## 7) Erros & Observabilidade

### 7.1 — `@ControllerAdvice` — 4h
**Implementação**: mapear `404/400/409/500` com `{status, code, message, traceId}`.

**Teste (Integração)**: forçar erros e validar schema.

---

### 7.2 — Logs estruturados + Actuator — 4h
**Implementação**: log JSON com `requestId/userId`; `/actuator/health`.

**Teste (Integração)**: `GET /actuator/health` 200; verificação básica de formato de log.

---
ea
## 8) Documentação

### 8.1 — OpenAPI/Swagger + Segurança — 4h
**Implementação**: springdoc com Bearer.

**Teste (Integração)**: `GET /v3/api-docs` contém endpoints e security scheme.

---

### 8.2 — README + scripts `curl` — 3h
**Implementação**: fluxo completo com `jq` (register → login → admin cria produto → cliente carrinho → checkout → pay).

**Teste (E2E guiado)**: executar scripts e validar respostas esperadas.

---

## 9) Testes E2E (scripts)

### 9.1 — Script `e2e.sh` — 6h
**Implementação**: shell com `curl`+`jq`, valida códigos HTTP e payloads.

**Teste (E2E)**: rodar local/CI; saída de sucesso/falha.

---

## 10) Performance & Hardening

### 10.1 — Paginação default & índices — 3h
**Implementação**: `size` padrão (ex.: 100), índices `sku`, `category_id`, `order.user_id`.

**Teste (Integração)**: sem `size` usa default; consultas ágeis em dataset sintético.

---

## 11) Empacotamento & Deploy Local

### 11.1 — Docker image da app — 3h
**Implementação**: `Dockerfile`; variáveis por ambiente; compose com app+db.

**Teste (Smoke Container)**: subir compose; `health` ok; fluxos básicos passam.

---

## Linha do tempo (sequencial)
- Semana 1: 0.1–0.2, 1.1–1.2, 2.1–2.3
- Semana 2: 2.4–2.5, 3.1–3.4
- Semana 3: 4.1–4.4
- Semana 4: 4.5–4.7
- Semana 5: 5.1–5.7
- Semana 6: 6.1, 7.1–7.2, 8.1–8.2
- Semana 7: 9.1, 10.1, 11.1 → Release v1

---

## Checklist de PR
- Código na camada correta.
- DTOs/Mapper atualizados.
- **Teste unitário** (Service/Mapper) cobrindo regras/validações.
- **Teste de integração** (MockMvc + Testcontainers) cobrindo endpoints/DB.
- Swagger/README atualizados.
- Logs/erros padronizados.
- Scripts `curl` ajustados quando aplicável.
