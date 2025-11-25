package com.supplychain;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;
import com.supplychain.gui.LoginFrame;
import com.supplychain.util.DatabaseConnectionManager;
import com.supplychain.util.DatabaseInitializer;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main entry point for the Blockchain-based Supply Chain Management System.
 * Initializes the database, creates the genesis block, and launches the login GUI.
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
            
            // Step 4: Launch login GUI
            LOGGER.info("Step 4: Launching login GUI...");
            launchLoginGUI();
            
            LOGGER.info("Application started successfully!");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
            showErrorDialog("Database Connection Error",
                "Failed to connect to the database. Please check your database configuration.\n\n" +
                "Error: " + e.getMessage());
            System.exit(1);
            
        } catch (DatabaseException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database schema", e);
            showErrorDialog("Database Initialization Error",
                "Failed to initialize the database schema.\n\n" +
                "Error: " + e.getMessage());
            System.exit(1);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during application startup", e);
            showErrorDialog("Application Startup Error",
                "An unexpected error occurred during application startup.\n\n" +
                "Error: " + e.getMessage());
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
     * Launches the login GUI on the Event Dispatch Thread.
     * Requirements: 9.1 - Launch login GUI for user authentication
     */
    private static void launchLoginGUI() {
        LOGGER.info("Launching login GUI...");
        
        // Set system look and feel for better native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LOGGER.info("System look and feel applied");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to set system look and feel, using default", e);
        }
        
        // Create and display the login frame on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                    LOGGER.info("Login GUI launched successfully");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to create login frame", e);
                    showErrorDialog("GUI Error",
                        "Failed to launch the login interface.\n\n" +
                        "Error: " + e.getMessage());
                    System.exit(1);
                }
            }
        });
    }
    
    /**
     * Displays an error dialog to the user.
     * 
     * @param title The dialog title
     * @param message The error message to display
     */
    private static void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
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
