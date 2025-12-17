package com.supplychain.service;

import com.supplychain.dao.UserDAO;
import com.supplychain.exception.AuthenticationException;
import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;
import com.supplychain.model.User;
import com.supplychain.util.ValidationUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for user-related business operations.
 */
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final UserDAO userDAO;

    public UserService() throws DatabaseException {
        try {
            this.userDAO = new UserDAO();
        } catch (ConnectionException e) {
            throw new DatabaseException("Failed to initialize UserService", e);
        }
    }

    /**
     * Authenticates a user with comprehensive validation and security measures.
     */
    public User authenticateUser(String username, String password) throws AuthenticationException {
        
        // Input validation
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username is required");
        }
        
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password is required");
        }
        
        // Sanitize username
        String sanitizedUsername = ValidationUtil.sanitizeInput(username.trim());
        
        // Rate limiting could be implemented here
        
        try {
            User user = getUserByUsername(sanitizedUsername);
            
            if (user == null) {
                // Log failed attempt for security monitoring
                LOGGER.warning("Authentication failed - user not found: " + sanitizedUsername);
                throw new AuthenticationException("Invalid username or password");
            }

            // Hash the provided password and compare with stored hash
            String hashedPassword = hashPassword(password);
            
            if (!hashedPassword.equals(user.getPassword())) {
                // Log failed attempt for security monitoring
                LOGGER.warning("Authentication failed - invalid password for user: " + sanitizedUsername);
                throw new AuthenticationException("Invalid username or password");
            }

            LOGGER.info("User authenticated successfully: " + sanitizedUsername + " (Role: " + user.getRole() + ")");
            return user;
            
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Database error during authentication for user: " + sanitizedUsername, e);
            throw new AuthenticationException("Authentication system temporarily unavailable. Please try again later.");
        }
    }

    /**
     * Gets a user by their ID.
     */
    public User getUserById(String userId) throws DatabaseException {
        try {
            return userDAO.findById(userId);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get user by ID", e);
        }
    }

    /**
     * Gets a user by their username.
     */
    public User getUserByUsername(String username) throws DatabaseException {
        try {
            List<User> allUsers = userDAO.findAll();
            return allUsers.stream()
                    .filter(user -> username.equals(user.getUsername()))
                    .findFirst()
                    .orElse(null);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get user by username", e);
        }
    }

    /**
     * Hashes a password using SHA-256.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}