package com.supplychain.blockchain;

import com.supplychain.model.Transaction;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Manages multithreading components for blockchain operations.
 * Coordinates the transaction processor and block mining threads.
 * Provides thread-safe methods for submitting transactions and managing lifecycle.
 * 
 * Requirements: 1.2 - Transaction persistence and availability
 * Requirements: 4.2 - Block creation with cryptographic hash
 */
public class BlockchainThreadManager {
    private static final Logger logger = Logger.getLogger(BlockchainThreadManager.class.getName());
    
    private final BlockchainManager blockchainManager;
    private final ConcurrentLinkedQueue<Transaction> incomingTransactions;
    private final ConcurrentLinkedQueue<Transaction> pendingTransactions;
    
    private TransactionProcessorThread transactionProcessor;
    private BlockMiningThread blockMiningThread;
    
    private Thread processorThread;
    
    private final int batchSize;
    private final long miningInterval;
    private final long processingInterval;
    
    /**
     * Constructor for BlockchainThreadManager.
     * 
     * @param blockchainManager The blockchain manager to use
     * @param batchSize Number of transactions per block
     * @param miningInterval Time in milliseconds between mining attempts
     * @param processingInterval Time in milliseconds between processing attempts
     */
    public BlockchainThreadManager(BlockchainManager blockchainManager,
                                  int batchSize,
                                  long miningInterval,
                                  long processingInterval) {
        this.blockchainManager = blockchainManager;
        this.batchSize = batchSize;
        this.miningInterval = miningInterval;
        this.processingInterval = processingInterval;
        
        // Initialize thread-safe queues
        this.incomingTransactions = new ConcurrentLinkedQueue<>();
        this.pendingTransactions = new ConcurrentLinkedQueue<>();
    }
    
    /**
     * Starts the multithreading components.
     * Initializes and starts both the transaction processor and mining threads.
     */
    public synchronized void start() {
        if (transactionProcessor != null && transactionProcessor.isRunning()) {
            logger.warning("Thread manager already started");
            return;
        }
        
        logger.info("Starting blockchain thread manager");
        
        // Create and start transaction processor thread
        transactionProcessor = new TransactionProcessorThread(
            incomingTransactions,
            pendingTransactions,
            processingInterval
        );
        processorThread = new Thread(transactionProcessor, "TransactionProcessor");
        processorThread.setDaemon(true);
        processorThread.start();
        
        // Create and start block mining thread
        blockMiningThread = new BlockMiningThread(
            blockchainManager,
            pendingTransactions,
            batchSize,
            miningInterval
        );
        blockMiningThread.start();
        
        logger.info("Blockchain thread manager started successfully");
    }
    
    /**
     * Stops the multithreading components gracefully.
     * Waits for threads to finish processing current operations.
     */
    public synchronized void stop() {
        logger.info("Stopping blockchain thread manager");
        
        // Stop transaction processor
        if (transactionProcessor != null) {
            transactionProcessor.stop();
        }
        
        // Stop mining thread
        if (blockMiningThread != null) {
            blockMiningThread.stopMining();
        }
        
        // Wait for threads to finish
        try {
            if (processorThread != null && processorThread.isAlive()) {
                processorThread.join(5000); // Wait up to 5 seconds
            }
            
            if (blockMiningThread != null && blockMiningThread.isAlive()) {
                blockMiningThread.join(5000); // Wait up to 5 seconds
            }
        } catch (InterruptedException e) {
            logger.warning("Interrupted while waiting for threads to stop");
            Thread.currentThread().interrupt();
        }
        
        logger.info("Blockchain thread manager stopped");
    }
    
    /**
     * Submits a transaction for processing.
     * The transaction will be validated and eventually mined into a block.
     * This method is thread-safe and non-blocking.
     * 
     * @param transaction The transaction to submit
     */
    public void submitTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        
        incomingTransactions.offer(transaction);
        logger.fine("Transaction submitted: " + transaction.getTransactionId());
    }
    
    /**
     * Gets the number of transactions waiting to be processed.
     * 
     * @return Number of incoming transactions
     */
    public int getIncomingTransactionCount() {
        return incomingTransactions.size();
    }
    
    /**
     * Gets the number of transactions waiting to be mined.
     * 
     * @return Number of pending transactions
     */
    public int getPendingTransactionCount() {
        return pendingTransactions.size();
    }
    
    /**
     * Checks if the thread manager is currently running.
     * 
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return transactionProcessor != null && transactionProcessor.isRunning() &&
               blockMiningThread != null && blockMiningThread.isRunning();
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
