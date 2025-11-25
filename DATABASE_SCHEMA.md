# Database Schema - Blockchain Supply Chain Management System

## Overview
The database uses MySQL 8.0 with InnoDB engine for ACID compliance and transaction support. All tables use UTF-8 encoding for international character support.

## Database Information
- **Database Name**: supply_chain_db
- **Engine**: InnoDB
- **Character Set**: utf8mb4
- **Collation**: utf8mb4_general_ci

---

## Tables

### 1. USERS
**Description**: Stores user accounts with authentication and role information

| Column    | Type         | Size | Nullable | Key | Description |
|-----------|--------------|------|----------|-----|-------------|
| user_id   | VARCHAR      | 50   | NO       | PRI | Unique user identifier |
| username  | VARCHAR      | 100  | NO       | UNI | Unique username for login |
| password  | VARCHAR      | 255  | NO       | -   | SHA-256 hashed password |
| email     | VARCHAR      | 100  | YES      | -   | User email address |
| role      | VARCHAR      | 20   | NO       | MUL | User role (MANAGER, SUPPLIER, RETAILER) |

**Indexes**:
- PRIMARY KEY: `user_id`
- UNIQUE INDEX: `username`
- INDEX: `idx_username` on `username`
- INDEX: `idx_role` on `role`

**Sample Data**:
```
user_id  | username   | role      | email
---------|------------|-----------|-------------------------
MGR001   | admin      | MANAGER   | admin@supplychain.com
SUP001   | supplier1  | SUPPLIER  | supplier1@supplychain.com
RET001   | retailer1  | RETAILER  | retailer1@supplychain.com
```

**Password Hashing**: All passwords are hashed using SHA-256
- admin password: `password123` → `240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9`
- supplier1/retailer1 password: `password` → `9b8769a4a742959a2d0298c36fb70623f2dfacda8436237df08d8dfd5b37374c`

---

### 2. PRODUCTS
**Description**: Stores product information tracked in the supply chain

| Column           | Type      | Size  | Nullable | Key | Description |
|------------------|-----------|-------|----------|-----|-------------|
| product_id       | VARCHAR   | 50    | NO       | PRI | Unique product identifier |
| name             | VARCHAR   | 200   | NO       | -   | Product name |
| description      | TEXT      | 65535 | YES      | -   | Product description |
| origin           | VARCHAR   | 200   | YES      | MUL | Product origin location |
| current_location | VARCHAR   | 200   | YES      | -   | Current product location |
| status           | VARCHAR   | 20    | NO       | MUL | Product status (CREATED, IN_TRANSIT, DELIVERED) |
| created_at       | TIMESTAMP | -     | YES      | -   | Creation timestamp |

**Indexes**:
- PRIMARY KEY: `product_id`
- INDEX: `idx_status` on `status`
- INDEX: `idx_origin` on `origin`

**Sample Data**:
```
product_id | name             | origin | current_location | status
-----------|------------------|--------|------------------|--------
PROD001    | Laptop Computer  | China  | China            | CREATED
PROD002    | Smartphone       | Korea  | Korea            | CREATED
```

---

### 3. TRANSACTIONS
**Description**: Records all supply chain transactions between users

| Column           | Type      | Size  | Nullable | Key | Description |
|------------------|-----------|-------|----------|-----|-------------|
| transaction_id   | VARCHAR   | 50    | NO       | PRI | Unique transaction identifier |
| transaction_type | VARCHAR   | 50    | NO       | MUL | Type of transaction |
| timestamp        | TIMESTAMP | -     | NO       | MUL | Transaction timestamp |
| from_party       | VARCHAR   | 50    | YES      | -   | Sender user ID |
| to_party         | VARCHAR   | 50    | YES      | -   | Receiver user ID |
| product_id       | VARCHAR   | 50    | YES      | MUL | Related product ID (FK) |
| data             | TEXT      | 65535 | YES      | -   | Additional transaction data (JSON) |

**Indexes**:
- PRIMARY KEY: `transaction_id`
- INDEX: `idx_transaction_type` on `transaction_type`
- INDEX: `idx_timestamp` on `timestamp`
- INDEX: `idx_product_id` on `product_id`

**Foreign Keys**:
- `product_id` REFERENCES `products(product_id)` ON DELETE SET NULL

**Transaction Types**:
- `CREATE_PRODUCT`: Product creation
- `TRANSFER_PRODUCT`: Product transfer between parties
- `UPDATE_STATUS`: Product status update

---

### 4. BLOCKS
**Description**: Stores blockchain blocks containing transaction data

| Column        | Type      | Size  | Nullable | Key | Description |
|---------------|-----------|-------|----------|-----|-------------|
| block_index   | INT       | -     | NO       | PRI | Sequential block number |
| timestamp     | TIMESTAMP | -     | NO       | MUL | Block creation timestamp |
| transactions  | TEXT      | 65535 | NO       | -   | JSON array of transaction IDs |
| previous_hash | VARCHAR   | 64    | NO       | -   | Hash of previous block |
| hash          | VARCHAR   | 64    | NO       | MUL | SHA-256 hash of this block |
| nonce         | BIGINT    | -     | NO       | -   | Proof-of-work nonce value |

**Indexes**:
- PRIMARY KEY: `block_index`
- INDEX: `idx_timestamp` on `timestamp`
- INDEX: `idx_hash` on `hash`

**Blockchain Properties**:
- Genesis block has `block_index = 0`
- Each block references the previous block's hash
- Blocks are immutable once created
- Mining difficulty ensures blockchain integrity

---

## Relationships

```
users (1) ----< (N) transactions [from_party/to_party]
products (1) ----< (N) transactions [product_id]
transactions (N) ----< (1) blocks [stored in transactions JSON]
```

---

## Setup Instructions

### Initialize Database
Run the database setup utility:
```bash
mvn compile exec:java -Dexec.mainClass=com.supplychain.util.DatabaseSetup -Dexec.cleanupDaemonThreads=false
```

### Manual SQL Execution
```sql
-- Create database
CREATE DATABASE IF NOT EXISTS supply_chain_db;
USE supply_chain_db;

-- Run schema script
SOURCE src/main/resources/schema-simple.sql;
```

---

## Security Features

1. **Password Hashing**: All passwords stored as SHA-256 hashes
2. **Foreign Key Constraints**: Maintain referential integrity
3. **Transaction Support**: InnoDB engine ensures ACID properties
4. **Indexed Queries**: Optimized performance for common queries
5. **Blockchain Immutability**: Blocks cannot be modified once created

---

## Performance Optimizations

1. **Connection Pooling**: 50 pre-initialized connections
2. **Strategic Indexes**: On frequently queried columns
3. **InnoDB Engine**: Row-level locking for concurrent access
4. **UTF-8 Encoding**: Efficient character storage

---

## Database Statistics

After initialization:
- **Users**: 3 rows (admin, supplier1, retailer1)
- **Products**: 2 rows (Laptop, Smartphone)
- **Transactions**: 0 rows (populated during runtime)
- **Blocks**: 0 rows (genesis block created on first transaction)