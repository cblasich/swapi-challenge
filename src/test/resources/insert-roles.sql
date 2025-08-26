DELETE FROM users_roles;
DELETE FROM roles;
DELETE FROM users;
INSERT INTO roles (role_id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (role_id, name) VALUES (2, 'ROLE_PEOPLE');