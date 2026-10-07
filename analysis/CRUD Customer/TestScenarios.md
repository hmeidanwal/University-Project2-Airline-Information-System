# TEST SCENARIOS: ADD CUSTOMER

| Field             | Details |
|-------------------|---------|
| **Name**          | Add Customer |
| **Scenario**      | 1. Actor inserts the customer contact details .<br> - First name: John <br> - Last name: Doe <br> - Date of birth: 15/05/1993 <br> - Email address: john.doe@example.com <br> - Phone number: +31 6 12345678 <br> 2. System validates the data.<br> 3. System saves the new customer |
| **Result**        | The system successfully adds a new customer, John Doe, and the Sales Employee can now proceed to make trip bookings. |
| **Extensions**    | **2a. Invalid data** <br> 1. If the email is already in the system, the system will throw a DuplicateEmailException. <br> 2. If the first name is missing, too short or contains invalid characters, the system will throw a FirstNameException. <br> 3. If the last name is missing, too short or contains invalid characters, the system will throw a LastNameException. <br> 4. If the birth date is missing or the customer is under 18, the system will throw a DateOfBirthException. <br> 5. If the birth date is in the future, the system will throw a FutureDateOfBirthException. <br> 6. If the birth date is before 1900, the system will throw a TooOldBirthDateException. <br> 7. If the email is missing or has an invalid format, the system will throw an EmailException. <br> 8. If the phone number is missing or contains invalid characters, the system will throw a PhoneException. |

<br>
<br>
<br>

# TEST SCENARIOS: DELETE CUSTOMER 
| Field             | Details|
|-------------------|--------|
| **Name**          | Delete customer |
| **Scenario**      | 1. System displays a list of all customers including details: <br> - Email <br> - Last name <br> - First name <br> - Date of birth <br> - Phone number <br> 2. Actor selects the desired customer to be deleted by email: john.doe@example.com. <br> 3. System requests confirmation: “Are you sure you want to delete the customer with email address: john.doe@example.com ?”. <br> 4. Actor confirms the deletion. <br> 5. System deletes customer John Doe with email address: john.doe@example.com from the database.    |
| **results**       | Customer John Doe with email address john.doe@example.com is successfully deleted and no longer exists in the system.                |
| **Extensions**    | **5a. Deletion failed** <br> 1. System failed to delete the customer due to server or database error and throws a DeletionFailedException.               |

<br>
<br>
<br>

# TEST SCENARIOS: RETRIEVE LIST OF CUSTOMERS
| Field             | Details|
|-------------------|--------|
| **Name**          | Retrieve list of customers |
| **Scenario**      | 1. System retrieves and displays a list of all customers, including: <br> - Email <br> - Last name <br> - First name <br> - Date of birth <br> - Phone number <br> - Actions                |
| **results**       | An up-to-date list of all customers is displayed, including details like: email, last name, first name, date of birth, phone number, and actions.               |
| **Extensions**    | **1a. Data retrieval failed** <br> System is unable to fetch the customer data due to a server or database issue, system will throw a retrievalFailedException.              |

