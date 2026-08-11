CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role_id INT NOT NULL REFERENCES roles(id),
                       created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            description TEXT
);

CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
                          category_id INT NOT NULL REFERENCES categories(id),
                          stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0)
);

CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES users(id),
                        total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                        status VARCHAR(50) NOT NULL DEFAULT 'NEW',
                        created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
                             id SERIAL PRIMARY KEY,
                             order_id INT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id INT NOT NULL REFERENCES products(id),
                             quantity INT NOT NULL CHECK (quantity > 0),
                             price_at_moment DECIMAL(10, 2) NOT NULL,
                             UNIQUE (order_id, product_id)
);
