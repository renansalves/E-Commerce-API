-- V2: Adiciona o campo para contar a quantidade no stock, Ajuste para que garanta a criação do enum uma unica vez. 
-- 
-- Banco: PostgreSQL
-- Schema: ECOMERCE
CREATE SCHEMA IF NOT EXISTS ECOMMERCE;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type t JOIN pg_namespace n ON n.oid = t.typnamespace 
                   WHERE t.typname = 'user_role_enum' AND n.nspname = 'ecommerce') THEN
        CREATE TYPE ECOMMERCE.user_role_enum AS ENUM ('ADMIN', 'CLIENTE');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_type t JOIN pg_namespace n ON n.oid = t.typnamespace 
                   WHERE t.typname = 'order_status_enum' AND n.nspname = 'ecommerce') THEN
        CREATE TYPE ECOMMERCE.order_status_enum AS ENUM ('PENDING','PAID','SHIPPED','DELIVERED','CANCELLED');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS ECOMMERCE.product (
  id              BIGSERIAL PRIMARY KEY,
  sku             TEXT        NOT NULL UNIQUE,
  name            TEXT        NOT NULL,
  description     TEXT,
  price_cents     BIGINT      NOT NULL CHECK (price_cents >= 0),
  stock_quantity  INTEGER     NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0), -- Integrando a lógica de estoque
  currency        TEXT        NOT NULL DEFAULT 'BRL',
  active          BOOLEAN     NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ
);

-- 4) Categoria e Relacionamento
CREATE TABLE IF NOT EXISTS ECOMMERCE.category (
  id              BIGSERIAL PRIMARY KEY,
  name            TEXT        NOT NULL,
  description     TEXT,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS ECOMMERCE.product_category (
  product_id      BIGINT      NOT NULL REFERENCES ECOMMERCE.product(id) ON DELETE CASCADE,
  category_id     BIGINT      NOT NULL REFERENCES ECOMMERCE.category(id) ON DELETE CASCADE,
  PRIMARY KEY (product_id, category_id)
);

CREATE TABLE IF NOT EXISTS ECOMMERCE.users (
  id              BIGSERIAL PRIMARY KEY,
  email           TEXT        NOT NULL UNIQUE,
  password_hash        TEXT        NOT NULL,
  role            ECOMMERCE.user_role_enum NOT NULL,
  enabled         BOOLEAN     NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS ECOMMERCE.carts (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT      NOT NULL UNIQUE REFERENCES ECOMMERCE.users(id) ON DELETE CASCADE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS ECOMMERCE.cart_items (
  id              BIGSERIAL PRIMARY KEY,
  cart_id         BIGINT      NOT NULL REFERENCES ECOMMERCE.carts(id) ON DELETE CASCADE,
  product_id      BIGINT      NOT NULL REFERENCES ECOMMERCE.product(id) ON DELETE RESTRICT,
  quantity        INTEGER     NOT NULL CHECK (quantity > 0),
  unit_price      BIGINT      NOT NULL CHECK (unit_price >= 0), -- Sincronizado com sua entidade
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (cart_id, product_id)
);

CREATE TABLE IF NOT EXISTS ECOMMERCE.orders (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT      NOT NULL REFERENCES ECOMMERCE.users(id) ON DELETE RESTRICT,
  status          ECOMMERCE.order_status_enum NOT NULL DEFAULT 'PENDING',
  total_cents     BIGINT      NOT NULL CHECK (total_cents >= 0),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS ECOMMERCE.order_items (
  id              BIGSERIAL PRIMARY KEY,
  order_id        BIGINT      NOT NULL REFERENCES ECOMMERCE.orders(id) ON DELETE CASCADE,
  product_id      BIGINT      NOT NULL REFERENCES ECOMMERCE.product(id) ON DELETE RESTRICT,
  quantity        INTEGER     NOT NULL CHECK (quantity > 0),
  unit_price      BIGINT      NOT NULL CHECK (unit_price >= 0),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_product_active         ON ECOMMERCE.product(active);
CREATE INDEX IF NOT EXISTS idx_product_sku            ON ECOMMERCE.product(sku);
CREATE INDEX IF NOT EXISTS idx_users_email            ON ECOMMERCE.users(email);
CREATE INDEX IF NOT EXISTS idx_orders_user_id_status  ON ECOMMERCE.orders(user_id, status);
