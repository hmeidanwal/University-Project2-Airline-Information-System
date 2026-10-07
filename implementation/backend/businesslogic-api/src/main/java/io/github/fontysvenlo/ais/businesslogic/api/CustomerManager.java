package io.github.fontysvenlo.ais.businesslogic.api;

import io.github.fontysvenlo.ais.datarecords.CustomerData;
import io.github.fontysvenlo.ais.exceptions.DuplicateEmailException;
import io.github.fontysvenlo.ais.exceptions.PersistenceException;

import java.util.List;

/**
 * Interface for objects that are able to manage Customers.
 */
public interface CustomerManager {

    /**
     * Add a customer.
     *
     * @param customerData the customer to add
     * @return the added customer data
     */
      CustomerData add(CustomerData customerData);

    /**
     * Retrieve all customers.
     *
     * @return a list of all customers
     */
    public List<CustomerData> list();

       /**
     * Delete a customer by email.
     *
     * @param email the email of the customer to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean delete(String email);
}


