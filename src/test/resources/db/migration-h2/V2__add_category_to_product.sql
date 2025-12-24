ALTER TABLE ECOMMERCE.product ADD COLUMN category_id BIGINT;

ALTER TABLE ECOMMERCE.product 
ADD CONSTRAINT fk_product_category 
FOREIGN KEY (category_id) REFERENCES ECOMMERCE.category(id);

CREATE INDEX idx_product_category_id ON ECOMMERCE.product(category_id);

DROP TABLE ECOMMERCE.product_category;
