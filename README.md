# 🛒Api E-Commerce🛒


[![Build](https://License)

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
- Para execução do projeto, api + banco de dados **Postgres**. Necessário possuir o docker + docker-compose para inicialização completa do ambiente.

### Docker-Compose
- `docker compose up -d --build app postgres`
--- 
## 📖 Documentação da API

- `Swagger UI: http://localhost:8080/swagger-ui.html`
- `OpenAPI JSON: http://localhost:8080/v3/api-docs`
---
## 🧪 Testes

### Gradle

#### Preparação para os testes.
- Para rodar os testes, devemos primeiro inicialiizar nosso container do **postgres** de testes. Com o seguinte commando:
``` shell
docke compose --profile test up -d
```
Em seguida podemos realizar os testes

- `./gradlew test`
- `./gradlew jacocoTestReport`
---
---

## 💻 Exemplos de Uso (cURL)

Aqui estão os comandos para testar os fluxos principais via terminal. 
*Nota: Substitua `<TOKEN>` pelo JWT retornado no login.*

### Criar Usuario.
```bash
# Adiciona um admin
curl -X POST http://localhost:8080/api/users/register \
     -H "Content-Type: application/json" \
     -d '{
       "email": "admin@db.com",
       "password": "123456789",
       "role": "ADMIN"
     }'

# Adiciona um CLIENTE
curl -X POST http://localhost:8080/api/users/register \
     -H "Content-Type: application/json" \
     -d '{
       "email": "cliente@db.com",
       "password": "123456789",
       "role": "CLIENTE"
     }'

```
### 1. Autenticação
```bash
# Login para obter o Token
curl -X POST http://localhost:8080/api/users/login \
     -H "Content-Type: application/json" \
     -d '{"email": "cliente@db.com", "password": "123456789"}

```
### 2. Adicionar Produto ao carrinho
```bash
# 2. Adicionar produto ao carrinho
curl -X POST http://localhost:8080/api/cart/items \
     -H "Authorization: Bearer <TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{"productId": 1, "quantity": 2}'
```
### 2.1 Listar meu carrinho atual
```bash
# Listar meu carrinho atual
curl -X GET http://localhost:8080/api/cart \
     -H "Authorization: Bearer <TOKEN>"
```

### 3. Fazer Checkout
```bash
# Realizar Checkout (Cria o pedido a partir do carrinho)
curl -X POST http://localhost:8080/api/orders/checkout \
     -H "Authorization: Bearer <TOKEN>"
```

### Adicionar produtos e categorias (ADMIN)
- Fazer login como admin.
```bash
# Login para obter o Token
curl -X POST http://localhost:8080/api/users/login \
     -H "Content-Type: application/json" \
     -d '{"email": "admin@db.com", "password": "123456789"}'
```
- Chamar endpoint para criar a categoria.

```bash
curl -X POST http://localhost:8080/api/categories/admin \
     -H "Authorization: Bearer <TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{"name": "Categoria 1","description":"Categoria generica"}'
```
- Chamar endpoint para criar o produto.

```bash
curl -X POST http://localhost:8080/api/admin/products \
     -H "Authorization: Bearer <TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{"name":, "Produto 1","priceCents": 40000, "stockQuantity":4,"categoryId":1}'
```


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
- `GET /api/orders` — list current user's orders (paginated).  
- `GET /api/admin/orders` — ADMIN list all orders (paginated).

**Error Response Schema:**
```json
{
  "status": 400,
  "code": "BAD_REQUEST",
  "message": "Quantity must be greater than zero",
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

## TODO

- [] `POST /api/orders/{orderId}/pay`   
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
