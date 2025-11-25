package com.supplychain.service;

import com.supplychain.model.Transaction;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for analyzing supply chain data and generating optimization recommendations.
 * Requirements: 2.1, 2.2, 2.3
 */
public class OptimizationAnalyzer {
    
    /**
     * Analyzes supply chain data and generates actionable recommendations.
     * Requirements: 2.1 - Generate actionable recommendations
     * Requirements: 2.2 - Identify bottlenecks, delays, and inefficiencies
     * Requirements: 2.3 - Provide specific suggestions with expected impact metrics
     * 
     * @param transactions List of transactions to analyze
     * @return List of optimization recommendations
     */
    public List<OptimizationRecommendation> generateRecommendations(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions list cannot be null");
        }
        
        List<OptimizationRecommendation> recommendations = new ArrayList<>();
        
        // Analyze for various optimization opportunities
        recommendations.addAll(analyzeTransitTimes(transactions));
        recommendations.addAll(analyzeTransactionVolume(transactions));
        recommendations.addAll(analyzeSupplierPerformance(transactions));
        
        return recommendations;
    }
    
    /**
     * Analyzes transit times to identify delays.
     * 
     * @param transactions List of transactions to analyze
     * @return List of recommendations related to transit times
     */
    private List<OptimizationRecommendation> analyzeTransitTimes(List<Transaction> transactions) {
        List<OptimizationRecommendation> recommendations = new ArrayList<>();
        
        // Group transactions by product to track transit times
        Map<String, List<Transaction>> productTransactions = groupByProduct(transactions);
        
        for (Map.Entry<String, List<Transaction>> entry : productTransactions.entrySet()) {
            String productId = entry.getKey();
            List<Transaction> productTxns = entry.getValue();
            
            if (productTxns.size() < 2) {
                continue; // Need at least 2 transactions to measure transit time
            }
            
            // Calculate average time between transactions
            long totalHours = 0;
            int intervals = 0;
            
            for (int i = 1; i < productTxns.size(); i++) {
                LocalDateTime prev = productTxns.get(i - 1).getTimestamp();
                LocalDateTime current = productTxns.get(i).getTimestamp();
                
                if (prev != null && current != null) {
                    Duration duration = Duration.between(prev, current);
                    totalHours += duration.toHours();
                    intervals++;
                }
            }
            
            if (intervals > 0) {
                long avgHours = totalHours / intervals;
                
                // If average transit time is high, recommend optimization
                if (avgHours > 48) { // More than 2 days
                    String suggestion = "Reduce transit time for product " + productId + 
                                      " by optimizing logistics routes or using faster shipping methods";
                    String expectedImpact = "Expected reduction: " + (avgHours * 0.3) + " hours (30% improvement)";
                    recommendations.add(new OptimizationRecommendation(
                        "TRANSIT_TIME_OPTIMIZATION",
                        suggestion,
                        expectedImpact
                    ));
                }
            }
        }
        
        return recommendations;
    }
    
    /**
     * Analyzes transaction volume to identify bottlenecks.
     * 
     * @param transactions List of transactions to analyze
     * @return List of recommendations related to transaction volume
     */
    private List<OptimizationRecommendation> analyzeTransactionVolume(List<Transaction> transactions) {
        List<OptimizationRecommendation> recommendations = new ArrayList<>();
        
        // Count transactions by type
        Map<String, Integer> transactionTypeCounts = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            String type = transaction.getTransactionType();
            transactionTypeCounts.put(type, transactionTypeCounts.getOrDefault(type, 0) + 1);
        }
        
        // Identify if certain transaction types are disproportionately high
        int totalTransactions = transactions.size();
        
        for (Map.Entry<String, Integer> entry : transactionTypeCounts.entrySet()) {
            String type = entry.getKey();
            int count = entry.getValue();
            double percentage = (count * 100.0) / totalTransactions;
            
            // If a transaction type represents more than 60% of all transactions, it might be a bottleneck
            if (percentage > 60) {
                String suggestion = "High volume of " + type + " transactions detected (" + 
                                  String.format("%.1f", percentage) + "%). Consider automating or streamlining this process";
                String expectedImpact = "Expected efficiency gain: 25% reduction in processing time";
                recommendations.add(new OptimizationRecommendation(
                    "VOLUME_BOTTLENECK",
                    suggestion,
                    expectedImpact
                ));
            }
        }
        
        return recommendations;
    }
    
    /**
     * Analyzes supplier performance to identify inefficiencies.
     * 
     * @param transactions List of transactions to analyze
     * @return List of recommendations related to supplier performance
     */
    private List<OptimizationRecommendation> analyzeSupplierPerformance(List<Transaction> transactions) {
        List<OptimizationRecommendation> recommendations = new ArrayList<>();
        
        // Count transactions by supplier
        Map<String, Integer> supplierCounts = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            Object fromParty = transaction.getTransactionData().get("fromParty");
            if (fromParty != null) {
                String supplier = fromParty.toString();
                supplierCounts.put(supplier, supplierCounts.getOrDefault(supplier, 0) + 1);
            }
        }
        
        // Identify suppliers with low transaction counts (potential underutilization)
        int avgTransactions = transactions.size() / Math.max(1, supplierCounts.size());
        
        for (Map.Entry<String, Integer> entry : supplierCounts.entrySet()) {
            String supplier = entry.getKey();
            int count = entry.getValue();
            
            // If supplier has significantly fewer transactions than average
            if (count < avgTransactions * 0.5 && supplierCounts.size() > 1) {
                String suggestion = "Supplier " + supplier + " is underutilized. Consider redistributing workload or evaluating supplier capacity";
                String expectedImpact = "Expected improvement: Better resource utilization and 15% cost reduction";
                recommendations.add(new OptimizationRecommendation(
                    "SUPPLIER_UNDERUTILIZATION",
                    suggestion,
                    expectedImpact
                ));
            }
        }
        
        return recommendations;
    }
    
    /**
     * Groups transactions by product ID.
     * 
     * @param transactions List of transactions
     * @return Map of product ID to list of transactions
     */
    private Map<String, List<Transaction>> groupByProduct(List<Transaction> transactions) {
        Map<String, List<Transaction>> grouped = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            Object productId = transaction.getTransactionData().get("productId");
            if (productId != null) {
                String pid = productId.toString();
                grouped.computeIfAbsent(pid, k -> new ArrayList<>()).add(transaction);
            }
        }
        
        return grouped;
    }
    
    /**
     * Inner class representing an optimization recommendation.
     */
    public static class OptimizationRecommendation {
        private String type;
        private String suggestion;
        private String expectedImpact;
        
        public OptimizationRecommendation(String type, String suggestion, String expectedImpact) {
            if (suggestion == null || suggestion.trim().isEmpty()) {
                throw new IllegalArgumentException("Suggestion cannot be null or empty");
            }
            if (expectedImpact == null || expectedImpact.trim().isEmpty()) {
                throw new IllegalArgumentException("Expected impact cannot be null or empty");
            }
            
            this.type = type;
            this.suggestion = suggestion;
            this.expectedImpact = expectedImpact;
        }
        
        public String getType() {
            return type;
        }
        
        public String getSuggestion() {
            return suggestion;
        }
        
        public String getExpectedImpact() {
            return expectedImpact;
        }
        
        @Override
        public String toString() {
            return "OptimizationRecommendation{" +
                   "type='" + type + '\'' +
                   ", suggestion='" + suggestion + '\'' +
                   ", expectedImpact='" + expectedImpact + '\'' +
                   '}';
        }
    }
}
