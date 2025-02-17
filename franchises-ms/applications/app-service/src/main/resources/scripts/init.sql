DROP DATABASE IF EXISTS franchise_test;
CREATE DATABASE franchise_test;
USE franchise_test;

DROP TABLE IF EXISTS product_branch;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS branch;
DROP TABLE IF EXISTS franchise;

CREATE TABLE franchise (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE branch (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    franchise_id BIGINT,
    FOREIGN KEY (franchise_id) REFERENCES franchise(id) ON DELETE CASCADE
);

CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE product_branch (
    product_id BIGINT,
    branch_id BIGINT,
    stock INT DEFAULT 0,
    PRIMARY KEY (product_id, branch_id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE,
    FOREIGN KEY (branch_id) REFERENCES branch(id) ON DELETE CASCADE
);

INSERT INTO franchise (name) VALUES
    ('Franquicia Norte'),
    ('Franquicia Sur');

INSERT INTO branch (name, franchise_id) VALUES
    ('Sucursal Central', 1),
    ('Sucursal Puerto', 1),
    ('Sucursal Centro', 2);

INSERT INTO product (name) VALUES
    ('Producto A'),
    ('Producto B'),
    ('Producto C');

INSERT INTO product_branch (product_id, branch_id, stock) VALUES
    (1, 1, 100),
    (1, 2, 150),
    (2, 1, 200),
    (3, 3, 300);