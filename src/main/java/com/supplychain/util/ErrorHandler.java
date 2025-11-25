package com.supplychain.util;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for consistent error handling and logging across the application.
 * Provides methods to log exceptions with stack traces and display user-friendly error dialogs.
 * 
 * Requirements: 8.1, 8.3, 8.4
 */
public class ErrorHandler {
    
    /**
     * Logs an exception with full stack trace and displays a user-friendly error dialog.
     * Requirements: 8.4 - Log error details and present user-friendly messages
     * 
     * @param logger The logger to use for logging the exception
     * @param parentComponent The parent component for the error dialog
     * @param userMessage The user-friendly message to display
     * @param exception The exception that occurred
     * @param title The title for the error dialog
     */
    public static void handleException(Logger logger, Component parentComponent, 
                                      String userMessage, Exception exception, String title) {
        // Log the exception with full stack trace
        logger.log(Level.SEVERE, "Exception occurred: " + userMessage, exception);
        
        // Display user-friendly error dialog
        showErrorDialog(parentComponent, userMessage, title);
    }
    
    /**
     * Logs an exception with full stack trace and displays a user-friendly error dialog.
     * Uses default title "Error".
     * 
     * @param logger The logger to use for logging the exception
     * @param parentComponent The parent component for the error dialog
     * @param userMessage The user-friendly message to display
     * @param exception The exception that occurred
     */
    public static void handleException(Logger logger, Component parentComponent, 
                                      String userMessage, Exception exception) {
        handleException(logger, parentComponent, userMessage, exception, "Error");
    }
    
    /**
     * Logs a blockchain validation error with details and displays a user-friendly error dialog.
     * Requirements: 8.3 - Provide detailed error information about validation failures
     * 
     * @param logger The logger to use for logging the error
     * @param parentComponent The parent component for the error dialog
     * @param validationDetails Detailed information about which validation check failed
     * @param exception The exception that occurred (can be null)
     */
    public static void handleBlockchainValidationError(Logger logger, Component parentComponent, 
                                                       String validationDetails, Exception exception) {
        // Log detailed validation error
        if (exception != null) {
            logger.log(Level.SEVERE, "Blockchain validation failed: " + validationDetails, exception);
        } else {
            logger.severe("Blockchain validation failed: " + validationDetails);
        }
        
        // Display user-friendly error dialog with validation details
        String userMessage = "Blockchain validation failed.\n\n" +
                           "Details: " + validationDetails + "\n\n" +
                           "This may indicate data tampering or corruption.";
        
        showErrorDialog(parentComponent, userMessage, "Blockchain Validation Error");
    }
    
    /**
     * Logs an invalid input error and displays a user-friendly warning dialog.
     * Requirements: 8.1 - Provide specific error messages for invalid data
     * 
     * @param logger The logger to use for logging the error
     * @param parentComponent The parent component for the warning dialog
     * @param fieldName The name of the field with invalid input
     * @param reason The reason why the input is invalid
     */
    public static void handleInvalidInput(Logger logger, Component parentComponent, 
                                         String fieldName, String reason) {
        // Log the invalid input
        logger.warning("Invalid input for " + fieldName + ": " + reason);
        
        // Display user-friendly warning dialog
        String userMessage = "Invalid input for " + fieldName + ".\n\n" + reason;
        showWarningDialog(parentComponent, userMessage, "Validation Error");
    }
    
    /**
     * Logs a database connection error and displays a user-friendly error dialog.
     * Requirements: 8.2 - Handle database connection failures
     * 
     * @param logger The logger to use for logging the error
     * @param parentComponent The parent component for the error dialog
     * @param exception The exception that occurred
     */
    public static void handleDatabaseError(Logger logger, Component parentComponent, Exception exception) {
        // Log the database error with full stack trace
        logger.log(Level.SEVERE, "Database error occurred", exception);
        
        // Display user-friendly error dialog
        String userMessage = "A database error occurred.\n\n" +
                           "Please check your database connection and try again.\n" +
                           "If the problem persists, contact your system administrator.";
        
        showErrorDialog(parentComponent, userMessage, "Database Error");
    }
    
    /**
     * Displays a user-friendly error dialog.
     * 
     * @param parentComponent The parent component for the dialog
     * @param message The error message to display
     * @param title The title for the dialog
     */
    public static void showErrorDialog(Component parentComponent, String message, String title) {
        JOptionPane.showMessageDialog(
            parentComponent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Displays a user-friendly warning dialog.
     * 
     * @param parentComponent The parent component for the dialog
     * @param message The warning message to display
     * @param title The title for the dialog
     */
    public static void showWarningDialog(Component parentComponent, String message, String title) {
        JOptionPane.showMessageDialog(
            parentComponent,
            message,
            title,
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Displays a user-friendly information dialog.
     * 
     * @param parentComponent The parent component for the dialog
     * @param message The information message to display
     * @param title The title for the dialog
     */
    public static void showInfoDialog(Component parentComponent, String message, String title) {
        JOptionPane.showMessageDialog(
            parentComponent,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
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
