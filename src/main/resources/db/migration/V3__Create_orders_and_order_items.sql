CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        user_id INTEGER NOT NULL REFERENCES users(user_id),
                        total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                        status VARCHAR(50) NOT NULL DEFAULT 'NEW',
                        created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE order_items (
                             id SERIAL PRIMARY KEY,
                             order_id INTEGER NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id INTEGER NOT NULL REFERENCES products(product_id),
                             quantity INTEGER NOT NULL CHECK (quantity > 0),
                             price_at_moment DECIMAL(10, 2) NOT NULL,
                             UNIQUE (order_id, product_id)
);

