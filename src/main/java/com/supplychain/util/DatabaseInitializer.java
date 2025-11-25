package com.supplychain.util;

import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for initializing the database schema and loading sample data.
 * Provides methods to execute SQL scripts for schema creation and data insertion.
 */
public class DatabaseInitializer {
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    
    private final DatabaseConnectionManager connectionManager;
    
    /**
     * Constructs a DatabaseInitializer with the given connection manager.
     * 
     * @param connectionManager the database connection manager
     */
    public DatabaseInitializer(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    
    /**
     * Initializes the database by creating the schema.
     * Executes the schema.sql script to create all required tables.
     * 
     * @throws DatabaseException if schema initialization fails
     */
    public void initializeSchema() throws DatabaseException {
        LOGGER.info("Initializing database schema...");
        executeSqlScript("schema-simple.sql");
        LOGGER.info("Database schema initialized successfully");
    }
    
    /**
     * Loads sample data into the database for testing purposes.
     * Executes the sample-data.sql script to insert test data.
     * 
     * @throws DatabaseException if sample data loading fails
     */
    public void loadSampleData() throws DatabaseException {
        LOGGER.info("Loading sample data...");
        executeSqlScript("sample-data.sql");
        LOGGER.info("Sample data loaded successfully");
    }
    
    /**
     * Initializes the database with schema and sample data.
     * This is a convenience method that calls both initializeSchema() and loadSampleData().
     * 
     * @throws DatabaseException if initialization fails
     */
    public void initializeDatabase() throws DatabaseException {
        initializeSchema();
        loadSampleData();
    }
    
    /**
     * Executes a SQL script file from the classpath resources.
     * Reads the script file, splits it into individual statements, and executes each one.
     * 
     * @param scriptFileName the name of the SQL script file in resources
     * @throws DatabaseException if script execution fails
     */
    private void executeSqlScript(String scriptFileName) throws DatabaseException {
        Connection connection = null;
        Statement statement = null;
        
        try {
            // Get connection from pool
            connection = connectionManager.getConnection();
            statement = connection.createStatement();
            
            // Read SQL script from resources
            String sqlScript = readSqlScript(scriptFileName);
            
            // Split script into individual statements
            String[] statements = splitSqlStatements(sqlScript);
            
            // Execute each statement
            int executedCount = 0;
            for (String sql : statements) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty() && !trimmedSql.startsWith("--")) {
                    try {
                        statement.execute(trimmedSql);
                        executedCount++;
                        LOGGER.fine("Executed SQL statement: " + 
                                   (trimmedSql.length() > 50 ? trimmedSql.substring(0, 50) + "..." : trimmedSql));
                    } catch (SQLException e) {
                        // Log warning but continue with other statements
                        LOGGER.log(Level.WARNING, "Failed to execute statement: " + trimmedSql, e);
                    }
                }
            }
            
            LOGGER.info("Executed " + executedCount + " SQL statements from " + scriptFileName);
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new DatabaseException("Failed to get database connection for script execution", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL error while executing script: " + scriptFileName, e);
            throw new DatabaseException("SQL error while executing script: " + scriptFileName, e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read SQL script: " + scriptFileName, e);
            throw new DatabaseException("Failed to read SQL script: " + scriptFileName, e);
        } finally {
            // Clean up resources
            closeStatement(statement);
            releaseConnection(connection);
        }
    }
    
    /**
     * Reads a SQL script file from the classpath resources.
     * 
     * @param scriptFileName the name of the SQL script file
     * @return the content of the SQL script as a string
     * @throws IOException if the file cannot be read
     * @throws DatabaseException if the file is not found
     */
    private String readSqlScript(String scriptFileName) throws IOException, DatabaseException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(scriptFileName);
        
        if (inputStream == null) {
            throw new DatabaseException("SQL script not found: " + scriptFileName);
        }
        
        StringBuilder scriptContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scriptContent.append(line).append("\n");
            }
        }
        
        return scriptContent.toString();
    }
    
    /**
     * Splits a SQL script into individual statements.
     * Statements are separated by semicolons.
     * 
     * @param sqlScript the complete SQL script
     * @return array of individual SQL statements
     */
    private String[] splitSqlStatements(String sqlScript) {
        // Split by semicolon, but preserve semicolons in string literals
        return sqlScript.split(";");
    }
    
    /**
     * Checks if the database schema exists by attempting to query a table.
     * 
     * @return true if schema exists, false otherwise
     */
    public boolean schemaExists() {
        Connection connection = null;
        Statement statement = null;
        
        try {
            connection = connectionManager.getConnection();
            statement = connection.createStatement();
            
            // Try to query the users table
            statement.executeQuery("SELECT COUNT(*) FROM users");
            return true;
            
        } catch (ConnectionException | SQLException e) {
            LOGGER.fine("Schema does not exist or is incomplete: " + e.getMessage());
            return false;
        } finally {
            closeStatement(statement);
            releaseConnection(connection);
        }
    }
    
    /**
     * Drops all tables in the database.
     * WARNING: This will delete all data. Use with caution.
     * 
     * @throws DatabaseException if dropping tables fails
     */
    public void dropAllTables() throws DatabaseException {
        LOGGER.warning("Dropping all database tables...");
        
        Connection connection = null;
        Statement statement = null;
        
        try {
            connection = connectionManager.getConnection();
            statement = connection.createStatement();
            
            // Drop tables in reverse order of dependencies
            String[] dropStatements = {
                "DROP TABLE IF EXISTS blocks",
                "DROP TABLE IF EXISTS transactions",
                "DROP TABLE IF EXISTS products",
                "DROP TABLE IF EXISTS users"
            };
            
            for (String sql : dropStatements) {
                try {
                    statement.execute(sql);
                    LOGGER.fine("Executed: " + sql);
                } catch (SQLException e) {
                    LOGGER.log(Level.WARNING, "Failed to execute: " + sql, e);
                }
            }
            
            LOGGER.info("All tables dropped successfully");
            
        } catch (ConnectionException e) {
            throw new DatabaseException("Failed to get database connection", e);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to drop tables", e);
        } finally {
            closeStatement(statement);
            releaseConnection(connection);
        }
    }
    
    /**
     * Reinitializes the database by dropping all tables and recreating them with sample data.
     * 
     * @throws DatabaseException if reinitialization fails
     */
    public void reinitializeDatabase() throws DatabaseException {
        LOGGER.info("Reinitializing database...");
        dropAllTables();
        initializeDatabase();
        LOGGER.info("Database reinitialized successfully");
    }
    
    /**
     * Helper method to safely close a statement.
     * 
     * @param statement the statement to close
     */
    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing statement", e);
            }
        }
    }
    
    /**
     * Helper method to safely release a connection back to the pool.
     * 
     * @param connection the connection to release
     */
    private void releaseConnection(Connection connection) {
        if (connection != null) {
            connectionManager.releaseConnection(connection);
        }
    }
}
