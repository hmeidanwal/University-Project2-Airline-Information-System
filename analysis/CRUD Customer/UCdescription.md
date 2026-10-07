## Use Case: Add Customer

| Field             | Details |
|-------------------|---------|
| **Name**          | Add Customer |
| **Actor**         | Sales Employee |
| **Description**   | As a sales Employee I want to be able to add new customers, so that I can create bookings. |
| **Pre-condition** | - Actor is logged in |
| **Scenario**      | 1. Actor inserts the customer’s contact details: <br> - First name <br> - Last name <br> - Date of birth <br> - Email address <br> - Phone number <br> 2. System validates the data <br> 3. System saves the new customer |
| **Result**        | A new customer is succesfully added and can now make trip bookings. |
| **Exceptions**    | 2.1 DuplicateEmailException - The provided email already exist in the system. <br> 2.2 FirstNameException - First name is missing, too short or contains invalid characters. <br> 2.3 LastNameException - Last name is missing, too short or contains invalid characters. <br> 2.4 DateOfBirthException - Birth date is missing, or customer is under 18. <br> 2.5 FutureDateOfBirthException - Birth date is in the Future. <br> 2.6 TooOldBirthDateException - Birth date is before 1900. <br> 2.7 EmailException - Email is missing or has invalid format. <br> 2.8 PhoneException - Phone number is missing or consists invalid characters |

<br>
<br>
<br>

## Use case: Delete customer
| Field             | Details |
|-------------------|---------|
| **Name**          | Delete Customer |
| **Actor**         | Sales Employee |
| **Description**   | As a sales employee I want to be able to delete existing customers, so that I can maintain an up-to-date and accurate customer database. |
| **Pre-condition** | Actor is logged in |
| **Scenario**      | 1. System displays a list of all customers including details: <br> - Email <br> - Last name <br> - First name <br> - Date of birth <br> - Phone number <br> 2. Actor selects the desired customer to be deleted by email. <br> 3. System requests confirmation. <br> 4. Actor confirms the deletion. <br> 5. System deletes the customer from the database. |
| **Result**        | A customer is successfully deleted and no longer exists in the system. |
| **Exceptions**    | 5.1 DeletionFailedException - A server or database error prevents the customer from being deleted. |

<br>
<br>
<br>

## Use case: Retrieve list of customers
| Field             | Details |
|-------------------|---------|
| **Name**          | Retrieve list of customers |
| **Actor**         | Sales Employee |
| **Description**   | As a sales Employee I want to be able to retrieve a list of customers, so that I can view, edit, or delete customer records as needed.|
| **Pre-condition** | Actor is logged in |
| **Scenario**      | 1. System retrieves and displays a list of all customers, including: <br> - Email <br> - Last name <br> - First name <br> - Phone number <br> - Actions |
| **Result**        | An up-to-date list of all customers is displayed, including details like: email, last name, first name, phone number, and actions.                                                             |
| **Exceptions**    | 1.1 RetrievalFailedException - System is unable to fetch the customer data due to a server or database issue.                                                                  |








