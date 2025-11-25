package com.supplychain.service;

import com.supplychain.model.Transaction;
import com.supplychain.service.ComplianceValidator.ComplianceReport;
import com.supplychain.service.ComplianceValidator.RequirementResult;
import net.jqwik.api.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based test for compliance report completeness.
 * Feature: blockchain-supply-chain, Property 7: Compliance report completeness
 * Validates: Requirements 3.3
 * 
 * Property: For any compliance report generated, it should include a pass or fail status 
 * for each regulatory requirement being evaluated.
 */
public class ComplianceReportCompletenessPropertyTest {
    
    @Property(tries = 100)
    @Label("Compliance report completeness")
    void complianceReportCompleteness(
            @ForAll("requirementLists") List<RequirementData> requirements,
            @ForAll("transactionLists") List<Transaction> transactions) {
        
        // Create a new ComplianceValidator
        ComplianceValidator validator = new ComplianceValidator();
        
        // Store all requirements
        for (RequirementData req : requirements) {
            validator.storeRequirement(req.id, req.description, req.rule);
        }
        
        // Evaluate compliance
        ComplianceReport report = validator.evaluateCompliance(transactions);
        
        // Verify the report contains a result for each requirement
        Map<String, RequirementResult> results = report.getResults();
        assertEquals(requirements.size(), results.size(), 
                    "Report should contain a result for each requirement");
        
        // Verify each requirement has a pass/fail status
        for (RequirementData req : requirements) {
            assertTrue(results.containsKey(req.id), 
                      "Report should contain result for requirement " + req.id);
            
            RequirementResult result = results.get(req.id);
            assertNotNull(result, "Result should not be null");
            assertEquals(req.id, result.getRequirementId(), "Requirement ID should match");
            assertEquals(req.description, result.getDescription(), "Description should match");
            
            // Verify the result has a boolean pass/fail status (not null)
            assertNotNull(result.isPassed(), "Pass/fail status should not be null");
        }
    }
    
    @Provide
    Arbitrary<List<RequirementData>> requirementLists() {
        Arbitrary<RequirementData> requirement = Combinators.combine(
                Arbitraries.strings().alpha().numeric().ofMinLength(1).ofMaxLength(10),
                Arbitraries.strings().alpha().withChars(' ').ofMinLength(5).ofMaxLength(30).filter(s -> s.trim().length() > 0),
                Arbitraries.of("origin_required", "verification_required", "timestamp_required")
        ).as((id, desc, rule) -> new RequirementData(id, desc, rule));
        
        return requirement.list().ofMinSize(1).ofMaxSize(5).uniqueElements(r -> r.id);
    }
    
    @Provide
    Arbitrary<List<Transaction>> transactionLists() {
        return Arbitraries.of(
                createMockTransaction("tx1", "PRODUCT_CREATION", true, true),
                createMockTransaction("tx2", "PRODUCT_TRANSFER", true, false),
                createMockTransaction("tx3", "PRODUCT_VERIFICATION", false, true)
        ).list().ofMinSize(0).ofMaxSize(10);
    }
    
    private Transaction createMockTransaction(String id, String type, boolean hasOrigin, boolean isVerified) {
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
                return LocalDateTime.now();
            }
            
            @Override
            public Map<String, Object> getTransactionData() {
                Map<String, Object> data = new HashMap<>();
                if (hasOrigin) {
                    data.put("origin", "Factory A");
                }
                data.put("verified", isVerified);
                return data;
            }
            
            @Override
            public boolean validate() {
                return true;
            }
        };
    }
    
    static class RequirementData {
        String id;
        String description;
        String rule;
        
        RequirementData(String id, String description, String rule) {
            this.id = id;
            this.description = description;
            this.rule = rule;
        }
    }
}
