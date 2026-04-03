CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    items INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    retail_price DECIMAL(10,2) NOT NULL,
    wholesale_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    special TINYINT(1) NOT NULL DEFAULT 0,
    active TINYINT(1) NOT NULL DEFAULT 1,
    user_id INT NOT NULL
);

ALTER TABLE products
ADD CONSTRAINT products_FK_1
FOREIGN KEY (user_id) REFERENCES users(id);