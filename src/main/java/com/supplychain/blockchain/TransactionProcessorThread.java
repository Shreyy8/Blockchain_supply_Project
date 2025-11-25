package com.supplychain.blockchain;

import com.supplychain.model.Transaction;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Runnable thread for processing incoming transactions asynchronously.
 * Validates transactions and adds them to the pending queue for mining.
 * Uses thread-safe ConcurrentLinkedQueue for transaction management.
 * 
 * Requirements: 1.2 - Transaction persistence and availability
 * Requirements: 4.1 - Transaction validation
 */
public class TransactionProcessorThread implements Runnable {
    private static final Logger logger = Logger.getLogger(TransactionProcessorThread.class.getName());
    
    private final ConcurrentLinkedQueue<Transaction> incomingTransactions;
    private final ConcurrentLinkedQueue<Transaction> pendingTransactions;
    private volatile boolean running;
    private final long processingInterval;
    
    /**
     * Constructor for TransactionProcessorThread.
     * 
     * @param incomingTransactions Queue of incoming transactions to validate
     * @param pendingTransactions Queue of validated transactions ready for mining
     * @param processingInterval Time in milliseconds to wait between processing attempts
     */
    public TransactionProcessorThread(ConcurrentLinkedQueue<Transaction> incomingTransactions,
                                     ConcurrentLinkedQueue<Transaction> pendingTransactions,
                                     long processingInterval) {
        this.incomingTransactions = incomingTransactions;
        this.pendingTransactions = pendingTransactions;
        this.processingInterval = processingInterval;
        this.running = true;
    }
    
    /**
     * Main execution method for the transaction processor.
     * Continuously processes incoming transactions and validates them.
     */
    @Override
    public void run() {
        logger.info("Transaction processor thread started");
        
        while (running) {
            try {
                // Process all available incoming transactions
                while (!incomingTransactions.isEmpty()) {
                    Transaction transaction = incomingTransactions.poll();
                    
                    if (transaction != null) {
                        processTransaction(transaction);
                    }
                }
                
                // Wait before checking again
                Thread.sleep(processingInterval);
                
            } catch (InterruptedException e) {
                logger.info("Transaction processor interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.severe("Error in transaction processor: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        logger.info("Transaction processor thread stopped");
    }
    
    /**
     * Processes and validates a single transaction.
     * Valid transactions are added to the pending queue for mining.
     * 
     * @param transaction The transaction to process
     */
    private void processTransaction(Transaction transaction) {
        try {
            // Validate the transaction
            if (transaction.validate()) {
                // Add to pending transactions queue
                pendingTransactions.offer(transaction);
                logger.info("Transaction validated and queued: " + transaction.getTransactionId());
            } else {
                logger.warning("Transaction validation failed: " + transaction.getTransactionId());
            }
            
        } catch (Exception e) {
            logger.severe("Error processing transaction " + transaction.getTransactionId() + 
                         ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Stops the transaction processor gracefully.
     */
    public void stop() {
        logger.info("Stopping transaction processor");
        running = false;
    }
    
    /**
     * Checks if the transaction processor is currently running.
     * 
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
}
