-- Copy this in to DBeaver to create the tables
<!-- ADD YOUR CHANGES TO DATABASE HERE. THIS WAY EVERYONE IS ABLE TO UPDATE THE DATABASE-->

DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Passenger;
DROP TABLE IF EXISTS Discounts;
DROP TABLE IF EXISTS Classes;
DROP TABLE IF EXISTS Flight;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Customer;
Drop


-- Table: Customer
CREATE TABLE IF NOT EXISTS Customer (
    Email VARCHAR(100) PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    BirthDate DATE, <!-- CHANGED 'age INT' TO 'BirthDate DATE' AS IT'S MORE EFFICIENT TO USE -->
    Phone VARCHAR(20),
    Address TEXT <!--REMOVED THE ADDRES! -->
);

-- Table: Employee
CREATE TABLE IF NOT EXISTS Employee (
    ID SERIAL PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Email VARCHAR(100) UNIQUE,
    Password VARCHAR(100),
    Role VARCHAR(50)
);

-- this is still in progress the flight uses different method for now
-- Table: Flight
CREATE TABLE IF NOT EXISTS Flight (
    ID SERIAL PRIMARY KEY,
    DepartureDate DATE,
    ArrivalDate DATE,
    DepartureAirport VARCHAR(100),
    ArrivalAirport VARCHAR(100),
    Duration INTERVAL,
    MaximumSeat INT
);
CREATE TABLE IF NOT EXISTS Flight (
    id SERIAL PRIMARY KEY, 
    departure VARCHAR(100),
    arrival VARCHAR(100),
    date DATE,
    duration decimal
);

-- Table: Classes
CREATE TABLE IF NOT EXISTS Classes (
    ID SERIAL PRIMARY KEY,
    Type VARCHAR(50),
    AmountOfSeat INT,
    Price DECIMAL(10,2),
    FlightID INT REFERENCES Flight(ID)
);

-- Table: Discounts
CREATE TABLE IF NOT EXISTS Discounts (
    ID SERIAL PRIMARY KEY,
    Type VARCHAR(50),
    Amount DECIMAL(5,2)
);

-- Table: Passenger
CREATE TABLE IF NOT EXISTS Passenger (
    PassportNumber VARCHAR(50) PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    BirthDate INT, <!-- CHANGED 'age INT' TO 'BirthDate DATE' AS IT'S MORE EFFICIENT TO USE -->
    CustomerEmail VARCHAR(100) REFERENCES Customer(Email)
);

-- Table: Booking
CREATE TABLE IF NOT EXISTS Booking (
    ID SERIAL PRIMARY KEY,
    FlightID INT REFERENCES Flight(ID),
    Price DECIMAL(10,2),
    DiscountID INT REFERENCES Discounts(ID),
    Luggage BOOLEAN,
    Food BOOLEAN,
    ClassType VARCHAR(50),
    SeatNumber VARCHAR(10),
    CustomerEmail VARCHAR(100) REFERENCES Customer(Email),
    EmployeeID INT REFERENCES Employee(ID),
    Paid BOOLEAN
);


-- Sample data for Customer <!--(Age -> BirthDate)-->
INSERT INTO Customer (Email, FirstName, LastName, BirthDate, Phone, Address) VALUES
('john.doe@example.com', 'John', 'Doe', '1993-05-15', '123-456-7890', '123 Elm Street, Springfield, IL'),
('jane.smith@example.com', 'Jane', 'Smith', '1995-08-22', '987-654-3210', '456 Oak Avenue, Springfield, IL'),
('bob.jones@example.com', 'Bob', 'Jones', '1978-11-30', '555-123-4567', '789 Pine Road, Springfield, IL');

-- Sample data for Employee
INSERT INTO Employee (FirstName, LastName, Email, Password, Role) VALUES
('Alice', 'Brown', 'alice.brown@example.com', 'password123', 'Manager'),
('David', 'Wilson', 'david.wilson@example.com', 'password123', 'Agent'),
('Sarah', 'Taylor', 'sarah.taylor@example.com', 'password123', 'Agent');

-- this is still in progress and the search uses different method for now
-- Sample data for Flight
-- INSERT INTO Flight (DepartureDate, ArrivalDate, DepartureAirport, ArrivalAirport, Duration, MaximumSeat) VALUES
-- ('2025-05-01', '2025-05-01', 'amsterdam', 'london', '02:00:00', 200),
-- ('2025-05-02', '2025-05-02', 'berlin', 'new york', '03:30:00', 150),
-- ('2025-05-03', '2025-05-03', 'london', 'eindhoven', '04:00:00', 180);
INSERT INTO flights (departure, arrival, date, duration) VALUES
    ('Amsterdam', 'London', '2025-06-01', 1.0),
    ('Paris', 'Berlin', '2025-07-02', 1.75),
    ('London', 'Amsterdam', '2025-07-01', 1.0),
    ('Amsterdam', 'London', '2025-07-03', 1.5),
    ('London', 'Paris', '2026-04-02', 2.0),
    ('London', 'Paris', '2026-04-04', 2.0),
    ('London', 'Amsterdam', '2026-05-04', 1.0);



-- Sample data for Classes
INSERT INTO Classes (Type, AmountOfSeat, Price, FlightID) VALUES
('Economy', 180, 200.00, 1),
('Business', 20, 500.00, 1),
('First Class', 10, 1000.00, 1),
('Economy', 120, 250.00, 2),
('Business', 20, 600.00, 2),
('Economy', 150, 300.00, 3);

-- Sample data for Discounts
INSERT INTO Discounts (Type, Amount) VALUES
('Student', 15.00),
('Senior', 20.00),
('Frequent Flyer', 10.00);

-- Sample data for Passenger <!--(Age -> BirthDate)-->
INSERT INTO Passenger (PassportNumber, FirstName, LastName, BirthDate, CustomerEmail) VALUES
('P12345678', 'Michael', 'Clark', '1988-04-10', 'john.doe@example.com'),
('P98765432', 'Emily', 'Johnson', '1983-07-18', 'jane.smith@example.com'),
('P11223344', 'Chris', 'Davis', '1973-12-05', 'bob.jones@example.com');

-- Sample data for Booking
INSERT INTO Booking (FlightID, Price, DiscountID, Luggage, Food, ClassType, SeatNumber, CustomerEmail, EmployeeID, Paid) VALUES
(1, 180.00, 1, TRUE, TRUE, 'Economy', '12A', 'john.doe@example.com', 2, TRUE),
(1, 500.00, 3, FALSE, TRUE, 'Business', '2B', 'jane.smith@example.com', 1, TRUE),
(2, 250.00, 2, TRUE, FALSE, 'Economy', '15D', 'bob.jones@example.com', 3, FALSE),
(2, 600.00, 2, TRUE, TRUE, 'Business', '3A', 'john.doe@example.com', 2, TRUE),
(3, 300.00, 1, FALSE, FALSE, 'Economy', '10F', 'jane.smith@example.com', 3, FALSE);

<!--ALTER TABLE Customer DROP COLUMN address;-->
