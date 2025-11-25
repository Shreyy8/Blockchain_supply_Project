package com.supplychain.model;

import net.jqwik.api.*;
import net.jqwik.api.constraints.NotBlank;
import net.jqwik.api.constraints.NotEmpty;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for transaction validation.
 * 
 * Feature: blockchain-supply-chain, Property 9: Transaction validation rejects invalid data
 * Validates: Requirements 4.1
 * 
 * This test verifies that for any transaction with incomplete or malformed data,
 * the validation process should reject it.
 */
public class TransactionValidationPropertyTest {
    
    /**
     * Property: Valid ProductCreationTransaction should pass validation
     * For any valid ProductCreationTransaction with all required fields,
     * validation should return true.
     */
    @Property(tries = 100)
    @Label("Valid ProductCreationTransaction passes validation")
    void validProductCreationTransactionPassesValidation(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String supplierId,
            @ForAll @NotBlank String productId,
            @ForAll @NotBlank String productName,
            @ForAll String productDescription,
            @ForAll @NotBlank String origin) {
        
        ProductCreationTransaction transaction = new ProductCreationTransaction(
            transactionId.trim(),
            supplierId.trim(),
            productId.trim(),
            productName.trim(),
            productDescription != null ? productDescription : "",
            origin.trim()
        );
        
        assertTrue(transaction.validate(), 
            "Valid ProductCreationTransaction should pass validation");
    }
    
    /**
     * Property: ProductCreationTransaction with null transactionId should fail validation
     */
    @Property(tries = 100)
    @Label("ProductCreationTransaction with null transactionId fails validation")
    void productCreationTransactionWithNullTransactionIdFailsValidation(
            @ForAll @NotBlank String supplierId,
            @ForAll @NotBlank String productId,
            @ForAll @NotBlank String productName,
            @ForAll String productDescription,
            @ForAll @NotBlank String origin) {
        
        ProductCreationTransaction transaction = new ProductCreationTransaction(
            null,
            supplierId.trim(),
            productId.trim(),
            productName.trim(),
            productDescription != null ? productDescription : "",
            origin.trim()
        );
        
        assertFalse(transaction.validate(), 
            "ProductCreationTransaction with null transactionId should fail validation");
    }
    
    /**
     * Property: ProductCreationTransaction with empty transactionId should fail validation
     */
    @Property(tries = 100)
    @Label("ProductCreationTransaction with empty transactionId fails validation")
    void productCreationTransactionWithEmptyTransactionIdFailsValidation(
            @ForAll("whitespaceStrings") String transactionId,
            @ForAll @NotBlank String supplierId,
            @ForAll @NotBlank String productId,
            @ForAll @NotBlank String productName,
            @ForAll String productDescription,
            @ForAll @NotBlank String origin) {
        
        ProductCreationTransaction transaction = new ProductCreationTransaction(
            transactionId,
            supplierId.trim(),
            productId.trim(),
            productName.trim(),
            productDescription != null ? productDescription : "",
            origin.trim()
        );
        
        assertFalse(transaction.validate(), 
            "ProductCreationTransaction with empty/whitespace transactionId should fail validation");
    }
    
    /**
     * Property: ProductCreationTransaction with null supplierId should fail validation
     */
    @Property(tries = 100)
    @Label("ProductCreationTransaction with null supplierId fails validation")
    void productCreationTransactionWithNullSupplierIdFailsValidation(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String productId,
            @ForAll @NotBlank String productName,
            @ForAll String productDescription,
            @ForAll @NotBlank String origin) {
        
        ProductCreationTransaction transaction = new ProductCreationTransaction(
            transactionId.trim(),
            null,
            productId.trim(),
            productName.trim(),
            productDescription != null ? productDescription : "",
            origin.trim()
        );
        
        assertFalse(transaction.validate(), 
            "ProductCreationTransaction with null supplierId should fail validation");
    }
    
    /**
     * Property: Valid ProductTransferTransaction should pass validation
     */
    @Property(tries = 100)
    @Label("Valid ProductTransferTransaction passes validation")
    void validProductTransferTransactionPassesValidation(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String fromParty,
            @ForAll @NotBlank String toParty,
            @ForAll @NotBlank String productId,
            @ForAll @NotBlank String fromLocation,
            @ForAll @NotBlank String toLocation,
            @ForAll ProductStatus status) {
        
        ProductTransferTransaction transaction = new ProductTransferTransaction(
            transactionId.trim(),
            fromParty.trim(),
            toParty.trim(),
            productId.trim(),
            fromLocation.trim(),
            toLocation.trim(),
            status
        );
        
        assertTrue(transaction.validate(), 
            "Valid ProductTransferTransaction should pass validation");
    }
    
    /**
     * Property: ProductTransferTransaction with null status should fail validation
     */
    @Property(tries = 100)
    @Label("ProductTransferTransaction with null status fails validation")
    void productTransferTransactionWithNullStatusFailsValidation(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String fromParty,
            @ForAll @NotBlank String toParty,
            @ForAll @NotBlank String productId,
            @ForAll @NotBlank String fromLocation,
            @ForAll @NotBlank String toLocation) {
        
        ProductTransferTransaction transaction = new ProductTransferTransaction(
            transactionId.trim(),
            fromParty.trim(),
            toParty.trim(),
            productId.trim(),
            fromLocation.trim(),
            toLocation.trim(),
            null
        );
        
        assertFalse(transaction.validate(), 
            "ProductTransferTransaction with null status should fail validation");
    }
    
    /**
     * Property: ProductTransferTransaction with null productId should fail validation
     */
    @Property(tries = 100)
    @Label("ProductTransferTransaction with null productId fails validation")
    void productTransferTransactionWithNullProductIdFailsValidation(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String fromParty,
            @ForAll @NotBlank String toParty,
            @ForAll @NotBlank String fromLocation,
            @ForAll @NotBlank String toLocation,
            @ForAll ProductStatus status) {
        
        ProductTransferTransaction transaction = new ProductTransferTransaction(
            transactionId.trim(),
            fromParty.trim(),
            toParty.trim(),
            null,
            fromLocation.trim(),
            toLocation.trim(),
            status
        );
        
        assertFalse(transaction.validate(), 
            "ProductTransferTransaction with null productId should fail validation");
    }
    
    /**
     * Property: Valid ProductVerificationTransaction should pass validation
     */
    @Property(tries = 100)
    @Label("Valid ProductVerificationTransaction passes validation")
    void validProductVerificationTransactionPassesValidation(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String verifierId,
            @ForAll @NotBlank String productId,
            @ForAll boolean verificationResult,
            @ForAll String verificationNotes) {
        
        ProductVerificationTransaction transaction = new ProductVerificationTransaction(
            transactionId.trim(),
            verifierId.trim(),
            productId.trim(),
            verificationResult,
            verificationNotes != null ? verificationNotes : ""
        );
        
        assertTrue(transaction.validate(), 
            "Valid ProductVerificationTransaction should pass validation");
    }
    
    /**
     * Property: ProductVerificationTransaction with null verifierId should fail validation
     */
    @Property(tries = 100)
    @Label("ProductVerificationTransaction with null verifierId fails validation")
    void productVerificationTransactionWithNullVerifierIdFailsValidation(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String productId,
            @ForAll boolean verificationResult,
            @ForAll String verificationNotes) {
        
        ProductVerificationTransaction transaction = new ProductVerificationTransaction(
            transactionId.trim(),
            null,
            productId.trim(),
            verificationResult,
            verificationNotes != null ? verificationNotes : ""
        );
        
        assertFalse(transaction.validate(), 
            "ProductVerificationTransaction with null verifierId should fail validation");
    }
    
    /**
     * Property: ProductVerificationTransaction with null verificationNotes should fail validation
     */
    @Property(tries = 100)
    @Label("ProductVerificationTransaction with null verificationNotes fails validation")
    void productVerificationTransactionWithNullNotesFailsValidation(
            @ForAll @NotBlank String transactionId,
            @ForAll @NotBlank String verifierId,
            @ForAll @NotBlank String productId,
            @ForAll boolean verificationResult) {
        
        ProductVerificationTransaction transaction = new ProductVerificationTransaction(
            transactionId.trim(),
            verifierId.trim(),
            productId.trim(),
            verificationResult,
            null
        );
        
        assertFalse(transaction.validate(), 
            "ProductVerificationTransaction with null verificationNotes should fail validation");
    }
    
    /**
     * Provides whitespace-only strings for testing
     */
    @Provide
    Arbitrary<String> whitespaceStrings() {
        return Arbitraries.of("", " ", "  ", "   ", "\t", "\n", " \t\n ");
    }
}
