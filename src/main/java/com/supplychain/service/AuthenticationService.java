package com.supplychain.service;

import com.supplychain.dao.UserDAO;
import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;
import com.supplychain.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for handling user authentication and session management.
 * Uses password hashing (SHA-256) for secure authentication and ConcurrentHashMap
 * for thread-safe session management.
 * 
 * Requirements: 9.1
 */
public class AuthenticationService {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    
    // Thread-safe session storage: sessionId -> User
    private final ConcurrentHashMap<String, User> activeSessions;
    
    // Thread-safe username to sessionId mapping for quick lookup
    private final ConcurrentHashMap<String, String> usernameToSessionId;
    
    private final UserDAO userDAO;
    
    // Singleton instance
    private static AuthenticationService instance;
    
    /**
     * Private constructor for singleton pattern.
     * Initializes the UserDAO and session storage.
     * 
     * @throws ConnectionException if database connection fails
     */
    private AuthenticationService() throws ConnectionException {
        this.userDAO = new UserDAO();
        this.activeSessions = new ConcurrentHashMap<>();
        this.usernameToSessionId = new ConcurrentHashMap<>();
    }
    
    /**
     * Gets the singleton instance of AuthenticationService.
     * 
     * @return The singleton instance
     * @throws ConnectionException if database connection fails during initialization
     */
    public static synchronized AuthenticationService getInstance() throws ConnectionException {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }
    
    /**
     * Authenticates a user with username and password.
     * Validates credentials against the database using password hashing.
     * Creates a new session if authentication is successful.
     * 
     * @param username The username to authenticate
     * @param password The plain-text password to validate
     * @return A session ID if authentication is successful, null otherwise
     * @throws DatabaseException if a database error occurs during authentication
     */
    public String login(String username, String password) throws DatabaseException {
        if (username == null || username.trim().isEmpty()) {
            LOGGER.warning("Login attempt with null or empty username");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            LOGGER.warning("Login attempt with null or empty password");
            return null;
        }
        
        try {
            // Find user by username (we need to search through all users)
            User user = findUserByUsername(username);
            
            if (user == null) {
                LOGGER.warning("Login failed: User not found - " + username);
                return null;
            }
            
            // Hash the provided password
            String hashedPassword = hashPassword(password);
            
            // Compare hashed password with stored password
            if (!user.getPassword().equals(hashedPassword)) {
                LOGGER.warning("Login failed: Invalid password for user - " + username);
                return null;
            }
            
            // Check if user already has an active session
            String existingSessionId = usernameToSessionId.get(username);
            if (existingSessionId != null) {
                LOGGER.info("User already has an active session: " + username);
                return existingSessionId;
            }
            
            // Create new session
            String sessionId = generateSessionId(username);
            activeSessions.put(sessionId, user);
            usernameToSessionId.put(username, sessionId);
            
            LOGGER.info("User logged in successfully: " + username + " (Session: " + sessionId + ")");
            return sessionId;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during login", e);
            throw new DatabaseException("Failed to authenticate user", e);
        }
    }
    
    /**
     * Logs out a user by removing their session.
     * 
     * @param sessionId The session ID to invalidate
     * @return true if logout was successful, false if session was not found
     */
    public boolean logout(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            LOGGER.warning("Logout attempt with null or empty session ID");
            return false;
        }
        
        User user = activeSessions.remove(sessionId);
        
        if (user != null) {
            usernameToSessionId.remove(user.getUsername());
            LOGGER.info("User logged out successfully: " + user.getUsername());
            return true;
        }
        
        LOGGER.warning("Logout failed: Session not found - " + sessionId);
        return false;
    }
    
    /**
     * Retrieves the user associated with a session ID.
     * 
     * @param sessionId The session ID to look up
     * @return The User object if session is valid, null otherwise
     */
    public User getUserBySession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return null;
        }
        
        return activeSessions.get(sessionId);
    }
    
    /**
     * Checks if a session is valid (exists in active sessions).
     * 
     * @param sessionId The session ID to validate
     * @return true if the session is valid, false otherwise
     */
    public boolean isSessionValid(String sessionId) {
        return sessionId != null && activeSessions.containsKey(sessionId);
    }
    
    /**
     * Gets the number of active sessions.
     * 
     * @return The count of active sessions
     */
    public int getActiveSessionCount() {
        return activeSessions.size();
    }
    
    /**
     * Hashes a password using SHA-256 algorithm.
     * 
     * @param password The plain-text password to hash
     * @return The hashed password as a hexadecimal string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Convert byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "SHA-256 algorithm not available", e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }
    
    /**
     * Finds a user by username by searching through all users.
     * This is a helper method since UserDAO doesn't have a findByUsername method.
     * 
     * @param username The username to search for
     * @return The User object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    private User findUserByUsername(String username) throws SQLException {
        // Since UserDAO doesn't have findByUsername, we need to search through all users
        // In a production system, this should be optimized with a database query
        for (User user : userDAO.findAll()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    /**
     * Generates a unique session ID for a user.
     * 
     * @param username The username to generate a session ID for
     * @return A unique session ID
     */
    private String generateSessionId(String username) {
        // Generate session ID using username and current timestamp
        String sessionData = username + System.currentTimeMillis() + System.nanoTime();
        return hashPassword(sessionData);
    }
    
    /**
     * Clears all active sessions.
     * Useful for testing or system shutdown.
     */
    public void clearAllSessions() {
        activeSessions.clear();
        usernameToSessionId.clear();
        LOGGER.info("All sessions cleared");
    }
}
