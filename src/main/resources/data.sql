CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NULL,
    last_login TIMESTAMP NULL,
    token VARCHAR(300) NULL,
    role VARCHAR(255) NULL,
    isactive BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS phones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(255),
    city_code INT,
    country_code VARCHAR(255),
    user_id UUID,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insertar usuarios con UUID generado
INSERT INTO users (id, name, username, email, password, role, isactive) VALUES
    (random_uuid(), 'Alejandro Sandoval Schwartzmann', 'asandoval', 'alejandro.sandoval@example.com', '$2a$12$yEOJ.hW/hvbctmQr6mUIEuDLnJle.QB2FuM/z18yEBYdBuJzfbX0O', 'ADMIN', TRUE),
    (random_uuid(), 'Alice Smith', 'asmith', 'alice.smith@example.com', '$2a$10$ZTIwWfhfjPIB3ZlQf7pWYOltTFHk.vpNybOj13RbGLjrm9OFSVKa2', 'USER', TRUE),
    (random_uuid(), 'Bob Johnson', 'bjohnson', 'bob.johnson@example.com', '$2a$10$71nhmlP3lNptbS1HGBUehO7YPWgyjN7uK5AhKgf2TVMwriTGsy8Ya', 'USER', TRUE),
    (random_uuid(), 'Cathy Brown', 'cbrown', 'cathy.brown@example.com', '$2a$10$nPrO8S6A0DGLFjZJlGsTbuwleRs.fPtcEqt6tJZK7JjG8ndJCPgE.', 'USER', TRUE),
    (random_uuid(), 'David White', 'dwhite', 'david.white@example.com', '$2a$10$FTmfVrPcgsy6cN6RpWoFPOePnqQ7zQPyxnRQXcKUC/r04Tml3PmRm', 'USER', FALSE);

-- Insertar teléfonos asociados a los usuarios
INSERT INTO phones (number, city_code, country_code, user_id) VALUES
    ('1234567', 1, '57', (SELECT id FROM users WHERE username = 'asandoval')),
    ('9876543', 2, '57', (SELECT id FROM users WHERE username = 'asmith')),
    ('4567890', 3, '57', (SELECT id FROM users WHERE username = 'bjohnson')),
    ('5432167', 1, '57', (SELECT id FROM users WHERE username = 'cbrown')),
    ('6789456', 2, '57', (SELECT id FROM users WHERE username = 'dwhite'));
