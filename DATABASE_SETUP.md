# Database Setup Guide

## Overview
This document describes the database setup for the Blockchain Supply Chain Management System.

## Database Configuration

**Database Name:** `supply_chain_db`  
**DBMS:** MySQL Server 8.0  
**Host:** localhost:3306  
**Username:** root  
**Password:** shery

## Schema

The database schema is defined in `src/main/resources/schema.sql` and includes the following tables:

### Tables

1. **users** - Stores user accounts (Supply Chain Managers, Suppliers, Retailers)
   - Primary Key: `user_id`
   - Unique constraint on `username`

2. **products** - Stores product information
   - Primary Key: `product_id`
   - Tracks product status and location

3. **transactions** - Stores blockchain transactions
   - Primary Key: `transaction_id`
   - Foreign Key: `product_id` references `products(product_id)`

4. **blocks** - Stores blockchain blocks
   - Primary Key: `block_index`
   - Contains transaction references and cryptographic hashes

## Setup Instructions

### Initial Setup

1. Ensure MySQL Server 8.0 is installed and running
2. Execute the schema script:
   ```bash
   mysql -u root -pshery < src/main/resources/schema.sql
   ```

### Configuration

The database connection is configured in `src/main/resources/database.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/supply_chain_db
db.username=root
db.password=shery
db.driver=com.mysql.cj.jdbc.Driver
db.pool.size=10
db.pool.timeout=30000
```

## Sample Data

The schema script includes sample data for testing:
- 3 sample users (1 manager, 1 supplier, 1 retailer)
- 2 sample products

## Testing

The database persistence is validated through property-based tests in:
`src/test/java/com/supplychain/dao/DatabasePersistenceRoundTripPropertyTest.java`

These tests verify that:
- Transactions can be saved and retrieved correctly
- Products can be saved and retrieved correctly
- Users can be saved and retrieved correctly

All tests use random data generation (100 iterations each) to ensure robustness.

## Notes

- The foreign key constraint on `transactions.product_id` requires that products exist before transactions can be created
- Connection pooling is managed by `DatabaseConnectionManager` with 10 connections by default
- All DAO classes use PreparedStatement to prevent SQL injection
