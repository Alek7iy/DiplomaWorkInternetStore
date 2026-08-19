-- Роли
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

-- Пользователи
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role_id INTEGER NOT NULL REFERENCES roles(id),
                       created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Категории
CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT
);

-- Продукты
CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
                          category_id INTEGER NOT NULL REFERENCES categories(id),
                          stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0)
);

-- Добавляем базовые данные
INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');
