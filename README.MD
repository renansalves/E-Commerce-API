# E‑Commerce Backend — Software Requirements Specification (SRS)

**Project Name:** E‑Commerce API

**Document Version:** 1.0  
**Date:** 2025-12-08  
**Owner:** Backend Team (Lead: Renan da Silveira Alves)

---

## 1. Executive Summary
Build a production‑grade **E‑Commerce backend** exposing RESTful APIs for product catalog, authentication/authorization, shopping cart, checkout & orders, and admin operations. The project is designed to **validate full‑stack backend proficiency** with Spring Boot (controllers, services, repositories), **JPA/Hibernate**, **DTO+Mapper**, **global error handling**, **Swagger/OpenAPI**, and **comprehensive testing** (unit + integration + e2e). Scope includes a **mock payment flow**, **stock management**, **pagination/sorting/filtering**, and **role‑based access**.

---

## 2. Objectives
- Deliver a **clean, well‑documented** backend demonstrating professional architecture and practices.
- Ensure **functional completeness** for core commerce flows (browse → cart → checkout → pay).
- Provide **non‑functional qualities**: security, performance, reliability, observability, and maintainability.
- Establish a platform that can evolve to **microservices** and **event‑driven** patterns later.

---

## 3. Scope
### 3.1 In‑Scope
- Product catalog: products, categories, inventory/stock, pricing, activation.
- Users: registration, login, roles (ADMIN, CUSTOMER), JWT authentication.
- Shopping cart: add/remove/update items, quantity checks, price snapshots.
- Checkout & orders: order creation, totals, status transitions (CREATED → PAID → SHIPPED/CANCELLED), mock payment.
- Admin operations: manage products/categories, view orders.
- Public browse endpoints: pagination, sorting, filtering.
- API documentation: Swagger/OpenAPI with security scheme.
- Error handling: centralized `@ControllerAdvice` with standardized error responses.
- Testing: unit, integration (Testcontainers), e2e flows.
- Deployment: local Docker Compose (Postgres; optional Redis).

### 3.2 Out‑of‑Scope (for v1)
- Real payment gateway integrations (Stripe/PayPal).
- Shipping integrations, taxes per region, invoicing compliance.
- Advanced search (Elasticsearch), recommendations, multi‑currency.
- Frontend UI (out‑of‑scope; consumers will be CLI/cURL or Swagger).
- Internationalization/localization of content.

---

## 4. Product Vision & Business Context
Provide a **reliable backend** enabling:  
1) **Customers** to discover products, manage a cart, and place orders;  
2) **Admins** to maintain catalog and oversee orders.

**Key Business Rules:**
- Product `price` is in a single currency (v1).  
- `stock` is authoritative; checkout must fail if insufficient.  
- Order total = Σ(`unitPrice` × `quantity`).  
- `unitPriceSnapshot` stored in cart/order to avoid retroactive price changes.  
- Order statuses change through valid transitions only (see §13 State Machine).

**Success Metrics:**
- API response < 300 ms p95 for catalog endpoints with 1k products.
- 95% unit test coverage for service layer; end‑to‑end flow test passing.
- Zero critical security findings in CI lints/scans.

---

## 5. Personas & Roles
- **Customer:** browses catalog, manages cart, checks out, pays.  
- **Admin:** manages categories/products, inspects orders.

**Roles:** `ADMIN`, `CUSTOMER`.  
**Authorization:**  
- Public GET: products/categories listing.  
- Admin‑only: create/update/delete products/categories.  
- Authenticated customer: cart & orders.

---

## 6. Architectural Paradigm & Style
- **Primary Paradigm:** **Layered MVC** within a **Clean/Hexagonal boundary mindset**.  
  - **Layers:** Controller (REST) → Service (domain rules) → Repository (JPA).  
  - **DTO + Mapper (MapStruct)** between API and domain entities.  
  - **Global error handling** via `@ControllerAdvice`.  
- **Open for evolution:** Ports/Adapters (Hexagonal) can be introduced for external systems (payments, messaging).  
- **Domain Modeling:** DDD tactical patterns (Entities, Value Objects where useful; Aggregates for Order & Cart).  
- **Transactions:** Service methods annotated with `@Transactional` for consistency.

**Why MVC now?** Clear for a single service, speeds delivery, matches Spring Boot idioms. **Hexagonal compatibility** keeps future microservices/event‑driven split feasible without rewrite.

---

## 7. Technology Stack & Versions
- **Language:** Java 21  
- **Framework:** Spring Boot 3.x (Web, Data JPA, Security, Validation)  
- **Auth:** JWT (stateless)  
- **DB:** PostgreSQL 16 (via Docker)  
- **ORM:** Hibernate (JPA)  
- **Mapping:** MapStruct 1.5+  
- **Docs:** springdoc‑openapi (Swagger UI)  
- **Tests:** JUnit 5, Mockito, Testcontainers 1.20+  
- **Migrations:** Flyway  
- **Cache (optional):** Redis 7  
- **Build:** Maven  
- **Packaging:** Docker Compose for local infra

---

## 8. Non‑Functional Requirements (NFRs)
- **Security:**
  - JWT bearer auth; strong password hashing (BCrypt).  
  - Role‑based access controls at endpoint level; input validation everywhere.  
  - No sensitive data in logs; rate limiting optional.
- **Reliability & Consistency:** ACID transactions for checkout & stock decrement; optimistic locking optional for concurrent updates.
- **Performance:** p95 < 300 ms for read endpoints with typical dataset; pagination mandatory for lists.
- **Scalability:** Stateless app; externalizing state in DB/Redis. Horizontal scaling feasible.
- **Observability:** Structured logs (request ID), basic metrics; health checks (`/actuator/health`).
- **Maintainability:** Layered architecture, DTO boundaries, unit tests ≥ 80% service coverage.
- **Portability:** Dockerized; environment config via properties.

---

## 9. Data Model (Entities & Constraints)
- **Category**: `id`, `name` (unique, not null), `description`.  
- **Product**: `id`, `name` (not null), `sku` (unique), `price` (decimal, >0), `stock` (int, ≥0), `active` (bool), `category_id`, timestamps.  
- **User**: `id`, `email` (unique), `passwordHash`, `role` (enum), `enabled`, `createdAt`.  
- **Cart**: `id`, `user_id` (unique 1:1), items[].  
- **CartItem**: `id`, `cart_id`, `product_id`, `quantity` (≥1), `unitPriceSnapshot`.  
- **Order**: `id`, `user_id`, `status` (enum), items[], `total`, timestamps.  
- **OrderItem**: `id`, `order_id`, `product_id`, `quantity`, `unitPrice`.

**Indexes:** `product.sku`, `product.category_id`, `order.user_id`.

---

## 10. API Contract (High‑Level)
### 10.1 Auth
- `POST /api/auth/register` — register user (ADMIN controlled or public depending on env).  
- `POST /api/auth/login` — returns JWT `{ token }`.

### 10.2 Categories
- `GET /api/categories` — public list (paginated).  
- `GET /api/categories/{id}` — public.  
- `POST /api/admin/categories` — ADMIN create.  
- `PUT /api/admin/categories/{id}` — ADMIN update.  
- `DELETE /api/admin/categories/{id}` — ADMIN delete.

### 10.3 Products
- `GET /api/products` — public list with `page`, `size`, `sort`, filters (`categoryId`, `priceMin`, `priceMax`, `active`).  
- `GET /api/products/{id}` — public.  
- `POST /api/admin/products` — ADMIN create (validated DTO).  
- `PUT /api/admin/products/{id}` — ADMIN update.  
- `DELETE /api/admin/products/{id}` — ADMIN delete.

### 10.4 Cart
- `GET /api/cart` — current user's cart.  
- `POST /api/cart/items` — add item (`productId`, `quantity`).  
- `PUT /api/cart/items/{itemId}` — update quantity.  
- `DELETE /api/cart/items/{itemId}` — remove item.  
- (All require JWT of CUSTOMER)

### 10.5 Orders
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

## 11. Validation Rules
- Product: `name` not blank; `price` > 0; `stock` ≥ 0; `categoryId` must exist.  
- Cart: `quantity` ≥ 1; merging items sums quantities; unit price snapshot set at add time.  
- Checkout: fail if any item `quantity` > available stock; all changes transactional.

---

## 12. Error Handling & Exceptions
Centralized `@ControllerAdvice` maps domain exceptions to HTTP:
- `NotFoundException` → 404  
- `BadRequestException` / validation → 400  
- `ConflictException` (e.g., stock conflict) → 409  
- Generic → 500

Include `traceId` in logs and in error payload where possible.

---

## 13. State Machine — Order Status
Allowed transitions:  
- `CREATED` → `PAID`  
- `CREATED` → `CANCELLED`  
- `PAID` → `SHIPPED`  
- `PAID` → `CANCELLED` (only via refund in future versions)  

Invalid transitions must return `409 Conflict`.

---

## 14. Security Model
- **Authentication:** JWT bearer; token contains `sub` (email), `role`, `exp`.  
- **Authorization:** endpoint annotations (`@PreAuthorize`).  
- **Password Storage:** BCrypt; minimum length & complexity enforced.  
- **Data Protection:** no passwords or tokens in logs; HTTPS assumed in production.

---

## 15. Performance & Scalability
- Pagination for all list endpoints; max page size default 100.  
- Caching (optional) for product reads; invalidate on write.  
- Use efficient queries; avoid N+1 via fetch strategies when appropriate.

---

## 16. Observability & Ops
- Logging: structured JSON with requestId, userId for write ops.  
- Metrics: basic JVM/HTTP metrics; health checks via Actuator.  
- CI: `mvn verify` including unit + integration tests; quality gate for coverage.

---

## 17. Testing Strategy
- **Unit:** services & domain rules (Mockito).  
- **Integration:** repository & controller tests with Postgres **Testcontainers**; `@SpringBootTest` + `MockMvc`.  
- **E2E:** scripted `curl` flows covering register → login → admin create product → customer cart → checkout → pay.  
- **Coverage Targets:** ≥ 80% service, critical paths 100%.

---

## 18. Deployment & Environments
- **Local:** Docker Compose (Postgres; optional Redis).  
- **Config:** externalized via `application.yml` + environment variables.  
- **Packaging:** runnable JAR; containerized image for app.

---

## 19. Risks & Mitigations
- **Concurrent stock updates:** use transactional decrement; consider optimistic locking.  
- **JWT leakage:** short token TTL; HTTPS; secure storage.  
- **Data loss:** backups for Postgres volume; Flyway migrations versioned.

---

## 20. Roadmap (v1 → v2+)
- **v1:** core flows as defined; mock payments; basic reporting via admin list endpoints.  
- **v2:** coupons/discounts, search specs, caching, rate limiting.  
- **v3:** split into microservices; event‑driven (Kafka) for order notifications; external payment provider.

---

## 21. Glossary
- **DTO:** Data Transfer Object; API payloads decoupled from domain entities.  
- **Mapper:** MapStruct interface converting DTO ↔ Entity.  
- **ControllerAdvice:** Spring component handling exceptions globally.  
- **Testcontainers:** library to run real DBs in tests.

---

## 22. Development Guidelines
- **Architecture:** MVC (Controller → Service → Repository) with DTO boundary; Hexagonal‑ready.  
- **Coding Standards:** Java 21, Lombok permitted; explicit validation; no business logic in controllers.  
- **Git Workflow:** feature branches; PR reviews; CI green before merge.  
- **Documentation:** Swagger maintained; README with setup & `curl` examples.

---

## 23. Acceptance Criteria (Samples)
- **Checkout succeeds:** Given cart with in‑stock items, when checkout is called, then an order with correct totals and `CREATED` status is persisted, stock decremented atomically, and cart cleared.  
- **Checkout fails on stock:** Given cart with over‑quantity, when checkout, then `409 Conflict` with message and no DB changes.  
- **Admin product creation:** Valid DTO persists product, appears in public listing; Swagger reflects endpoint.

---

## 24. Open Questions (to be decided during implementation)
- Should admin user creation be restricted to seeded data only in non‑prod?  
- What is default max page size? (suggest: 100)  
- Do we support soft‑delete for products? (suggest: `active=false`)

---

**Paradigm Decision:** Implement **Layered MVC** now; ensure code respects **Clean Architecture boundaries** (no business logic in controllers, DTO mapping at edges, services own transactions). Keep adapters/ports optional for future integrations.

