package com.supplychain.dao;

import com.supplychain.model.*;
import com.supplychain.util.DatabaseConnectionManager;
import com.supplychain.exception.ConnectionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for User entities.
 * Implements CRUD operations for users using JDBC with PreparedStatements.
 * 
 * Requirements: 11.2, 11.3, 11.4
 */
public class UserDAO implements DAO<User> {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private final DatabaseConnectionManager connectionManager;
    
    /**
     * Constructor that initializes the connection manager.
     * 
     * @throws ConnectionException if connection manager initialization fails
     */
    public UserDAO() throws ConnectionException {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }
    
    /**
     * Saves a new user to the database.
     * Uses PreparedStatement to prevent SQL injection.
     * 
     * @param entity The user to save
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void save(User entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        String sql = "INSERT INTO users (user_id, username, password, email, role) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getUserId());
            stmt.setString(2, entity.getUsername());
            stmt.setString(3, entity.getPassword());
            stmt.setString(4, entity.getEmail());
            stmt.setString(5, entity.getRole().toString());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("User saved successfully: " + entity.getUserId() + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Finds a user by their unique identifier.
     * 
     * @param id The unique identifier of the user
     * @return The user with the specified ID, or null if not found
     * @throws SQLException if a database access error occurs
     */
    @Override
    public User findById(String id) throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        String sql = "SELECT user_id, username, password, email, role FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
            return null;
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves all users from the database.
     * 
     * @return A list of all users, empty list if none exist
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT user_id, username, password, email, role FROM users";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                if (user != null) {
                    users.add(user);
                }
            }
            
            LOGGER.info("Retrieved " + users.size() + " users from database");
            return users;
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Updates an existing user in the database.
     * 
     * @param entity The user with updated values
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(User entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, role = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getUsername());
            stmt.setString(2, entity.getPassword());
            stmt.setString(3, entity.getEmail());
            stmt.setString(4, entity.getRole().toString());
            stmt.setString(5, entity.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("User updated successfully: " + entity.getUserId() + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Deletes a user from the database by their unique identifier.
     * 
     * @param id The unique identifier of the user to delete
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(String id) throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        String sql = "DELETE FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("User deleted successfully: " + id + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Extracts a User object from a ResultSet.
     * Creates the appropriate concrete user type based on the role.
     * 
     * @param rs The ResultSet containing user data
     * @return A User object, or null if role is invalid
     * @throws SQLException if a database access error occurs
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        String userId = rs.getString("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        String roleStr = rs.getString("role");
        
        UserRole role;
        try {
            role = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid role found in database: " + roleStr);
            return null;
        }
        
        // Create the appropriate concrete user type based on role
        User user;
        switch (role) {
            case MANAGER:
                user = new SupplyChainManager(userId, username, password, email);
                break;
            case SUPPLIER:
                user = new Supplier(userId, username, password, email);
                break;
            case RETAILER:
                user = new Retailer(userId, username, password, email);
                break;
            default:
                LOGGER.warning("Unknown role: " + role);
                return null;
        }
        
        return user;
    }
    
    /**
     * Closes database resources in the correct order.
     * Ensures resources are properly released even if exceptions occur.
     * 
     * @param conn The connection to close
     * @param stmt The statement to close
     * @param rs The result set to close
     */
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing Statement", e);
            }
        }
        
        if (conn != null) {
            connectionManager.releaseConnection(conn);
        }
    }
}
