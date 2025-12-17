package com.supplychain.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation and sanitization.
 * Provides comprehensive validation for all user inputs.
 */
public class ValidationUtil {
    
    // Regex patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );
    
    private static final Pattern PRODUCT_ID_PATTERN = Pattern.compile(
        "^PROD[A-Z0-9]{8}$"
    );
    
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9\\s\\-_.,()]+$"
    );

    /**
     * Validates if a string is not null and not empty after trimming.
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates username format and length.
     */
    public static ValidationResult validateUsername(String username) {
        if (!isNotEmpty(username)) {
            return ValidationResult.error("Username is required");
        }
        
        if (username.length() < 3 || username.length() > 20) {
            return ValidationResult.error("Username must be between 3 and 20 characters");
        }
        
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return ValidationResult.error("Username can only contain letters, numbers, and underscores");
        }
        
        return ValidationResult.success();
    }

    /**
     * Validates password strength.
     */
    public static ValidationResult validatePassword(String password) {
        if (!isNotEmpty(password)) {
            return ValidationResult.error("Password is required");
        }
        
        if (password.length() < 6) {
            return ValidationResult.error("Password must be at least 6 characters long");
        }
        
        if (password.length() > 100) {
            return ValidationResult.error("Password is too long (maximum 100 characters)");
        }
        
        return ValidationResult.success();
    }

    /**
     * Validates email format.
     */
    public static ValidationResult validateEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                return ValidationResult.error("Invalid email format");
            }
        }
        return ValidationResult.success();
    }

    /**
     * Validates product name.
     */
    public static ValidationResult validateProductName(String name) {
        if (!isNotEmpty(name)) {
            return ValidationResult.error("Product name is required");
        }
        
        if (name.length() < 2 || name.length() > 200) {
            return ValidationResult.error("Product name must be between 2 and 200 characters");
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(name).matches()) {
            return ValidationResult.error("Product name contains invalid characters");
        }
        
        return ValidationResult.success();
    }

    /**
     * Validates product description.
     */
    public static ValidationResult validateProductDescription(String description) {
        if (description != null && description.length() > 1000) {
            return ValidationResult.error("Product description is too long (maximum 1000 characters)");
        }
        return ValidationResult.success();
    }

    /**
     * Validates location/origin.
     */
    public static ValidationResult validateLocation(String location) {
        if (!isNotEmpty(location)) {
            return ValidationResult.error("Location is required");
        }
        
        if (location.length() < 2 || location.length() > 200) {
            return ValidationResult.error("Location must be between 2 and 200 characters");
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(location).matches()) {
            return ValidationResult.error("Location contains invalid characters");
        }
        
        return ValidationResult.success();
    }

    /**
     * Validates product ID format.
     */
    public static ValidationResult validateProductId(String productId) {
        if (!isNotEmpty(productId)) {
            return ValidationResult.error("Product ID is required");
        }
        
        if (!PRODUCT_ID_PATTERN.matcher(productId).matches()) {
            return ValidationResult.error("Invalid product ID format");
        }
        
        return ValidationResult.success();
    }

    /**
     * Sanitizes input by removing potentially harmful characters.
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("&", "&amp;");
    }

    /**
     * Validates numeric input within range.
     */
    public static ValidationResult validateNumericRange(String value, int min, int max, String fieldName) {
        if (!isNotEmpty(value)) {
            return ValidationResult.error(fieldName + " is required");
        }
        
        try {
            int numValue = Integer.parseInt(value);
            if (numValue < min || numValue > max) {
                return ValidationResult.error(fieldName + " must be between " + min + " and " + max);
            }
            return ValidationResult.success();
        } catch (NumberFormatException e) {
            return ValidationResult.error(fieldName + " must be a valid number");
        }
    }

    /**
     * Result class for validation operations.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}