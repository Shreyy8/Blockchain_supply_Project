package com.supplychain.util;

import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatabaseInitializer.
 * Tests schema creation, sample data loading, and database initialization.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseInitializerTest {
    
    private static DatabaseConnectionManager connectionManager;
    private static DatabaseInitializer initializer;
    
    @BeforeAll
    static void setUpClass() throws ConnectionException {
        connectionManager = DatabaseConnectionManager.getInstance();
        initializer = new DatabaseInitializer(connectionManager);
    }
    
    @Test
    @Order(1)
    @DisplayName("Test schema initialization creates all tables")
    void testInitializeSchema() throws DatabaseException, ConnectionException, SQLException {
        // Reinitialize to ensure clean state
        initializer.initializeSchema();
        
        // Verify tables exist by querying them
        Connection connection = connectionManager.getConnection();
        Statement statement = connection.createStatement();
        
        try {
            // Check users table
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM users");
            assertTrue(rs.next(), "Users table should exist");
            
            // Check products table
            rs = statement.executeQuery("SELECT COUNT(*) FROM products");
            assertTrue(rs.next(), "Products table should exist");
            
            // Check transactions table
            rs = statement.executeQuery("SELECT COUNT(*) FROM transactions");
            assertTrue(rs.next(), "Transactions table should exist");
            
            // Check blocks table
            rs = statement.executeQuery("SELECT COUNT(*) FROM blocks");
            assertTrue(rs.next(), "Blocks table should exist");
            
        } finally {
            statement.close();
            connectionManager.releaseConnection(connection);
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Test sample data loading inserts records")
    void testLoadSampleData() throws DatabaseException, ConnectionException, SQLException {
        // Load sample data
        initializer.loadSampleData();
        
        Connection connection = connectionManager.getConnection();
        Statement statement = connection.createStatement();
        
        try {
            // Verify users were inserted
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            int userCount = rs.getInt(1);
            assertTrue(userCount > 0, "Sample users should be inserted");
            
            // Verify products were inserted
            rs = statement.executeQuery("SELECT COUNT(*) FROM products");
            rs.next();
            int productCount = rs.getInt(1);
            assertTrue(productCount > 0, "Sample products should be inserted");
            
            // Verify transactions were inserted
            rs = statement.executeQuery("SELECT COUNT(*) FROM transactions");
            rs.next();
            int transactionCount = rs.getInt(1);
            assertTrue(transactionCount > 0, "Sample transactions should be inserted");
            
            // Verify genesis block was inserted
            rs = statement.executeQuery("SELECT COUNT(*) FROM blocks");
            rs.next();
            int blockCount = rs.getInt(1);
            assertTrue(blockCount > 0, "Genesis block should be inserted");
            
        } finally {
            statement.close();
            connectionManager.releaseConnection(connection);
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("Test complete database initialization")
    void testInitializeDatabase() throws DatabaseException {
        // This should create schema and load sample data
        initializer.reinitializeDatabase();
        
        // Verify schema exists
        assertTrue(initializer.schemaExists(), "Schema should exist after initialization");
    }
    
    @Test
    @Order(4)
    @DisplayName("Test schema exists check")
    void testSchemaExists() {
        // After previous tests, schema should exist
        assertTrue(initializer.schemaExists(), "Schema should exist");
    }
    
    @Test
    @Order(5)
    @DisplayName("Test sample data contains expected records")
    void testSampleDataContent() throws ConnectionException, SQLException {
        Connection connection = connectionManager.getConnection();
        Statement statement = connection.createStatement();
        
        try {
            // Check for specific sample user
            ResultSet rs = statement.executeQuery(
                "SELECT username, role FROM users WHERE user_id = 'USR001'"
            );
            assertTrue(rs.next(), "Sample user USR001 should exist");
            assertEquals("manager1", rs.getString("username"));
            assertEquals("MANAGER", rs.getString("role"));
            
            // Check for specific sample product
            rs = statement.executeQuery(
                "SELECT name, status FROM products WHERE product_id = 'PROD001'"
            );
            assertTrue(rs.next(), "Sample product PROD001 should exist");
            assertEquals("Organic Coffee Beans", rs.getString("name"));
            assertEquals("CREATED", rs.getString("status"));
            
            // Check for genesis block
            rs = statement.executeQuery(
                "SELECT block_index, previous_hash FROM blocks WHERE block_index = 0"
            );
            assertTrue(rs.next(), "Genesis block should exist");
            assertEquals("0", rs.getString("previous_hash"));
            
        } finally {
            statement.close();
            connectionManager.releaseConnection(connection);
        }
    }
    
    @AfterAll
    static void tearDownClass() {
        if (connectionManager != null) {
            connectionManager.shutdown();
        }
    }
}
