CREATE TABLE IF NOT EXISTS users (
                       id UUID PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email_address VARCHAR(255) NOT NULL,
                       role VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS addresses (
                           id UUID PRIMARY KEY,
                           country VARCHAR(255) NOT NULL,
                           city VARCHAR(255) NOT NULL,
                           county VARCHAR(255) NOT NULL,
                           street_address VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS product_categories (
                                    id UUID PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    description VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (
                           id UUID PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           address_id UUID NOT NULL,
                           CONSTRAINT fk_locations_address FOREIGN KEY (address_id) REFERENCES addresses(id)
);

CREATE TABLE IF NOT EXISTS products (
                          id UUID PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description VARCHAR(255) NOT NULL,
                          price DECIMAL(10,2) NOT NULL,
                          weight DOUBLE PRECISION,
                          image_url VARCHAR(255),
                          category_id UUID NOT NULL,
                          CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES product_categories(id)
);

CREATE TABLE IF NOT EXISTS orders (
                        id UUID PRIMARY KEY,
                        user_id UUID NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        total_amount DECIMAL(10, 2) NOT NULL,
                        address_id UUID NOT NULL,
                        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
                        CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES addresses(id)
);

CREATE TABLE IF NOT EXISTS stocks (
                        product_id UUID NOT NULL,
                        location_id UUID NOT NULL,
                        quantity INT NOT NULL,
                        PRIMARY KEY (product_id, location_id),
                        CONSTRAINT fk_stocks_product FOREIGN KEY (product_id) REFERENCES products(id),
                        CONSTRAINT fk_stocks_location FOREIGN KEY (location_id) REFERENCES locations(id)
);

CREATE TABLE IF NOT EXISTS order_details (
                               order_id UUID NOT NULL,
                               product_id UUID NOT NULL,
                               location_id UUID NOT NULL,
                               quantity INT NOT NULL,
                               unit_price DECIMAL(10, 2) NOT NULL,
                               PRIMARY KEY (order_id, product_id),
                               CONSTRAINT fk_order_details_order FOREIGN KEY (order_id) REFERENCES orders(id),
                               CONSTRAINT fk_order_details_product FOREIGN KEY (product_id) REFERENCES products(id),
                               CONSTRAINT fk_order_details_location FOREIGN KEY (location_id) REFERENCES locations(id)
);