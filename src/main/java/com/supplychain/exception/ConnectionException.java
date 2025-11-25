package com.supplychain.exception;

/**
 * Exception thrown when database connection fails.
 * This occurs when the system cannot establish or maintain a connection
 * to the database server.
 */
public class ConnectionException extends DatabaseException {
    
    /**
     * Constructs a new ConnectionException with the specified detail message.
     * 
     * @param message the detail message explaining the connection failure
     */
    public ConnectionException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ConnectionException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the connection failure
     * @param cause the cause of this exception
     */
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
