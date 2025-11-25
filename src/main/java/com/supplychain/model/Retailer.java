package com.supplychain.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Concrete user class representing a Retailer.
 * Retailers access blockchain data to verify product authenticity and traceability.
 * 
 * Requirements: 9.1, 6.1
 */
public class Retailer extends User {
    
    /**
     * Default constructor
     */
    public Retailer() {
        super();
        this.role = UserRole.RETAILER;
    }
    
    /**
     * Constructor with all fields
     * 
     * @param userId Unique identifier for the user
     * @param username Username for login
     * @param password Password (should be hashed)
     * @param email User's email address
     */
    public Retailer(String userId, String username, String password, String email) {
        super(userId, username, password, email, UserRole.RETAILER);
    }
    
    /**
     * Returns the dashboard view identifier for Retailers.
     * 
     * @return Dashboard view identifier
     */
    @Override
    public String getDashboardView() {
        return "RETAILER_DASHBOARD";
    }
    
    /**
     * Returns the permissions for Retailers.
     * Retailers can trace product history, verify product authenticity,
     * and view traceability reports.
     * 
     * @return Set of permission strings
     */
    @Override
    public Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("TRACE_PRODUCT_HISTORY");
        permissions.add("VERIFY_PRODUCT_AUTHENTICITY");
        permissions.add("VIEW_TRACEABILITY_REPORT");
        permissions.add("VIEW_PRODUCT_ORIGIN");
        permissions.add("VIEW_PRODUCT_STATUS");
        return permissions;
    }
    
    @Override
    public String toString() {
        return "Retailer{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
