CREATE TABLE providers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL,
    maps_link VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active TINYINT(1) NOT NULL DEFAULT 1,
    user_id INT NOT NULL
);

ALTER TABLE providers
ADD CONSTRAINT providers_FK_1
FOREIGN KEY (user_id) REFERENCES users(id);

INSERT INTO providers (name, address, phone, maps_link, created_at, active, user_id)
VALUES (
    'Zavala Muebles',
    'Av. Nacional Km 31, La Esmeralda, 55765 Ojo de Agua, Méx.',
    '5512231619',
    'https://www.google.com/maps/place/Abastecedora+de+muebles/@19.6563477,-99.0044416,14z/data=!4m6!3m5!1s0x85d1edcadaf1fcb7:0x5e3c1e2e86f743c3!8m2!3d19.6563477!4d-99.0044416!16s%2Fg%2F11gxqhg4gl?authuser=0&entry=ttu',
    CURRENT_TIMESTAMP,
    1,
    1
);

INSERT INTO providers (name, address, phone, maps_link, created_at, active, user_id)
VALUES (
    '22 Industrial',
    'Irapuato-Abasolo 3053, Las Plazas, 36620 Irapuato, Gto.',
    '4626306928',
    'https://www.google.com/maps/place/22+Industrial/@20.7000123,-101.3569246,20z/data=!4m6!3m5!1s0x842c7f53437a36e5:0x98f80f3ddca53753!8m2!3d20.7000123!4d-101.3566027!16s%2Fg%2F11ssg74glm?authuser=0&entry=ttu',
    CURRENT_TIMESTAMP,
    1,
    1
);