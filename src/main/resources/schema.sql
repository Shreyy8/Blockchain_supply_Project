-- Database Schema for Blockchain Supply Chain Management System
-- This script creates the database and all required tables
-- Note: Database is already selected via JDBC URL (jdbc:mysql://localhost:3306/supply_chain_db)

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS blocks;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create products table
CREATE TABLE products (
    product_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    origin VARCHAR(200),
    current_location VARCHAR(200),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_origin (origin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create transactions table
CREATE TABLE transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    transaction_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    from_party VARCHAR(50),
    to_party VARCHAR(50),
    product_id VARCHAR(50),
    data TEXT,
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_timestamp (timestamp),
    INDEX idx_product_id (product_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create blocks table
CREATE TABLE blocks (
    block_index INT PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    transactions TEXT NOT NULL,
    previous_hash VARCHAR(64) NOT NULL,
    hash VARCHAR(64) NOT NULL,
    nonce BIGINT NOT NULL,
    INDEX idx_timestamp (timestamp),
    INDEX idx_hash (hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert sample data for testing

-- Sample users (passwords are SHA-256 hashed)
-- admin123 -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- pass123  -> 9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c
INSERT INTO users (user_id, username, password, email, role) VALUES
('MGR001', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin@supplychain.com', 'MANAGER'),
('SUP001', 'supplier1', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'supplier1@supplychain.com', 'SUPPLIER'),
('RET001', 'retailer1', '9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c', 'retailer1@supplychain.com', 'RETAILER');

-- Sample products
INSERT INTO products (product_id, name, description, origin, current_location, status, created_at) VALUES
('PROD001', 'Laptop Computer', 'High-performance laptop', 'China', 'China', 'CREATED', NOW()),
('PROD002', 'Smartphone', 'Latest model smartphone', 'Korea', 'Korea', 'CREATED', NOW());

-- Display success message
SELECT 'Database and tables created successfully!' AS Status;
