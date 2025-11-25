package com.supplychain.gui;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.exception.ConnectionException;
import com.supplychain.model.User;
import com.supplychain.model.UserRole;
import com.supplychain.service.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main application frame that manages navigation and dashboard switching.
 * Provides role-based dashboard access and logout functionality.
 * 
 * Requirements: 9.1 - Use inheritance to model user behaviors and role-specific behaviors
 */
public class MainApplicationFrame extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainApplicationFrame.class.getName());
    
    // Services
    private AuthenticationService authService;
    private BlockchainManager blockchainManager;
    
    // User information
    private User currentUser;
    private String sessionId;
    
    // GUI Components
    private JPanel contentPanel;
    private JMenuBar menuBar;
    
    // Dashboard panels
    private SupplyChainManagerDashboard managerDashboard;
    private SupplierDashboard supplierDashboard;
    private RetailerDashboard retailerDashboard;
    
    /**
     * Constructor for MainApplicationFrame.
     * 
     * @param currentUser The logged-in user
     * @param sessionId The session ID for the current user
     * @param blockchainManager The blockchain manager instance
     * @throws ConnectionException if database connection fails
     */
    public MainApplicationFrame(User currentUser, String sessionId, BlockchainManager blockchainManager) 
            throws ConnectionException {
        super("Blockchain Supply Chain Management System");
        
        this.currentUser = currentUser;
        this.sessionId = sessionId;
        this.blockchainManager = blockchainManager;
        this.authService = AuthenticationService.getInstance();
        
        initializeComponents();
        setupMenuBar();
        setupLayout();
        loadDashboard();
        
        // Frame settings
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); // Center on screen
        
        // Add window listener for close operation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }
    
    /**
     * Initializes all GUI components.
     */
    private void initializeComponents() {
        contentPanel = new JPanel(new BorderLayout());
    }
    
    /**
     * Sets up the menu bar with navigation options.
     * Requirements: 9.1 - Add menu bar with navigation options
     */
    private void setupMenuBar() {
        menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setMnemonic('L');
        logoutItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('X');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExit();
            }
        });
        
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        
        JMenuItem refreshItem = new JMenuItem("Refresh Dashboard");
        refreshItem.setMnemonic('R');
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshDashboard();
            }
        });
        
        viewMenu.add(refreshItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        
        JMenuItem userInfoItem = new JMenuItem("User Information");
        userInfoItem.setMnemonic('U');
        userInfoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserInfoDialog();
            }
        });
        
        helpMenu.add(userInfoItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Sets up the layout of the main frame.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Loads the appropriate dashboard based on user role.
     * Requirements: 9.1 - Implement dashboard switching based on user role
     */
    private void loadDashboard() {
        try {
            // Clear existing content
            contentPanel.removeAll();
            
            // Load dashboard based on user role
            switch (currentUser.getRole()) {
                case MANAGER:
                    loadManagerDashboard();
                    break;
                case SUPPLIER:
                    loadSupplierDashboard();
                    break;
                case RETAILER:
                    loadRetailerDashboard();
                    break;
                default:
                    LOGGER.severe("Unknown user role: " + currentUser.getRole());
                    JOptionPane.showMessageDialog(this,
                        "Unknown user role. Please contact system administrator.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    handleLogout();
                    return;
            }
            
            // Refresh the display
            contentPanel.revalidate();
            contentPanel.repaint();
            
            LOGGER.info("Loaded dashboard for user role: " + currentUser.getRole());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard", e);
            JOptionPane.showMessageDialog(this,
                "Failed to load dashboard: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Loads the Supply Chain Manager dashboard.
     * 
     * @throws ConnectionException if database connection fails
     */
    private void loadManagerDashboard() throws ConnectionException {
        if (managerDashboard == null) {
            managerDashboard = new SupplyChainManagerDashboard(currentUser, blockchainManager);
        }
        contentPanel.add(managerDashboard, BorderLayout.CENTER);
        setTitle("Blockchain Supply Chain Management System - Manager Dashboard");
    }
    
    /**
     * Loads the Supplier dashboard.
     * 
     * @throws ConnectionException if database connection fails
     */
    private void loadSupplierDashboard() throws ConnectionException {
        if (supplierDashboard == null) {
            supplierDashboard = new SupplierDashboard(currentUser, blockchainManager);
        }
        contentPanel.add(supplierDashboard, BorderLayout.CENTER);
        setTitle("Blockchain Supply Chain Management System - Supplier Dashboard");
    }
    
    /**
     * Loads the Retailer dashboard.
     * 
     * @throws ConnectionException if database connection fails
     */
    private void loadRetailerDashboard() throws ConnectionException {
        if (retailerDashboard == null) {
            retailerDashboard = new RetailerDashboard(currentUser, blockchainManager);
        }
        contentPanel.add(retailerDashboard, BorderLayout.CENTER);
        setTitle("Blockchain Supply Chain Management System - Retailer Dashboard");
    }
    
    /**
     * Refreshes the current dashboard.
     */
    private void refreshDashboard() {
        try {
            // Recreate the dashboard for the current user role
            switch (currentUser.getRole()) {
                case MANAGER:
                    managerDashboard = null;
                    break;
                case SUPPLIER:
                    supplierDashboard = null;
                    break;
                case RETAILER:
                    retailerDashboard = null;
                    break;
            }
            
            loadDashboard();
            
            JOptionPane.showMessageDialog(this,
                "Dashboard refreshed successfully!",
                "Refresh",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error refreshing dashboard", e);
            JOptionPane.showMessageDialog(this,
                "Failed to refresh dashboard: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles user logout.
     * Requirements: 9.1 - Add logout functionality
     */
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Logout from authentication service
                authService.logout(sessionId);
                
                LOGGER.info("User logged out: " + currentUser.getUsername());
                
                // Close this frame
                dispose();
                
                // Show login frame
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        LoginFrame loginFrame = new LoginFrame();
                        loginFrame.setVisible(true);
                    }
                });
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error during logout", e);
                JOptionPane.showMessageDialog(this,
                    "Error during logout: " + e.getMessage(),
                    "Logout Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Handles application exit.
     */
    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Logout if user is logged in
                if (sessionId != null) {
                    authService.logout(sessionId);
                }
                
                LOGGER.info("Application exiting");
                
                // Exit the application
                System.exit(0);
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error during exit", e);
                // Exit anyway
                System.exit(1);
            }
        }
    }
    
    /**
     * Shows the About dialog.
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Blockchain-based Supply Chain Management System\n\n" +
            "Version 1.0\n\n" +
            "This system provides transparency and traceability in supply chain\n" +
            "operations by maintaining tamper-proof records of transactions\n" +
            "using blockchain technology.\n\n" +
            "© 2024 Supply Chain Management System",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows the User Information dialog.
     */
    private void showUserInfoDialog() {
        StringBuilder info = new StringBuilder();
        info.append("=== USER INFORMATION ===\n\n");
        info.append("User ID: ").append(currentUser.getUserId()).append("\n");
        info.append("Username: ").append(currentUser.getUsername()).append("\n");
        info.append("Email: ").append(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A").append("\n");
        info.append("Role: ").append(currentUser.getRole()).append("\n");
        info.append("Session ID: ").append(sessionId).append("\n\n");
        
        info.append("=== PERMISSIONS ===\n\n");
        for (String permission : currentUser.getPermissions()) {
            info.append("• ").append(permission).append("\n");
        }
        
        JTextArea textArea = new JTextArea(info.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this,
            scrollPane,
            "User Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Gets the current user.
     * 
     * @return The current user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Gets the session ID.
     * 
     * @return The session ID
     */
    public String getSessionId() {
        return sessionId;
    }
    
    /**
     * Gets the blockchain manager.
     * 
     * @return The blockchain manager
     */
    public BlockchainManager getBlockchainManager() {
        return blockchainManager;
    }
}
