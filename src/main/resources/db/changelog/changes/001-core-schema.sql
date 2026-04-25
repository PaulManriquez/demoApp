CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100) NOT NULL UNIQUE,
    start_time TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users_roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    rol_id INT NOT NULL,
    CONSTRAINT users_roles_constraint UNIQUE (user_id, rol_id),
    CONSTRAINT users_roles_fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT users_roles_fk_role FOREIGN KEY (rol_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    maps_link VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active TINYINT(1) NOT NULL DEFAULT 1,
    user_id INT NOT NULL,
    CONSTRAINT clients_fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    duration_minutes INT NOT NULL DEFAULT 60,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    visible TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,
    CONSTRAINT services_fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    technician_user_id INT NOT NULL,
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    notes VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT appointments_fk_client FOREIGN KEY (client_id) REFERENCES clients(id),
    CONSTRAINT appointments_fk_technician FOREIGN KEY (technician_user_id) REFERENCES users(id)
);

CREATE INDEX idx_appointments_technician_start_end
    ON appointments (technician_user_id, start_at, end_at);

CREATE TABLE appointment_services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    service_id INT NOT NULL,
    CONSTRAINT appointment_services_unique UNIQUE (appointment_id, service_id),
    CONSTRAINT appointment_services_fk_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    CONSTRAINT appointment_services_fk_service FOREIGN KEY (service_id) REFERENCES services(id)
);

