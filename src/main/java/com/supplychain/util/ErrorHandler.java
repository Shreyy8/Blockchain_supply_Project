package com.supplychain.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for consistent error handling and logging across the web application.
 * Provides methods to log exceptions with stack traces and format user-friendly error messages.
 * 
 * Requirements: 8.1, 8.3, 8.4
 */
public class ErrorHandler {
    
    /**
     * Logs an exception with full stack trace and returns a user-friendly error message.
     * Requirements: 8.4 - Log error details and present user-friendly messages
     * 
     * @param logger The logger to use for logging the exception
     * @param userMessage The user-friendly message to display
     * @param exception The exception that occurred
     * @param title The title for the error
     * @return The formatted user-friendly error message
     */
    public static String handleException(Logger logger, String userMessage, Exception exception, String title) {
        // Log the exception with full stack trace
        logger.log(Level.SEVERE, "Exception occurred: " + userMessage, exception);
        
        // Return formatted error message for web display
        return formatErrorMessage(title, userMessage);
    }
    
    /**
     * Logs an exception with full stack trace and returns a user-friendly error message.
     * Uses default title "Error".
     * 
     * @param logger The logger to use for logging the exception
     * @param userMessage The user-friendly message to display
     * @param exception The exception that occurred
     * @return The formatted user-friendly error message
     */
    public static String handleException(Logger logger, String userMessage, Exception exception) {
        return handleException(logger, userMessage, exception, "Error");
    }
    
    /**
     * Logs a blockchain validation error with details and returns a user-friendly error message.
     * Requirements: 8.3 - Provide detailed error information about validation failures
     * 
     * @param logger The logger to use for logging the error
     * @param validationDetails Detailed information about which validation check failed
     * @param exception The exception that occurred (can be null)
     * @return The formatted user-friendly error message
     */
    public static String handleBlockchainValidationError(Logger logger, String validationDetails, Exception exception) {
        // Log detailed validation error
        if (exception != null) {
            logger.log(Level.SEVERE, "Blockchain validation failed: " + validationDetails, exception);
        } else {
            logger.severe("Blockchain validation failed: " + validationDetails);
        }
        
        // Return user-friendly error message with validation details
        String userMessage = "Blockchain validation failed. Details: " + validationDetails + 
                           ". This may indicate data tampering or corruption.";
        
        return formatErrorMessage("Blockchain Validation Error", userMessage);
    }
    
    /**
     * Logs an invalid input error and returns a user-friendly warning message.
     * Requirements: 8.1 - Provide specific error messages for invalid data
     * 
     * @param logger The logger to use for logging the error
     * @param fieldName The name of the field with invalid input
     * @param reason The reason why the input is invalid
     * @return The formatted user-friendly warning message
     */
    public static String handleInvalidInput(Logger logger, String fieldName, String reason) {
        // Log the invalid input
        logger.warning("Invalid input for " + fieldName + ": " + reason);
        
        // Return user-friendly warning message
        String userMessage = "Invalid input for " + fieldName + ". " + reason;
        return formatErrorMessage("Validation Error", userMessage);
    }
    
    /**
     * Logs a database connection error and returns a user-friendly error message.
     * Requirements: 8.2 - Handle database connection failures
     * 
     * @param logger The logger to use for logging the error
     * @param exception The exception that occurred
     * @return The formatted user-friendly error message
     */
    public static String handleDatabaseError(Logger logger, Exception exception) {
        // Log the database error with full stack trace
        logger.log(Level.SEVERE, "Database error occurred", exception);
        
        // Return user-friendly error message
        String userMessage = "A database error occurred. Please check your database connection and try again. " +
                           "If the problem persists, contact your system administrator.";
        
        return formatErrorMessage("Database Error", userMessage);
    }
    
    /**
     * Formats an error message for web display.
     * 
     * @param title The title for the error
     * @param message The error message to display
     * @return The formatted error message
     */
    public static String formatErrorMessage(String title, String message) {
        return title + ": " + message;
    }
    
    /**
     * Formats a warning message for web display.
     * 
     * @param title The title for the warning
     * @param message The warning message to display
     * @return The formatted warning message
     */
    public static String formatWarningMessage(String title, String message) {
        return title + ": " + message;
    }
    
    /**
     * Formats an information message for web display.
     * 
     * @param title The title for the information
     * @param message The information message to display
     * @return The formatted information message
     */
    public static String formatInfoMessage(String title, String message) {
        return title + ": " + message;
    }
    
    /**
     * Formats an exception message for user display.
     * Removes technical details and stack traces, keeping only the essential message.
     * 
     * @param exception The exception to format
     * @return A user-friendly error message
     */
    public static String formatExceptionForUser(Exception exception) {
        String message = exception.getMessage();
        
        // If message is null or empty, provide a generic message
        if (message == null || message.trim().isEmpty()) {
            return "An unexpected error occurred. Please try again.";
        }
        
        // Remove any stack trace information from the message
        int stackTraceIndex = message.indexOf("\n\tat ");
        if (stackTraceIndex > 0) {
            message = message.substring(0, stackTraceIndex);
        }
        
        // Ensure the message is not too long
        if (message.length() > 500) {
            message = message.substring(0, 497) + "...";
        }
        
        return message;
    }
    
    /**
     * Logs an informational message.
     * 
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void logInfo(Logger logger, String message) {
        logger.info(message);
    }
    
    /**
     * Logs a warning message.
     * 
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void logWarning(Logger logger, String message) {
        logger.warning(message);
    }
    
    /**
     * Logs a severe error message.
     * 
     * @param logger The logger to use
     * @param message The message to log
     */
    public static void logSevere(Logger logger, String message) {
        logger.severe(message);
    }
}
