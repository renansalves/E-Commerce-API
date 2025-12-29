-- V2: criação inicial de schema e tabelas para e-commerce
-- Banco: PostgreSQL
-- Schema: ECOMERCE

-- 1) Schema
CREATE SCHEMA IF NOT EXISTS ECOMERCE;

-- 2) Tipos ENUM necessários
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role_enum') THEN
    CREATE TYPE ECOMERCE.user_role_enum AS ENUM ('ADMIN', 'CLIENTE');
  END IF;
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'order_status_enum') THEN
    CREATE TYPE ECOMERCE.order_status_enum AS ENUM ('PENDING','PAID','SHIPPED','DELIVERED','CANCELLED');
  END IF;
END$$;

-- 3) Tabelas de catálogo de produtos
CREATE TABLE IF NOT EXISTS ECOMERCE.product (
  id              BIGSERIAL PRIMARY KEY,
  sku             TEXT        NOT NULL UNIQUE,
  name            TEXT        NOT NULL,
  description     TEXT,
  price_cents     BIGINT      NOT NULL CHECK (price_cents >= 0),
  currency        TEXT        NOT NULL DEFAULT 'BRL',
  active          BOOLEAN     NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS ECOMERCE.category (
  id              BIGSERIAL PRIMARY KEY,
  name            TEXT        NOT NULL,
  description     TEXT,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS ECOMERCE.product_category (
  product_id      BIGINT      NOT NULL REFERENCES ECOMERCE.product(id) ON DELETE CASCADE,
  category_id     BIGINT      NOT NULL REFERENCES ECOMERCE.category(id) ON DELETE CASCADE,
  PRIMARY KEY (product_id, category_id)
);

-- 4) Usuários
CREATE TABLE IF NOT EXISTS ECOMERCE.users (
  id              BIGSERIAL PRIMARY KEY,
  email           TEXT        NOT NULL UNIQUE,
  password_hash   TEXT        NOT NULL,
  role            ECOMERCE.user_role_enum NOT NULL,
  enabled         BOOLEAN     NOT NULL DEFAULT TRUE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 5) Carrinho e itens
CREATE TABLE IF NOT EXISTS ECOMERCE.carts (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT      NOT NULL UNIQUE  -- 1:1 usuário <-> carrinho
                REFERENCES ECOMERCE.users(id) ON DELETE CASCADE,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS ECOMERCE.cart_items (
  id                  BIGSERIAL PRIMARY KEY,
  cart_id             BIGINT      NOT NULL REFERENCES ECOMERCE.carts(id)   ON DELETE CASCADE,
  product_id          BIGINT      NOT NULL REFERENCES ECOMERCE.product(id) ON DELETE RESTRICT,
  quantity            INTEGER     NOT NULL CHECK (quantity > 0),
  unit_price_snapshot BIGINT      NOT NULL CHECK (unit_price_snapshot >= 0),
  created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE (cart_id, product_id) -- não repetir o mesmo produto no carrinho
);

-- 6) Pedido e itens do pedido
CREATE TABLE IF NOT EXISTS ECOMERCE.orders (
  id              BIGSERIAL PRIMARY KEY,
  user_id         BIGINT      NOT NULL REFERENCES ECOMERCE.users(id) ON DELETE RESTRICT,
  status          ECOMERCE.order_status_enum NOT NULL DEFAULT 'PENDING',
  total_cents     BIGINT      NOT NULL CHECK (total_cents >= 0),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS ECOMERCE.order_items (
  id              BIGSERIAL PRIMARY KEY,
  order_id        BIGINT      NOT NULL REFERENCES ECOMERCE.orders(id)  ON DELETE CASCADE,
  product_id      BIGINT      NOT NULL REFERENCES ECOMERCE.product(id) ON DELETE RESTRICT,
  quantity        INTEGER     NOT NULL CHECK (quantity > 0),
  unit_price      BIGINT      NOT NULL CHECK (unit_price >= 0),
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_product_active         ON ECOMERCE.product(active);
CREATE INDEX IF NOT EXISTS idx_product_sku            ON ECOMERCE.product(sku);
CREATE INDEX IF NOT EXISTS idx_category_name          ON ECOMERCE.category(name);
CREATE INDEX IF NOT EXISTS idx_users_email            ON ECOMERCE.users(email);
CREATE INDEX IF NOT EXISTS idx_orders_user_id_status  ON ECOMERCE.orders(user_id, status);
CREATE INDEX IF NOT EXISTS idx_order_items_order_id   ON ECOMERCE.order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_cart_id     ON ECOMERCE.cart_items(cart_id);
