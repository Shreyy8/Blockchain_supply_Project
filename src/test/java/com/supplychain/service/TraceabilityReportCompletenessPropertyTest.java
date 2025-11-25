package com.supplychain.service;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.model.ProductCreationTransaction;
import com.supplychain.model.ProductStatus;
import com.supplychain.model.ProductTransferTransaction;
import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based test for traceability report completeness.
 * Feature: blockchain-supply-chain, Property 17: Traceability report completeness
 * 
 * Validates: Requirements 6.2
 * 
 * Property: For any product traceability report, it should include origin information,
 * all intermediate transactions, and current status.
 */
public class TraceabilityReportCompletenessPropertyTest {
    
    /**
     * Property: For any product with transactions, the traceability report should
     * include origin information, all intermediate transactions, and current status.
     */
    @Property(tries = 100)
    @Label("Traceability report includes origin, transactions, and current status")
    void traceabilityReportCompleteness(
            @ForAll("productIds") String productId,
            @ForAll("origins") String origin,
            @ForAll("transactionCounts") int transactionCount) {
        
        // Create blockchain manager and service
        BlockchainManager blockchainManager = new BlockchainManager(2);
        ProductTraceabilityService service = new ProductTraceabilityService(blockchainManager);
        
        // Create product creation transaction
        ProductCreationTransaction creation = new ProductCreationTransaction(
                "txn-" + productId + "-0",
                "supplier-1",
                productId,
                "Product " + productId,
                "Test product",
                origin
        );
        blockchainManager.addTransaction(creation);
        
        // Create transfer transactions
        String lastLocation = origin;
        ProductStatus lastStatus = ProductStatus.CREATED;
        
        for (int i = 1; i < transactionCount; i++) {
            String newLocation = "location-" + i;
            ProductStatus newStatus = i == transactionCount - 1 ? ProductStatus.DELIVERED : ProductStatus.IN_TRANSIT;
            
            ProductTransferTransaction transfer = new ProductTransferTransaction(
                    "txn-" + productId + "-" + i,
                    "party-" + (i - 1),
                    "party-" + i,
                    productId,
                    lastLocation,
                    newLocation,
                    newStatus
            );
            blockchainManager.addTransaction(transfer);
            
            lastLocation = newLocation;
            lastStatus = newStatus;
        }
        
        // Mine transactions
        blockchainManager.minePendingTransactions();
        
        // Generate traceability report
        TraceabilityReport report = service.generateTraceabilityReport(productId);
        
        // Verify report completeness
        assertNotNull(report, "Report should not be null");
        assertEquals(productId, report.getProductId(), "Report should have correct product ID");
        
        // Verify origin information is included
        assertNotNull(report.getOrigin(), "Report should include origin information");
        assertEquals(origin, report.getOrigin(), "Report should have correct origin");
        
        // Verify all transactions are included
        assertNotNull(report.getTransactions(), "Report should include transactions");
        assertEquals(transactionCount, report.getTransactions().size(),
                "Report should include all intermediate transactions");
        
        // Verify current status is included
        assertNotNull(report.getCurrentStatus(), "Report should include current status");
        assertEquals(lastStatus.toString(), report.getCurrentStatus(),
                "Report should have correct current status");
        
        // Verify current location is included
        assertNotNull(report.getCurrentLocation(), "Report should include current location");
        assertEquals(lastLocation, report.getCurrentLocation(),
                "Report should have correct current location");
        
        // Verify report is marked as complete
        assertTrue(report.isComplete(), "Report should be marked as complete when all data is present");
    }
    
    /**
     * Property: For any product with no transactions, the report should indicate
     * missing information.
     */
    @Property(tries = 100)
    @Label("Traceability report indicates missing information for non-existent products")
    void traceabilityReportIndicatesMissingInformation(
            @ForAll("productIds") String productId) {
        
        // Create blockchain manager and service with no transactions
        BlockchainManager blockchainManager = new BlockchainManager(2);
        ProductTraceabilityService service = new ProductTraceabilityService(blockchainManager);
        
        // Generate report for non-existent product
        TraceabilityReport report = service.generateTraceabilityReport(productId);
        
        // Verify report indicates incompleteness
        assertNotNull(report, "Report should not be null");
        assertFalse(report.isComplete(), "Report should be marked as incomplete");
        assertFalse(report.getMissingInformation().isEmpty(),
                "Report should indicate what information is missing");
    }
    
    @Provide
    Arbitrary<String> productIds() {
        return Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(5)
                .ofMaxLength(15);
    }
    
    @Provide
    Arbitrary<String> origins() {
        return Arbitraries.strings()
                .alpha()
                .ofMinLength(3)
                .ofMaxLength(20);
    }
    
    @Provide
    Arbitrary<Integer> transactionCounts() {
        return Arbitraries.integers().between(1, 10);
    }
}
