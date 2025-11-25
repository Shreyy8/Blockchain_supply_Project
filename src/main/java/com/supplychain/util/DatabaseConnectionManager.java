package com.supplychain.util;

import com.supplychain.exception.ConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class that manages database connections with connection pooling.
 * Provides thread-safe access to database connections and handles connection lifecycle.
 */
public class DatabaseConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionManager.class.getName());
    
    private static DatabaseConnectionManager instance;
    private static final Object instanceLock = new Object();
    
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;
    private int poolSize;
    private int poolTimeout;
    
    private final List<Connection> availableConnections;
    private final List<Connection> usedConnections;
    private final Object poolLock = new Object();
    
    /**
     * Private constructor to enforce singleton pattern.
     * Loads database configuration from properties file and initializes connection pool.
     * 
     * @throws ConnectionException if configuration loading or driver initialization fails
     */
    private DatabaseConnectionManager() throws ConnectionException {
        availableConnections = new ArrayList<>();
        usedConnections = new ArrayList<>();
        loadConfiguration();
        initializeDriver();
        initializeConnectionPool();
    }
    
    /**
     * Gets the singleton instance of DatabaseConnectionManager.
     * Thread-safe implementation using double-checked locking.
     * 
     * @return the singleton instance
     * @throws ConnectionException if initialization fails
     */
    public static DatabaseConnectionManager getInstance() throws ConnectionException {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Loads database configuration from database.properties file.
     * 
     * @throws ConnectionException if properties file cannot be loaded
     */
    private void loadConfiguration() throws ConnectionException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            
            if (input == null) {
                throw new ConnectionException("Unable to find database.properties file");
            }
            
            properties.load(input);
            
            dbUrl = properties.getProperty("db.url");
            dbUsername = properties.getProperty("db.username");
            dbPassword = properties.getProperty("db.password");
            dbDriver = properties.getProperty("db.driver");
            poolSize = Integer.parseInt(properties.getProperty("db.pool.size", "10"));
            poolTimeout = Integer.parseInt(properties.getProperty("db.pool.timeout", "30000"));
            
            validateConfiguration();
            
            LOGGER.info("Database configuration loaded successfully");
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load database configuration", e);
            throw new ConnectionException("Failed to load database configuration", e);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid pool configuration values", e);
            throw new ConnectionException("Invalid pool configuration values", e);
        }
    }
    
    /**
     * Validates that all required configuration properties are present.
     * 
     * @throws ConnectionException if any required property is missing
     */
    private void validateConfiguration() throws ConnectionException {
        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            throw new ConnectionException("Database URL is not configured");
        }
        if (dbDriver == null || dbDriver.trim().isEmpty()) {
            throw new ConnectionException("Database driver is not configured");
        }
        if (poolSize <= 0) {
            throw new ConnectionException("Pool size must be greater than 0");
        }
    }
    
    /**
     * Initializes the JDBC driver.
     * 
     * @throws ConnectionException if driver cannot be loaded
     */
    private void initializeDriver() throws ConnectionException {
        try {
            Class.forName(dbDriver);
            LOGGER.info("JDBC driver loaded successfully: " + dbDriver);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "JDBC driver not found: " + dbDriver, e);
            throw new ConnectionException("JDBC driver not found: " + dbDriver, e);
        }
    }
    
    /**
     * Initializes the connection pool with the configured number of connections.
     * 
     * @throws ConnectionException if pool initialization fails
     */
    private void initializeConnectionPool() throws ConnectionException {
        synchronized (poolLock) {
            try {
                for (int i = 0; i < poolSize; i++) {
                    availableConnections.add(createConnection());
                }
                LOGGER.info("Connection pool initialized with " + poolSize + " connections");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize connection pool", e);
                throw new ConnectionException("Failed to initialize connection pool", e);
            }
        }
    }
    
    /**
     * Creates a new database connection.
     * 
     * @return a new Connection object
     * @throws SQLException if connection creation fails
     */
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
    
    /**
     * Gets a connection from the pool. Thread-safe implementation.
     * If no connections are available, waits up to the configured timeout.
     * 
     * @return a database connection
     * @throws ConnectionException if no connection is available within timeout
     */
    public synchronized Connection getConnection() throws ConnectionException {
        long startTime = System.currentTimeMillis();
        
        while (true) {
            synchronized (poolLock) {
                if (!availableConnections.isEmpty()) {
                    Connection connection = availableConnections.remove(availableConnections.size() - 1);
                    
                    // Validate connection is still valid
                    try {
                        if (connection.isClosed() || !connection.isValid(2)) {
                            LOGGER.warning("Connection is invalid, creating new one");
                            connection = createConnection();
                        }
                    } catch (SQLException e) {
                        LOGGER.log(Level.WARNING, "Error validating connection, creating new one", e);
                        try {
                            connection = createConnection();
                        } catch (SQLException ex) {
                            throw new ConnectionException("Failed to create new connection", ex);
                        }
                    }
                    
                    usedConnections.add(connection);
                    LOGGER.fine("Connection retrieved from pool. Available: " + 
                               availableConnections.size() + ", Used: " + usedConnections.size());
                    return connection;
                }
            }
            
            // Check timeout
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= poolTimeout) {
                throw new ConnectionException("Connection pool timeout: no connections available after " + 
                                            poolTimeout + "ms");
            }
            
            // Wait a bit before trying again
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ConnectionException("Interrupted while waiting for connection", e);
            }
        }
    }
    
    /**
     * Returns a connection to the pool. Thread-safe implementation.
     * 
     * @param connection the connection to return
     * @return true if connection was successfully returned, false otherwise
     */
    public synchronized boolean releaseConnection(Connection connection) {
        if (connection == null) {
            LOGGER.warning("Attempted to release null connection");
            return false;
        }
        
        synchronized (poolLock) {
            if (usedConnections.remove(connection)) {
                availableConnections.add(connection);
                LOGGER.fine("Connection returned to pool. Available: " + 
                           availableConnections.size() + ", Used: " + usedConnections.size());
                return true;
            } else {
                LOGGER.warning("Attempted to release connection not from this pool");
                return false;
            }
        }
    }
    
    /**
     * Gets the current number of available connections in the pool.
     * 
     * @return number of available connections
     */
    public int getAvailableConnectionsCount() {
        synchronized (poolLock) {
            return availableConnections.size();
        }
    }
    
    /**
     * Gets the current number of used connections.
     * 
     * @return number of used connections
     */
    public int getUsedConnectionsCount() {
        synchronized (poolLock) {
            return usedConnections.size();
        }
    }
    
    /**
     * Closes all connections in the pool and cleans up resources.
     * Should be called when shutting down the application.
     */
    public synchronized void shutdown() {
        synchronized (poolLock) {
            closeConnections(availableConnections);
            closeConnections(usedConnections);
            availableConnections.clear();
            usedConnections.clear();
            LOGGER.info("Database connection pool shut down");
        }
    }
    
    /**
     * Helper method to close a list of connections.
     * 
     * @param connections list of connections to close
     */
    private void closeConnections(List<Connection> connections) {
        for (Connection connection : connections) {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing connection", e);
            }
        }
    }
    
    /**
     * Gets the configured database URL.
     * 
     * @return database URL
     */
    public String getDbUrl() {
        return dbUrl;
    }
    
    /**
     * Gets the configured pool size.
     * 
     * @return pool size
     */
    public int getPoolSize() {
        return poolSize;
    }
}
