package com.supplychain.exception;

/**
 * Base exception class for all database-related errors.
 * This exception serves as the parent for specific database operation failures.
 */
public class DatabaseException extends Exception {
    
    /**
     * Constructs a new DatabaseException with the specified detail message.
     * 
     * @param message the detail message explaining the exception
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new DatabaseException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the exception
     * @param cause the cause of this exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
