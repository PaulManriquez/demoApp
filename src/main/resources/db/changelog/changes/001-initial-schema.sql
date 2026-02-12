CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    start_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE users_roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    rol_id INT NOT NULL,
    CONSTRAINT users_roles_constraint UNIQUE (user_id, rol_id)
);

ALTER TABLE users_roles
ADD CONSTRAINT users_roles_fk_user
FOREIGN KEY (user_id)
REFERENCES users(id)
ON DELETE CASCADE;

ALTER TABLE users_roles
ADD CONSTRAINT users_roles_fk_role
FOREIGN KEY (rol_id)
REFERENCES roles(id);