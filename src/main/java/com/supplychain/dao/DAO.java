package com.supplychain.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Generic Data Access Object (DAO) interface defining standard CRUD operations.
 * This interface uses generics to provide type-safe data access patterns.
 * 
 * @param <T> The entity type this DAO manages
 */
public interface DAO<T> {
    
    /**
     * Persists a new entity to the database.
     * 
     * @param entity The entity to save
     * @throws SQLException if a database access error occurs
     */
    void save(T entity) throws SQLException;
    
    /**
     * Retrieves an entity by its unique identifier.
     * 
     * @param id The unique identifier of the entity
     * @return The entity with the specified ID, or null if not found
     * @throws SQLException if a database access error occurs
     */
    T findById(String id) throws SQLException;
    
    /**
     * Retrieves all entities of this type from the database.
     * 
     * @return A list of all entities, empty list if none exist
     * @throws SQLException if a database access error occurs
     */
    List<T> findAll() throws SQLException;
    
    /**
     * Updates an existing entity in the database.
     * 
     * @param entity The entity with updated values
     * @throws SQLException if a database access error occurs
     */
    void update(T entity) throws SQLException;
    
    /**
     * Deletes an entity from the database by its unique identifier.
     * 
     * @param id The unique identifier of the entity to delete
     * @throws SQLException if a database access error occurs
     */
    void delete(String id) throws SQLException;
}
