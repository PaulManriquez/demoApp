CREATE TABLE sales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    client_id INT NOT NULL,
    user_id INT NOT NULL
);

ALTER TABLE sales
ADD CONSTRAINT sales_FK_1
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE sales
ADD CONSTRAINT sales_FK_2
FOREIGN KEY (client_id) REFERENCES clients(id);



CREATE TABLE sale_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

ALTER TABLE sale_details
ADD CONSTRAINT sale_details_FK_1
FOREIGN KEY (sale_id) REFERENCES sales(id);

ALTER TABLE sale_details
ADD CONSTRAINT sale_details_FK_2
FOREIGN KEY (product_id) REFERENCES products(id);