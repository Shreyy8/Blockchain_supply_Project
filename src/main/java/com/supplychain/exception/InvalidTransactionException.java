package com.supplychain.exception;

/**
 * Exception thrown when transaction validation fails.
 * This occurs when a transaction has incomplete data, invalid format,
 * or does not meet business validation rules.
 */
public class InvalidTransactionException extends BlockchainException {
    
    /**
     * Constructs a new InvalidTransactionException with the specified detail message.
     * 
     * @param message the detail message explaining why the transaction is invalid
     */
    public InvalidTransactionException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new InvalidTransactionException with the specified detail message and cause.
     * 
     * @param message the detail message explaining why the transaction is invalid
     * @param cause the cause of this exception
     */
    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
