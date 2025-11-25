package com.supplychain.exception;

/**
 * Exception thrown when CRUD operations fail.
 * This occurs when database operations such as insert, update, delete,
 * or query operations encounter errors.
 */
public class DataAccessException extends DatabaseException {
    
    /**
     * Constructs a new DataAccessException with the specified detail message.
     * 
     * @param message the detail message explaining the data access failure
     */
    public DataAccessException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new DataAccessException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the data access failure
     * @param cause the cause of this exception
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
