package com.supplychain.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Concrete user class representing a Supply Chain Manager.
 * Supply Chain Managers oversee supply chain operations and monitor blockchain records.
 * 
 * Requirements: 9.1, 1.1
 */
public class SupplyChainManager extends User {
    
    /**
     * Default constructor
     */
    public SupplyChainManager() {
        super();
        this.role = UserRole.MANAGER;
    }
    
    /**
     * Constructor with all fields
     * 
     * @param userId Unique identifier for the user
     * @param username Username for login
     * @param password Password (should be hashed)
     * @param email User's email address
     */
    public SupplyChainManager(String userId, String username, String password, String email) {
        super(userId, username, password, email, UserRole.MANAGER);
    }
    
    /**
     * Returns the dashboard view identifier for Supply Chain Managers.
     * 
     * @return Dashboard view identifier
     */
    @Override
    public String getDashboardView() {
        return "SUPPLY_CHAIN_MANAGER_DASHBOARD";
    }
    
    /**
     * Returns the permissions for Supply Chain Managers.
     * Managers can monitor transactions, view optimization recommendations,
     * and manage compliance requirements.
     * 
     * @return Set of permission strings
     */
    @Override
    public Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("VIEW_TRANSACTION_HISTORY");
        permissions.add("MONITOR_BLOCKCHAIN");
        permissions.add("VIEW_OPTIMIZATION_RECOMMENDATIONS");
        permissions.add("MANAGE_COMPLIANCE");
        permissions.add("VIEW_COMPLIANCE_REPORTS");
        permissions.add("STORE_REGULATORY_REQUIREMENTS");
        permissions.add("VIEW_ALL_PRODUCTS");
        return permissions;
    }
    
    @Override
    public String toString() {
        return "SupplyChainManager{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
