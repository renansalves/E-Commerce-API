ALTER TABLE ecommerce.product ADD COLUMN category_id BIGINT;

ALTER TABLE ecommerce.product 
ADD CONSTRAINT fk_product_category 
FOREIGN KEY (category_id) REFERENCES ecommerce.category(id);

CREATE INDEX idx_product_category_id ON ecommerce.product(category_id);

DROP TABLE ecommerce.product_category;
