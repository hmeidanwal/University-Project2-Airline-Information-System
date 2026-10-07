package io.github.fontysvenlo.ais.businesslogic;

import io.github.fontysvenlo.ais.businesslogic.api.CustomerManager;
import java.util.List;

import io.github.fontysvenlo.ais.datarecords.CustomerData;
import io.github.fontysvenlo.ais.exceptions.DeletionFailedException;
import io.github.fontysvenlo.ais.exceptions.DuplicateEmailException;
import io.github.fontysvenlo.ais.exceptions.PersistenceException;
import io.github.fontysvenlo.ais.persistence.api.CustomerRepository;


/**
 * Manages customers in the business logic.
 * Linking pin between GUI and persistence. Connected to CustomerRepository
 * in order to retrieve customers and to persist changes.
 */
public class CustomerManagerImpl implements CustomerManager{

    private final CustomerRepository customerRepository;

    /**
     * Constructor
     * @param CustomerRepository the customer storage service
     */
    public CustomerManagerImpl( CustomerRepository CustomerRepository ) {
        this.customerRepository = CustomerRepository;
    }

/**
 * Adds a customer to the storage.
 * @param customerData the customer to add
 * @return the added customer data
 * @throws DuplicateEmailException if the email already exists
 * @throws PersistenceException if a persistence error occurs
 */
@Override
public CustomerData add(CustomerData customerData) {
    // Validate first using factory method
    Customer customer = Customer.createCustomer(customerData);
    // Then add to repository
    return customerRepository.add(customerData);
}

    /**
     * Retrieves all customers from the storage.
     * @return a list of all customers
     */
    @Override
    public List<CustomerData> list(){
        return customerRepository.getAll();
    }

    @Override
    public boolean delete(String email) throws DeletionFailedException {
    return customerRepository.delete(email);
}





}
