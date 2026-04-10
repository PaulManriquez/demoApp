CREATE TABLE stock (
    id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_id INT NOT NULL,
    sale_id INT DEFAULT NULL,
    product_id INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    purchase_price DECIMAL(10,2) NOT NULL,
    sale_price DECIMAL(10,2) DEFAULT NULL
);

ALTER TABLE stock
ADD CONSTRAINT stock_FK_1
FOREIGN KEY (purchase_id) REFERENCES purchases(id);

ALTER TABLE stock
ADD CONSTRAINT stock_FK_2
FOREIGN KEY (sale_id) REFERENCES sales(id);

ALTER TABLE stock
ADD CONSTRAINT stock_FK_3
FOREIGN KEY (product_id) REFERENCES products(id);