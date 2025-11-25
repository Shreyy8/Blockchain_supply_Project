package com.supplychain.dao;

import com.supplychain.blockchain.Block;
import com.supplychain.model.Transaction;
import com.supplychain.util.DatabaseConnectionManager;
import com.supplychain.exception.ConnectionException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Block entities.
 * Implements CRUD operations for blockchain blocks using JDBC with PreparedStatements.
 * 
 * Requirements: 11.2, 11.3, 11.4
 */
public class BlockDAO implements DAO<Block> {
    private static final Logger LOGGER = Logger.getLogger(BlockDAO.class.getName());
    private final DatabaseConnectionManager connectionManager;
    private final TransactionDAO transactionDAO;
    
    /**
     * Constructor that initializes the connection manager and transaction DAO.
     * 
     * @throws ConnectionException if connection manager initialization fails
     */
    public BlockDAO() throws ConnectionException {
        this.connectionManager = DatabaseConnectionManager.getInstance();
        this.transactionDAO = new TransactionDAO();
    }
    
    /**
     * Saves a new block to the database.
     * Uses PreparedStatement to prevent SQL injection.
     * Note: Transactions within the block should be saved separately using TransactionDAO.
     * 
     * @param entity The block to save
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void save(Block entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        
        String sql = "INSERT INTO blocks (block_index, timestamp, transactions, previous_hash, hash, nonce) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, entity.getIndex());
            stmt.setTimestamp(2, Timestamp.valueOf(entity.getTimestamp()));
            stmt.setString(3, serializeTransactions(entity.getTransactions()));
            stmt.setString(4, entity.getPreviousHash());
            stmt.setString(5, entity.getHash());
            stmt.setLong(6, entity.getNonce());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Block saved successfully: index=" + entity.getIndex() + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Finds a block by its index (used as ID).
     * Note: The id parameter is the block index as a string.
     * 
     * @param id The block index as a string
     * @return The block with the specified index, or null if not found
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Block findById(String id) throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Block index cannot be null or empty");
        }
        
        int index;
        try {
            index = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Block index must be a valid integer", e);
        }
        
        return findByIndex(index);
    }
    
    /**
     * Finds a block by its index.
     * 
     * @param index The block index
     * @return The block with the specified index, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public Block findByIndex(int index) throws SQLException {
        String sql = "SELECT block_index, timestamp, transactions, previous_hash, hash, nonce " +
                     "FROM blocks WHERE block_index = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, index);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractBlockFromResultSet(rs);
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
     * Retrieves all blocks from the database, ordered by index.
     * 
     * @return A list of all blocks, empty list if none exist
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<Block> findAll() throws SQLException {
        String sql = "SELECT block_index, timestamp, transactions, previous_hash, hash, nonce " +
                     "FROM blocks ORDER BY block_index";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Block> blocks = new ArrayList<>();
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Block block = extractBlockFromResultSet(rs);
                if (block != null) {
                    blocks.add(block);
                }
            }
            
            LOGGER.info("Retrieved " + blocks.size() + " blocks from database");
            return blocks;
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Updates an existing block in the database.
     * Note: In a real blockchain, blocks should be immutable after creation.
     * This method is provided for completeness but should be used with caution.
     * 
     * @param entity The block with updated values
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(Block entity) throws SQLException {
        if (entity == null) {
            throw new IllegalArgumentException("Block cannot be null");
        }
        
        String sql = "UPDATE blocks SET timestamp = ?, transactions = ?, previous_hash = ?, " +
                     "hash = ?, nonce = ? WHERE block_index = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setTimestamp(1, Timestamp.valueOf(entity.getTimestamp()));
            stmt.setString(2, serializeTransactions(entity.getTransactions()));
            stmt.setString(3, entity.getPreviousHash());
            stmt.setString(4, entity.getHash());
            stmt.setLong(5, entity.getNonce());
            stmt.setInt(6, entity.getIndex());
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.warning("Block updated (blockchain immutability violated): index=" + 
                          entity.getIndex() + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Deletes a block from the database by its index.
     * Note: In a real blockchain, blocks should not be deleted.
     * This method is provided for completeness but should be used with extreme caution.
     * 
     * @param id The block index as a string
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(String id) throws SQLException {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Block index cannot be null or empty");
        }
        
        int index;
        try {
            index = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Block index must be a valid integer", e);
        }
        
        String sql = "DELETE FROM blocks WHERE block_index = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = connectionManager.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, index);
            
            int rowsAffected = stmt.executeUpdate();
            LOGGER.warning("Block deleted (blockchain immutability violated): index=" + 
                          index + " (" + rowsAffected + " rows affected)");
            
        } catch (ConnectionException e) {
            LOGGER.log(Level.SEVERE, "Failed to get database connection", e);
            throw new SQLException("Failed to get database connection", e);
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Extracts a Block object from a ResultSet.
     * 
     * @param rs The ResultSet containing block data
     * @return A Block object, or null if data is invalid
     * @throws SQLException if a database access error occurs
     */
    private Block extractBlockFromResultSet(ResultSet rs) throws SQLException {
        int index = rs.getInt("block_index");
        Timestamp timestampValue = rs.getTimestamp("timestamp");
        LocalDateTime timestamp = timestampValue.toLocalDateTime();
        String transactionsData = rs.getString("transactions");
        String previousHash = rs.getString("previous_hash");
        String hash = rs.getString("hash");
        long nonce = rs.getLong("nonce");
        
        // Deserialize transactions
        List<Transaction> transactions = deserializeTransactions(transactionsData);
        
        return new Block(index, timestamp, transactions, previousHash, hash, nonce);
    }
    
    /**
     * Serializes a list of transactions to a string format for storage.
     * Stores transaction IDs separated by commas.
     * The actual transaction data should be stored separately in the transactions table.
     * 
     * @param transactions The list of transactions to serialize
     * @return Serialized transaction IDs
     */
    private String serializeTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < transactions.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(transactions.get(i).getTransactionId());
        }
        
        return sb.toString();
    }
    
    /**
     * Deserializes transaction IDs and loads the full transactions from the database.
     * 
     * @param transactionsData The serialized transaction IDs
     * @return List of Transaction objects
     */
    private List<Transaction> deserializeTransactions(String transactionsData) {
        List<Transaction> transactions = new ArrayList<>();
        
        if (transactionsData == null || transactionsData.trim().isEmpty()) {
            return transactions;
        }
        
        String[] transactionIds = transactionsData.split(",");
        for (String transactionId : transactionIds) {
            try {
                Transaction transaction = transactionDAO.findById(transactionId.trim());
                if (transaction != null) {
                    transactions.add(transaction);
                } else {
                    LOGGER.warning("Transaction not found: " + transactionId);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error loading transaction: " + transactionId, e);
            }
        }
        
        return transactions;
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
