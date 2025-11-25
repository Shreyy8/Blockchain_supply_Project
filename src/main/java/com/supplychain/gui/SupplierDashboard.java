package com.supplychain.gui;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.exception.ConnectionException;
import com.supplychain.model.*;
import com.supplychain.service.TransactionVerificationService;
import com.supplychain.service.TransactionVerificationResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dashboard for Suppliers to record transactions and verify blockchain records.
 * Provides forms for creating different transaction types and verifying transaction integrity.
 * 
 * Requirements: 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3
 */
public class SupplierDashboard extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(SupplierDashboard.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Services
    private BlockchainManager blockchainManager;
    private TransactionVerificationService verificationService;
    
    // User information
    private User currentUser;
    
    // Transaction Recording Components - Product Creation
    private JTextField productIdField;
    private JTextField productNameField;
    private JTextArea productDescriptionArea;
    private JTextField originField;
    private JButton createProductButton;
    
    // Transaction Recording Components - Product Transfer
    private JTextField transferProductIdField;
    private JTextField toPartyField;
    private JTextField fromLocationField;
    private JTextField toLocationField;
    private JComboBox<ProductStatus> statusComboBox;
    private JButton transferProductButton;
    
    // Transaction Verification Components
    private JTextField verifyTransactionIdField;
    private JButton verifyTransactionButton;
    private JTextArea verificationResultArea;
    private JButton validateBlockchainButton;
    
    // Transaction History Display
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    private JButton refreshTransactionsButton;
    
    /**
     * Constructor for SupplierDashboard.
     * 
     * @param currentUser The logged-in supplier user
     * @param blockchainManager The blockchain manager instance
     * @throws ConnectionException if database connection fails
     */
    public SupplierDashboard(User currentUser, BlockchainManager blockchainManager) 
            throws ConnectionException {
        this.currentUser = currentUser;
        this.blockchainManager = blockchainManager;
        this.verificationService = new TransactionVerificationService(blockchainManager);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Load initial data
        loadTransactionData();
    }
    
    /**
     * Initializes all GUI components.
     */
    private void initializeComponents() {
        // Product Creation Components
        productIdField = new JTextField(20);
        productNameField = new JTextField(30);
        productDescriptionArea = new JTextArea(3, 30);
        productDescriptionArea.setLineWrap(true);
        productDescriptionArea.setWrapStyleWord(true);
        originField = new JTextField(30);
        createProductButton = new JButton("Create Product Transaction");
        
        // Product Transfer Components
        transferProductIdField = new JTextField(20);
        toPartyField = new JTextField(20);
        fromLocationField = new JTextField(30);
        toLocationField = new JTextField(30);
        statusComboBox = new JComboBox<>(ProductStatus.values());
        transferProductButton = new JButton("Record Transfer Transaction");
        
        // Transaction Verification Components
        verifyTransactionIdField = new JTextField(30);
        verifyTransactionButton = new JButton("Verify Transaction");
        validateBlockchainButton = new JButton("Validate Blockchain Integrity");
        verificationResultArea = new JTextArea(10, 40);
        verificationResultArea.setEditable(false);
        verificationResultArea.setLineWrap(true);
        verificationResultArea.setWrapStyleWord(true);
        verificationResultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Transaction History Components
        String[] columnNames = {"Transaction ID", "Type", "Timestamp", "Product ID", "Status"};
        transactionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        transactionTable = new JTable(transactionTableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        refreshTransactionsButton = new JButton("Refresh Transactions");
    }
    
    /**
     * Sets up the layout of the dashboard.
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create tabbed pane for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add Transaction Recording Panel
        tabbedPane.addTab("Record Transactions", createTransactionRecordingPanel());
        
        // Add Transaction Verification Panel
        tabbedPane.addTab("Verify Transactions", createTransactionVerificationPanel());
        
        // Add Transaction History Panel
        tabbedPane.addTab("Transaction History", createTransactionHistoryPanel());
        
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
        
        JLabel titleLabel = new JLabel("Supplier Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel userLabel = new JLabel("User: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Creates the transaction recording panel with forms for different transaction types.
     * Requirements: 4.1, 4.2, 4.3, 4.4 - Record transaction details on blockchain
     * 
     * @return The transaction recording panel
     */
    private JPanel createTransactionRecordingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create sub-panels for different transaction types
        JPanel formsPanel = new JPanel();
        formsPanel.setLayout(new BoxLayout(formsPanel, BoxLayout.Y_AXIS));
        
        // Add Product Creation Form
        formsPanel.add(createProductCreationForm());
        formsPanel.add(Box.createVerticalStrut(20));
        
        // Add Product Transfer Form
        formsPanel.add(createProductTransferForm());
        
        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(formsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the product creation form.
     * Requirements: 4.1, 4.4 - Transaction validation and completeness
     * 
     * @return The product creation form panel
     */
    private JPanel createProductCreationForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Product"));
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
        formPanel.add(productIdField, gbc);
        
        // Product Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Product Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(productNameField, gbc);
        
        // Product Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.anchor = GridBagConstraints.CENTER;
        JScrollPane descScrollPane = new JScrollPane(productDescriptionArea);
        formPanel.add(descScrollPane, gbc);
        
        // Origin
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Origin:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(originField, gbc);
        
        // Button
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createProductButton, gbc);
        
        return formPanel;
    }
    
    /**
     * Creates the product transfer form.
     * Requirements: 4.1, 4.4 - Transaction validation and completeness
     * 
     * @return The product transfer form panel
     */
    private JPanel createProductTransferForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Transfer Product"));
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
        formPanel.add(transferProductIdField, gbc);
        
        // To Party
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("To Party:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(toPartyField, gbc);
        
        // From Location
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("From Location:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(fromLocationField, gbc);
        
        // To Location
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("To Location:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(toLocationField, gbc);
        
        // Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("New Status:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(statusComboBox, gbc);
        
        // Button
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(transferProductButton, gbc);
        
        return formPanel;
    }
    
    /**
     * Creates the transaction verification panel.
     * Requirements: 5.1, 5.2, 5.3 - Verify transaction records on blockchain
     * 
     * @return The transaction verification panel
     */
    private JPanel createTransactionVerificationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with verification form
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Transaction Verification");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Verify Transaction Record"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Transaction ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Transaction ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(verifyTransactionIdField, gbc);
        
        // Buttons
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(verifyTransactionButton);
        buttonPanel.add(validateBlockchainButton);
        formPanel.add(buttonPanel, gbc);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        // Verification result display area
        JScrollPane scrollPane = new JScrollPane(verificationResultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Verification Results"));
        scrollPane.setPreferredSize(new Dimension(800, 300));
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Verify that transaction records are accurately stored and detect any tampering");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoPanel.add(infoLabel);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the transaction history panel.
     * 
     * @return The transaction history panel
     */
    private JPanel createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(refreshTransactionsButton);
        
        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("View all your recorded transactions");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoPanel.add(infoLabel);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Sets up event handlers for all buttons.
     */
    private void setupEventHandlers() {
        // Create Product Button
        createProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createProductTransaction();
            }
        });
        
        // Transfer Product Button
        transferProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferProductTransaction();
            }
        });
        
        // Verify Transaction Button
        verifyTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyTransaction();
            }
        });
        
        // Validate Blockchain Button
        validateBlockchainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateBlockchainIntegrity();
            }
        });
        
        // Refresh Transactions Button
        refreshTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTransactionData();
            }
        });
    }
    
    /**
     * Creates a product creation transaction and adds it to the blockchain.
     * Requirements: 4.1 - Validate transaction data format and completeness
     * Requirements: 4.2 - Create new block with transaction details and cryptographic hash
     * Requirements: 4.4 - Include timestamp, supplier identifier, product details, and transaction type
     */
    private void createProductTransaction() {
        try {
            // Get input values
            String productId = productIdField.getText().trim();
            String productName = productNameField.getText().trim();
            String productDescription = productDescriptionArea.getText().trim();
            String origin = originField.getText().trim();
            
            // Validate input
            if (productId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Product ID",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                productIdField.requestFocus();
                return;
            }
            
            if (productName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Product Name",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                productNameField.requestFocus();
                return;
            }
            
            if (origin.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter an Origin",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                originField.requestFocus();
                return;
            }
            
            // Generate transaction ID
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
            
            // Create transaction
            ProductCreationTransaction transaction = new ProductCreationTransaction(
                transactionId,
                currentUser.getUserId(),
                productId,
                productName,
                productDescription.isEmpty() ? "" : productDescription,
                origin
            );
            
            // Add to blockchain
            blockchainManager.addTransaction(transaction);
            
            // Mine the block
            blockchainManager.minePendingTransactions();
            
            // Clear fields
            productIdField.setText("");
            productNameField.setText("");
            productDescriptionArea.setText("");
            originField.setText("");
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Product creation transaction recorded successfully!\n\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Product ID: " + productId + "\n" +
                "Product Name: " + productName + "\n\n" +
                "The transaction has been added to the blockchain.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            LOGGER.info("Created product transaction: " + transactionId);
            
            // Refresh transaction history
            loadTransactionData();
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error creating product transaction", e);
            JOptionPane.showMessageDialog(this,
                "Transaction validation failed: " + e.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating product transaction", e);
            JOptionPane.showMessageDialog(this,
                "Failed to create transaction: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Creates a product transfer transaction and adds it to the blockchain.
     * Requirements: 4.1 - Validate transaction data format and completeness
     * Requirements: 4.2 - Create new block with transaction details and cryptographic hash
     * Requirements: 4.3 - Link to previous block using cryptographic hashing
     * Requirements: 4.4 - Include timestamp, supplier identifier, product details, and transaction type
     */
    private void transferProductTransaction() {
        try {
            // Get input values
            String productId = transferProductIdField.getText().trim();
            String toParty = toPartyField.getText().trim();
            String fromLocation = fromLocationField.getText().trim();
            String toLocation = toLocationField.getText().trim();
            ProductStatus newStatus = (ProductStatus) statusComboBox.getSelectedItem();
            
            // Validate input
            if (productId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Product ID",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                transferProductIdField.requestFocus();
                return;
            }
            
            if (toParty.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a To Party",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                toPartyField.requestFocus();
                return;
            }
            
            if (fromLocation.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a From Location",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                fromLocationField.requestFocus();
                return;
            }
            
            if (toLocation.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a To Location",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                toLocationField.requestFocus();
                return;
            }
            
            // Generate transaction ID
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
            
            // Create transaction
            ProductTransferTransaction transaction = new ProductTransferTransaction(
                transactionId,
                currentUser.getUserId(),
                toParty,
                productId,
                fromLocation,
                toLocation,
                newStatus
            );
            
            // Add to blockchain
            blockchainManager.addTransaction(transaction);
            
            // Mine the block
            blockchainManager.minePendingTransactions();
            
            // Clear fields
            transferProductIdField.setText("");
            toPartyField.setText("");
            fromLocationField.setText("");
            toLocationField.setText("");
            statusComboBox.setSelectedIndex(0);
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Product transfer transaction recorded successfully!\n\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Product ID: " + productId + "\n" +
                "From: " + fromLocation + " → To: " + toLocation + "\n" +
                "New Status: " + newStatus + "\n\n" +
                "The transaction has been added to the blockchain.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            LOGGER.info("Created transfer transaction: " + transactionId);
            
            // Refresh transaction history
            loadTransactionData();
            
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error creating transfer transaction", e);
            JOptionPane.showMessageDialog(this,
                "Transaction validation failed: " + e.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating transfer transaction", e);
            JOptionPane.showMessageDialog(this,
                "Failed to create transaction: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Verifies a transaction against the blockchain record.
     * Requirements: 5.1 - Retrieve blockchain record and compare with original submission
     * Requirements: 5.2 - Validate cryptographic hashes to ensure data has not been tampered with
     * Requirements: 5.3 - Provide confirmation status indicating whether records match
     */
    private void verifyTransaction() {
        try {
            String transactionId = verifyTransactionIdField.getText().trim();
            
            // Validate input
            if (transactionId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Transaction ID",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                verifyTransactionIdField.requestFocus();
                return;
            }
            
            // Disable button during processing
            verifyTransactionButton.setEnabled(false);
            verifyTransactionButton.setText("Verifying...");
            
            // Find the transaction in blockchain
            List<Transaction> allTransactions = blockchainManager.getTransactionHistory();
            Transaction foundTransaction = null;
            
            for (Transaction t : allTransactions) {
                if (transactionId.equals(t.getTransactionId())) {
                    foundTransaction = t;
                    break;
                }
            }
            
            if (foundTransaction == null) {
                verificationResultArea.setText("=== TRANSACTION VERIFICATION RESULT ===\n\n" +
                    "Transaction ID: " + transactionId + "\n\n" +
                    "Status: NOT FOUND ✗\n\n" +
                    "The specified transaction was not found in the blockchain.\n" +
                    "Please verify the transaction ID and try again.");
                return;
            }
            
            // Verify the transaction
            TransactionVerificationResult result = verificationService.verifyTransaction(foundTransaction);
            
            // Display result
            StringBuilder sb = new StringBuilder();
            sb.append("=== TRANSACTION VERIFICATION RESULT ===\n\n");
            sb.append("Transaction ID: ").append(transactionId).append("\n");
            sb.append("Transaction Type: ").append(foundTransaction.getTransactionType()).append("\n");
            sb.append("Timestamp: ").append(foundTransaction.getTimestamp().format(DATE_FORMATTER)).append("\n\n");
            
            if (result.isVerified()) {
                sb.append("Status: VERIFIED ✓\n\n");
                sb.append("Message: ").append(result.getMessage()).append("\n\n");
                sb.append("The transaction data matches the blockchain record.\n");
                sb.append("No tampering detected.\n");
            } else {
                sb.append("Status: VERIFICATION FAILED ✗\n\n");
                sb.append("Message: ").append(result.getMessage()).append("\n\n");
                sb.append("⚠ WARNING: The transaction data does not match the blockchain record.\n");
                sb.append("This may indicate data tampering or corruption.\n");
            }
            
            // Display transaction details
            sb.append("\n--- TRANSACTION DETAILS ---\n\n");
            for (var entry : foundTransaction.getTransactionData().entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            
            verificationResultArea.setText(sb.toString());
            verificationResultArea.setCaretPosition(0); // Scroll to top
            
            LOGGER.info("Verified transaction: " + transactionId + " - Result: " + result.isVerified());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error verifying transaction", e);
            JOptionPane.showMessageDialog(this,
                "Failed to verify transaction: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Re-enable button
            verifyTransactionButton.setEnabled(true);
            verifyTransactionButton.setText("Verify Transaction");
        }
    }
    
    /**
     * Validates the integrity of the entire blockchain.
     * Requirements: 5.2 - Validate cryptographic hashes to ensure data has not been tampered with
     * Requirements: 5.3 - Provide confirmation status
     */
    private void validateBlockchainIntegrity() {
        try {
            // Disable button during processing
            validateBlockchainButton.setEnabled(false);
            validateBlockchainButton.setText("Validating...");
            
            // Validate blockchain
            TransactionVerificationResult result = verificationService.validateBlockchainIntegrity();
            
            // Display result
            StringBuilder sb = new StringBuilder();
            sb.append("=== BLOCKCHAIN INTEGRITY VALIDATION ===\n\n");
            sb.append("Validation Date: ").append(java.time.LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
            sb.append("Total Blocks: ").append(blockchainManager.getChain().size()).append("\n");
            sb.append("Total Transactions: ").append(blockchainManager.getTransactionHistory().size()).append("\n\n");
            
            if (result.isVerified()) {
                sb.append("Status: VALID ✓\n\n");
                sb.append("Message: ").append(result.getMessage()).append("\n\n");
                sb.append("All blocks have valid hashes and proper linkage.\n");
                sb.append("No tampering detected in the blockchain.\n");
                sb.append("The blockchain integrity is intact.\n");
            } else {
                sb.append("Status: INVALID ✗\n\n");
                sb.append("Message: ").append(result.getMessage()).append("\n\n");
                sb.append("⚠ CRITICAL WARNING: Blockchain integrity compromised!\n");
                sb.append("Hash validation failed or improper block linkage detected.\n");
                sb.append("This indicates potential tampering or data corruption.\n");
            }
            
            verificationResultArea.setText(sb.toString());
            verificationResultArea.setCaretPosition(0); // Scroll to top
            
            LOGGER.info("Blockchain integrity validation - Result: " + result.isVerified());
            
            // Show dialog for critical issues
            if (!result.isVerified()) {
                JOptionPane.showMessageDialog(this,
                    "⚠ CRITICAL: Blockchain integrity compromised!\n\n" +
                    result.getMessage(),
                    "Blockchain Validation Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error validating blockchain integrity", e);
            JOptionPane.showMessageDialog(this,
                "Failed to validate blockchain: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Re-enable button
            validateBlockchainButton.setEnabled(true);
            validateBlockchainButton.setText("Validate Blockchain Integrity");
        }
    }
    
    /**
     * Loads transaction data from the blockchain and displays it in the table.
     */
    private void loadTransactionData() {
        try {
            // Clear existing data
            transactionTableModel.setRowCount(0);
            
            // Get all transactions from blockchain
            List<Transaction> transactions = blockchainManager.getTransactionHistory();
            
            // Populate table with transaction data
            for (Transaction transaction : transactions) {
                String transactionId = transaction.getTransactionId();
                String type = transaction.getTransactionType();
                String timestamp = transaction.getTimestamp() != null ? 
                    transaction.getTimestamp().format(DATE_FORMATTER) : "N/A";
                
                // Extract product ID from transaction data
                var data = transaction.getTransactionData();
                String productId = data.get("productId") != null ? data.get("productId").toString() : "N/A";
                
                // Determine status based on transaction type
                String status = "Recorded";
                if (type.equals("PRODUCT_TRANSFER")) {
                    Object statusObj = data.get("newStatus");
                    status = statusObj != null ? statusObj.toString() : "N/A";
                }
                
                Object[] row = {transactionId, type, timestamp, productId, status};
                transactionTableModel.addRow(row);
            }
            
            LOGGER.info("Loaded " + transactions.size() + " transactions");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading transaction data", e);
            JOptionPane.showMessageDialog(this,
                "Failed to load transaction data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
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
     * Gets the transaction verification service instance.
     * 
     * @return The verification service
     */
    public TransactionVerificationService getVerificationService() {
        return verificationService;
    }
}
