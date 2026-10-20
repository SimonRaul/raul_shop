CREATE TABLE IF NOT EXISTS revenues (
    id UUID PRIMARY KEY,
    date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    location_id UUID NOT NULL,
    CONSTRAINT fk_revenues_location FOREIGN KEY (location_id) REFERENCES locations(id)
);