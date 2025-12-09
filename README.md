# 🛒Api E-Commerce🛒


[![Build](https://License

> <Descrição breve: Api E-commerce, responsavel por gerenciar catalogo de produtos e controle de estoque. Para empressas de comercio digital/fisico.>  
> Ex.: API REST para gerenciamento de catalogo de produtos,com authenticação de usuarios, papeis definidos `Cliente e administrador` com CRUD completo, validação e documentação via OpenAPI.
---
## 📚 Sumário
- Arquitetura
- Stack de Tecnologias
- Configuração
- Execução
- Documentação da API
- Exemplos de uso curl/jq
- CI/CD
- Deploy Docker/k8s
- Contribuição
- Licença
---

## 🏛️ Arquitetura
- **Padrão**: MVC + DTOs + Services + Repository
- **Camadas**:
  - `controller`: expõe endpoints REST
  - `service`: regras de negócio
  - `repository`: acesso a dados
  - `dto`: transferência de dados `request/response`
  - `domain/model`: entidades
  - `config`: configuração global `Jpa, Swagger, Jackson, etc.`
- **Principais decisões**:
  - Validações com `javax.validation` `@NotNull`, `@Email`, etc.
  - Tratamento de erros com `@ControllerAdvice`
  - Mapeamento via `MapStruct` opcional ou conversões manuais
  - Paginação e ordenação via `Pageable` Spring Data
  
> Diagrama (opcional): `[architecture](docs/architecture.png)`

---
## 💻 Stack de tecnologia
- **Language:** Java 21
- **Framework** Spring Boot 3.x (Web, Data, Jpa, Security, Validation)
- **Auth:** JWT (stateless)
- **DB:** PostgreSQL 16 (via Docker)
- **ORM:** Hibernate (JPA)
- **Mapping:** MapStruct 1.5+
- **Docs:** springdoc-openapi (Swagger UI)
- **Tests:** JUnit 5, Mockito, TestContainers 1.20+
- **Migrações:** Flyway
- **Cache (optiona):** Redis 7
- **Build:** gradle
- **Packaging:** Docker Compose

## 🚀 Execução

### Build
- `./gradlew clean build`

### Run
- `./gradlew bootRun`

### Empacotamento 
- `java -jar target/<artifact>-<version>.jar`

### Docker-Compose
- `docker compose up -d`
--- 
## 📖 Documentação da API

- `Swagger UI: http://localhost:8080/swagger-ui.html`
- `OpenAPI JSON: http://localhost:8080/v3/api-docs`
---
## 🧪 Testes

### Gradle
- `./gradlew test`
- `./gradlew jacocoTestReport`
---
## 🧹 Qualidade (Lint/Format)

# Gradle
- `./gradlew spotlessApply`
- `./gradlew checkstyleMain`
---
## Contrato de API 
### Auth
- `POST /api/auth/register` — register user (ADMIN controlled or public depending on env).  
- `POST /api/auth/login` — returns JWT `{ token }`.

### Categories
- `GET /api/categories` — public list (paginated).  
- `GET /api/categories/{id}` — public.  
- `POST /api/admin/categories` — ADMIN create.  
- `PUT /api/admin/categories/{id}` — ADMIN update.  
- `DELETE /api/admin/categories/{id}` — ADMIN delete.

### Products
- `GET /api/products` — public list with `page`, `size`, `sort`, filters (`categoryId`, `priceMin`, `priceMax`, `active`).  
- `GET /api/products/{id}` — public.  
- `POST /api/admin/products` — ADMIN create (validated DTO).  
- `PUT /api/admin/products/{id}` — ADMIN update.  
- `DELETE /api/admin/products/{id}` — ADMIN delete.

### Cart
- `GET /api/cart` — current user's cart.  
- `POST /api/cart/items` — add item (`productId`, `quantity`).  
- `PUT /api/cart/items/{itemId}` — update quantity.  
- `DELETE /api/cart/items/{itemId}` — remove item.  
- (All require JWT of CUSTOMER)

### Orders
- `POST /api/orders/checkout` — create order from cart (validates stock).  
- `POST /api/orders/{orderId}/pay` — mock payment (idempotent).  
- `GET /api/orders` — list current user's orders (paginated).  
- `GET /api/admin/orders` — ADMIN list all orders (paginated).

**Error Response Schema:**
```json
{
  "status": 400,
  "code": "BAD_REQUEST",
  "message": "Quantity must be greater than zero",
  "traceId": "req-12345"
}
```

---

## Maquina de Estados — Status dos pedidos
Allowed transitions:  
- `CREATED` → `PAID`  
- `CREATED` → `CANCELLED`  
- `PAID` → `SHIPPED`  
- `PAID` → `CANCELLED` (somente via reembolso para versoes futuras)  

Transições invalidas devem retornar `409 Conflict`.
---
🤝 Contribuição

Crie uma branch (feature/<tema> ou fix/<tema>)
Abra um PR com descrição clara
Padrão de commits: Conventional Commits

feat:
fix:
docs:
refactor:
test:
chore:


Rodar lint/test antes do push
PR deve ter pelo menos 1 aprovação
