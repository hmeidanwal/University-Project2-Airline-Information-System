-- AIS Database Initialization Script
-- Drop existing tables and views if they exist
DROP VIEW IF EXISTS FlightAvailability;
DROP TABLE IF EXISTS Booking;
DROP TABLE IF EXISTS Passenger;
DROP TABLE IF EXISTS Classes;
DROP TABLE IF EXISTS Discounts;
DROP TABLE IF EXISTS Flight;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Customer;

-- Create Customer table
CREATE TABLE Customer (
    Email VARCHAR(100) PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    BirthDate DATE NOT NULL,
    Phone VARCHAR(20) NOT NULL
);

-- Create Employee table
CREATE TABLE Employee (
    ID SERIAL PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(100) NOT NULL,
    Role VARCHAR(50) NOT NULL
);

-- Create Flight table (consistent with FlightRepositoryImpl queries)
CREATE TABLE flights (
    id SERIAL PRIMARY KEY, 
    departure VARCHAR(100) NOT NULL,
    arrival VARCHAR(100) NOT NULL,
    departuretime TIMESTAMP NOT NULL,
    arrivaltime TIMESTAMP NOT NULL,
    totalSeats INTEGER DEFAULT 60 NOT NULL
);

-- Create Flight table alias for BookingRepositoryImpl (maintains compatibility)
CREATE VIEW Flight AS 
SELECT id, departure, arrival, 
       departuretime::DATE as date, 
       EXTRACT(EPOCH FROM (arrivaltime - departuretime))/3600 as duration,
       totalSeats
FROM flights;

-- Create Classes table
CREATE TABLE Classes (
    ID SERIAL PRIMARY KEY,
    Type VARCHAR(50) NOT NULL,
    AmountOfSeat INTEGER NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    FlightID INTEGER REFERENCES flights(id) ON DELETE CASCADE
);

-- Create Discounts table
CREATE TABLE Discounts (
    ID SERIAL PRIMARY KEY,
    Type VARCHAR(50) NOT NULL,
    Amount DECIMAL(5,2) NOT NULL
);

-- Create Passenger table
CREATE TABLE Passenger (
    PassportNumber VARCHAR(50) PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    BirthDate DATE NOT NULL,
    CustomerEmail VARCHAR(100) REFERENCES Customer(Email) ON DELETE CASCADE
);

-- Create Booking table (consistent with BookingData record)
CREATE TABLE Booking (
    ID SERIAL PRIMARY KEY,
    FlightID INTEGER REFERENCES flights(id) ON DELETE CASCADE,
    Price DECIMAL(10,2) NOT NULL,
    DiscountID INTEGER REFERENCES Discounts(ID),
    Luggage BOOLEAN DEFAULT FALSE,
    Food BOOLEAN DEFAULT FALSE,
    ClassType VARCHAR(50) DEFAULT 'Economy',
    SeatNumber VARCHAR(10) NOT NULL,
    CustomerEmail VARCHAR(100) REFERENCES Customer(Email) ON DELETE CASCADE,
    CustomerName VARCHAR(100) NOT NULL,
    EmployeeID INTEGER REFERENCES Employee(ID),
    Paid BOOLEAN DEFAULT FALSE,
    BookingDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(FlightID, SeatNumber)
);

-- Create FlightAvailability view (required by BookingRepositoryImpl)
CREATE VIEW FlightAvailability AS
SELECT 
    f.id AS FlightID,
    f.departure,
    f.arrival,
    f.departuretime::DATE::VARCHAR AS date,
    f.totalSeats AS TotalSeats,
    COALESCE(b.BookedSeats, 0) AS BookedSeats,
    (f.totalSeats - COALESCE(b.BookedSeats, 0)) AS AvailableSeats,
    CASE WHEN COALESCE(b.BookedSeats, 0) >= f.totalSeats THEN TRUE ELSE FALSE END AS IsFull,
    CASE 
        WHEN f.totalSeats > 0 THEN ROUND((COALESCE(b.BookedSeats, 0) * 100.0 / f.totalSeats), 2)
        ELSE 0.0 
    END AS OccupancyPercentage
FROM flights f
LEFT JOIN (
    SELECT FlightID, COUNT(*) AS BookedSeats
    FROM Booking
    GROUP BY FlightID
) b ON f.id = b.FlightID;

-- Insert sample data

-- Sample Customers
INSERT INTO Customer (Email, FirstName, LastName, BirthDate, Phone) VALUES
('john.doe@example.com', 'John', 'Doe', '1990-05-15', '+31-123-456-7890'),
('jane.smith@example.com', 'Jane', 'Smith', '1985-08-22', '+31-987-654-3210'),
('bob.jones@example.com', 'Bob', 'Jones', '1978-11-30', '+31-555-123-4567'),
('alice.brown@example.com', 'Alice', 'Brown', '1992-03-10', '+31-111-222-3333'),
('charlie.wilson@example.com', 'Charlie', 'Wilson', '1988-07-25', '+31-444-555-6666');

-- Sample Employees
INSERT INTO Employee (FirstName, LastName, Email, Password, Role) VALUES
('Alice', 'Brown', 'alice.brown@ais.com', '$2a$10$example', 'Manager'),
('David', 'Wilson', 'david.wilson@ais.com', '$2a$10$example', 'Sales Employee'),
('Sarah', 'Taylor', 'sarah.taylor@ais.com', '$2a$10$example', 'Sales Employee'),
('Michael', 'Davis', 'michael.davis@ais.com', '$2a$10$example', 'Sales Employee');

-- Sample Flights (consistent with FlightRepositoryImpl expectations)
INSERT INTO flights (departure, arrival, departuretime, arrivaltime, totalSeats) VALUES
('Amsterdam', 'London', '2025-06-01 08:00:00', '2025-06-01 09:30:00', 60),
('Paris', 'Berlin', '2025-07-02 10:00:00', '2025-07-02 11:45:00', 60),
('London', 'Amsterdam', '2025-07-01 14:00:00', '2025-07-01 15:15:00', 60),
('Amsterdam', 'London', '2025-07-03 16:00:00', '2025-07-03 17:30:00', 60),
('London', 'Paris', '2025-04-02 09:00:00', '2025-04-02 11:00:00', 60),
('London', 'Paris', '2025-04-04 13:00:00', '2025-04-04 15:00:00', 60),
('London', 'Amsterdam', '2025-05-04 11:00:00', '2025-05-04 12:15:00', 60),
('Berlin', 'Rome', '2025-06-15 07:30:00', '2025-06-15 10:00:00', 60),
('Rome', 'Madrid', '2025-06-20 15:00:00', '2025-06-20 17:00:00', 60),
('Madrid', 'London', '2025-06-25 12:00:00', '2025-06-25 14:15:00', 60),
-- Add more connecting flights for transfer possibilities
('Amsterdam', 'Paris', '2025-06-01 10:00:00', '2025-06-01 11:15:00', 60),
('Paris', 'London', '2025-06-01 13:00:00', '2025-06-01 14:15:00', 60),
('London', 'Berlin', '2025-07-02 14:00:00', '2025-07-02 15:45:00', 60),
('Berlin', 'Amsterdam', '2025-07-02 17:00:00', '2025-07-02 18:15:00', 60),
('Amsterdam', 'Rome', '2025-06-15 09:00:00', '2025-06-15 11:30:00', 60);

-- Sample Classes for flights
INSERT INTO Classes (Type, AmountOfSeat, Price, FlightID) VALUES
-- Flight 1: Amsterdam to London
('Economy', 150, 200.00, 1),
('Business', 25, 500.00, 1),
('First Class', 5, 1000.00, 1),
-- Flight 2: Paris to Berlin
('Economy', 120, 250.00, 2),
('Business', 25, 600.00, 2),
('First Class', 5, 1200.00, 2),
-- Flight 3: London to Amsterdam
('Economy', 150, 180.00, 3),
('Business', 25, 450.00, 3),
('First Class', 5, 900.00, 3);

-- Sample Discounts
INSERT INTO Discounts (Type, Amount) VALUES
('Student', 15.00),
('Senior', 20.00),
('Frequent Flyer', 10.00),
('Group', 12.00),
('Early Bird', 8.00);

-- Sample Passengers
INSERT INTO Passenger (PassportNumber, FirstName, LastName, BirthDate, CustomerEmail) VALUES
('P12345678', 'John', 'Doe', '1990-05-15', 'john.doe@example.com'),
('P98765432', 'Jane', 'Smith', '1985-08-22', 'jane.smith@example.com'),
('P11223344', 'Bob', 'Jones', '1978-11-30', 'bob.jones@example.com'),
('P55667788', 'Alice', 'Brown', '1992-03-10', 'alice.brown@example.com'),
('P99887766', 'Charlie', 'Wilson', '1988-07-25', 'charlie.wilson@example.com');

-- Sample Bookings
INSERT INTO Booking (FlightID, Price, DiscountID, Luggage, Food, ClassType, SeatNumber, CustomerEmail, CustomerName, EmployeeID, Paid) VALUES
(1, 170.00, 1, TRUE, TRUE, 'Economy', '12A', 'john.doe@example.com', 'John Doe', 2, TRUE),
(1, 450.00, 3, FALSE, TRUE, 'Business', '2B', 'jane.smith@example.com', 'Jane Smith', 2, TRUE),
(2, 200.00, 2, TRUE, FALSE, 'Economy', '15D', 'bob.jones@example.com', 'Bob Jones', 3, FALSE),
(2, 480.00, 2, TRUE, TRUE, 'Business', '3A', 'alice.brown@example.com', 'Alice Brown', 2, TRUE),
(3, 160.00, 1, FALSE, FALSE, 'Economy', '10F', 'charlie.wilson@example.com', 'Charlie Wilson', 3, TRUE),
(1, 200.00, NULL, TRUE, FALSE, 'Economy', '14C', 'bob.jones@example.com', 'Bob Jones', 3, TRUE),
(1, 200.00, NULL, FALSE, TRUE, 'Economy', '16A', 'alice.brown@example.com', 'Alice Brown', 2, FALSE);

-- Create indexes for better performance
CREATE INDEX idx_booking_flight_id ON Booking(FlightID);
CREATE INDEX idx_booking_customer_email ON Booking(CustomerEmail);
CREATE INDEX idx_booking_seat ON Booking(FlightID, SeatNumber);
CREATE INDEX idx_flight_search ON flights(departure, arrival, departuretime);
CREATE INDEX idx_customer_email ON Customer(Email);

-- Create missing functions that BookingRepositoryImpl expects

-- Function to check if flight is full
CREATE OR REPLACE FUNCTION is_flight_full(flight_id INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN (
        SELECT COALESCE(COUNT(*), 0) >= f.totalSeats
        FROM Booking b
        RIGHT JOIN flights f ON f.id = flight_id
        WHERE b.FlightID = flight_id
        GROUP BY f.totalSeats
    );
END;
$$ LANGUAGE plpgsql;

-- Function to get available seats count
CREATE OR REPLACE FUNCTION get_available_seats_count(flight_id INTEGER)
RETURNS INTEGER AS $$
BEGIN
    RETURN (
        SELECT f.totalSeats - COALESCE(COUNT(b.ID), 0)
        FROM flights f
        LEFT JOIN Booking b ON b.FlightID = f.id
        WHERE f.id = flight_id
        GROUP BY f.totalSeats
    );
END;
$$ LANGUAGE plpgsql;

-- Function to get booked seats
CREATE OR REPLACE FUNCTION get_booked_seats(flight_id INTEGER)
RETURNS TABLE(seat VARCHAR(10)) AS $$
BEGIN
    RETURN QUERY
    SELECT b.SeatNumber
    FROM Booking b
    WHERE b.FlightID = flight_id
    ORDER BY b.SeatNumber;
END;
$$ LANGUAGE plpgsql;

-- Function to generate seat suggestions
CREATE OR REPLACE FUNCTION suggest_next_seat(flight_id INTEGER)
RETURNS VARCHAR(10) AS $$
DECLARE
    seat_number VARCHAR(10);
    row_num INTEGER;
    seat_letter CHAR(1);
BEGIN
    -- Generate seats from 1A to 10F (rows 1-10, seats A-F for 60 total seats)
    FOR row_num IN 1..10 LOOP
        FOR seat_letter IN SELECT unnest(ARRAY['A', 'B', 'C', 'D', 'E', 'F']) LOOP
            seat_number := row_num || seat_letter;
            
            -- Check if seat is available
            IF NOT EXISTS (
                SELECT 1 FROM Booking 
                WHERE FlightID = flight_id AND SeatNumber = seat_number
            ) THEN
                RETURN seat_number;
            END IF;
        END LOOP;
    END LOOP;
    
    -- If no seat found, return null
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Grant permissions (if needed)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO aisdemouser;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO aisdemouser;
-- GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO aisdemouser;

COMMIT;
