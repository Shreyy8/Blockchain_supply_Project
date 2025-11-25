package com.supplychain.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Concrete user class representing a Supplier.
 * Suppliers record transactions and verify records in the supply chain.
 * 
 * Requirements: 9.1, 4.1
 */
public class Supplier extends User {
    
    /**
     * Default constructor
     */
    public Supplier() {
        super();
        this.role = UserRole.SUPPLIER;
    }
    
    /**
     * Constructor with all fields
     * 
     * @param userId Unique identifier for the user
     * @param username Username for login
     * @param password Password (should be hashed)
     * @param email User's email address
     */
    public Supplier(String userId, String username, String password, String email) {
        super(userId, username, password, email, UserRole.SUPPLIER);
    }
    
    /**
     * Returns the dashboard view identifier for Suppliers.
     * 
     * @return Dashboard view identifier
     */
    @Override
    public String getDashboardView() {
        return "SUPPLIER_DASHBOARD";
    }
    
    /**
     * Returns the permissions for Suppliers.
     * Suppliers can record transactions, verify their own transactions,
     * and view product information.
     * 
     * @return Set of permission strings
     */
    @Override
    public Set<String> getPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("RECORD_TRANSACTION");
        permissions.add("CREATE_PRODUCT");
        permissions.add("TRANSFER_PRODUCT");
        permissions.add("VERIFY_OWN_TRANSACTIONS");
        permissions.add("VIEW_OWN_TRANSACTIONS");
        permissions.add("VIEW_PRODUCT_STATUS");
        return permissions;
    }
    
    @Override
    public String toString() {
        return "Supplier{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
