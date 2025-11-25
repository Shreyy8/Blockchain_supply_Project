package com.supplychain.gui;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.exception.ConnectionException;
import com.supplychain.model.Transaction;
import com.supplychain.model.User;
import com.supplychain.service.AuthenticityVerifier;
import com.supplychain.service.ProductTraceabilityService;
import com.supplychain.service.TraceabilityReport;
import com.supplychain.service.VerificationResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dashboard for Retailers to trace product history and verify product authenticity.
 * Provides forms for searching product traceability and verifying authenticity.
 * 
 * Requirements: 6.1, 6.2, 6.3, 7.1, 7.2, 7.3
 */
public class RetailerDashboard extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(RetailerDashboard.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Services
    private BlockchainManager blockchainManager;
    private ProductTraceabilityService traceabilityService;
    private AuthenticityVerifier authenticityVerifier;
    
    // User information
    private User currentUser;
    
    // Product Traceability Components
    private JTextField traceProductIdField;
    private JButton traceProductButton;
    private JTextArea traceabilityReportArea;
    
    // Product History Table
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    
    // Authenticity Verification Components
    private JTextField verifyProductIdField;
    private JButton verifyAuthenticityButton;
    private JTextArea authenticityResultArea;
    
    /**
     * Constructor for RetailerDashboard.
     * 
     * @param currentUser The logged-in retailer user
     * @param blockchainManager The blockchain manager instance
     * @throws ConnectionException if database connection fails
     */
    public RetailerDashboard(User currentUser, BlockchainManager blockchainManager) 
            throws ConnectionException {
        this.currentUser = currentUser;
        this.blockchainManager = blockchainManager;
        this.traceabilityService = new ProductTraceabilityService(blockchainManager);
        this.authenticityVerifier = new AuthenticityVerifier(blockchainManager);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    /**
     * Initializes all GUI components.
     */
    private void initializeComponents() {
        // Product Traceability Components
        traceProductIdField = new JTextField(30);
        traceProductButton = new JButton("Generate Traceability Report");
        traceabilityReportArea = new JTextArea(12, 40);
        traceabilityReportArea.setEditable(false);
        traceabilityReportArea.setLineWrap(true);
        traceabilityReportArea.setWrapStyleWord(true);
        traceabilityReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Product History Table
        String[] columnNames = {"Transaction ID", "Type", "Timestamp", "From", "To", "Location"};
        historyTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // Authenticity Verification Components
        verifyProductIdField = new JTextField(30);
        verifyAuthenticityButton = new JButton("Verify Authenticity");
        authenticityResultArea = new JTextArea(10, 40);
        authenticityResultArea.setEditable(false);
        authenticityResultArea.setLineWrap(true);
        authenticityResultArea.setWrapStyleWord(true);
        authenticityResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }
    
    /**
     * Sets up the layout of the dashboard.
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create tabbed pane for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add Product Traceability Panel
        tabbedPane.addTab("Product Traceability", createTraceabilityPanel());
        
        // Add Authenticity Verification Panel
        tabbedPane.addTab("Authenticity Verification", createAuthenticityPanel());
        
        // Add header panel
        JPanel headerPanel = createHeaderPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the header panel with user information.
     * 
     * @return The header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
        
        JLabel titleLabel = new JLabel("Retailer Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel userLabel = new JLabel("User: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Creates the product traceability panel.
     * Requirements: 6.1, 6.2, 6.3 - Trace product history and origin
     * 
     * @return The traceability panel
     */
    private JPanel createTraceabilityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with search form
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Product Traceability");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Search form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Search Product History"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Product ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Product ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(traceProductIdField, gbc);
        
        // Button
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(traceProductButton, gbc);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        // Center panel with split pane for report and history table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        
        // Traceability report display area
        JScrollPane reportScrollPane = new JScrollPane(traceabilityReportArea);
        reportScrollPane.setBorder(BorderFactory.createTitledBorder("Traceability Report"));
        reportScrollPane.setPreferredSize(new Dimension(800, 200));
        
        // History table
        JScrollPane tableScrollPane = new JScrollPane(historyTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        tableScrollPane.setPreferredSize(new Dimension(800, 200));
        
        splitPane.setTopComponent(reportScrollPane);
        splitPane.setBottomComponent(tableScrollPane);
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Trace product history from origin to current location with complete transaction details");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoPanel.add(infoLabel);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the authenticity verification panel.
     * Requirements: 7.1, 7.2, 7.3 - Verify product authenticity
     * 
     * @return The authenticity verification panel
     */
    private JPanel createAuthenticityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with verification form
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Product Authenticity Verification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Verify Product"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Product ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Product ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(verifyProductIdField, gbc);
        
        // Button
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(verifyAuthenticityButton, gbc);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        // Authenticity result display area
        JScrollPane scrollPane = new JScrollPane(authenticityResultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Verification Results"));
        scrollPane.setPreferredSize(new Dimension(800, 300));
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Verify product authenticity using blockchain records and cryptographic validation");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoPanel.add(infoLabel);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Sets up event handlers for all buttons.
     */
    private void setupEventHandlers() {
        // Trace Product Button
        traceProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                traceProduct();
            }
        });
        
        // Verify Authenticity Button
        verifyAuthenticityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyAuthenticity();
            }
        });
    }
    
    /**
     * Traces product history and generates a traceability report.
     * Requirements: 6.1 - Retrieve all blockchain records for product
     * Requirements: 6.2 - Generate report with origin, transactions, and current status
     * Requirements: 6.3 - Display data in chronological order
     */
    private void traceProduct() {
        try {
            String productId = traceProductIdField.getText().trim();
            
            // Validate input
            if (productId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Product ID",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                traceProductIdField.requestFocus();
                return;
            }
            
            // Disable button during processing
            traceProductButton.setEnabled(false);
            traceProductButton.setText("Generating...");
            
            // Generate traceability report
            TraceabilityReport report = traceabilityService.generateTraceabilityReport(productId);
            
            // Display report
            StringBuilder sb = new StringBuilder();
            sb.append("=== PRODUCT TRACEABILITY REPORT ===\n\n");
            sb.append("Product ID: ").append(report.getProductId()).append("\n");
            sb.append("Report Generated: ").append(java.time.LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");
            
            // Display origin information
            sb.append("--- ORIGIN INFORMATION ---\n");
            if (report.getOrigin() != null) {
                sb.append("Origin: ").append(report.getOrigin()).append("\n");
            } else {
                sb.append("Origin: Not Available\n");
            }
            sb.append("\n");
            
            // Display current status
            sb.append("--- CURRENT STATUS ---\n");
            if (report.getCurrentLocation() != null) {
                sb.append("Current Location: ").append(report.getCurrentLocation()).append("\n");
            } else {
                sb.append("Current Location: Not Available\n");
            }
            
            if (report.getCurrentStatus() != null) {
                sb.append("Current Status: ").append(report.getCurrentStatus()).append("\n");
            } else {
                sb.append("Current Status: Not Available\n");
            }
            sb.append("\n");
            
            // Display transaction count
            sb.append("--- TRANSACTION SUMMARY ---\n");
            sb.append("Total Transactions: ").append(report.getTransactions().size()).append("\n\n");
            
            // Display completeness status
            if (report.isComplete()) {
                sb.append("✓ Report is complete with all required information\n");
            } else {
                sb.append("⚠ Report is incomplete. Missing information:\n");
                for (String missing : report.getMissingInformation()) {
                    sb.append("  - ").append(missing).append("\n");
                }
            }
            
            traceabilityReportArea.setText(sb.toString());
            traceabilityReportArea.setCaretPosition(0); // Scroll to top
            
            // Display transaction history in table
            displayTransactionHistory(report.getTransactions());
            
            LOGGER.info("Generated traceability report for product: " + productId);
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error tracing product", e);
            JOptionPane.showMessageDialog(this,
                "Validation error: " + e.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error tracing product", e);
            JOptionPane.showMessageDialog(this,
                "Failed to trace product: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Re-enable button
            traceProductButton.setEnabled(true);
            traceProductButton.setText("Generate Traceability Report");
        }
    }
    
    /**
     * Displays transaction history in the table.
     * Requirements: 6.3 - Chronological ordering from origin to current location
     * 
     * @param transactions List of transactions to display
     */
    private void displayTransactionHistory(List<Transaction> transactions) {
        // Clear existing data
        historyTableModel.setRowCount(0);
        
        // Populate table with transaction data (already in chronological order)
        for (Transaction transaction : transactions) {
            String transactionId = transaction.getTransactionId();
            String type = transaction.getTransactionType();
            String timestamp = transaction.getTimestamp() != null ? 
                transaction.getTimestamp().format(DATE_FORMATTER) : "N/A";
            
            // Extract from and to parties and location from transaction data
            Map<String, Object> data = transaction.getTransactionData();
            String fromParty = data.get("fromParty") != null ? data.get("fromParty").toString() : "N/A";
            String toParty = data.get("toParty") != null ? data.get("toParty").toString() : "N/A";
            
            String location = "N/A";
            if ("PRODUCT_CREATION".equals(type)) {
                location = data.get("origin") != null ? data.get("origin").toString() : "N/A";
            } else if ("PRODUCT_TRANSFER".equals(type)) {
                String fromLoc = data.get("fromLocation") != null ? data.get("fromLocation").toString() : "";
                String toLoc = data.get("toLocation") != null ? data.get("toLocation").toString() : "";
                location = fromLoc + " → " + toLoc;
            }
            
            Object[] row = {transactionId, type, timestamp, fromParty, toParty, location};
            historyTableModel.addRow(row);
        }
    }
    
    /**
     * Verifies product authenticity using blockchain records.
     * Requirements: 7.1 - Validate product identifier against blockchain records
     * Requirements: 7.2 - Check transaction chain validity with cryptographic linkage
     * Requirements: 7.3 - Provide clear confirmation or rejection status
     */
    private void verifyAuthenticity() {
        try {
            String productId = verifyProductIdField.getText().trim();
            
            // Validate input
            if (productId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Product ID",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                verifyProductIdField.requestFocus();
                return;
            }
            
            // Disable button during processing
            verifyAuthenticityButton.setEnabled(false);
            verifyAuthenticityButton.setText("Verifying...");
            
            // Verify product authenticity
            VerificationResult result = authenticityVerifier.verifyProductAuthenticity(productId);
            
            // Display result
            StringBuilder sb = new StringBuilder();
            sb.append("=== PRODUCT AUTHENTICITY VERIFICATION ===\n\n");
            sb.append("Product ID: ").append(result.getProductId()).append("\n");
            sb.append("Verification Date: ").append(java.time.LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");
            
            // Display status
            sb.append("--- VERIFICATION STATUS ---\n");
            if (result.isAuthentic()) {
                sb.append("Status: ").append(result.getStatus()).append(" ✓\n\n");
                sb.append("✓ PRODUCT IS AUTHENTIC\n\n");
                sb.append("The product has been verified as genuine.\n");
                sb.append("All blockchain records are valid with proper cryptographic linkage.\n");
            } else {
                sb.append("Status: ").append(result.getStatus()).append(" ✗\n\n");
                sb.append("✗ PRODUCT AUTHENTICITY CANNOT BE CONFIRMED\n\n");
                sb.append("⚠ WARNING: This product may be counterfeit or have invalid records.\n");
            }
            
            // Display reasons
            sb.append("\n--- VERIFICATION DETAILS ---\n");
            for (String reason : result.getReasons()) {
                sb.append("• ").append(reason).append("\n");
            }
            
            authenticityResultArea.setText(sb.toString());
            authenticityResultArea.setCaretPosition(0); // Scroll to top
            
            LOGGER.info("Verified product authenticity: " + productId + " - Result: " + result.isAuthentic());
            
            // Show dialog for rejected products
            if (!result.isAuthentic()) {
                JOptionPane.showMessageDialog(this,
                    "⚠ WARNING: Product authenticity cannot be confirmed!\n\n" +
                    "This product may be counterfeit or have invalid records.\n" +
                    "Please review the verification details.",
                    "Authenticity Verification Failed",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "✓ Product is authentic!\n\n" +
                    "The product has been verified as genuine.",
                    "Authenticity Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error verifying authenticity", e);
            JOptionPane.showMessageDialog(this,
                "Validation error: " + e.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verifying authenticity", e);
            JOptionPane.showMessageDialog(this,
                "Failed to verify authenticity: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Re-enable button
            verifyAuthenticityButton.setEnabled(true);
            verifyAuthenticityButton.setText("Verify Authenticity");
        }
    }
    
    /**
     * Gets the blockchain manager instance.
     * 
     * @return The blockchain manager
     */
    public BlockchainManager getBlockchainManager() {
        return blockchainManager;
    }
    
    /**
     * Gets the traceability service instance.
     * 
     * @return The traceability service
     */
    public ProductTraceabilityService getTraceabilityService() {
        return traceabilityService;
    }
    
    /**
     * Gets the authenticity verifier instance.
     * 
     * @return The authenticity verifier
     */
    public AuthenticityVerifier getAuthenticityVerifier() {
        return authenticityVerifier;
    }
}
