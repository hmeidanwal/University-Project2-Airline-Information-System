package io.github.fontysvenlo.ais.businesslogic;

import io.github.fontysvenlo.ais.datarecords.CustomerData;
import io.github.fontysvenlo.ais.exceptions.DateOfBirthException;
import io.github.fontysvenlo.ais.exceptions.EmailException;
import io.github.fontysvenlo.ais.exceptions.FirstNameException;
import io.github.fontysvenlo.ais.exceptions.FutureDateOfBirthException;
import io.github.fontysvenlo.ais.exceptions.LastNameException;
import io.github.fontysvenlo.ais.exceptions.PhoneException;
import io.github.fontysvenlo.ais.exceptions.TooOldBirthDateException;

import java.time.LocalDate;

/**
 * Wrapper class that contains CustomerData and Customer Business Logic.
 */
public class Customer {

    private final CustomerData customerData;

    /**
     * Private constructor - use createCustomer factory method instead.
     * This ensures object is only created after validation passes.
     */
    private Customer(CustomerData customerData) {
        this.customerData = customerData;
    }

    /**
     * Factory method to create a new Customer after validating the data.
     * 
     * @param customerData the customer data to validate
     * @return a new Customer instance
     * @throws FirstNameException if first name is invalid
     * @throws LastNameException if last name is invalid
     * @throws DateOfBirthException if date of birth is invalid
     * @throws EmailException if email is invalid
     * @throws PhoneException if phone number is invalid
     */
    public static Customer createCustomer(CustomerData customerData) {
        validateCustomerData(customerData);
        return new Customer(customerData);
    }

    /**
     * Validates all customer data fields.
     * 
     * @param customerData the data to validate
     * @throws FirstNameException if first name is invalid
     * @throws LastNameException if last name is invalid
     * @throws DateOfBirthException if date of birth is invalid
     * @throws EmailException if email is invalid
     * @throws PhoneException if phone number is invalid
     */
    private static void validateCustomerData(CustomerData customerData) {
        validateFirstName(customerData.firstName());
        validateLastName(customerData.lastName());
        validateDateOfBirth(customerData.dateOfBirth());
        validateEmail(customerData.email());
        validatePhone(customerData.phone());
    }

    private static void validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new FirstNameException("First name is required");
        }
        if (firstName.length() < 2) {
            throw new FirstNameException("First name must be at least 2 characters");
        }
        if (!firstName.matches("^[\\p{L} ]+$")) {
            throw new FirstNameException("First name can only contain letters and spaces");
        }
    }

    private static void validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new LastNameException("Last name is required");
        }
        if (lastName.length() < 2) {
            throw new LastNameException("Last name must be at least 2 characters");
        }
        if (!lastName.matches("^[\\p{L} ]+$")) {
            throw new LastNameException("Last name can only contain letters and spaces");
        }
    }

    private static void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new DateOfBirthException("Date of birth is required");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new FutureDateOfBirthException("Date of birth cannot be in the future");
        }
        if (dateOfBirth.isBefore(LocalDate.of(1900, 1, 1))) {
            throw new TooOldBirthDateException("Date of birth cannot be before 1900");
        }
        if (dateOfBirth.isAfter(LocalDate.now().minusYears(18))) {
            throw new DateOfBirthException("Customer must be at least 18 years old");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new EmailException("Email is required");
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new EmailException("Invalid email format");
        }
    }

    private static void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new PhoneException("Phone number is required");
        }
        if (!phone.matches("^[+]?[0-9\\s]+$")) {
            throw new PhoneException("Phone number can only contain numbers, spaces, and optional + prefix");
        }
        if (phone.replaceAll("[^0-9]", "").length() < 6) {
            throw new PhoneException("Phone number must contain at least 6 digits");
        }
    }

    /**
     * Get the customer data.
     * @return the customer data
     */
    public CustomerData getCustomerData() {
        return customerData;
    }
}

// The DuplicateEmailException is not used in the business logic, but it is used
// in the persistence layer.
// It checks for duplicate email addresses when adding a new customer to the
// database.
// Why in persistence layer? Because the customer class only knows about the
// data for one customer.
// Only the CustomerRepositoryImpl (which talks to the database) knows if an
// email already exists.
// The business logic should not be concerned with how the data is stored or
// retrieved!