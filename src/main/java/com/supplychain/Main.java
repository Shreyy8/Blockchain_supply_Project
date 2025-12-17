package com.supplychain;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;
import com.supplychain.util.DatabaseConnectionManager;
import com.supplychain.util.DatabaseInitializer;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main entry point for the Blockchain-based Supply Chain Management System.
 * Initializes the database and creates the genesis block for the blockchain.
 * This class provides core system initialization for the web-based application.
 * 
 * Requirements: 11.1 - Database connection and schema initialization
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    // Application-wide blockchain manager instance
    private static BlockchainManager blockchainManager;
    
    // Mining difficulty for proof-of-work
    private static final int BLOCKCHAIN_DIFFICULTY = 4;
    
    /**
     * Main method - application entry point.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Configure logging
        configureLogging();
        
        LOGGER.info("Starting Blockchain Supply Chain Management System...");
        
        try {
            // Step 1: Initialize database connection
            LOGGER.info("Step 1: Initializing database connection...");
            DatabaseConnectionManager connectionManager = initializeDatabaseConnection();
            
            // Step 2: Initialize database schema
            LOGGER.info("Step 2: Initializing database schema...");
            initializeDatabaseSchema(connectionManager);
            
            // Step 3: Create genesis block for blockchain
            LOGGER.info("Step 3: Creating genesis block for blockchain...");
            createGenesisBlock();
            
            LOGGER.info("Core system initialization completed successfully!");
            LOGGER.info("Web application can now be started using: mvn jetty:run");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
            System.err.println("Database Connection Error: " + e.getMessage());
            System.err.println("Please check your database configuration and ensure MySQL is running.");
            System.exit(1);
            
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database schema", e);
            System.err.println("Database Initialization Error: " + e.getMessage());
            System.err.println("Please check database permissions and configuration.");
            System.exit(1);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during application startup", e);
            System.err.println("Application Startup Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Configures the Java logging system.
     * Loads logging configuration from logging.properties if available.
     */
    private static void configureLogging() {
        try {
            // Try to load logging configuration from properties file
            LogManager.getLogManager().readConfiguration(
                Main.class.getClassLoader().getResourceAsStream("logging.properties")
            );
            LOGGER.info("Logging configuration loaded successfully");
        } catch (Exception e) {
            // If loading fails, use default configuration
            System.err.println("Warning: Could not load logging configuration, using defaults");
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the database connection manager.
     * Requirements: 11.1 - Establish JDBC connection to configured database
     * 
     * @return DatabaseConnectionManager instance
     * @throws ConnectionException if connection initialization fails
     */
    private static DatabaseConnectionManager initializeDatabaseConnection() throws ConnectionException {
        LOGGER.info("Initializing database connection manager...");
        
        DatabaseConnectionManager connectionManager = DatabaseConnectionManager.getInstance();
        
        LOGGER.info("Database connection established successfully");
        LOGGER.info("Database URL: " + connectionManager.getDbUrl());
        LOGGER.info("Connection pool size: " + connectionManager.getPoolSize());
        LOGGER.info("Available connections: " + connectionManager.getAvailableConnectionsCount());
        
        return connectionManager;
    }
    
    /**
     * Initializes the database schema if it doesn't already exist.
     * Creates all required tables and loads sample data.
     * Requirements: 11.1 - Database schema initialization
     * 
     * @param connectionManager The database connection manager
     * @throws DatabaseException if schema initialization fails
     */
    private static void initializeDatabaseSchema(DatabaseConnectionManager connectionManager) 
            throws DatabaseException {
        LOGGER.info("Checking database schema...");
        
        DatabaseInitializer initializer = new DatabaseInitializer(connectionManager);
        
        // Check if schema already exists
        if (initializer.schemaExists()) {
            LOGGER.info("Database schema already exists, skipping initialization");
            return;
        }
        
        LOGGER.info("Database schema does not exist, initializing...");
        
        // Initialize schema and load sample data
        initializer.initializeDatabase();
        
        LOGGER.info("Database schema initialized successfully");
    }
    
    /**
     * Creates the genesis block for the blockchain.
     * The genesis block is the first block in the blockchain with no previous hash.
     * Requirements: 4.2, 4.3 - Blockchain initialization with genesis block
     */
    private static void createGenesisBlock() {
        LOGGER.info("Creating blockchain with genesis block...");
        
        // Create blockchain manager with specified difficulty
        // The constructor automatically creates the genesis block
        blockchainManager = new BlockchainManager(BLOCKCHAIN_DIFFICULTY);
        
        LOGGER.info("Genesis block created successfully");
        LOGGER.info("Blockchain difficulty: " + BLOCKCHAIN_DIFFICULTY);
        LOGGER.info("Genesis block hash: " + blockchainManager.getLatestBlock().getHash());
        LOGGER.info("Blockchain initialized with " + blockchainManager.getChain().size() + " block(s)");
    }
    

    
    /**
     * Gets the application-wide blockchain manager instance.
     * 
     * @return The BlockchainManager instance
     */
    public static BlockchainManager getBlockchainManager() {
        return blockchainManager;
    }
    
    /**
     * Shuts down the application gracefully.
     * Closes database connections and performs cleanup.
     */
    public static void shutdown() {
        LOGGER.info("Shutting down application...");
        
        try {
            // Close database connections
            DatabaseConnectionManager connectionManager = DatabaseConnectionManager.getInstance();
            connectionManager.shutdown();
            LOGGER.info("Database connections closed");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during shutdown", e);
        }
        
        LOGGER.info("Application shutdown complete");
    }
}
