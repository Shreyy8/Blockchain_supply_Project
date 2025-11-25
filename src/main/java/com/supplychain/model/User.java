package com.supplychain.model;

import java.util.Objects;
import java.util.Set;

/**
 * Abstract base class representing a user in the supply chain system.
 * Demonstrates inheritance and polymorphism - different user types share common behavior
 * but implement role-specific functionality.
 * 
 * Requirements: 9.1
 */
public abstract class User {
    protected String userId;
    protected String username;
    protected String password;
    protected String email;
    protected UserRole role;
    
    /**
     * Default constructor
     */
    public User() {
    }
    
    /**
     * Constructor with all fields
     * 
     * @param userId Unique identifier for the user
     * @param username Username for login
     * @param password Password (should be hashed)
     * @param email User's email address
     * @param role User's role in the system
     */
    public User(String userId, String username, String password, String email, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    /**
     * Abstract method to get the dashboard view for this user type.
     * Each concrete user class implements this to return their specific dashboard.
     * 
     * @return String identifier for the dashboard view
     */
    public abstract String getDashboardView();
    
    /**
     * Abstract method to get the permissions for this user type.
     * Each concrete user class implements this to return their specific permissions.
     * 
     * @return Set of permission strings
     */
    public abstract Set<String> getPermissions();
    
    /**
     * Concrete method for login functionality.
     * Validates user credentials (in a real system, this would check hashed passwords).
     * 
     * @param inputPassword The password to validate
     * @return true if login is successful, false otherwise
     */
    public boolean login(String inputPassword) {
        // In a real system, this would compare hashed passwords
        return this.password != null && this.password.equals(inputPassword);
    }
    
    /**
     * Concrete method for logout functionality.
     * Performs any necessary cleanup when user logs out.
     */
    public void logout() {
        // Perform logout operations (clear session, etc.)
        // This is a placeholder for actual logout logic
    }
    
    /**
     * Concrete method to update user profile information.
     * 
     * @param email New email address
     */
    public void updateProfile(String email) {
        this.email = email;
    }
    
    /**
     * Validates that the user has all required fields.
     * 
     * @return true if the user is valid, false otherwise
     */
    public boolean isValid() {
        return userId != null && !userId.trim().isEmpty() &&
               username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               role != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
