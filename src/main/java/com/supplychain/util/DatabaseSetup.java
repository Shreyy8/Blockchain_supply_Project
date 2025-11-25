package com.supplychain.util;

import com.supplychain.exception.DatabaseException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Standalone utility to initialize the database and display schema information.
 */
public class DatabaseSetup {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("BLOCKCHAIN SUPPLY CHAIN MANAGEMENT SYSTEM - DATABASE SETUP");
        System.out.println("=".repeat(80));
        System.out.println();
        
        DatabaseConnectionManager connectionManager = null;
        
        try {
            // Initialize connection manager
            System.out.println("Connecting to database...");
            connectionManager = DatabaseConnectionManager.getInstance();
            System.out.println("✓ Database connection established");
            System.out.println();
            
            // Initialize database
            DatabaseInitializer initializer = new DatabaseInitializer(connectionManager);
            
            System.out.println("Initializing database schema...");
            initializer.initializeSchema();
            System.out.println("✓ Schema created successfully");
            System.out.println();
            
            System.out.println("Loading sample data...");
            initializer.loadSampleData();
            System.out.println("✓ Sample data loaded successfully");
            System.out.println();
            
            // Display schema information
            displaySchemaInfo(connectionManager);
            
            System.out.println();
            System.out.println("=".repeat(80));
            System.out.println("DATABASE SETUP COMPLETED SUCCESSFULLY");
            System.out.println("=".repeat(80));
            
        } catch (DatabaseException e) {
            System.err.println("✗ Database initialization failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (connectionManager != null) {
                connectionManager.shutdown();
            }
        }
    }
    
    private static void displaySchemaInfo(DatabaseConnectionManager connectionManager) {
        System.out.println("=".repeat(80));
        System.out.println("DATABASE SCHEMA INFORMATION");
        System.out.println("=".repeat(80));
        System.out.println();
        
        Connection connection = null;
        Statement statement = null;
        
        try {
            connection = connectionManager.getConnection();
            statement = connection.createStatement();
            
            // Display Users table
            displayTableInfo(statement, "USERS", 
                "Stores user accounts with authentication and role information");
            
            // Display Products table
            displayTableInfo(statement, "PRODUCTS",
                "Stores product information tracked in the supply chain");
            
            // Display Transactions table
            displayTableInfo(statement, "TRANSACTIONS",
                "Records all supply chain transactions between users");
            
            // Display Blocks table
            displayTableInfo(statement, "BLOCKS",
                "Stores blockchain blocks containing transaction data");
            
        } catch (Exception e) {
            System.err.println("Error displaying schema info: " + e.getMessage());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
            if (connection != null) {
                connectionManager.releaseConnection(connection);
            }
        }
    }
    
    private static void displayTableInfo(Statement statement, String tableName, String description) 
            throws SQLException {
        System.out.println("TABLE: " + tableName);
        System.out.println("-".repeat(80));
        System.out.println("Description: " + description);
        System.out.println();
        
        // Get column information from MySQL information schema
        ResultSet rs = statement.executeQuery(
            "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE, COLUMN_KEY " +
            "FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = '" + tableName + "' " +
            "AND TABLE_SCHEMA = DATABASE() " +
            "ORDER BY ORDINAL_POSITION"
        );
        
        System.out.printf("%-25s %-15s %-10s %-10s %-10s%n", "COLUMN", "TYPE", "SIZE", "NULLABLE", "KEY");
        System.out.println("-".repeat(80));
        
        while (rs.next()) {
            String columnName = rs.getString("COLUMN_NAME");
            String dataType = rs.getString("DATA_TYPE");
            Object maxLength = rs.getObject("CHARACTER_MAXIMUM_LENGTH");
            String nullable = rs.getString("IS_NULLABLE");
            String columnKey = rs.getString("COLUMN_KEY");
            
            String size = maxLength != null ? maxLength.toString() : "-";
            String key = columnKey != null && !columnKey.isEmpty() ? columnKey : "-";
            
            System.out.printf("%-25s %-15s %-10s %-10s %-10s%n", 
                columnName, dataType, size, nullable, key);
        }
        rs.close();
        
        // Get row count
        ResultSet countRs = statement.executeQuery("SELECT COUNT(*) FROM " + tableName);
        if (countRs.next()) {
            int count = countRs.getInt(1);
            System.out.println();
            System.out.println("Total rows: " + count);
        }
        countRs.close();
        
        System.out.println();
    }
}
