package com.supplychain.service;

import com.supplychain.dao.UserDAO;
import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;
import com.supplychain.model.SupplyChainManager;
import com.supplychain.model.User;
import com.supplychain.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuthenticationService.
 * Tests authentication, session management, and password hashing functionality.
 */
class AuthenticationServiceTest {
    
    private AuthenticationService authService;
    
    @BeforeEach
    void setUp() throws ConnectionException {
        authService = AuthenticationService.getInstance();
        // Clear any existing sessions before each test
        authService.clearAllSessions();
    }
    
    @Test
    void testPasswordHashing() {
        String password = "testPassword123";
        String hash1 = AuthenticationService.hashPassword(password);
        String hash2 = AuthenticationService.hashPassword(password);
        
        // Same password should produce same hash
        assertEquals(hash1, hash2);
        
        // Hash should be 64 characters (SHA-256 produces 256 bits = 64 hex chars)
        assertEquals(64, hash1.length());
        
        // Different passwords should produce different hashes
        String differentPassword = "differentPassword456";
        String hash3 = AuthenticationService.hashPassword(differentPassword);
        assertNotEquals(hash1, hash3);
    }
    
    @Test
    void testLoginWithNullUsername() throws DatabaseException {
        String sessionId = authService.login(null, "password");
        assertNull(sessionId, "Login with null username should return null");
    }
    
    @Test
    void testLoginWithEmptyUsername() throws DatabaseException {
        String sessionId = authService.login("", "password");
        assertNull(sessionId, "Login with empty username should return null");
    }
    
    @Test
    void testLoginWithNullPassword() throws DatabaseException {
        String sessionId = authService.login("testuser", null);
        assertNull(sessionId, "Login with null password should return null");
    }
    
    @Test
    void testLoginWithEmptyPassword() throws DatabaseException {
        String sessionId = authService.login("testuser", "");
        assertNull(sessionId, "Login with empty password should return null");
    }
    
    @Test
    void testLogoutWithNullSessionId() {
        boolean result = authService.logout(null);
        assertFalse(result, "Logout with null session ID should return false");
    }
    
    @Test
    void testLogoutWithEmptySessionId() {
        boolean result = authService.logout("");
        assertFalse(result, "Logout with empty session ID should return false");
    }
    
    @Test
    void testLogoutWithInvalidSessionId() {
        boolean result = authService.logout("invalid-session-id");
        assertFalse(result, "Logout with invalid session ID should return false");
    }
    
    @Test
    void testGetUserBySessionWithNullSessionId() {
        User user = authService.getUserBySession(null);
        assertNull(user, "Getting user with null session ID should return null");
    }
    
    @Test
    void testGetUserBySessionWithEmptySessionId() {
        User user = authService.getUserBySession("");
        assertNull(user, "Getting user with empty session ID should return null");
    }
    
    @Test
    void testGetUserBySessionWithInvalidSessionId() {
        User user = authService.getUserBySession("invalid-session-id");
        assertNull(user, "Getting user with invalid session ID should return null");
    }
    
    @Test
    void testIsSessionValidWithNullSessionId() {
        boolean isValid = authService.isSessionValid(null);
        assertFalse(isValid, "Null session ID should not be valid");
    }
    
    @Test
    void testIsSessionValidWithInvalidSessionId() {
        boolean isValid = authService.isSessionValid("invalid-session-id");
        assertFalse(isValid, "Invalid session ID should not be valid");
    }
    
    @Test
    void testGetActiveSessionCount() {
        int count = authService.getActiveSessionCount();
        assertEquals(0, count, "Initial active session count should be 0");
    }
    
    @Test
    void testClearAllSessions() {
        // This test just ensures the method doesn't throw exceptions
        authService.clearAllSessions();
        assertEquals(0, authService.getActiveSessionCount());
    }
    
    @Test
    void testHashPasswordConsistency() {
        // Test that the same password always produces the same hash
        String password = "mySecurePassword!@#";
        String hash1 = AuthenticationService.hashPassword(password);
        String hash2 = AuthenticationService.hashPassword(password);
        String hash3 = AuthenticationService.hashPassword(password);
        
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }
    
    @Test
    void testHashPasswordUniqueness() {
        // Test that different passwords produce different hashes
        String[] passwords = {
            "password1",
            "password2",
            "Password1",  // Case sensitive
            "password1 ", // With space
            "password12"  // Extra character
        };
        
        String[] hashes = new String[passwords.length];
        for (int i = 0; i < passwords.length; i++) {
            hashes[i] = AuthenticationService.hashPassword(passwords[i]);
        }
        
        // Check that all hashes are unique
        for (int i = 0; i < hashes.length; i++) {
            for (int j = i + 1; j < hashes.length; j++) {
                assertNotEquals(hashes[i], hashes[j], 
                    "Hashes for '" + passwords[i] + "' and '" + passwords[j] + "' should be different");
            }
        }
    }
}
