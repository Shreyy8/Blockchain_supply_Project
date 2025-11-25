package com.supplychain.dao;

import com.supplychain.model.*;
import com.supplychain.util.DatabaseConnectionManager;
import com.supplychain.exception.ConnectionException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Transaction entities.
 * Implements CRUD operations for transactions using JDBC with PreparedStatements.
 * Handles polymorphic transaction types by storing transaction-specific data as text.
 * 
 * Requirements: 11.2, 11.3, 11.4
 */
public class TransactionDAO implements DAO<Transaction> {
    private static final Logger LOGGER = Logger.getLogger(TransactionDAO.class.getName());
    private final DatabaseConnectionManager connectionManager;
    
    /**
     * Constructor that initializes the connection manager.
     * 
     * @throws ConnectionException if connection manager initialization fails
     */
    public TransactionDAO() throws ConnectionException {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }
    
    /**
     * Saves a new transaction to the database.
     * Uses PreparedStatement to prevent SQL injection.
     * 
     * @param entity The transaction to save
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void save(Transaction entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        
        String sql = "INSERT INTO transactions (transaction_id, transaction_type, timestamp, from_party, to_party, product_id, data) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getTransactionId());
            stmt.setString(2, entity.getTransactionType());
            stmt.setTimestamp(3, Timestamp.valueOf(entity.getTimestamp()));
            
            // Extract from_party, to_party, and product_id from transaction data
            String fromParty = extractFromParty(entity);
            String toParty = extractToParty(entity);
            String productId = extractProductId(entity);
            
            stmt.setString(4, fromParty);
            stmt.setString(5, toParty);
            stmt.setString(6, productId);
            stmt.setString(7, serializeTransactionData(entity));
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Transaction saved successfully: " + entity.getTransactionId() + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Finds a transaction by its unique identifier.
     * 
     * @param id The unique identifier of the transaction
     * @return The transaction with the specified ID, or null if not found
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Transaction findById(String id) throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
        
        String sql = "SELECT transaction_id, transaction_type, timestamp, from_party, to_party, product_id, data " +
                     "FROM transactions WHERE transaction_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
            
            return null;
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves all transactions from the database.
     * 
     * @return A list of all transactions, empty list if none exist
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT transaction_id, transaction_type, timestamp, from_party, to_party, product_id, data FROM transactions";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = extractTransactionFromResultSet(rs);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
            
            LOGGER.info("Retrieved " + transactions.size() + " transactions from database");
            return transactions;
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Updates an existing transaction in the database.
     * 
     * @param entity The transaction with updated values
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(Transaction entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        
        String sql = "UPDATE transactions SET transaction_type = ?, timestamp = ?, from_party = ?, " +
                     "to_party = ?, product_id = ?, data = ? WHERE transaction_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, entity.getTransactionType());
            stmt.setTimestamp(2, Timestamp.valueOf(entity.getTimestamp()));
            
            String fromParty = extractFromParty(entity);
            String toParty = extractToParty(entity);
            String productId = extractProductId(entity);
            
            stmt.setString(3, fromParty);
            stmt.setString(4, toParty);
            stmt.setString(5, productId);
            stmt.setString(6, serializeTransactionData(entity));
            stmt.setString(7, entity.getTransactionId());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Transaction updated successfully: " + entity.getTransactionId() + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Deletes a transaction from the database by its unique identifier.
     * 
     * @param id The unique identifier of the transaction to delete
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(String id) throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
        
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Transaction deleted successfully: " + id + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Extracts a Transaction object from a ResultSet.
     * Creates the appropriate concrete transaction type based on the transaction_type field.
     * 
     * @param rs The ResultSet containing transaction data
     * @return A Transaction object, or null if type is invalid
     * @throws SQLException if a database access error occurs
     */
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        String transactionId = rs.getString("transaction_id");
        String transactionType = rs.getString("transaction_type");
        Timestamp timestampValue = rs.getTimestamp("timestamp");
        LocalDateTime timestamp = timestampValue.toLocalDateTime();
        String data = rs.getString("data");
        
        // Parse the data field and create the appropriate transaction type
        Transaction transaction = null;
        
        switch (transactionType) {
            case "PRODUCT_CREATION":
                transaction = deserializeProductCreationTransaction(transactionId, timestamp, data);
                break;
            case "PRODUCT_TRANSFER":
                transaction = deserializeProductTransferTransaction(transactionId, timestamp, data);
                break;
            case "PRODUCT_VERIFICATION":
                transaction = deserializeProductVerificationTransaction(transactionId, timestamp, data);
                break;
            default:
                LOGGER.warning("Unknown transaction type: " + transactionType);
                return null;
        }
        
        return transaction;
    }
    
    /**
     * Serializes transaction data to a string format for storage.
     * Uses a simple key=value format separated by semicolons.
     * 
     * @param transaction The transaction to serialize
     * @return Serialized transaction data
     */
    private String serializeTransactionData(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        
        if (transaction instanceof ProductCreationTransaction) {
            ProductCreationTransaction pct = (ProductCreationTransaction) transaction;
            sb.append("supplierId=").append(pct.getSupplierId()).append(";");
            sb.append("productId=").append(pct.getProductId()).append(";");
            sb.append("productName=").append(escapeValue(pct.getProductName())).append(";");
            sb.append("productDescription=").append(escapeValue(pct.getProductDescription())).append(";");
            sb.append("origin=").append(escapeValue(pct.getOrigin()));
        } else if (transaction instanceof ProductTransferTransaction) {
            ProductTransferTransaction ptt = (ProductTransferTransaction) transaction;
            sb.append("fromParty=").append(ptt.getFromParty()).append(";");
            sb.append("toParty=").append(ptt.getToParty()).append(";");
            sb.append("productId=").append(ptt.getProductId()).append(";");
            sb.append("fromLocation=").append(escapeValue(ptt.getFromLocation())).append(";");
            sb.append("toLocation=").append(escapeValue(ptt.getToLocation())).append(";");
            sb.append("newStatus=").append(ptt.getNewStatus().toString());
        } else if (transaction instanceof ProductVerificationTransaction) {
            ProductVerificationTransaction pvt = (ProductVerificationTransaction) transaction;
            sb.append("verifierId=").append(pvt.getVerifierId()).append(";");
            sb.append("productId=").append(pvt.getProductId()).append(";");
            sb.append("verificationResult=").append(pvt.isVerificationResult()).append(";");
            sb.append("verificationNotes=").append(escapeValue(pvt.getVerificationNotes()));
        }
        
        return sb.toString();
    }
    
    /**
     * Escapes special characters in values for serialization.
     * 
     * @param value The value to escape
     * @return Escaped value
     */
    private String escapeValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(";", "\\;").replace("=", "\\=");
    }
    
    /**
     * Unescapes special characters in values after deserialization.
     * 
     * @param value The value to unescape
     * @return Unescaped value
     */
    private String unescapeValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\;", ";").replace("\\=", "=");
    }
    
    /**
     * Parses serialized data into a key-value map.
     * 
     * @param data The serialized data string
     * @return Map of key-value pairs
     */
    private java.util.Map<String, String> parseData(String data) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        if (data == null || data.trim().isEmpty()) {
            return map;
        }
        
        String[] pairs = data.split("(?<!\\\\);");
        for (String pair : pairs) {
            String[] keyValue = pair.split("(?<!\\\\)=", 2);
            if (keyValue.length == 2) {
                map.put(keyValue[0].trim(), unescapeValue(keyValue[1].trim()));
            }
        }
        
        return map;
    }
    
    /**
     * Deserializes a ProductCreationTransaction from data string.
     */
    private ProductCreationTransaction deserializeProductCreationTransaction(
            String transactionId, LocalDateTime timestamp, String data) {
        java.util.Map<String, String> map = parseData(data);
        
        return new ProductCreationTransaction(
            transactionId,
            timestamp,
            map.get("supplierId"),
            map.get("productId"),
            map.get("productName"),
            map.get("productDescription"),
            map.get("origin")
        );
    }
    
    /**
     * Deserializes a ProductTransferTransaction from data string.
     */
    private ProductTransferTransaction deserializeProductTransferTransaction(
            String transactionId, LocalDateTime timestamp, String data) {
        java.util.Map<String, String> map = parseData(data);
        
        ProductStatus status = ProductStatus.valueOf(map.get("newStatus"));
        
        return new ProductTransferTransaction(
            transactionId,
            timestamp,
            map.get("fromParty"),
            map.get("toParty"),
            map.get("productId"),
            map.get("fromLocation"),
            map.get("toLocation"),
            status
        );
    }
    
    /**
     * Deserializes a ProductVerificationTransaction from data string.
     */
    private ProductVerificationTransaction deserializeProductVerificationTransaction(
            String transactionId, LocalDateTime timestamp, String data) {
        java.util.Map<String, String> map = parseData(data);
        
        boolean verificationResult = Boolean.parseBoolean(map.get("verificationResult"));
        
        return new ProductVerificationTransaction(
            transactionId,
            timestamp,
            map.get("verifierId"),
            map.get("productId"),
            verificationResult,
            map.get("verificationNotes")
        );
    }
    
    /**
     * Extracts the from_party field from a transaction.
     */
    private String extractFromParty(Transaction transaction) {
        if (transaction instanceof ProductCreationTransaction) {
            return ((ProductCreationTransaction) transaction).getSupplierId();
        } else if (transaction instanceof ProductTransferTransaction) {
            return ((ProductTransferTransaction) transaction).getFromParty();
        } else if (transaction instanceof ProductVerificationTransaction) {
            return ((ProductVerificationTransaction) transaction).getVerifierId();
        }
        return null;
    }
    
    /**
     * Extracts the to_party field from a transaction.
     */
    private String extractToParty(Transaction transaction) {
        if (transaction instanceof ProductTransferTransaction) {
            return ((ProductTransferTransaction) transaction).getToParty();
        }
        return null;
    }
    
    /**
     * Extracts the product_id field from a transaction.
     */
    private String extractProductId(Transaction transaction) {
        if (transaction instanceof ProductCreationTransaction) {
            return ((ProductCreationTransaction) transaction).getProductId();
        } else if (transaction instanceof ProductTransferTransaction) {
            return ((ProductTransferTransaction) transaction).getProductId();
        } else if (transaction instanceof ProductVerificationTransaction) {
            return ((ProductVerificationTransaction) transaction).getProductId();
        }
        return null;
    }
    
    /**
     * Closes database resources in the correct order.
     * Ensures resources are properly released even if exceptions occur.
     * 
     * @param conn The connection to close
     * @param stmt The statement to close
     * @param rs The result set to close
     */
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing Statement", e);
            }
        }
        
        if (conn != null) {
            connectionManager.releaseConnection(conn);
        }
    }
}
