package com.supplychain.service;

import com.supplychain.service.ComplianceValidator.RegulatoryRequirement;
import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based test for regulatory rule persistence round-trip.
 * Feature: blockchain-supply-chain, Property 6: Regulatory rule persistence round-trip
 * Validates: Requirements 3.1
 * 
 * Property: For any regulatory requirement stored in the system, 
 * retrieving it should return the same rule with all details intact.
 */
public class RegulatoryRulePersistencePropertyTest {
    
    @Property(tries = 100)
    @Label("Regulatory rule persistence round-trip")
    void regulatoryRulePersistenceRoundTrip(
            @ForAll("requirementIds") String requirementId,
            @ForAll("descriptions") String description,
            @ForAll("rules") String rule) {
        
        // Create a new ComplianceValidator
        ComplianceValidator validator = new ComplianceValidator();
        
        // Store the requirement
        validator.storeRequirement(requirementId, description, rule);
        
        // Retrieve the requirement
        RegulatoryRequirement retrieved = validator.getRequirement(requirementId);
        
        // Verify the retrieved requirement matches the original
        assertNotNull(retrieved, "Retrieved requirement should not be null");
        assertEquals(requirementId, retrieved.getRequirementId(), 
                    "Requirement ID should match");
        assertEquals(description, retrieved.getDescription(), 
                    "Description should match");
        assertEquals(rule, retrieved.getRule(), 
                    "Rule should match");
    }
    
    @Provide
    Arbitrary<String> requirementIds() {
        return Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(1)
                .ofMaxLength(20);
    }
    
    @Provide
    Arbitrary<String> descriptions() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withCharRange('0', '9')
                .withChars(' ', ',', '.', '-')
                .ofMinLength(10)
                .ofMaxLength(100)
                .filter(s -> s.trim().length() > 0); // Exclude whitespace-only strings
    }
    
    @Provide
    Arbitrary<String> rules() {
        return Arbitraries.of(
                "origin_required",
                "verification_required",
                "timestamp_required",
                "origin_required AND verification_required",
                "timestamp_required AND origin_required"
        );
    }
}
