package io.github.fontysvenlo.ais.businesslogic;

import io.github.fontysvenlo.ais.datarecords.CustomerData;
import io.github.fontysvenlo.ais.exceptions.DateOfBirthException;
import io.github.fontysvenlo.ais.exceptions.EmailException;
import io.github.fontysvenlo.ais.exceptions.FirstNameException;
import io.github.fontysvenlo.ais.exceptions.FutureDateOfBirthException;
import io.github.fontysvenlo.ais.exceptions.LastNameException;
import io.github.fontysvenlo.ais.exceptions.PhoneException;
import io.github.fontysvenlo.ais.exceptions.TooOldBirthDateException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

/**
 * Tests for Customer class based on test scenarios.
 * Scenario 1: Valid customer creation
 * Scenario 2: Invalid data validation tests
 */
class CustomerTest {

    // Test data from scenarios
    private final String VALID_FIRST_NAME = "John";
    private final String VALID_LAST_NAME = "Doe";
    private final LocalDate VALID_DOB = LocalDate.of(1993, 5, 15);
    private final String VALID_EMAIL = "john.doe@example.com";
    private final String VALID_PHONE = "+31 6 12345678";

    /**
     * Test Scenario 1: Valid customer creation
     * Actor inserts valid customer contact details
     */
    @Test
    void testValidCustomerCreation() {
        // Arrange - Using exact data from scenario
        CustomerData validData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_DOB,
                VALID_EMAIL,
                VALID_PHONE);

        // Act
        Customer customer = Customer.createCustomer(validData);

        // Assert - Verify all fields match input
        assertNotNull(customer);
        assertEquals(VALID_FIRST_NAME, customer.getCustomerData().firstName());
        assertEquals(VALID_LAST_NAME, customer.getCustomerData().lastName());
        assertEquals(VALID_DOB, customer.getCustomerData().dateOfBirth());
        assertEquals(VALID_EMAIL, customer.getCustomerData().email());
        assertEquals(VALID_PHONE, customer.getCustomerData().phone());
    }

    /**
     * Test Scenario 2.2: First name validation
     * "If the first name is missing, too short or contains invalid characters,
     * the system will throw a FirstNameException"
     */
    @Test
    void testFirstNameMissing() {
        CustomerData invalidData = new CustomerData(
                null,
                VALID_LAST_NAME,
                VALID_DOB,
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(FirstNameException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testFirstNameBlank() {
        CustomerData invalidData = new CustomerData(
                "   ", // Only spaces
                VALID_LAST_NAME,
                VALID_DOB,
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(FirstNameException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testFirstNameTooShort() {
        CustomerData invalidData = new CustomerData(
                "J", // Single character
                VALID_LAST_NAME,
                VALID_DOB,
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(FirstNameException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testFirstNameInvalidCharacters() {
        CustomerData invalidData = new CustomerData(
                "John123", // Contains numbers
                VALID_LAST_NAME,
                VALID_DOB,
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(FirstNameException.class, () -> Customer.createCustomer(invalidData));
    }

    /**
     * Test Scenario 2.3: Last name validation
     * "If the last name is missing, too short or contains invalid characters,
     * the system will throw a LastNameException"
     */
    @Test
    void testLastNameMissing() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                null,
                VALID_DOB,
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(LastNameException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testLastNameBlank() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                "   ",
                VALID_DOB,
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(LastNameException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testLastNameTooShort() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                "D", // Single character
                VALID_DOB,
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(LastNameException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testLastNameInvalidCharacters() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                "Doe123", // Contains numbers
                VALID_DOB,
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(LastNameException.class, () -> Customer.createCustomer(invalidData));
    }

    /**
     * Test Scenario 2.4: Birth date validation
     * "If the birth date is missing or the customer is under 18,
     * the system will throw a DateOfBirthException"
     */
    @Test
    void testDateOfBirthMissing() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                null,
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(DateOfBirthException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testDateOfBirthUnder18() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                LocalDate.now().minusYears(17), // 17 years old
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(DateOfBirthException.class, () -> Customer.createCustomer(invalidData));
    }

    /**
     * Test Scenario 2.5: Future birth date validation
     * "If the birth date is in the future,
     * the system will throw a FutureDateOfBirthException"
     */
    @Test
    void testDateOfBirthInFuture() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                LocalDate.now().plusDays(1),
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(FutureDateOfBirthException.class, () -> Customer.createCustomer(invalidData));
    }

    /**
     * Test Scenario 2.6: Too old birth date validation
     * "If the birth date is before 1900,
     * the system will throw a TooOldBirthDateException"
     */
    @Test
    void testDateOfBirthTooOld() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                LocalDate.of(1899, 12, 31),
                VALID_EMAIL,
                VALID_PHONE);

        assertThrows(TooOldBirthDateException.class, () -> Customer.createCustomer(invalidData));
    }

    /**
     * Test Scenario 2.7: Email validation
     * "If the email is missing or has an invalid format,
     * the system will throw an EmailException"
     */
    @Test
    void testEmailMissing() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_DOB,
                null,
                VALID_PHONE);

        assertThrows(EmailException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testEmailBlank() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_DOB,
                "   ",
                VALID_PHONE);

        assertThrows(EmailException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testEmailInvalidFormat() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_DOB,
                "invalid-email", // Invalid email format
                VALID_PHONE);

        assertThrows(EmailException.class, () -> Customer.createCustomer(invalidData));
    }

    /**
     * Test Scenario 2.8: Phone validation
     * "If the phone number is missing or contains invalid characters,
     * the system will throw a PhoneException"
     */
    @Test
    void testPhoneMissing() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_DOB,
                VALID_EMAIL,
                null);

        assertThrows(PhoneException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testPhoneBlank() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_DOB,
                VALID_EMAIL,
                "   ");

        assertThrows(PhoneException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testPhoneInvalidCharacters() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_DOB,
                VALID_EMAIL,
                "abc12345" // Contains letters
        );

        assertThrows(PhoneException.class, () -> Customer.createCustomer(invalidData));
    }

    @Test
    void testPhoneTooShort() {
        CustomerData invalidData = new CustomerData(
                VALID_FIRST_NAME,
                VALID_LAST_NAME,
                VALID_DOB,
                VALID_EMAIL,
                "+31 1" // only 3 digits
        );

        assertThrows(PhoneException.class, () -> Customer.createCustomer(invalidData));
    }

}