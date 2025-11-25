package com.supplychain.service;

import com.supplychain.model.Transaction;
import com.supplychain.service.ComplianceValidator.ComplianceReport;
import net.jqwik.api.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based test for non-compliant transaction flagging.
 * Feature: blockchain-supply-chain, Property 8: Non-compliant transaction flagging
 * Validates: Requirements 3.4
 * 
 * Property: For any transaction that violates stored regulatory requirements, 
 * the system should flag it as non-compliant and generate an alert.
 */
public class NonCompliantFlaggingPropertyTest {
    
    @Property(tries = 100)
    @Label("Non-compliant transaction flagging")
    void nonCompliantTransactionFlagging(
            @ForAll("transactionsWithViolations") TransactionSet transactionSet) {
        
        // Create a new ComplianceValidator
        ComplianceValidator validator = new ComplianceValidator();
        
        // Store a requirement that will be violated
        validator.storeRequirement("REQ001", "Origin must be specified", "origin_required");
        
        // Evaluate compliance
        ComplianceReport report = validator.evaluateCompliance(transactionSet.transactions);
        
        // If there are transactions without origin, they should be flagged
        Map<String, List<String>> nonCompliantTransactions = report.getNonCompliantTransactions();
        
        if (transactionSet.expectedViolations > 0) {
            // Verify that non-compliant transactions are flagged
            assertTrue(report.hasNonCompliantTransactions(), 
                      "Report should indicate non-compliant transactions exist");
            
            // Verify that the violating transactions are in the flagged list
            for (String violatingTxId : transactionSet.violatingTransactionIds) {
                assertTrue(nonCompliantTransactions.containsKey(violatingTxId),
                          "Transaction " + violatingTxId + " should be flagged as non-compliant");
                
                List<String> violatedRequirements = nonCompliantTransactions.get(violatingTxId);
                assertNotNull(violatedRequirements, "Violated requirements list should not be null");
                assertTrue(violatedRequirements.contains("REQ001"),
                          "Transaction should be flagged for violating REQ001");
            }
        } else {
            // If no violations expected, no transactions should be flagged
            assertFalse(report.hasNonCompliantTransactions(),
                       "Report should not indicate non-compliant transactions when all comply");
        }
    }
    
    @Provide
    Arbitrary<TransactionSet> transactionsWithViolations() {
        return Arbitraries.integers().between(0, 5).flatMap(violationCount -> {
            int compliantCount = Arbitraries.integers().between(0, 5).sample();
            
            List<Transaction> transactions = new ArrayList<>();
            Set<String> violatingIds = new HashSet<>();
            
            // Add compliant transactions (with origin)
            for (int i = 0; i < compliantCount; i++) {
                String txId = "compliant-" + i;
                transactions.add(createTransaction(txId, true));
            }
            
            // Add non-compliant transactions (without origin)
            for (int i = 0; i < violationCount; i++) {
                String txId = "violating-" + i;
                transactions.add(createTransaction(txId, false));
                violatingIds.add(txId);
            }
            
            return Arbitraries.just(new TransactionSet(transactions, violationCount, violatingIds));
        });
    }
    
    private Transaction createTransaction(String id, boolean hasOrigin) {
        return new Transaction() {
            @Override
            public String getTransactionId() {
                return id;
            }
            
            @Override
            public String getTransactionType() {
                return "PRODUCT_CREATION";
            }
            
            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.now();
            }
            
            @Override
            public Map<String, Object> getTransactionData() {
                Map<String, Object> data = new HashMap<>();
                if (hasOrigin) {
                    data.put("origin", "Factory A");
                } else {
                    // No origin - this violates the requirement
                    data.put("productId", "P123");
                }
                return data;
            }
            
            @Override
            public boolean validate() {
                return true;
            }
        };
    }
    
    static class TransactionSet {
        List<Transaction> transactions;
        int expectedViolations;
        Set<String> violatingTransactionIds;
        
        TransactionSet(List<Transaction> transactions, int expectedViolations, Set<String> violatingTransactionIds) {
            this.transactions = transactions;
            this.expectedViolations = expectedViolations;
            this.violatingTransactionIds = violatingTransactionIds;
        }
    }
}
