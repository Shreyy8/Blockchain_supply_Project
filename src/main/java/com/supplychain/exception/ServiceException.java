package com.supplychain.exception;

/**
 * Exception thrown when service layer operations fail.
 */
public class ServiceException extends Exception {
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}