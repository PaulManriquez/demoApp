CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    maps_link VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active TINYINT(1) NOT NULL DEFAULT 1,
    user_id INT NOT NULL
);

ALTER TABLE clients
ADD CONSTRAINT clients_FK_1
FOREIGN KEY (user_id) REFERENCES users(id);

INSERT INTO clients (
    first_name,
    last_name,
    address,
    maps_link,
    phone,
    created_at,
    active,
    user_id
)
VALUES (
    'Juan',
    'Pérez',
    'Av. Vallarta 1234, Guadalajara, Jal.',
    'https://www.google.com/maps?q=Av.+Vallarta+1234+Guadalajara',
    '3312345678',
    CURRENT_TIMESTAMP,
    1,
    1
);