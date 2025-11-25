package com.supplychain.service;

import com.supplychain.model.Transaction;
import com.supplychain.service.OptimizationAnalyzer.OptimizationRecommendation;
import net.jqwik.api.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based test for recommendation format completeness.
 * Feature: blockchain-supply-chain, Property 5: Recommendation format completeness
 * Validates: Requirements 2.3
 * 
 * Property: For any generated optimization recommendation, it should contain 
 * specific suggestions and expected impact metrics.
 */
public class RecommendationFormatPropertyTest {
    
    @Property(tries = 100)
    @Label("Recommendation format completeness")
    void recommendationFormatCompleteness(
            @ForAll("transactionLists") List<Transaction> transactions) {
        
        // Create a new OptimizationAnalyzer
        OptimizationAnalyzer analyzer = new OptimizationAnalyzer();
        
        // Generate recommendations
        List<OptimizationRecommendation> recommendations = analyzer.generateRecommendations(transactions);
        
        // Verify each recommendation has the required format
        for (OptimizationRecommendation recommendation : recommendations) {
            // Verify suggestion is present and not empty
            assertNotNull(recommendation.getSuggestion(), 
                         "Recommendation should have a suggestion");
            assertFalse(recommendation.getSuggestion().trim().isEmpty(), 
                       "Suggestion should not be empty");
            
            // Verify expected impact is present and not empty
            assertNotNull(recommendation.getExpectedImpact(), 
                         "Recommendation should have expected impact metrics");
            assertFalse(recommendation.getExpectedImpact().trim().isEmpty(), 
                       "Expected impact should not be empty");
            
            // Verify the suggestion is specific (contains actionable information)
            assertTrue(recommendation.getSuggestion().length() > 10,
                      "Suggestion should be specific and detailed");
            
            // Verify the expected impact contains metrics or measurable information
            assertTrue(recommendation.getExpectedImpact().length() > 5,
                      "Expected impact should contain measurable information");
        }
    }
    
    @Provide
    Arbitrary<List<Transaction>> transactionLists() {
        return Arbitraries.integers().between(0, 20).flatMap(count -> {
            List<Transaction> transactions = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                // Create transactions with varying characteristics to trigger different recommendations
                String txId = "tx-" + i;
                String type = i % 3 == 0 ? "PRODUCT_CREATION" : 
                             i % 3 == 1 ? "PRODUCT_TRANSFER" : "PRODUCT_VERIFICATION";
                String supplier = "supplier-" + (i % 3);
                LocalDateTime timestamp = LocalDateTime.now().minusHours(i * 10);
                
                transactions.add(createTransaction(txId, type, supplier, timestamp));
            }
            
            return Arbitraries.just(transactions);
        });
    }
    
    private Transaction createTransaction(String id, String type, String supplier, LocalDateTime timestamp) {
        return new Transaction() {
            @Override
            public String getTransactionId() {
                return id;
            }
            
            @Override
            public String getTransactionType() {
                return type;
            }
            
            @Override
            public LocalDateTime getTimestamp() {
                return timestamp;
            }
            
            @Override
            public Map<String, Object> getTransactionData() {
                Map<String, Object> data = new HashMap<>();
                data.put("productId", "P" + (id.hashCode() % 5));
                data.put("fromParty", supplier);
                data.put("toParty", "retailer-" + (id.hashCode() % 2));
                data.put("origin", "Factory A");
                return data;
            }
            
            @Override
            public boolean validate() {
                return true;
            }
        };
    }
}
