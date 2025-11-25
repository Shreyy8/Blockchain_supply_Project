package com.supplychain.util;

import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;

/**
 * Standalone utility class to initialize the database.
 * This class can be run directly to set up the database schema and load sample data.
 * 
 * Usage:
 * 1. Ensure MySQL is running and database 'supply_chain_db' exists
 * 2. Configure database credentials in src/main/resources/database.properties
 * 3. Run this class: java com.supplychain.util.InitializeDatabase
 */
public class InitializeDatabase {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("Blockchain Supply Chain - Database Initializer");
        System.out.println("=================================================\n");
        
        try {
            // Get database connection manager
            System.out.println("Connecting to database...");
            DatabaseConnectionManager connectionManager = DatabaseConnectionManager.getInstance();
            System.out.println("✓ Connected successfully\n");
            
            // Create initializer
            DatabaseInitializer initializer = new DatabaseInitializer(connectionManager);
            
            // Check if schema already exists
            if (initializer.schemaExists()) {
                System.out.println("WARNING: Database schema already exists!");
                System.out.println("This will drop all existing tables and data.");
                System.out.println("Press Ctrl+C to cancel, or Enter to continue...");
                
                try {
                    System.in.read();
                } catch (Exception e) {
                    // Ignore
                }
                
                System.out.println("\nReinitializing database...");
                initializer.reinitializeDatabase();
            } else {
                System.out.println("Initializing database...");
                initializer.initializeDatabase();
            }
            
            System.out.println("\n✓ Database initialized successfully!");
            System.out.println("\nDatabase contains:");
            System.out.println("  - Users table (with 5 sample users)");
            System.out.println("  - Products table (with 5 sample products)");
            System.out.println("  - Transactions table (with 5 sample transactions)");
            System.out.println("  - Blocks table (with genesis block)");
            
            System.out.println("\nSample users:");
            System.out.println("  - manager1 (USR001) - MANAGER role");
            System.out.println("  - supplier1 (USR002) - SUPPLIER role");
            System.out.println("  - supplier2 (USR003) - SUPPLIER role");
            System.out.println("  - retailer1 (USR004) - RETAILER role");
            System.out.println("  - retailer2 (USR005) - RETAILER role");
            
            // Shutdown connection pool
            connectionManager.shutdown();
            System.out.println("\n✓ Connection pool shut down");
            
            System.out.println("\n=================================================");
            System.out.println("Database initialization complete!");
            System.out.println("=================================================");
            
        } catch (ConnectionException e) {
            System.err.println("\n✗ Failed to connect to database:");
            System.err.println("  " + e.getMessage());
            System.err.println("\nPlease check:");
            System.err.println("  1. MySQL server is running");
            System.err.println("  2. Database 'supply_chain_db' exists");
            System.err.println("  3. Credentials in database.properties are correct");
            System.err.println("  4. MySQL JDBC driver is in classpath");
            
            if (e.getCause() != null) {
                System.err.println("\nCause: " + e.getCause().getMessage());
            }
            
            System.exit(1);
            
        } catch (DatabaseException e) {
            System.err.println("\n✗ Failed to initialize database:");
            System.err.println("  " + e.getMessage());
            
            if (e.getCause() != null) {
                System.err.println("\nCause: " + e.getCause().getMessage());
            }
            
            System.exit(1);
        }
    }
}
