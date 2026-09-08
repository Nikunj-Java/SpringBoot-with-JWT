CREATE TABLE IF NOT EXISTS accounts (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          balance NUMERIC(15,2) NOT NULL
);