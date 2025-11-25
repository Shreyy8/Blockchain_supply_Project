package com.supplychain.service;

import com.supplychain.model.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for storing and evaluating regulatory requirements for supply chain compliance.
 * Requirements: 3.1, 3.2, 3.3, 3.4
 */
public class ComplianceValidator {
    
    // Storage for regulatory requirements: requirement ID -> requirement details
    private Map<String, RegulatoryRequirement> requirements;
    
    /**
     * Constructor initializes the requirements storage.
     */
    public ComplianceValidator() {
        this.requirements = new HashMap<>();
    }
    
    /**
     * Stores a regulatory requirement in the system.
     * Requirements: 3.1 - Store regulatory requirements
     * 
     * @param requirementId Unique identifier for the requirement
     * @param description Description of the regulatory requirement
     * @param rule The rule/condition that must be satisfied
     */
    public void storeRequirement(String requirementId, String description, String rule) {
        if (requirementId == null || requirementId.trim().isEmpty()) {
            throw new IllegalArgumentException("Requirement ID cannot be null or empty");
        }
        
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Requirement description cannot be null or empty");
        }
        
        if (rule == null || rule.trim().isEmpty()) {
            throw new IllegalArgumentException("Requirement rule cannot be null or empty");
        }
        
        RegulatoryRequirement requirement = new RegulatoryRequirement(requirementId, description, rule);
        requirements.put(requirementId, requirement);
    }
    
    /**
     * Retrieves a stored regulatory requirement.
     * Requirements: 3.1 - Regulatory rule persistence
     * 
     * @param requirementId The ID of the requirement to retrieve
     * @return The regulatory requirement, or null if not found
     */
    public RegulatoryRequirement getRequirement(String requirementId) {
        return requirements.get(requirementId);
    }
    
    /**
     * Evaluates compliance of supply chain operations against stored regulatory requirements.
     * Requirements: 3.2 - Compare operations against regulatory requirements
     * Requirements: 3.3 - Generate compliance reports with pass/fail status
     * Requirements: 3.4 - Flag non-compliant transactions
     * 
     * @param transactions List of transactions to evaluate
     * @return ComplianceReport containing evaluation results
     */
    public ComplianceReport evaluateCompliance(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions list cannot be null");
        }
        
        ComplianceReport report = new ComplianceReport();
        
        // Evaluate each requirement against the transactions
        for (RegulatoryRequirement requirement : requirements.values()) {
            boolean passed = evaluateRequirement(requirement, transactions);
            report.addRequirementResult(requirement.getRequirementId(), requirement.getDescription(), passed);
            
            // If requirement failed, flag non-compliant transactions
            if (!passed) {
                List<Transaction> nonCompliantTransactions = findNonCompliantTransactions(requirement, transactions);
                for (Transaction transaction : nonCompliantTransactions) {
                    report.flagNonCompliantTransaction(transaction.getTransactionId(), requirement.getRequirementId());
                }
            }
        }
        
        return report;
    }
    
    /**
     * Evaluates a single requirement against a list of transactions.
     * 
     * @param requirement The requirement to evaluate
     * @param transactions The transactions to check
     * @return true if all transactions comply with the requirement, false otherwise
     */
    private boolean evaluateRequirement(RegulatoryRequirement requirement, List<Transaction> transactions) {
        // Simple rule evaluation based on common compliance patterns
        String rule = requirement.getRule().toLowerCase();
        
        for (Transaction transaction : transactions) {
            if (!checkTransactionAgainstRule(transaction, rule)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if a transaction complies with a specific rule.
     * 
     * @param transaction The transaction to check
     * @param rule The rule to evaluate
     * @return true if the transaction complies, false otherwise
     */
    private boolean checkTransactionAgainstRule(Transaction transaction, String rule) {
        Map<String, Object> data = transaction.getTransactionData();
        
        // Example rule patterns (simplified for demonstration)
        if (rule.contains("origin_required")) {
            Object origin = data.get("origin");
            return origin != null && !origin.toString().trim().isEmpty();
        }
        
        if (rule.contains("verification_required")) {
            Object verified = data.get("verified");
            return verified != null && Boolean.parseBoolean(verified.toString());
        }
        
        if (rule.contains("timestamp_required")) {
            return transaction.getTimestamp() != null;
        }
        
        // Default: assume compliance if no specific rule matched
        return true;
    }
    
    /**
     * Finds all transactions that violate a specific requirement.
     * 
     * @param requirement The requirement to check
     * @param transactions The transactions to evaluate
     * @return List of non-compliant transactions
     */
    private List<Transaction> findNonCompliantTransactions(RegulatoryRequirement requirement, List<Transaction> transactions) {
        List<Transaction> nonCompliant = new ArrayList<>();
        String rule = requirement.getRule().toLowerCase();
        
        for (Transaction transaction : transactions) {
            if (!checkTransactionAgainstRule(transaction, rule)) {
                nonCompliant.add(transaction);
            }
        }
        
        return nonCompliant;
    }
    
    /**
     * Gets all stored requirements.
     * 
     * @return Map of all regulatory requirements
     */
    public Map<String, RegulatoryRequirement> getAllRequirements() {
        return new HashMap<>(requirements);
    }
    
    /**
     * Inner class representing a regulatory requirement.
     */
    public static class RegulatoryRequirement {
        private String requirementId;
        private String description;
        private String rule;
        
        public RegulatoryRequirement(String requirementId, String description, String rule) {
            this.requirementId = requirementId;
            this.description = description;
            this.rule = rule;
        }
        
        public String getRequirementId() {
            return requirementId;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getRule() {
            return rule;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            RegulatoryRequirement that = (RegulatoryRequirement) obj;
            return requirementId.equals(that.requirementId) &&
                   description.equals(that.description) &&
                   rule.equals(that.rule);
        }
        
        @Override
        public int hashCode() {
            int result = requirementId.hashCode();
            result = 31 * result + description.hashCode();
            result = 31 * result + rule.hashCode();
            return result;
        }
    }
    
    /**
     * Inner class representing a compliance report.
     */
    public static class ComplianceReport {
        private Map<String, RequirementResult> results;
        private Map<String, List<String>> nonCompliantTransactions; // transactionId -> list of violated requirement IDs
        
        public ComplianceReport() {
            this.results = new HashMap<>();
            this.nonCompliantTransactions = new HashMap<>();
        }
        
        public void addRequirementResult(String requirementId, String description, boolean passed) {
            results.put(requirementId, new RequirementResult(requirementId, description, passed));
        }
        
        public void flagNonCompliantTransaction(String transactionId, String requirementId) {
            nonCompliantTransactions.computeIfAbsent(transactionId, k -> new ArrayList<>()).add(requirementId);
        }
        
        public Map<String, RequirementResult> getResults() {
            return new HashMap<>(results);
        }
        
        public Map<String, List<String>> getNonCompliantTransactions() {
            return new HashMap<>(nonCompliantTransactions);
        }
        
        public boolean hasNonCompliantTransactions() {
            return !nonCompliantTransactions.isEmpty();
        }
    }
    
    /**
     * Inner class representing the result of evaluating a single requirement.
     */
    public static class RequirementResult {
        private String requirementId;
        private String description;
        private boolean passed;
        
        public RequirementResult(String requirementId, String description, boolean passed) {
            this.requirementId = requirementId;
            this.description = description;
            this.passed = passed;
        }
        
        public String getRequirementId() {
            return requirementId;
        }
        
        public String getDescription() {
            return description;
        }
        
        public boolean isPassed() {
            return passed;
        }
    }
}
