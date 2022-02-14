-- Table users
CREATE TABLE users(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    account_non_locked BIT NOT NULL DEFAULT 1,
    active BIT NOT NULL DEFAULT 0,
    activation_code VARCHAR(255) UNIQUE
)
ENGINE=InnoDB;

-- Table roles
CREATE TABLE roles(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
)
ENGINE=InnoDB;

-- Table for mapping users and roles
CREATE TABLE user_roles(
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    UNIQUE (user_id,role_id)
)
ENGINE=InnoDB;

-- Table orders
CREATE TABLE orders(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(1000) NOT NULL UNIQUE,
    is_accepted BIT NOT NULL DEFAULT 0,
    is_completed BIT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
)
ENGINE =InnoDB;

-- insert data
INSERT INTO users VALUES (1, 'admin', 'laim778@yandex.ru', '+7(996)404-45-13', '$2a$12$25GU6Wki1S3HpZbZNt.BhObLYg4423s.h3xl/8lGMJi2YZK5/lJxS', 1,1 , null);
INSERT INTO roles VALUES (1, 'ROLE_USER');
INSERT INTO roles VALUES (2, 'ROLE_ADMIN');

INSERT INTO user_roles VALUES (1,2);
