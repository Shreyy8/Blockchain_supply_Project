package com.supplychain.dao;

import com.supplychain.exception.ConnectionException;
import com.supplychain.model.*;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

import java.sql.SQLException;

/**
 * Property-based test for database persistence round-trip.
 * Feature: blockchain-supply-chain, Property 25: Database persistence round-trip
 * Validates: Requirements 11.2
 */
public class DatabasePersistenceRoundTripPropertyTest {
    
    /**
     * Property 25: Database persistence round-trip for ProductCreationTransaction
     * For any transaction persisted to the database, retrieving it should return 
     * the same transaction data.
     */
    @Property(tries = 100)
    @Label("Database persistence round-trip for ProductCreationTransaction")
    void productCreationTransactionRoundTrip(
        @ForAll("validProductCreationTransaction") ProductCreationTransaction transaction
    ) {
        TransactionDAO transactionDAO = null;
        ProductDAO productDAO = null;
        
        try {
            transactionDAO = new TransactionDAO();
            productDAO = new ProductDAO();
            
            // Create a product first (required by foreign key constraint)
            Product product = new Product(
                transaction.getProductId(),
                transaction.getProductName(),
                transaction.getProductDescription(),
                transaction.getOrigin()
            );
            productDAO.save(product);
            
            // Save the transaction
            transactionDAO.save(transaction);
            
            // Retrieve the transaction
            Transaction retrieved = transactionDAO.findById(transaction.getTransactionId());
            
            // Verify the transaction was retrieved
            Assertions.assertNotNull(retrieved, 
                "Retrieved transaction should not be null");
            
            // Verify it's the same type
            Assertions.assertTrue(retrieved instanceof ProductCreationTransaction,
                "Retrieved transaction should be ProductCreationTransaction");
            
            ProductCreationTransaction retrievedPCT = (ProductCreationTransaction) retrieved;
            
            // Verify all fields match
            Assertions.assertEquals(transaction.getTransactionId(), retrievedPCT.getTransactionId(),
                "Transaction ID should match");
            Assertions.assertEquals(transaction.getTransactionType(), retrievedPCT.getTransactionType(),
                "Transaction type should match");
            Assertions.assertEquals(transaction.getSupplierId(), retrievedPCT.getSupplierId(),
                "Supplier ID should match");
            Assertions.assertEquals(transaction.getProductId(), retrievedPCT.getProductId(),
                "Product ID should match");
            Assertions.assertEquals(transaction.getProductName(), retrievedPCT.getProductName(),
                "Product name should match");
            Assertions.assertEquals(transaction.getProductDescription(), retrievedPCT.getProductDescription(),
                "Product description should match");
            Assertions.assertEquals(transaction.getOrigin(), retrievedPCT.getOrigin(),
                "Origin should match");
            
            // Clean up - delete the transaction and product
            transactionDAO.delete(transaction.getTransactionId());
            productDAO.delete(product.getProductId());
            
        } catch (ConnectionException | SQLException e) {
            Assertions.fail("Database operation failed: " + e.getMessage());
        }
    }
    
    /**
     * Property 25: Database persistence round-trip for products
     * For any product persisted to the database, retrieving it should return 
     * the same product data.
     */
    @Property(tries = 100)
    @Label("Database persistence round-trip for products")
    void productDatabaseRoundTrip(
        @ForAll("validProduct") Product product
    ) {
        ProductDAO dao = null;
        
        try {
            dao = new ProductDAO();
            
            // Save the product
            dao.save(product);
            
            // Retrieve the product
            Product retrieved = dao.findById(product.getProductId());
            
            // Verify the product was retrieved
            Assertions.assertNotNull(retrieved, 
                "Retrieved product should not be null");
            
            // Verify all fields match
            Assertions.assertEquals(product.getProductId(), retrieved.getProductId(),
                "Product ID should match");
            Assertions.assertEquals(product.getName(), retrieved.getName(),
                "Product name should match");
            Assertions.assertEquals(product.getDescription(), retrieved.getDescription(),
                "Product description should match");
            Assertions.assertEquals(product.getOrigin(), retrieved.getOrigin(),
                "Product origin should match");
            Assertions.assertEquals(product.getCurrentLocation(), retrieved.getCurrentLocation(),
                "Product current location should match");
            Assertions.assertEquals(product.getStatus(), retrieved.getStatus(),
                "Product status should match");
            
            // Clean up - delete the product
            dao.delete(product.getProductId());
            
        } catch (ConnectionException | SQLException e) {
            Assertions.fail("Database operation failed: " + e.getMessage());
        }
    }
    
    /**
     * Property 25: Database persistence round-trip for users
     * For any user persisted to the database, retrieving it should return 
     * the same user data.
     */
    @Property(tries = 100)
    @Label("Database persistence round-trip for users")
    void userDatabaseRoundTrip(
        @ForAll("validSupplier") Supplier user
    ) {
        UserDAO dao = null;
        
        try {
            dao = new UserDAO();
            
            // Save the user
            dao.save(user);
            
            // Retrieve the user
            User retrieved = dao.findById(user.getUserId());
            
            // Verify the user was retrieved
            Assertions.assertNotNull(retrieved, 
                "Retrieved user should not be null");
            
            // Verify it's the same type
            Assertions.assertTrue(retrieved instanceof Supplier,
                "Retrieved user should be Supplier");
            
            // Verify all fields match
            Assertions.assertEquals(user.getUserId(), retrieved.getUserId(),
                "User ID should match");
            Assertions.assertEquals(user.getUsername(), retrieved.getUsername(),
                "Username should match");
            Assertions.assertEquals(user.getPassword(), retrieved.getPassword(),
                "Password should match");
            Assertions.assertEquals(user.getEmail(), retrieved.getEmail(),
                "Email should match");
            Assertions.assertEquals(user.getRole(), retrieved.getRole(),
                "Role should match");
            
            // Clean up - delete the user
            dao.delete(user.getUserId());
            
        } catch (ConnectionException | SQLException e) {
            Assertions.fail("Database operation failed: " + e.getMessage());
        }
    }
    
    /**
     * Property 25: Database persistence round-trip for ProductTransferTransaction
     */
    @Property(tries = 100)
    @Label("Database persistence round-trip for ProductTransferTransaction")
    void productTransferTransactionRoundTrip(
        @ForAll("validProductTransferTransaction") ProductTransferTransaction transaction
    ) {
        TransactionDAO transactionDAO = null;
        ProductDAO productDAO = null;
        
        try {
            transactionDAO = new TransactionDAO();
            productDAO = new ProductDAO();
            
            // Create the product first (required by foreign key constraint)
            Product product = new Product(
                transaction.getProductId(),
                "Test Product",
                "Test Description",
                transaction.getFromLocation()
            );
            productDAO.save(product);
            
            // Save the transaction
            transactionDAO.save(transaction);
            
            // Retrieve the transaction
            Transaction retrieved = transactionDAO.findById(transaction.getTransactionId());
            
            // Verify the transaction was retrieved
            Assertions.assertNotNull(retrieved, 
                "Retrieved transaction should not be null");
            
            // Verify it's the same type
            Assertions.assertTrue(retrieved instanceof ProductTransferTransaction,
                "Retrieved transaction should be ProductTransferTransaction");
            
            ProductTransferTransaction retrievedPTT = (ProductTransferTransaction) retrieved;
            
            // Verify all fields match
            Assertions.assertEquals(transaction.getTransactionId(), retrievedPTT.getTransactionId(),
                "Transaction ID should match");
            Assertions.assertEquals(transaction.getTransactionType(), retrievedPTT.getTransactionType(),
                "Transaction type should match");
            Assertions.assertEquals(transaction.getFromParty(), retrievedPTT.getFromParty(),
                "From party should match");
            Assertions.assertEquals(transaction.getToParty(), retrievedPTT.getToParty(),
                "To party should match");
            Assertions.assertEquals(transaction.getProductId(), retrievedPTT.getProductId(),
                "Product ID should match");
            Assertions.assertEquals(transaction.getFromLocation(), retrievedPTT.getFromLocation(),
                "From location should match");
            Assertions.assertEquals(transaction.getToLocation(), retrievedPTT.getToLocation(),
                "To location should match");
            Assertions.assertEquals(transaction.getNewStatus(), retrievedPTT.getNewStatus(),
                "New status should match");
            
            // Clean up - delete the transaction and product
            transactionDAO.delete(transaction.getTransactionId());
            productDAO.delete(product.getProductId());
            
        } catch (ConnectionException | SQLException e) {
            Assertions.fail("Database operation failed: " + e.getMessage());
        }
    }
    
    /**
     * Property 25: Database persistence round-trip for ProductVerificationTransaction
     */
    @Property(tries = 100)
    @Label("Database persistence round-trip for ProductVerificationTransaction")
    void productVerificationTransactionRoundTrip(
        @ForAll("validProductVerificationTransaction") ProductVerificationTransaction transaction
    ) {
        TransactionDAO transactionDAO = null;
        ProductDAO productDAO = null;
        
        try {
            transactionDAO = new TransactionDAO();
            productDAO = new ProductDAO();
            
            // Create the product first (required by foreign key constraint)
            Product product = new Product(
                transaction.getProductId(),
                "Test Product",
                "Test Description",
                "Test Origin"
            );
            productDAO.save(product);
            
            // Save the transaction
            transactionDAO.save(transaction);
            
            // Retrieve the transaction
            Transaction retrieved = transactionDAO.findById(transaction.getTransactionId());
            
            // Verify the transaction was retrieved
            Assertions.assertNotNull(retrieved, 
                "Retrieved transaction should not be null");
            
            // Verify it's the same type
            Assertions.assertTrue(retrieved instanceof ProductVerificationTransaction,
                "Retrieved transaction should be ProductVerificationTransaction");
            
            ProductVerificationTransaction retrievedPVT = (ProductVerificationTransaction) retrieved;
            
            // Verify all fields match
            Assertions.assertEquals(transaction.getTransactionId(), retrievedPVT.getTransactionId(),
                "Transaction ID should match");
            Assertions.assertEquals(transaction.getTransactionType(), retrievedPVT.getTransactionType(),
                "Transaction type should match");
            Assertions.assertEquals(transaction.getVerifierId(), retrievedPVT.getVerifierId(),
                "Verifier ID should match");
            Assertions.assertEquals(transaction.getProductId(), retrievedPVT.getProductId(),
                "Product ID should match");
            Assertions.assertEquals(transaction.isVerificationResult(), retrievedPVT.isVerificationResult(),
                "Verification result should match");
            Assertions.assertEquals(transaction.getVerificationNotes(), retrievedPVT.getVerificationNotes(),
                "Verification notes should match");
            
            // Clean up - delete the transaction and product
            transactionDAO.delete(transaction.getTransactionId());
            productDAO.delete(product.getProductId());
            
        } catch (ConnectionException | SQLException e) {
            Assertions.fail("Database operation failed: " + e.getMessage());
        }
    }
    
    /**
     * Provides arbitrary valid ProductCreationTransaction instances for testing.
     */
    @Provide
    Arbitrary<ProductCreationTransaction> validProductCreationTransaction() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50),
            Arbitraries.strings().alpha().ofMinLength(0).ofMaxLength(100),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20)
        ).as((txId, supplierId, productId, productName, productDesc, origin) -> 
            new ProductCreationTransaction(
                txId,
                supplierId,
                productId,
                productName,
                productDesc,
                origin
            )
        );
    }
    
    /**
     * Provides arbitrary valid ProductTransferTransaction instances for testing.
     */
    @Provide
    Arbitrary<ProductTransferTransaction> validProductTransferTransaction() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.of(ProductStatus.class)
        ).as((txId, fromParty, toParty, productId, fromLoc, toLoc, status) -> 
            new ProductTransferTransaction(
                txId,
                fromParty,
                toParty,
                productId,
                fromLoc,
                toLoc,
                status
            )
        );
    }
    
    /**
     * Provides arbitrary valid ProductVerificationTransaction instances for testing.
     */
    @Provide
    Arbitrary<ProductVerificationTransaction> validProductVerificationTransaction() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.of(true, false),
            Arbitraries.strings().alpha().ofMinLength(0).ofMaxLength(100)
        ).as((txId, verifierId, productId, result, notes) -> 
            new ProductVerificationTransaction(
                txId,
                verifierId,
                productId,
                result,
                notes
            )
        );
    }
    
    /**
     * Provides arbitrary valid Product instances for testing.
     */
    @Provide
    Arbitrary<Product> validProduct() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50),
            Arbitraries.strings().alpha().ofMinLength(0).ofMaxLength(100),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20)
        ).as((productId, name, description, origin) -> 
            new Product(productId, name, description, origin)
        );
    }
    
    /**
     * Provides arbitrary valid Supplier instances for testing.
     */
    @Provide
    Arbitrary<Supplier> validSupplier() {
        return Combinators.combine(
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(8).ofMaxLength(20),
            Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(30).map(s -> s + "@test.com")
        ).as((userId, username, password, email) -> 
            new Supplier(userId, username, password, email)
        );
    }
}
