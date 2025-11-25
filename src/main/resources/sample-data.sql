-- Sample Data for Testing
-- Blockchain-based Supply Chain Management System

-- Insert sample users
INSERT INTO users (user_id, username, password, email, role) VALUES
('USR001', 'manager1', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'manager@supplychain.com', 'MANAGER'),
('USR002', 'supplier1', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'supplier1@supplychain.com', 'SUPPLIER'),
('USR003', 'supplier2', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'supplier2@supplychain.com', 'SUPPLIER'),
('USR004', 'retailer1', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'retailer1@supplychain.com', 'RETAILER'),
('USR005', 'retailer2', 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'retailer2@supplychain.com', 'RETAILER');

-- Insert sample products
INSERT INTO products (product_id, name, description, origin, current_location, status) VALUES
('PROD001', 'Organic Coffee Beans', 'Premium organic coffee beans from Colombia', 'Colombia', 'Warehouse A', 'CREATED'),
('PROD002', 'Electronic Components', 'High-quality electronic components for manufacturing', 'China', 'Distribution Center', 'IN_TRANSIT'),
('PROD003', 'Pharmaceutical Supplies', 'Medical supplies requiring temperature control', 'Germany', 'Retailer Store', 'DELIVERED'),
('PROD004', 'Textile Materials', 'Sustainable textile materials for clothing', 'India', 'Factory', 'VERIFIED'),
('PROD005', 'Fresh Produce', 'Organic vegetables and fruits', 'Local Farm', 'Market', 'DELIVERED');

-- Insert sample transactions
INSERT INTO transactions (transaction_id, transaction_type, timestamp, from_party, to_party, product_id, data) VALUES
('TXN001', 'ProductCreationTransaction', '2024-01-15 10:00:00', 'USR002', NULL, 'PROD001', '{"action":"created","location":"Colombia","quality":"Grade A"}'),
('TXN002', 'ProductTransferTransaction', '2024-01-16 14:30:00', 'USR002', 'USR004', 'PROD001', '{"action":"transferred","from":"Colombia","to":"Warehouse A"}'),
('TXN003', 'ProductCreationTransaction', '2024-01-17 09:15:00', 'USR003', NULL, 'PROD002', '{"action":"created","location":"China","batch":"B2024-001"}'),
('TXN004', 'ProductTransferTransaction', '2024-01-18 11:45:00', 'USR003', 'USR004', 'PROD002', '{"action":"transferred","from":"China","to":"Distribution Center"}'),
('TXN005', 'ProductVerificationTransaction', '2024-01-19 16:20:00', 'USR004', NULL, 'PROD003', '{"action":"verified","status":"authentic","inspector":"USR004"}');

-- Insert genesis block (first block in blockchain)
INSERT INTO blocks (block_index, timestamp, transactions, previous_hash, hash, nonce) VALUES
(0, '2024-01-01 00:00:00', '[]', '0', '0000000000000000000000000000000000000000000000000000000000000000', 0);
