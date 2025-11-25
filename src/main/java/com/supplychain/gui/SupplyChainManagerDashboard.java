package com.supplychain.gui;

import com.supplychain.blockchain.BlockchainManager;
import com.supplychain.exception.ConnectionException;
import com.supplychain.model.Transaction;
import com.supplychain.model.User;
import com.supplychain.service.ComplianceValidator;
import com.supplychain.service.ComplianceValidator.ComplianceReport;
import com.supplychain.service.ComplianceValidator.RequirementResult;
import com.supplychain.service.OptimizationAnalyzer;
import com.supplychain.service.OptimizationAnalyzer.OptimizationRecommendation;
import com.supplychain.util.DatabaseConnectionManager;

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
 * Dashboard for Supply Chain Managers to monitor blockchain transactions,
 * view optimization recommendations, and manage compliance requirements.
 * 
 * Requirements: 1.1, 1.2, 1.3, 2.1, 3.1, 3.2, 3.3
 */
public class SupplyChainManagerDashboard extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(SupplyChainManagerDashboard.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Services
    private BlockchainManager blockchainManager;
    private OptimizationAnalyzer optimizationAnalyzer;
    private ComplianceValidator complianceValidator;
    
    // User information
    private User currentUser;
    
    // Transaction Monitoring Components
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    private JButton refreshTransactionsButton;
    private JLabel transactionCountLabel;
    
    // Optimization Recommendations Components
    private JTextArea recommendationsTextArea;
    private JButton generateRecommendationsButton;
    
    // Compliance Management Components
    private JTextArea complianceReportTextArea;
    private JButton evaluateComplianceButton;
    private JButton addRequirementButton;
    private JTextField requirementIdField;
    private JTextField requirementDescField;
    private JTextField requirementRuleField;
    
    /**
     * Constructor for SupplyChainManagerDashboard.
     * 
     * @param currentUser The logged-in user
     * @param blockchainManager The blockchain manager instance
     * @throws ConnectionException if database connection fails
     */
    public SupplyChainManagerDashboard(User currentUser, BlockchainManager blockchainManager) 
            throws ConnectionException {
        this.currentUser = currentUser;
        this.blockchainManager = blockchainManager;
        this.optimizationAnalyzer = new OptimizationAnalyzer();
        this.complianceValidator = new ComplianceValidator();
        
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
        // Transaction Monitoring Components
        String[] columnNames = {"Transaction ID", "Type", "Timestamp", "From", "To", "Product ID"};
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
        transactionCountLabel = new JLabel("Total Transactions: 0");
        
        // Optimization Recommendations Components
        recommendationsTextArea = new JTextArea(10, 40);
        recommendationsTextArea.setEditable(false);
        recommendationsTextArea.setLineWrap(true);
        recommendationsTextArea.setWrapStyleWord(true);
        recommendationsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        generateRecommendationsButton = new JButton("Generate Recommendations");
        
        // Compliance Management Components
        complianceReportTextArea = new JTextArea(10, 40);
        complianceReportTextArea.setEditable(false);
        complianceReportTextArea.setLineWrap(true);
        complianceReportTextArea.setWrapStyleWord(true);
        complianceReportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        evaluateComplianceButton = new JButton("Evaluate Compliance");
        addRequirementButton = new JButton("Add Requirement");
        
        requirementIdField = new JTextField(15);
        requirementDescField = new JTextField(30);
        requirementRuleField = new JTextField(30);
    }
    
    /**
     * Sets up the layout of the dashboard.
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create tabbed pane for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add Transaction Monitoring Panel
        tabbedPane.addTab("Transaction Monitoring", createTransactionMonitoringPanel());
        
        // Add Optimization Recommendations Panel
        tabbedPane.addTab("Optimization", createOptimizationPanel());
        
        // Add Compliance Management Panel
        tabbedPane.addTab("Compliance", createCompliancePanel());
        
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
        
        JLabel titleLabel = new JLabel("Supply Chain Manager Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel userLabel = new JLabel("User: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Creates the transaction monitoring panel.
     * Requirements: 1.1, 1.2, 1.3 - Monitor blockchain transaction data
     * 
     * @return The transaction monitoring panel
     */
    private JPanel createTransactionMonitoringPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(refreshTransactionsButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(transactionCountLabel);
        
        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("View all blockchain transactions with complete details in chronological order");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoPanel.add(infoLabel);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the optimization recommendations panel.
     * Requirements: 2.1 - Generate optimization recommendations
     * 
     * @return The optimization panel
     */
    private JPanel createOptimizationPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with title and button
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Supply Chain Optimization Recommendations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(generateRecommendationsButton);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Recommendations display area
        JScrollPane scrollPane = new JScrollPane(recommendationsTextArea);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Analyze supply chain data to identify bottlenecks, delays, and inefficiencies");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoPanel.add(infoLabel);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the compliance management panel.
     * Requirements: 3.1, 3.2, 3.3 - Manage compliance requirements
     * 
     * @return The compliance panel
     */
    private JPanel createCompliancePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with requirement input form
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Compliance Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Form panel for adding requirements
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Regulatory Requirement"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Requirement ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Requirement ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(requirementIdField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(requirementDescField, gbc);
        
        // Rule
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Rule:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(requirementRuleField, gbc);
        
        // Button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(addRequirementButton, gbc);
        
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        // Middle panel with evaluate button
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        middlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        middlePanel.add(evaluateComplianceButton);
        
        // Compliance report display area
        JScrollPane scrollPane = new JScrollPane(complianceReportTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Compliance Report"));
        scrollPane.setPreferredSize(new Dimension(800, 250));
        
        // Bottom panel with info
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Store regulatory requirements and evaluate compliance status");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoPanel.add(infoLabel);
        
        // Combine middle and report panels
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(middlePanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Sets up event handlers for all buttons.
     */
    private void setupEventHandlers() {
        // Refresh Transactions Button
        refreshTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTransactionData();
            }
        });
        
        // Generate Recommendations Button
        generateRecommendationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateOptimizationRecommendations();
            }
        });
        
        // Add Requirement Button
        addRequirementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRegulatoryRequirement();
            }
        });
        
        // Evaluate Compliance Button
        evaluateComplianceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                evaluateCompliance();
            }
        });
    }
    
    /**
     * Loads transaction data from the blockchain and displays it in the table.
     * Requirements: 1.1 - Retrieve and display all blockchain transaction records
     * Requirements: 1.3 - Present data in chronological order
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
                
                // Extract from and to parties from transaction data
                Map<String, Object> data = transaction.getTransactionData();
                String fromParty = data.get("fromParty") != null ? data.get("fromParty").toString() : "N/A";
                String toParty = data.get("toParty") != null ? data.get("toParty").toString() : "N/A";
                String productId = data.get("productId") != null ? data.get("productId").toString() : "N/A";
                
                Object[] row = {transactionId, type, timestamp, fromParty, toParty, productId};
                transactionTableModel.addRow(row);
            }
            
            // Update transaction count
            transactionCountLabel.setText("Total Transactions: " + transactions.size());
            
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
     * Generates optimization recommendations based on blockchain data.
     * Requirements: 2.1 - Analyze supply chain data and generate actionable recommendations
     */
    private void generateOptimizationRecommendations() {
        try {
            // Disable button during processing
            generateRecommendationsButton.setEnabled(false);
            generateRecommendationsButton.setText("Analyzing...");
            
            // Get all transactions
            List<Transaction> transactions = blockchainManager.getTransactionHistory();
            
            if (transactions.isEmpty()) {
                recommendationsTextArea.setText("No transactions available for analysis.\n\n" +
                    "Please ensure transactions have been recorded in the blockchain before generating recommendations.");
                return;
            }
            
            // Generate recommendations
            List<OptimizationRecommendation> recommendations = 
                optimizationAnalyzer.generateRecommendations(transactions);
            
            // Display recommendations
            StringBuilder sb = new StringBuilder();
            sb.append("=== SUPPLY CHAIN OPTIMIZATION RECOMMENDATIONS ===\n\n");
            sb.append("Analysis Date: ").append(java.time.LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
            sb.append("Transactions Analyzed: ").append(transactions.size()).append("\n\n");
            
            if (recommendations.isEmpty()) {
                sb.append("No optimization opportunities identified at this time.\n");
                sb.append("The supply chain is operating efficiently based on current data.\n");
            } else {
                sb.append("Found ").append(recommendations.size()).append(" optimization opportunity(ies):\n\n");
                
                int count = 1;
                for (OptimizationRecommendation rec : recommendations) {
                    sb.append(count++).append(". ").append(rec.getType()).append("\n");
                    sb.append("   Suggestion: ").append(rec.getSuggestion()).append("\n");
                    sb.append("   Expected Impact: ").append(rec.getExpectedImpact()).append("\n\n");
                }
            }
            
            recommendationsTextArea.setText(sb.toString());
            recommendationsTextArea.setCaretPosition(0); // Scroll to top
            
            LOGGER.info("Generated " + recommendations.size() + " optimization recommendations");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating recommendations", e);
            JOptionPane.showMessageDialog(this,
                "Failed to generate recommendations: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Re-enable button
            generateRecommendationsButton.setEnabled(true);
            generateRecommendationsButton.setText("Generate Recommendations");
        }
    }
    
    /**
     * Adds a regulatory requirement to the compliance validator.
     * Requirements: 3.1 - Store regulatory requirements
     */
    private void addRegulatoryRequirement() {
        try {
            String requirementId = requirementIdField.getText().trim();
            String description = requirementDescField.getText().trim();
            String rule = requirementRuleField.getText().trim();
            
            // Validate input
            if (requirementId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Requirement ID",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                requirementIdField.requestFocus();
                return;
            }
            
            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Description",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                requirementDescField.requestFocus();
                return;
            }
            
            if (rule.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Rule",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                requirementRuleField.requestFocus();
                return;
            }
            
            // Store requirement
            complianceValidator.storeRequirement(requirementId, description, rule);
            
            // Clear fields
            requirementIdField.setText("");
            requirementDescField.setText("");
            requirementRuleField.setText("");
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Regulatory requirement added successfully!\n\n" +
                "ID: " + requirementId + "\n" +
                "Description: " + description,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            LOGGER.info("Added regulatory requirement: " + requirementId);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding regulatory requirement", e);
            JOptionPane.showMessageDialog(this,
                "Failed to add requirement: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Evaluates compliance of blockchain transactions against stored requirements.
     * Requirements: 3.2 - Compare operations against regulatory requirements
     * Requirements: 3.3 - Generate compliance reports with pass/fail status
     */
    private void evaluateCompliance() {
        try {
            // Disable button during processing
            evaluateComplianceButton.setEnabled(false);
            evaluateComplianceButton.setText("Evaluating...");
            
            // Get all transactions
            List<Transaction> transactions = blockchainManager.getTransactionHistory();
            
            if (transactions.isEmpty()) {
                complianceReportTextArea.setText("No transactions available for compliance evaluation.\n\n" +
                    "Please ensure transactions have been recorded in the blockchain.");
                return;
            }
            
            // Check if any requirements are stored
            if (complianceValidator.getAllRequirements().isEmpty()) {
                complianceReportTextArea.setText("No regulatory requirements have been stored.\n\n" +
                    "Please add regulatory requirements before evaluating compliance.");
                return;
            }
            
            // Evaluate compliance
            ComplianceReport report = complianceValidator.evaluateCompliance(transactions);
            
            // Display report
            StringBuilder sb = new StringBuilder();
            sb.append("=== COMPLIANCE EVALUATION REPORT ===\n\n");
            sb.append("Evaluation Date: ").append(java.time.LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
            sb.append("Transactions Evaluated: ").append(transactions.size()).append("\n");
            sb.append("Requirements Checked: ").append(report.getResults().size()).append("\n\n");
            
            // Display results for each requirement
            sb.append("--- REQUIREMENT RESULTS ---\n\n");
            for (RequirementResult result : report.getResults().values()) {
                sb.append("Requirement: ").append(result.getRequirementId()).append("\n");
                sb.append("Description: ").append(result.getDescription()).append("\n");
                sb.append("Status: ").append(result.isPassed() ? "PASS ✓" : "FAIL ✗").append("\n\n");
            }
            
            // Display non-compliant transactions if any
            if (report.hasNonCompliantTransactions()) {
                sb.append("--- NON-COMPLIANT TRANSACTIONS ---\n\n");
                Map<String, List<String>> nonCompliant = report.getNonCompliantTransactions();
                
                for (Map.Entry<String, List<String>> entry : nonCompliant.entrySet()) {
                    sb.append("Transaction ID: ").append(entry.getKey()).append("\n");
                    sb.append("Violated Requirements: ").append(String.join(", ", entry.getValue())).append("\n\n");
                }
                
                sb.append("⚠ ALERT: ").append(nonCompliant.size())
                  .append(" non-compliant transaction(s) detected!\n");
            } else {
                sb.append("✓ All transactions are compliant with regulatory requirements.\n");
            }
            
            complianceReportTextArea.setText(sb.toString());
            complianceReportTextArea.setCaretPosition(0); // Scroll to top
            
            LOGGER.info("Compliance evaluation completed. Non-compliant transactions: " + 
                       report.getNonCompliantTransactions().size());
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error evaluating compliance", e);
            JOptionPane.showMessageDialog(this,
                "Failed to evaluate compliance: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Re-enable button
            evaluateComplianceButton.setEnabled(true);
            evaluateComplianceButton.setText("Evaluate Compliance");
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
     * Gets the optimization analyzer instance.
     * 
     * @return The optimization analyzer
     */
    public OptimizationAnalyzer getOptimizationAnalyzer() {
        return optimizationAnalyzer;
    }
    
    /**
     * Gets the compliance validator instance.
     * 
     * @return The compliance validator
     */
    public ComplianceValidator getComplianceValidator() {
        return complianceValidator;
    }
}
