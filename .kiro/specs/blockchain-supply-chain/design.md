# Design Document

## Overview

The Blockchain-based Supply Chain Management System is a Java application that demonstrates core OOP principles while providing supply chain transparency through blockchain technology. The system uses a layered architecture with clear separation between presentation (GUI), business logic, data access, and blockchain components.

The application will be built using Java Swing for the GUI, JDBC for database persistence, and custom blockchain implementation. The design emphasizes proper use of inheritance, polymorphism, interfaces, exception handling, collections, generics, and multithreading.

## Architecture

The system follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│     Presentation Layer (GUI)        │
│  - User Dashboards (Swing)          │
│  - Login/Authentication UI           │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Business Logic Layer           │
│  - Blockchain Manager                │
│  - Transaction Processor             │
│  - Compliance Validator              │
│  - Optimization Analyzer             │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Data Access Layer (DAO)        │
│  - Database Connection Manager      │
│  - Transaction DAO                   │
│  - User DAO                          │
│  - Product DAO                       │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│         Database (JDBC)              │
│  - MySQL/PostgreSQL                  │
└─────────────────────────────────────┘
```

## Components and Interfaces

### 1. User Model Hierarchy (Inheritance & Polymorphism)

**Abstract Base Class: User**
- Attributes: userId, username, password, email, role
- Abstract methods: getDashboardView(), getPermissions()
- Concrete methods: login(), logout(), updateProfile()

**Concrete Classes:**
- **SupplyChainManager extends User**: Implements manager-specific dashboard and permissions
- **Supplier extends User**: Implements supplier-specific dashboard and permissions
- **Retailer extends User**: Implements retailer-specific dashboard and permissions

This demonstrates inheritance and polymorphism - different user types share common behavior but implement role-specific functionality.

### 2. Blockchain Components

**Block Class:**
- Attributes: index, timestamp, transactions, previousHash, hash, nonce
- Methods: calculateHash(), mineBlock(difficulty)

**Transaction Interface:**
```java
public interface Transaction {
    String getTransactionId();
    String getTransactionType();
    LocalDateTime getTimestamp();
    Map<String, Object> getTransactionData();
    boolean validate();
}
```

**Concrete Transaction Types (Polymorphism):**
- **ProductCreationTransaction implements Transaction**
- **ProductTransferTransaction implements Transaction**
- **ProductVerificationTransaction implements Transaction**

This demonstrates interface implementation and polymorphism - different transaction types can be processed uniformly through the Transaction interface.

### 3. Blockchain Manager

**BlockchainManager Class:**
- Attributes: chain (List<Block>), difficulty, pendingTransactions (List<Transaction>)
- Methods:
  - addTransaction(Transaction t)
  - minePendingTransactions()
  - isChainValid()
  - getTransactionHistory()
  - getProductHistory(String productId)

Uses generics with List<Block> and List<Transaction> for type-safe collections.

### 4. Data Access Layer (Interfaces & JDBC)

**DAO Interface Pattern:**
```java
public interface DAO<T> {
    void save(T entity) throws SQLException;
    T findById(String id) throws SQLException;
    List<T> findAll() throws SQLException;
    void update(T entity) throws SQLException;
    void delete(String id) throws SQLException;
}
```

**Concrete DAO Implementations:**
- **TransactionDAO implements DAO<Transaction>**: JDBC operations for transactions
- **UserDAO implements DAO<User>**: JDBC operations for users
- **ProductDAO implements DAO<Product>**: JDBC operations for products
- **BlockDAO implements DAO<Block>**: JDBC operations for blockchain blocks

Each DAO uses PreparedStatement for SQL injection prevention and proper exception handling.

### 5. Database Connection Manager

**DatabaseConnectionManager Class (Singleton Pattern):**
- Manages connection pooling
- Provides thread-safe database connections
- Handles connection lifecycle and error recovery
- Uses synchronized methods for thread safety

### 6. Exception Handling Hierarchy

**Custom Exception Classes:**
- **BlockchainException extends Exception**: Base exception for blockchain operations
  - **InvalidBlockException extends BlockchainException**
  - **InvalidTransactionException extends BlockchainException**
  - **ChainValidationException extends BlockchainException**
- **DatabaseException extends Exception**: Base exception for database operations
  - **ConnectionException extends DatabaseException**
  - **DataAccessException extends DatabaseException**

Demonstrates exception handling with custom exception hierarchy.

### 7. Multithreading Components

**BlockMiningThread extends Thread:**
- Performs proof-of-work mining in background
- Uses synchronized access to shared blockchain data
- Notifies listeners when mining completes

**TransactionProcessorThread implements Runnable:**
- Processes pending transactions asynchronously
- Uses thread-safe collections (ConcurrentLinkedQueue)
- Demonstrates multithreading and synchronization

### 8. Collections and Generics Usage

- **List<Block>**: Ordered blockchain storage
- **List<Transaction>**: Transaction history
- **Map<String, Product>**: Product lookup by ID
- **Set<String>**: Unique product identifiers
- **Queue<Transaction>**: Pending transaction queue
- **ConcurrentHashMap<String, User>**: Thread-safe user session management

All collections use generics for type safety.

## Data Models

### User
- userId: String (Primary Key)
- username: String
- password: String (hashed)
- email: String
- role: UserRole (enum: MANAGER, SUPPLIER, RETAILER)

### Product
- productId: String (Primary Key)
- name: String
- description: String
- origin: String
- currentLocation: String
- status: ProductStatus (enum: CREATED, IN_TRANSIT, DELIVERED, VERIFIED)

### Transaction
- transactionId: String (Primary Key)
- transactionType: String
- timestamp: LocalDateTime
- fromParty: String
- toParty: String
- productId: String
- data: String (JSON format)

### Block
- index: int
- timestamp: LocalDateTime
- transactions: String (JSON array)
- previousHash: String
- hash: String
- nonce: long

### Database Schema

```sql
CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL
);

CREATE TABLE products (
    product_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    origin VARCHAR(200),
    current_location VARCHAR(200),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    transaction_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    from_party VARCHAR(50),
    to_party VARCHAR(50),
    product_id VARCHAR(50),
    data TEXT,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE blocks (
    block_index INT PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    transactions TEXT NOT NULL,
    previous_hash VARCHAR(64) NOT NULL,
    hash VARCHAR(64) NOT NULL,
    nonce BIGINT NOT NULL
);
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Transaction retrieval completeness
*For any* blockchain with a set of transactions, retrieving transaction history should return all transactions with complete details (timestamp, participant information, transaction data).
**Validates: Requirements 1.1**

### Property 2: Transaction persistence and availability
*For any* valid transaction added to the blockchain, subsequent queries for transaction history should include that transaction.
**Validates: Requirements 1.2**

### Property 3: Chronological ordering of transactions
*For any* set of transactions with different timestamps, retrieving transaction history should return them ordered chronologically from earliest to latest.
**Validates: Requirements 1.3**

### Property 4: Blockchain hash linkage integrity
*For any* blockchain with multiple blocks, each block's previousHash field should equal the hash of the preceding block, forming a valid chain.
**Validates: Requirements 1.4**

### Property 5: Recommendation format completeness
*For any* generated optimization recommendation, it should contain specific suggestions and expected impact metrics.
**Validates: Requirements 2.3**

### Property 6: Regulatory rule persistence round-trip
*For any* regulatory requirement stored in the system, retrieving it should return the same rule with all details intact.
**Validates: Requirements 3.1**

### Property 7: Compliance report completeness
*For any* compliance report generated, it should include a pass or fail status for each regulatory requirement being evaluated.
**Validates: Requirements 3.3**

### Property 8: Non-compliant transaction flagging
*For any* transaction that violates stored regulatory requirements, the system should flag it as non-compliant and generate an alert.
**Validates: Requirements 3.4**

### Property 9: Transaction validation rejects invalid data
*For any* transaction with incomplete or malformed data, the validation process should reject it and provide specific error messages.
**Validates: Requirements 4.1**

### Property 10: Valid transaction creates proper block
*For any* valid transaction, creating a block should result in a block containing the transaction details and a valid cryptographic hash.
**Validates: Requirements 4.2**

### Property 11: Block linkage consistency
*For any* sequence of blocks created, each new block should have a previousHash that matches the hash of the block created immediately before it.
**Validates: Requirements 4.3**

### Property 12: Transaction record completeness
*For any* transaction recorded in the system, it should contain all required fields: timestamp, supplier identifier, product details, and transaction type.
**Validates: Requirements 4.4**

### Property 13: Transaction verification round-trip
*For any* transaction submitted to the blockchain, retrieving and verifying it should return data that matches the original submission.
**Validates: Requirements 5.1**

### Property 14: Hash validation detects tampering
*For any* block in the blockchain, if its data is modified, hash validation should detect the tampering and fail validation.
**Validates: Requirements 5.2**

### Property 15: Verification status clarity
*For any* verification request, the system should return a clear boolean or status indicator showing whether records match.
**Validates: Requirements 5.3**

### Property 16: Product history retrieval completeness
*For any* product with associated transactions, retrieving its history should return all transactions involving that product.
**Validates: Requirements 6.1**

### Property 17: Traceability report completeness
*For any* product traceability report, it should include origin information, all intermediate transactions, and current status.
**Validates: Requirements 6.2**

### Property 18: Product history chronological ordering
*For any* product with multiple transactions, the history should be presented in chronological order from creation to current state.
**Validates: Requirements 6.3**

### Property 19: Product authenticity validation
*For any* product identifier, authenticity verification should validate it against blockchain records and return a clear confirmation or rejection.
**Validates: Requirements 7.1**

### Property 20: Product chain validation
*For any* product's transaction chain, authenticity verification should validate that all transactions form a valid chain with proper cryptographic linkage.
**Validates: Requirements 7.2**

### Property 21: Authenticity verification status clarity
*For any* authenticity verification request, the system should provide a clear confirmation or rejection status.
**Validates: Requirements 7.3**

### Property 22: Invalid input error messages
*For any* invalid data submitted to the system, it should be rejected with specific error messages indicating what is invalid.
**Validates: Requirements 8.1**

### Property 23: Blockchain validation error details
*For any* blockchain validation failure, the system should provide detailed error information about which validation check failed.
**Validates: Requirements 8.3**

### Property 24: Exception logging and user messaging
*For any* exception that occurs, the system should both log detailed error information and present a user-friendly message.
**Validates: Requirements 8.4**

### Property 25: Database persistence round-trip
*For any* transaction persisted to the database, retrieving it should return the same transaction data.
**Validates: Requirements 11.2**

## Error Handling

The system implements a comprehensive error handling strategy:

### Exception Hierarchy

1. **BlockchainException**: Base exception for all blockchain-related errors
   - **InvalidBlockException**: Thrown when block validation fails
   - **InvalidTransactionException**: Thrown when transaction validation fails
   - **ChainValidationException**: Thrown when blockchain integrity check fails

2. **DatabaseException**: Base exception for all database-related errors
   - **ConnectionException**: Thrown when database connection fails
   - **DataAccessException**: Thrown when CRUD operations fail

### Error Handling Patterns

1. **Input Validation**: All user inputs are validated before processing. Invalid inputs throw appropriate exceptions with descriptive messages.

2. **Database Operations**: All JDBC operations are wrapped in try-catch blocks. SQLException is caught and wrapped in custom DatabaseException with context.

3. **Blockchain Operations**: All blockchain operations validate data integrity. Hash mismatches, invalid blocks, or chain corruption throw specific exceptions.

4. **User Feedback**: All exceptions are caught at the presentation layer and converted to user-friendly messages displayed in the GUI.

5. **Logging**: All exceptions are logged with full stack traces using Java logging framework for debugging purposes.

### Example Error Handling:

```java
public void addTransaction(Transaction transaction) throws InvalidTransactionException {
    try {
        if (!transaction.validate()) {
            throw new InvalidTransactionException("Transaction validation failed: " + transaction.getTransactionId());
        }
        pendingTransactions.add(transaction);
        transactionDAO.save(transaction);
    } catch (SQLException e) {
        logger.error("Database error while saving transaction", e);
        throw new DataAccessException("Failed to persist transaction", e);
    }
}
```

## Testing Strategy

The system will employ a dual testing approach combining unit tests and property-based tests to ensure comprehensive coverage and correctness.

### Unit Testing

Unit tests will verify specific examples, edge cases, and integration points:

1. **User Authentication**: Test login with valid/invalid credentials
2. **Block Creation**: Test creating blocks with specific transaction data
3. **Hash Calculation**: Test SHA-256 hash generation with known inputs
4. **Database Connection**: Test connection establishment and error handling
5. **GUI Components**: Test dashboard rendering and user interactions
6. **Edge Cases**:
   - Empty blockchain initialization
   - Single block blockchain
   - Transactions with missing optional fields
   - Database connection failures
   - Invalid product identifiers

**Testing Framework**: JUnit 5 will be used for unit testing.

### Property-Based Testing

Property-based tests will verify universal properties across all inputs using random data generation:

**Testing Framework**: We will use **jqwik** (https://jqwik.net/), a property-based testing library for Java that integrates with JUnit 5.

**Configuration**: Each property-based test will be configured to run a minimum of 100 iterations to ensure thorough coverage of the input space.

**Test Tagging**: Each property-based test will include a comment tag in this exact format:
```java
// Feature: blockchain-supply-chain, Property X: [property description]
```

**Property Test Coverage**:

1. **Property 1 - Transaction retrieval completeness**: Generate random blockchains with varying numbers of transactions, retrieve history, verify all transactions are present with complete data.

2. **Property 4 - Blockchain hash linkage integrity**: Generate random blockchains, verify each block's previousHash matches the previous block's hash.

3. **Property 9 - Transaction validation rejects invalid data**: Generate random invalid transactions (missing fields, wrong types), verify all are rejected with appropriate errors.

4. **Property 10 - Valid transaction creates proper block**: Generate random valid transactions, create blocks, verify blocks contain transaction data and valid hashes.

5. **Property 13 - Transaction verification round-trip**: Generate random transactions, submit them, retrieve them, verify data matches original.

6. **Property 14 - Hash validation detects tampering**: Generate random blocks, modify their data, verify hash validation detects tampering.

7. **Property 16 - Product history retrieval completeness**: Generate random products with varying numbers of transactions, retrieve history, verify all transactions are returned.

8. **Property 18 - Product history chronological ordering**: Generate random product transactions with different timestamps, verify history is ordered chronologically.

9. **Property 25 - Database persistence round-trip**: Generate random transactions, persist to database, retrieve from database, verify data matches.

**Example Property Test Structure**:
```java
// Feature: blockchain-supply-chain, Property 13: Transaction verification round-trip
@Property
@Label("Transaction verification round-trip")
void transactionRoundTrip(@ForAll("validTransactions") Transaction transaction) {
    blockchain.addTransaction(transaction);
    Transaction retrieved = blockchain.getTransaction(transaction.getTransactionId());
    assertEquals(transaction, retrieved);
}

@Provide
Arbitrary<Transaction> validTransactions() {
    return Combinators.combine(
        Arbitraries.strings().alpha().ofLength(10),
        Arbitraries.strings().alpha().ofLength(10),
        Arbitraries.strings().alpha().ofLength(10)
    ).as((id, from, to) -> new ProductTransferTransaction(id, from, to));
}
```

### Integration Testing

Integration tests will verify end-to-end workflows:
1. Complete user workflow: login → create transaction → verify on blockchain → logout
2. Multi-user scenarios: supplier creates transaction, retailer verifies product
3. Database integration: persist blockchain state, restart application, verify state restored

### Test Coverage Goals

- Unit test coverage: Minimum 70% code coverage
- Property test coverage: All 25 correctness properties implemented as property-based tests
- Integration test coverage: All major user workflows covered

## Implementation Notes

### OOP Principles Demonstration

1. **Inheritance**: User class hierarchy (User → SupplyChainManager/Supplier/Retailer)
2. **Polymorphism**: Transaction interface with multiple implementations, DAO interface pattern
3. **Encapsulation**: Private fields with public getters/setters, data hiding in classes
4. **Abstraction**: Abstract User class, Transaction interface, DAO interface

### Collections and Generics

- **List<Block>**: Maintains ordered blockchain
- **List<Transaction>**: Stores transaction history
- **Map<String, Product>**: Fast product lookup
- **Set<String>**: Unique identifiers
- **Queue<Transaction>**: Pending transactions
- **Generic DAO<T>**: Reusable data access pattern

### Multithreading and Synchronization

- **BlockMiningThread**: Background proof-of-work mining
- **Synchronized methods**: Thread-safe access to shared blockchain data
- **ConcurrentHashMap**: Thread-safe user session management
- **ReentrantLock**: Fine-grained locking for critical sections

### JDBC Implementation

- **Connection Pooling**: DatabaseConnectionManager maintains connection pool
- **Prepared Statements**: All queries use PreparedStatement to prevent SQL injection
- **Transaction Management**: Database transactions for atomic operations
- **Exception Handling**: Proper SQLException handling with resource cleanup

### GUI Implementation

- **Java Swing**: Main GUI framework
- **MVC Pattern**: Separation of model, view, and controller
- **Event Handling**: Action listeners for button clicks and user interactions
- **Custom Components**: Reusable dashboard panels for different user types

## Security Considerations

1. **Password Hashing**: User passwords stored as SHA-256 hashes
2. **SQL Injection Prevention**: All database queries use PreparedStatement
3. **Input Validation**: All user inputs validated before processing
4. **Blockchain Integrity**: Cryptographic hashing ensures data immutability
5. **Access Control**: Role-based permissions for different user types

## Performance Considerations

1. **Connection Pooling**: Reuse database connections to reduce overhead
2. **Lazy Loading**: Load blockchain data on-demand rather than all at once
3. **Indexing**: Database indexes on frequently queried fields (product_id, transaction_id)
4. **Caching**: In-memory cache for frequently accessed blockchain data
5. **Asynchronous Mining**: Background threads for proof-of-work to avoid blocking UI
