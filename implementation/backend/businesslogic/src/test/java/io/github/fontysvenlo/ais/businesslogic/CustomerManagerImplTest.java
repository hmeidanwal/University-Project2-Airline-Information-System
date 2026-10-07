package io.github.fontysvenlo.ais.businesslogic;

import io.github.fontysvenlo.ais.datarecords.CustomerData;
import io.github.fontysvenlo.ais.exceptions.DeletionFailedException;
import io.github.fontysvenlo.ais.exceptions.DuplicateEmailException;
import io.github.fontysvenlo.ais.exceptions.RetrievalFailedException;
import io.github.fontysvenlo.ais.persistence.api.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CustomerManagerImpl based on test scenarios.
 */
class CustomerManagerImplTest {

    private CustomerManagerImpl customerManager;
    private CustomerRepository mockRepository;

    // Test data from scenario
    private final CustomerData johnDoe = new CustomerData(
            "John",
            "Doe",
            LocalDate.of(1993, 5, 15),
            "john.doe@example.com",
            "+31 6 12345678");

    @BeforeEach
    void setUp() {
        mockRepository = mock(CustomerRepository.class);
        customerManager = new CustomerManagerImpl(mockRepository);
    }

    /**
     * Test Scenario: Retrieve list of customers - Valid case
     * "System retrieves and displays a list of all customers"
     */
    @Test
    void testGetAllCustomers() {
        // Arrange
        List<CustomerData> expectedCustomers = Arrays.asList(johnDoe);
        when(mockRepository.getAll()).thenReturn(expectedCustomers);

        // Act
        List<CustomerData> actualCustomers = customerManager.list();

        // Assert
        assertEquals(expectedCustomers, actualCustomers);
        verify(mockRepository).getAll();
    }

    /**
     * Test Scenario: Retrieve list of customers - Extension 1a
     * "System is unable to fetch the customer data due to a server or database
     * issue"
     */
    @Test
    void testGetAllCustomersFailure() {
        // Arrange
        when(mockRepository.getAll()).thenThrow(new RetrievalFailedException(
                "Customer data cannot be retrieved at the moment due to a temporary server issue. Please try again later!"));

        // Act & Assert
        assertThrows(RetrievalFailedException.class, () -> customerManager.list());
        verify(mockRepository).getAll();
    }

    /**
     * Test Scenario: Delete customer - Valid case
     * "System deletes customer John Doe with email address: john.doe@example.com
     * from the database"
     */
    @Test
    void testDeleteCustomer() {
        // Arrange
        String email = "john.doe@example.com";
        when(mockRepository.delete(email)).thenReturn(true);

        // Act
        boolean result = customerManager.delete(email);

        // Assert
        assertTrue(result);
        verify(mockRepository).delete(email);
    }

    /**
     * Test Scenario: Delete customer - Extension 5a
     * "System failed to delete the customer due to server or database error"
     */
    @Test
    void testDeleteCustomerFailure() {
        // Arrange
        String email = "john.doe@example.com";
        when(mockRepository.delete(email))
                .thenThrow(new DeletionFailedException("Failed to delete customer with Email address: " + email));

        // Act & Assert
        assertThrows(DeletionFailedException.class, () -> customerManager.delete(email));
        verify(mockRepository).delete(email);
    }

    @Test
    void testDeleteCustomerReturnsFalse() throws DeletionFailedException {
        when(mockRepository.delete(johnDoe.email())).thenReturn(false);

        boolean result = customerManager.delete(johnDoe.email());

        assertFalse(result);
        verify(mockRepository).delete(johnDoe.email());
    }

    @Test
    void testAddCustomerDuplicateEmail() {
        when(mockRepository.add(johnDoe)).thenThrow(new DuplicateEmailException(johnDoe.email()));

        assertThrows(DuplicateEmailException.class, () -> customerManager.add(johnDoe));
        verify(mockRepository).add(johnDoe);
    }

    @Test
    void testAddCustomerSuccess() {
        when(mockRepository.add(johnDoe)).thenReturn(johnDoe);

        CustomerData result = customerManager.add(johnDoe);

        assertEquals(johnDoe, result);
        verify(mockRepository).add(johnDoe);
    }

}