package com.supplychain.dao;

import com.supplychain.model.Product;
import com.supplychain.model.ProductStatus;
import com.supplychain.util.DatabaseConnectionManager;
import com.supplychain.exception.ConnectionException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Product entities.
 * Implements CRUD operations for products using JDBC with PreparedStatements.
 * 
 * Requirements: 11.2, 11.3, 11.4
 */
public class ProductDAO implements DAO<Product> {
    private static final Logger LOGGER = Logger.getLogger(ProductDAO.class.getName());
    private final DatabaseConnectionManager connectionManager;
    
    /**
     * Constructor that initializes the connection manager.
     * 
     * @throws ConnectionException if connection manager initialization fails
     */
    public ProductDAO() throws ConnectionException {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }
    
    /**
     * Saves a new product to the database.
     * Uses PreparedStatement to prevent SQL injection.
     * 
     * @param entity The product to save
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void save(Product entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        String sql = "INSERT INTO products (product_id, name, description, origin, current_location, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getProductId());
            stmt.setString(2, entity.getName());
            stmt.setString(3, entity.getDescription());
            stmt.setString(4, entity.getOrigin());
            stmt.setString(5, entity.getCurrentLocation());
            stmt.setString(6, entity.getStatus().toString());
            stmt.setTimestamp(7, Timestamp.valueOf(entity.getCreatedAt()));
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Product saved successfully: " + entity.getProductId() + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Finds a product by its unique identifier.
     * 
     * @param id The unique identifier of the product
     * @return The product with the specified ID, or null if not found
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Product findById(String id) throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        
        String sql = "SELECT product_id, name, description, origin, current_location, status, created_at " +
                     "FROM products WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractProductFromResultSet(rs);
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
     * Retrieves all products from the database.
     * 
     * @return A list of all products, empty list if none exist
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT product_id, name, description, origin, current_location, status, created_at FROM products";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Product> products = new ArrayList<>();
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                if (product != null) {
                    products.add(product);
                }
            }
            
            LOGGER.info("Retrieved " + products.size() + " products from database");
            return products;
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Updates an existing product in the database.
     * 
     * @param entity The product with updated values
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(Product entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        String sql = "UPDATE products SET name = ?, description = ?, origin = ?, " +
                     "current_location = ?, status = ?, created_at = ? WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getName());
            stmt.setString(2, entity.getDescription());
            stmt.setString(3, entity.getOrigin());
            stmt.setString(4, entity.getCurrentLocation());
            stmt.setString(5, entity.getStatus().toString());
            stmt.setTimestamp(6, Timestamp.valueOf(entity.getCreatedAt()));
            stmt.setString(7, entity.getProductId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Product updated successfully: " + entity.getProductId() + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Deletes a product from the database by its unique identifier.
     * 
     * @param id The unique identifier of the product to delete
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(String id) throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        
        String sql = "DELETE FROM products WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Product deleted successfully: " + id + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Extracts a Product object from a ResultSet.
     * 
     * @param rs The ResultSet containing product data
     * @return A Product object, or null if data is invalid
     * @throws SQLException if a database access error occurs
     */
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        String productId = rs.getString("product_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String origin = rs.getString("origin");
        String currentLocation = rs.getString("current_location");
        String statusStr = rs.getString("status");
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        
        ProductStatus status;
        try {
            status = ProductStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid status found in database: " + statusStr);
            return null;
        }
        
        LocalDateTime createdAt = createdAtTimestamp.toLocalDateTime();
        
        return new Product(productId, name, description, origin, currentLocation, status, createdAt);
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
