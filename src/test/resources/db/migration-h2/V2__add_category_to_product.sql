-- Adiciona a coluna permitindo null temporariamente se já houver dados
ALTER TABLE ecommerce.product ADD COLUMN category_id BIGINT;

-- Cria a restrição de chave estrangeira
ALTER TABLE ecommerce.product 
ADD CONSTRAINT fk_product_category 
FOREIGN KEY (category_id) REFERENCES ecommerce.category(id);

-- Cria um índice para otimizar buscas de produtos por categoria (Plano MD 10.1)
CREATE INDEX idx_product_category_id ON ecommerce.product(category_id);

DROP TABLE ecommerce.product_category;
