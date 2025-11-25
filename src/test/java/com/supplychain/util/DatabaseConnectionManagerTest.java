package com.supplychain.util;

import com.supplychain.exception.ConnectionException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DatabaseConnectionManager.
 * Tests connection pooling, thread safety, and connection lifecycle.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseConnectionManagerTest {
    
    private static DatabaseConnectionManager connectionManager;
    
    @BeforeAll
    static void setUpClass() throws ConnectionException {
        connectionManager = DatabaseConnectionManager.getInstance();
    }
    
    @Test
    @Order(1)
    @DisplayName("Test singleton instance")
    void testSingletonInstance() throws ConnectionException {
        DatabaseConnectionManager instance1 = DatabaseConnectionManager.getInstance();
        DatabaseConnectionManager instance2 = DatabaseConnectionManager.getInstance();
        
        assertSame(instance1, instance2, "getInstance should return same instance");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test get connection")
    void testGetConnection() throws ConnectionException, SQLException {
        Connection connection = connectionManager.getConnection();
        
        assertNotNull(connection, "Connection should not be null");
        assertFalse(connection.isClosed(), "Connection should be open");
        
        // Clean up
        connectionManager.releaseConnection(connection);
    }
    
    @Test
    @Order(3)
    @DisplayName("Test connection is valid")
    void testConnectionIsValid() throws ConnectionException, SQLException {
        Connection connection = connectionManager.getConnection();
        
        assertTrue(connection.isValid(2), "Connection should be valid");
        
        // Clean up
        connectionManager.releaseConnection(connection);
    }
    
    @Test
    @Order(4)
    @DisplayName("Test release connection")
    void testReleaseConnection() throws ConnectionException {
        int initialAvailable = connectionManager.getAvailableConnectionsCount();
        
        Connection connection = connectionManager.getConnection();
        int afterGet = connectionManager.getAvailableConnectionsCount();
        
        assertEquals(initialAvailable - 1, afterGet, "Available connections should decrease");
        
        boolean released = connectionManager.releaseConnection(connection);
        int afterRelease = connectionManager.getAvailableConnectionsCount();
        
        assertTrue(released, "Release should return true");
        assertEquals(initialAvailable, afterRelease, "Available connections should be restored");
    }
    
    @Test
    @Order(5)
    @DisplayName("Test release null connection")
    void testReleaseNullConnection() {
        boolean result = connectionManager.releaseConnection(null);
        
        assertFalse(result, "Releasing null connection should return false");
    }
    
    @Test
    @Order(6)
    @DisplayName("Test connection pool counts")
    void testConnectionPoolCounts() throws ConnectionException {
        int initialAvailable = connectionManager.getAvailableConnectionsCount();
        int initialUsed = connectionManager.getUsedConnectionsCount();
        
        Connection conn1 = connectionManager.getConnection();
        Connection conn2 = connectionManager.getConnection();
        
        assertEquals(initialAvailable - 2, connectionManager.getAvailableConnectionsCount());
        assertEquals(initialUsed + 2, connectionManager.getUsedConnectionsCount());
        
        connectionManager.releaseConnection(conn1);
        connectionManager.releaseConnection(conn2);
        
        assertEquals(initialAvailable, connectionManager.getAvailableConnectionsCount());
        assertEquals(initialUsed, connectionManager.getUsedConnectionsCount());
    }
    
    @Test
    @Order(7)
    @DisplayName("Test multiple connections")
    void testMultipleConnections() throws ConnectionException {
        Connection conn1 = connectionManager.getConnection();
        Connection conn2 = connectionManager.getConnection();
        Connection conn3 = connectionManager.getConnection();
        
        assertNotNull(conn1);
        assertNotNull(conn2);
        assertNotNull(conn3);
        
        // All connections should be different objects
        assertNotSame(conn1, conn2);
        assertNotSame(conn2, conn3);
        assertNotSame(conn1, conn3);
        
        // Clean up
        connectionManager.releaseConnection(conn1);
        connectionManager.releaseConnection(conn2);
        connectionManager.releaseConnection(conn3);
    }
    
    @Test
    @Order(8)
    @DisplayName("Test connection reuse")
    void testConnectionReuse() throws ConnectionException {
        Connection conn1 = connectionManager.getConnection();
        connectionManager.releaseConnection(conn1);
        
        Connection conn2 = connectionManager.getConnection();
        
        // Should get the same connection back (reused from pool)
        assertSame(conn1, conn2, "Connection should be reused from pool");
        
        // Clean up
        connectionManager.releaseConnection(conn2);
    }
    
    @Test
    @Order(9)
    @DisplayName("Test pool size configuration")
    void testPoolSizeConfiguration() {
        int poolSize = connectionManager.getPoolSize();
        
        assertTrue(poolSize > 0, "Pool size should be greater than 0");
        assertTrue(poolSize <= 20, "Pool size should be reasonable (<=20)");
    }
    
    @Test
    @Order(10)
    @DisplayName("Test database URL configuration")
    void testDatabaseUrlConfiguration() {
        String dbUrl = connectionManager.getDbUrl();
        
        assertNotNull(dbUrl, "Database URL should not be null");
        assertTrue(dbUrl.startsWith("jdbc:"), "Database URL should start with 'jdbc:'");
    }
    
    @Test
    @Order(11)
    @DisplayName("Test concurrent connection requests")
    void testConcurrentConnectionRequests() throws InterruptedException {
        final int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        final boolean[] success = new boolean[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    Connection conn = connectionManager.getConnection();
                    assertNotNull(conn);
                    Thread.sleep(50); // Hold connection briefly
                    connectionManager.releaseConnection(conn);
                    success[index] = true;
                } catch (Exception e) {
                    success[index] = false;
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // All threads should have succeeded
        for (int i = 0; i < threadCount; i++) {
            assertTrue(success[i], "Thread " + i + " should have succeeded");
        }
    }
    
    @AfterAll
    static void tearDownClass() {
        // Note: We don't shutdown the connection manager here because
        // other tests may still need it. The application should handle
        // shutdown when it terminates.
    }
}
