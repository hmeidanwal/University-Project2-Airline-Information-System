package io.github.fontysvenlo.ais.persistence.api;

import io.github.fontysvenlo.ais.datarecords.CustomerData;
import io.github.fontysvenlo.ais.exceptions.DuplicateEmailException;
import io.github.fontysvenlo.ais.exceptions.PersistenceException;

import java.util.List;

/**
 * Interface that describes all services offered by the CustomerRepository.
 */
public interface CustomerRepository {
    /**
     * Adds a customer to the storage.
     * @param customerData the customer to add
     * @return the added customer
     */
    CustomerData add(CustomerData customerData) throws DuplicateEmailException, PersistenceException;

    /**
     * Retrieves all customers from the storage.
     * @return a list of all customers
     */
    List<CustomerData> getAll();

    /**
 * Deletes a customer from the storage.
 * @param email the email of the customer to delete
 * @return true if deletion was successful, false otherwise
 */
boolean delete(String email);
}