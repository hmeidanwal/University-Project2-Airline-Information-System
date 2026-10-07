# Airline Information System - Setup Guide

## Overview

This is a complete airline information system with:
- **Customer Management**: Create, view, delete customers
- **Flight Search**: Search for flights with transfer options
- **Booking System**: Create bookings with seat selection and pricing

## Architecture

- **Backend**: Java with layered architecture (RestAPI → Business Logic → Persistence)
- **Frontend**: SvelteKit
- **Database**: PostgreSQL

## Quick Start

### 1. Start the Database

```bash
# Start PostgreSQL with Docker
docker-compose up -d

# Initialize the database
docker exec -i ais-postgres psql -U aisdemouser -d aisdb < init.sql
```

### 2. Start the Backend

```bash
# Navigate to assembler directory
cd implementation/backend/assembler

# Start the backend server
mvn exec:java
```

The backend will start on `http://localhost:8080`

### 3. Start the Frontend

```bash
# Navigate to frontend directory
cd implementation/frontend

# Install dependencies (first time only)
npm install

# Start the development server
npm run dev
```

The frontend will start on `http://localhost:5173`

## Database Structure

The system uses the following main tables:

### Customer
- Email (PK)
- FirstName, LastName
- BirthDate, Phone

### flights
- id (PK)
- departure, arrival
- departuretime, arrivaltime
- totalSeats

### Booking
- ID (PK)
- FlightID → flights(id)
- CustomerEmail → Customer(Email)
- SeatNumber, Price, ClassType
- Luggage, Food, Paid
- CustomerName, EmployeeID

### Supporting Tables
- Classes (seat classes per flight)
- Discounts (discount types)
- Passenger (passenger details)
- Employee (system users)

### Views
- `Flight` - Compatibility view for booking system
- `FlightAvailability` - Real-time seat availability

## Functionality Analysis

### ✅ Working Features

1. **Customer Management**
   - ✅ Create customers with validation
   - ✅ View customer list
   - ✅ Delete customers
   - ✅ Email uniqueness validation
   - ✅ Frontend form with error handling

2. **Flight Search**
   - ✅ Search by departure, arrival, date
   - ✅ Advanced transfer flight routing
   - ✅ Complex recursive SQL queries
   - ✅ Multiple connection possibilities

3. **Booking System**
   - ✅ Create bookings with seat selection
   - ✅ Real-time seat availability checking
   - ✅ Price calculation
   - ✅ Seat suggestion algorithm
   - ✅ Flight capacity management

### 🔧 Areas for Enhancement

1. **Authentication System**
   - Employee login not fully implemented
   - Role-based access control needed

2. **Payment Processing**
   - Payment marked as boolean only
   - No actual payment gateway integration

3. **Advanced Search**
   - Date range searches
   - Price filtering
   - Class-specific searches

## API Endpoints

### Customer Management
- `GET /api/v1/customers` - List all customers
- `POST /api/v1/customers` - Create customer
- `DELETE /api/v1/customers/{email}` - Delete customer

### Flight Search
- `GET /api/v1/flights/search?departure=X&arrival=Y&date=Z`

### Booking Management
- `POST /api/v1/bookings` - Create booking
- `GET /api/v1/flights/{id}/capacity` - Get flight capacity
- `GET /api/v1/flights/{id}/booked-seats` - Get booked seats
- `GET /api/v1/flights/{id}/suggest-seat` - Get seat suggestions

## Sample Data

The database includes:
- 5 sample customers
- 15 sample flights with transfers
- 4 sample employees
- Sample bookings and seat assignments
- Various discounts and class types

## Testing the System

1. **Customer Management**
   - Navigate to `/customers`
   - Add new customer
   - View customer list
   - Delete customers

2. **Flight Search**
   - Navigate to `/search`
   - Search: Amsterdam → London, 2025-06-01
   - View search results

3. **Booking**
   - From search results, click "Book Flight"
   - Fill in passenger details
   - Select seats, meals, baggage
   - Complete booking

## Configuration Files

- `implementation/backend/assembler/src/main/resources/db.properties`
- `implementation/backend/assembler/src/main/resources/application.properties`

## Troubleshooting

### Database Issues
```bash
# Reset database
docker-compose down
docker-compose up -d
docker exec -i ais-postgres psql -U aisdemouser -d aisdb < init.sql
```

### Backend Issues
```bash
# Clean and rebuild
cd implementation/backend
mvn clean compile
cd assembler
mvn exec:java
```

### Frontend Issues
```bash
# Clear cache and reinstall
cd implementation/frontend
rm -rf node_modules package-lock.json
npm install
npm run dev
```

## System Requirements

- Java 17+
- Node.js 18+
- Docker & Docker Compose
- Maven 3.6+

## Database Connection

- Host: localhost:5432
- Database: aisdb
- Username: aisdemouser
- Password: ais

## Conclusion

The system successfully implements the core airline booking functionality with:
- ✅ Customer management with full CRUD operations
- ✅ Advanced flight search with transfer routing
- ✅ Complete booking system with seat management
- ✅ Real-time availability checking
- ✅ Professional UI with error handling

All three main use cases (Customer Management, Flight Search, Booking) are working and can be tested through the web interface. 