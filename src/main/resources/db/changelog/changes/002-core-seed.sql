INSERT INTO roles (id, name) VALUES (1, 'ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'TECHNICIAN');

INSERT INTO users
(id, name, last_name, username, password, phone, email, start_time, status)
VALUES
(1,
 'Paul',
 'Manriquez',
 'PaulM',
 '$2a$10$wSjy1ijXqjF.UDgVZHq4S.MOjMH6dRjx1PwNkwRQHcJ7qVPE9PHwu',
 '4621909060',
 'paulmanriquezengineer@gmail.com',
 NOW(),
 1);

INSERT INTO users_roles (user_id, rol_id) VALUES (1, 1);
INSERT INTO users_roles (user_id, rol_id) VALUES (1, 2);
