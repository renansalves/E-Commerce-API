-- V5: Adiciona o campo nome no usuário.
-- 
-- Banco: PostgreSQL
-- Schema: ECOMERCE
ALTER TABLE ecommerce.users ADD COLUMN name TEXT NOT NULL DEFAULT 'sem_nome';
