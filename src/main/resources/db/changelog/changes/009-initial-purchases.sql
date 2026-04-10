CREATE TABLE purchases (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    provider_id INT NOT NULL,
    user_id INT NOT NULL
);

ALTER TABLE purchases
ADD CONSTRAINT purchases_FK_1
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE purchases
ADD CONSTRAINT purchases_FK_2
FOREIGN KEY (provider_id) REFERENCES providers(id);