-- Заказы
CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        user_id INTEGER NOT NULL,
                        total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
                        status VARCHAR(50) NOT NULL DEFAULT 'NEW',
                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                        CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Позиции заказа
CREATE TABLE order_items (
                             id SERIAL PRIMARY KEY,
                             order_id INTEGER NOT NULL,
                             product_id INTEGER NOT NULL,
                             quantity INTEGER NOT NULL CHECK (quantity > 0),
                             price_at_moment DECIMAL(10, 2) NOT NULL,
                             CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products(product_id),
                             CONSTRAINT uq_order_product UNIQUE (order_id, product_id)
);
