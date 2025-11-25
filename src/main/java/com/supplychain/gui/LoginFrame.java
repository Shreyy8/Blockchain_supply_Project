package com.supplychain.gui;

import com.supplychain.exception.ConnectionException;
import com.supplychain.exception.DatabaseException;
import com.supplychain.model.User;
import com.supplychain.service.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Login screen GUI for the Blockchain-based Supply Chain Management System.
 * Provides username and password fields for user authentication.
 * 
 * Requirements: 9.1
 */
public class LoginFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(LoginFrame.class.getName());
    
    // GUI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    // Services
    private AuthenticationService authService;
    
    // Session information
    private String currentSessionId;
    private User currentUser;
    
    /**
     * Constructor for LoginFrame.
     * Initializes the GUI components and authentication service.
     */
    public LoginFrame() {
        super("Supply Chain Management System - Login");
        
        try {
            authService = AuthenticationService.getInstance();
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize authentication service", e);
            JOptionPane.showMessageDialog(this,
                "Failed to connect to database. Please check your database configuration.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Frame settings
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
    }
    
    /**
     * Initializes all GUI components.
     */
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    /**
     * Sets up the layout of the login form.
     */
    private void setupLayout() {
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Blockchain Supply Chain System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        
        // Form panel with GridBagLayout for better control
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(usernameField, gbc);
        
        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(passwordField, gbc);
        
        // Login button panel
        JPanel buttonPanel = new JPanel();
        loginButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(loginButton);
        
        // Status label panel
        JPanel statusPanel = new JPanel();
        statusPanel.add(statusLabel);
        
        // Add all panels to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add status label below button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(statusPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Sets up event handlers for the login button and enter key.
     */
    private void setupEventHandlers() {
        // Login button action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Allow Enter key to trigger login from password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Allow Enter key to trigger login from username field
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.requestFocus();
            }
        });
    }
    
    /**
     * Performs the login operation.
     * Validates input, authenticates user, and handles success/failure.
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Clear previous status
        statusLabel.setText(" ");
        
        // Validate input
        if (username.isEmpty()) {
            displayError("Please enter a username");
            usernameField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            displayError("Please enter a password");
            passwordField.requestFocus();
            return;
        }
        
        // Disable login button during authentication
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        
        // Perform authentication in a separate thread to avoid blocking UI
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return authService.login(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    String sessionId = get();
                    
                    if (sessionId != null) {
                        // Login successful
                        currentSessionId = sessionId;
                        currentUser = authService.getUserBySession(sessionId);
                        onLoginSuccess();
                    } else {
                        // Login failed
                        displayError("Invalid username or password");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error during login", e);
                    displayError("Login failed: " + e.getMessage());
                } finally {
                    // Re-enable login button
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Displays an error message to the user.
     * 
     * @param message The error message to display
     */
    private void displayError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.RED);
        
        // Also show a dialog for critical errors
        if (message.contains("failed") || message.contains("error")) {
            JOptionPane.showMessageDialog(this,
                message,
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles successful login.
     * Opens the main application frame with the appropriate dashboard.
     * Requirements: 9.1 - Navigate to appropriate dashboard based on user role
     */
    private void onLoginSuccess() {
        LOGGER.info("Login successful for user: " + currentUser.getUsername() + 
                   " (Role: " + currentUser.getRole() + ")");
        
        try {
            // Create blockchain manager instance with difficulty 4
            com.supplychain.blockchain.BlockchainManager blockchainManager = 
                new com.supplychain.blockchain.BlockchainManager(4);
            
            // Create and show main application frame
            MainApplicationFrame mainFrame = new MainApplicationFrame(
                currentUser, 
                currentSessionId, 
                blockchainManager
            );
            mainFrame.setVisible(true);
            
            // Close login frame
            this.dispose();
            
            LOGGER.info("Opened main application frame for user: " + currentUser.getUsername());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error opening main application frame", e);
            JOptionPane.showMessageDialog(this,
                "Failed to open application: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Gets the current session ID.
     * 
     * @return The current session ID, or null if not logged in
     */
    public String getCurrentSessionId() {
        return currentSessionId;
    }
    
    /**
     * Gets the current logged-in user.
     * 
     * @return The current User object, or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Main method to launch the login screen.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to set system look and feel", e);
        }
        
        // Create and display the login frame on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
