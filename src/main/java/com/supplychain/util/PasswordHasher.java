package com.supplychain.util;

import com.supplychain.service.AuthenticationService;

/**
 * Utility to generate SHA-256 hashed passwords for database insertion.
 */
public class PasswordHasher {
    public static void main(String[] args) {
        String[] passwords = {"admin123", "pass123"};
        
        System.out.println("Password Hashes for Database:");
        System.out.println("================================");
        
        for (String password : passwords) {
            String hash = AuthenticationService.hashPassword(password);
            System.out.println("Password: " + password);
            System.out.println("Hash:     " + hash);
            System.out.println();
        }
    }
}
