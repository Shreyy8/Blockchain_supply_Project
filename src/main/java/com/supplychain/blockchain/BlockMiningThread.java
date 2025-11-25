package com.supplychain.blockchain;

import com.supplychain.model.Transaction;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * Background thread for mining blocks from pending transactions.
 * Performs proof-of-work mining asynchronously to avoid blocking the main application.
 * Uses synchronized access to ensure thread-safe blockchain operations.
 * 
 * Requirements: 1.2 - Transaction persistence and availability
 * Requirements: 4.2 - Block creation with cryptographic hash
 */
public class BlockMiningThread extends Thread {
    private static final Logger logger = Logger.getLogger(BlockMiningThread.class.getName());
    
    private final BlockchainManager blockchainManager;
    private final ConcurrentLinkedQueue<Transaction> pendingTransactions;
    private volatile boolean running;
    private final int batchSize;
    private final long miningInterval;
    
    /**
     * Constructor for BlockMiningThread.
     * 
     * @param blockchainManager The blockchain manager to mine blocks for
     * @param pendingTransactions Queue of pending transactions to process
     * @param batchSize Number of transactions to include in each block
     * @param miningInterval Time in milliseconds to wait between mining attempts
     */
    public BlockMiningThread(BlockchainManager blockchainManager, 
                            ConcurrentLinkedQueue<Transaction> pendingTransactions,
                            int batchSize,
                            long miningInterval) {
        this.blockchainManager = blockchainManager;
        this.pendingTransactions = pendingTransactions;
        this.batchSize = batchSize;
        this.miningInterval = miningInterval;
        this.running = true;
        this.setName("BlockMiningThread");
        this.setDaemon(true); // Allow JVM to exit even if this thread is running
    }
    
    /**
     * Main execution method for the mining thread.
     * Continuously checks for pending transactions and mines blocks when available.
     */
    @Override
    public void run() {
        logger.info("Block mining thread started");
        
        while (running) {
            try {
                // Check if there are enough pending transactions to mine
                if (pendingTransactions.size() >= batchSize) {
                    mineBlock();
                }
                
                // Wait before checking again
                Thread.sleep(miningInterval);
                
            } catch (InterruptedException e) {
                logger.info("Mining thread interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.severe("Error in mining thread: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        logger.info("Block mining thread stopped");
    }
    
    /**
     * Mines a block from pending transactions with synchronized access to blockchain.
     * Ensures thread-safe operations when adding transactions and mining blocks.
     */
    private void mineBlock() {
        synchronized (blockchainManager) {
            try {
                // Transfer transactions from queue to blockchain manager
                int count = 0;
                while (!pendingTransactions.isEmpty() && count < batchSize) {
                    Transaction transaction = pendingTransactions.poll();
                    if (transaction != null) {
                        blockchainManager.addTransaction(transaction);
                        count++;
                    }
                }
                
                // Mine the block if we added any transactions
                if (count > 0) {
                    logger.info("Mining block with " + count + " transactions");
                    Block minedBlock = blockchainManager.minePendingTransactions();
                    
                    if (minedBlock != null) {
                        logger.info("Successfully mined block #" + minedBlock.getIndex() + 
                                   " with hash: " + minedBlock.getHash());
                    }
                }
                
            } catch (Exception e) {
                logger.severe("Error mining block: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Stops the mining thread gracefully.
     */
    public void stopMining() {
        logger.info("Stopping mining thread");
        running = false;
        this.interrupt();
    }
    
    /**
     * Checks if the mining thread is currently running.
     * 
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
}
