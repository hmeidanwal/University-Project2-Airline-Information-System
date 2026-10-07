package io.github.fontysvenlo.ais.businesslogic;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.fontysvenlo.ais.datarecords.BookingData;
import io.github.fontysvenlo.ais.datarecords.FlightCapacityData;
import io.github.fontysvenlo.ais.exceptions.InvalidBookingDataException;
import io.github.fontysvenlo.ais.persistence.api.BookingRepository;
import io.github.fontysvenlo.ais.persistence.api.SeatNotAvailableException;

/**
 * Tests for BookingManagerImpl focusing on the createBooking use case.
 */
class BookingManagerImplTest {

    private BookingManagerImpl bookingManager;
    private BookingRepository mockRepository;

    // Valid test data
    private final BookingData validBooking = new BookingData(
            null, // id - will be set by repository
            101, // flightId
            new BigDecimal("299.99"), // price
            true, // luggage
            false, // food
            "Economy", // classType
            "5A", // seatNumber
            "john.doe@example.com", // customerEmail
            "John Doe", // customerName
            true // paid
    );

    @BeforeEach
    void setUp() {
        mockRepository = mock(BookingRepository.class);
        bookingManager = new BookingManagerImpl(mockRepository);
    }

    /**
     * Test Scenario: Valid booking creation
     * System successfully creates a booking with valid data
     */
    @Test
    void testCreateBookingValid() throws SeatNotAvailableException, InvalidBookingDataException {
        // Arrange
        BookingData expectedBooking = new BookingData(
                1, // assigned id
                validBooking.flightId(),
                validBooking.price(),
                validBooking.luggage(),
                validBooking.food(),
                validBooking.classType(),
                validBooking.seatNumber(),
                validBooking.customerEmail(),
                validBooking.customerName(),
                validBooking.paid()
        );
        when(mockRepository.createBooking(validBooking)).thenReturn(expectedBooking);

        // Act
        BookingData result = bookingManager.createBooking(validBooking);

        // Assert
        assertEquals(expectedBooking, result);
        verify(mockRepository).createBooking(validBooking);
    }

    /**
     * Test Scenario: Null booking data
     * System throws exception when booking data is null
     */
    @Test
    void testCreateBookingNullData() throws SeatNotAvailableException {
        // Act & Assert
        assertThrows(InvalidBookingDataException.class, () -> {
            bookingManager.createBooking(null);
        });
        
        verify(mockRepository, never()).createBooking(any());
    }

    /**
     * Test Scenario: Missing customer email
     * System throws exception when customer email is null or blank
     */
    @Test
    void testCreateBookingMissingEmail() throws SeatNotAvailableException {
        // Test null email
        BookingData bookingNullEmail = new BookingData(
                null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                "5A", null, "John Doe", true);

        assertThrows(InvalidBookingDataException.class, () -> {
            bookingManager.createBooking(bookingNullEmail);
        });
        
        verify(mockRepository, never()).createBooking(any());
    }

    /**
     * Test Scenario: Missing seat number
     * System throws exception when seat number is null or blank
     */
    @Test
    void testCreateBookingMissingSeatNumber() throws SeatNotAvailableException {
        // Test null seat number
        BookingData bookingNullSeat = new BookingData(
                null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                null, "john.doe@example.com", "John Doe", true);

        assertThrows(InvalidBookingDataException.class, () -> {
            bookingManager.createBooking(bookingNullSeat);
        });
        
        verify(mockRepository, never()).createBooking(any());
    }

    /**
     * Test Scenario: Invalid seat format
     * System throws exception when seat number format is invalid
     */
    @Test
    void testCreateBookingInvalidSeatFormat() throws SeatNotAvailableException {
        // Test invalid seat formats
        String[] invalidSeats = {"0A", "11A", "1G", "5Z", "AA", "55", "1a", "A1"};
        
        BookingData bookingInvalidSeat = new BookingData(
                null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                invalidSeats[0], "john.doe@example.com", "John Doe", true);

        assertThrows(InvalidBookingDataException.class, () -> {
            bookingManager.createBooking(bookingInvalidSeat);
        });
        
        verify(mockRepository, never()).createBooking(any());
    }

    /**
     * Test Scenario: Valid seat formats
     * System accepts valid seat formats (1A-10F)
     */
    @Test
    void testCreateBookingValidSeatFormats() throws SeatNotAvailableException, InvalidBookingDataException {
        // Test valid seat formats
        String[] validSeats = {"1A", "1F", "5C", "10A", "10F", "9B"};
        
        for (String validSeat : validSeats) {
            BookingData bookingValidSeat = new BookingData(
                    null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                    validSeat, "john.doe@example.com", "John Doe", true);

            BookingData expectedBooking = new BookingData(
                    1, 101, new BigDecimal("299.99"), true, false, "Economy", 
                    validSeat, "john.doe@example.com", "John Doe", true);

            when(mockRepository.createBooking(bookingValidSeat)).thenReturn(expectedBooking);

            // Should not throw exception
            BookingData result = bookingManager.createBooking(bookingValidSeat);
            assertEquals(expectedBooking, result);
        }
    }

    /**
     * Test Scenario: Invalid customer name format
     * System throws exception when customer name doesn't follow proper capitalization
     */
    @Test
    void testCreateBookingInvalidNameFormat() throws SeatNotAvailableException {
        // Test invalid name formats
        BookingData bookingInvalidName = new BookingData(
                null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                "5A", "john.doe@example.com", "john doe", true);

        assertThrows(InvalidBookingDataException.class, () -> {
            bookingManager.createBooking(bookingInvalidName);
        });
        
        verify(mockRepository, never()).createBooking(any());
    }

    /**
     * Test Scenario: Valid customer name formats
     * System accepts valid customer name formats
     */
    @Test
    void testCreateBookingValidNameFormats() throws SeatNotAvailableException, InvalidBookingDataException {
        // Test valid name formats
        String[] validNames = {"John Doe", "Jane Smith", "Mary Johnson Brown"};
        
        for (String validName : validNames) {
            BookingData bookingValidName = new BookingData(
                    null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                    "5A", "john.doe@example.com", validName, true);

            BookingData expectedBooking = new BookingData(
                    1, 101, new BigDecimal("299.99"), true, false, "Economy", 
                    "5A", "john.doe@example.com", validName, true);

            when(mockRepository.createBooking(bookingValidName)).thenReturn(expectedBooking);

            // Should not throw exception
            BookingData result = bookingManager.createBooking(bookingValidName);
            assertEquals(expectedBooking, result);
        }
    }

    /**
     * Test Scenario: Booking not paid
     * System throws exception when booking is not paid
     */
    @Test
    void testCreateBookingNotPaid() throws SeatNotAvailableException {
        BookingData unpaidBooking = new BookingData(
                null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                "5A", "john.doe@example.com", "John Doe", false);

        assertThrows(InvalidBookingDataException.class, () -> {
            bookingManager.createBooking(unpaidBooking);
        });
        
        verify(mockRepository, never()).createBooking(any());
    }

    /**
     * Test Scenario: Booking with null payment status
     * System accepts booking when payment status is null (assuming it's valid)
     */
    @Test
    void testCreateBookingNullPaymentStatus() throws SeatNotAvailableException, InvalidBookingDataException {
        BookingData bookingNullPaid = new BookingData(
                null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                "5A", "john.doe@example.com", "John Doe", null);

        BookingData expectedBooking = new BookingData(
                1, 101, new BigDecimal("299.99"), true, false, "Economy", 
                "5A", "john.doe@example.com", "John Doe", null);

        when(mockRepository.createBooking(bookingNullPaid)).thenReturn(expectedBooking);

        // Should not throw exception
        BookingData result = bookingManager.createBooking(bookingNullPaid);
        
        assertEquals(expectedBooking, result);
        verify(mockRepository).createBooking(bookingNullPaid);
    }

    /**
     * Test Scenario: Booking with null or blank customer name
     * System accepts booking when customer name is null or blank (optional field)
     */
    @Test
    void testCreateBookingNullOrBlankCustomerName() throws SeatNotAvailableException, InvalidBookingDataException {
        // Test null customer name
        BookingData bookingNullName = new BookingData(
                null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                "5A", "john.doe@example.com", null, true);

        BookingData expectedBookingNullName = new BookingData(
                1, 101, new BigDecimal("299.99"), true, false, "Economy", 
                "5A", "john.doe@example.com", null, true);

        when(mockRepository.createBooking(bookingNullName)).thenReturn(expectedBookingNullName);

        BookingData result = bookingManager.createBooking(bookingNullName);
        assertEquals(expectedBookingNullName, result);

        // Test blank customer name
        BookingData bookingBlankName = new BookingData(
                null, 101, new BigDecimal("299.99"), true, false, "Economy", 
                "5A", "john.doe@example.com", "   ", true);

        BookingData expectedBookingBlankName = new BookingData(
                1, 101, new BigDecimal("299.99"), true, false, "Economy", 
                "5A", "john.doe@example.com", "   ", true);

        when(mockRepository.createBooking(bookingBlankName)).thenReturn(expectedBookingBlankName);

        result = bookingManager.createBooking(bookingBlankName);
        assertEquals(expectedBookingBlankName, result);
        
        verify(mockRepository).createBooking(bookingNullName);
        verify(mockRepository).createBooking(bookingBlankName);
    }

    /**
     * Test Scenario: Repository throws exception
     * System propagates repository exceptions
     */
    @Test
    void testCreateBookingRepositoryException() throws SeatNotAvailableException, InvalidBookingDataException {
        when(mockRepository.createBooking(validBooking))
            .thenThrow(new SeatNotAvailableException("Seat is already booked"));

        try {
            bookingManager.createBooking(validBooking);
            fail("Expected SeatNotAvailableException to be thrown");
        } catch (SeatNotAvailableException e) {
            assertEquals("Seat is already booked", e.getMessage());
        }
        
        verify(mockRepository).createBooking(validBooking);
    }

    // ========== NEW TESTS FOR MISSING COVERAGE ==========

    /**
     * Test Scenario: Check if flight is full - true case
     * System correctly delegates to repository and returns true when flight is full
     */
    @Test
    void testIsFlightFullTrue() throws SeatNotAvailableException {
        int flightId = 101;
        when(mockRepository.isFlightFull(flightId)).thenReturn(true);

        boolean result = bookingManager.isFlightFull(flightId);

        assertTrue(result);
        verify(mockRepository).isFlightFull(flightId);
    }

    /**
     * Test Scenario: Check if flight is full - false case
     * System correctly delegates to repository and returns false when flight has available seats
     */
    @Test
    void testIsFlightFullFalse() throws SeatNotAvailableException {
        int flightId = 101;
        when(mockRepository.isFlightFull(flightId)).thenReturn(false);

        boolean result = bookingManager.isFlightFull(flightId);

        assertFalse(result);
        verify(mockRepository).isFlightFull(flightId);
    }

    /**
     * Test Scenario: Get available seats count
     * System correctly delegates to repository and returns available seat count
     */
    @Test
    void testGetAvailableSeatsCount() throws SeatNotAvailableException {
        int flightId = 101;
        int expectedCount = 45;
        when(mockRepository.getAvailableSeatsCount(flightId)).thenReturn(expectedCount);

        int result = bookingManager.getAvailableSeatsCount(flightId);

        assertEquals(expectedCount, result);
        verify(mockRepository).getAvailableSeatsCount(flightId);
    }

    /**
     * Test Scenario: Get booked seats list
     * System correctly delegates to repository and returns list of booked seats
     */
    @Test
    void testGetBookedSeats() throws SeatNotAvailableException {
        int flightId = 101;
        List<String> expectedSeats = Arrays.asList("1A", "2B", "3C", "5A", "10F");
        when(mockRepository.getBookedSeats(flightId)).thenReturn(expectedSeats);

        List<String> result = bookingManager.getBookedSeats(flightId);

        assertEquals(expectedSeats, result);
        assertEquals(5, result.size());
        assertTrue(result.contains("1A"));
        assertTrue(result.contains("10F"));
        verify(mockRepository).getBookedSeats(flightId);
    }

    /**
     * Test Scenario: Suggest next available seat
     * System correctly delegates to repository and returns suggested seat
     */
    @Test
    void testSuggestNextSeat() throws SeatNotAvailableException {
        int flightId = 101;
        String expectedSeat = "1B";
        when(mockRepository.suggestNextSeat(flightId)).thenReturn(expectedSeat);

        String result = bookingManager.suggestNextSeat(flightId);

        assertEquals(expectedSeat, result);
        verify(mockRepository).suggestNextSeat(flightId);
    }

    /**
     * Test Scenario: Get flight capacity information
     * System correctly delegates to repository and returns flight capacity data
     */
    @Test
    void testGetFlightCapacity() throws SeatNotAvailableException {
        int flightId = 101;
        FlightCapacityData expectedCapacity = new FlightCapacityData(
            flightId, 
            "Amsterdam", 
            "Paris", 
            "2024-06-20",
            60, 
            15, 
            45, 
            false,
            75.0, 
            Arrays.asList("1A", "2B", "3C")
        );
        when(mockRepository.getFlightCapacity(flightId)).thenReturn(expectedCapacity);

        FlightCapacityData result = bookingManager.getFlightCapacity(flightId);

        assertEquals(expectedCapacity, result);
        assertEquals(flightId, result.flightId());
        assertEquals("Amsterdam", result.departure());
        assertEquals("Paris", result.arrival());
        assertEquals("2024-06-20", result.date());
        assertEquals(60, result.totalSeats());
        assertEquals(15, result.bookedSeats());
        assertEquals(45, result.availableSeats());
        assertEquals(false, result.isFull());
        assertEquals(75.0, result.occupancyPercentage(), 0.01);
        assertEquals(3, result.bookedSeatNumbers().size());
        verify(mockRepository).getFlightCapacity(flightId);
    }

    /**
     * Test Scenario: Repository methods throw SeatNotAvailableException
     * System correctly propagates exceptions from repository methods
     */
    @Test
    void testRepositoryMethodsThrowException() throws SeatNotAvailableException {
        int flightId = 101;
        String errorMessage = "Flight not found";

        // Test isFlightFull exception
        when(mockRepository.isFlightFull(flightId))
            .thenThrow(new SeatNotAvailableException(errorMessage));
        
        assertThrows(SeatNotAvailableException.class, () -> {
            bookingManager.isFlightFull(flightId);
        });

        // Test getAvailableSeatsCount exception
        when(mockRepository.getAvailableSeatsCount(flightId))
            .thenThrow(new SeatNotAvailableException(errorMessage));
        
        assertThrows(SeatNotAvailableException.class, () -> {
            bookingManager.getAvailableSeatsCount(flightId);
        });

        // Test getBookedSeats exception
        when(mockRepository.getBookedSeats(flightId))
            .thenThrow(new SeatNotAvailableException(errorMessage));
        
        assertThrows(SeatNotAvailableException.class, () -> {
            bookingManager.getBookedSeats(flightId);
        });

        // Test suggestNextSeat exception
        when(mockRepository.suggestNextSeat(flightId))
            .thenThrow(new SeatNotAvailableException(errorMessage));
        
        assertThrows(SeatNotAvailableException.class, () -> {
            bookingManager.suggestNextSeat(flightId);
        });

        // Test getFlightCapacity exception
        when(mockRepository.getFlightCapacity(flightId))
            .thenThrow(new SeatNotAvailableException(errorMessage));
        
        assertThrows(SeatNotAvailableException.class, () -> {
            bookingManager.getFlightCapacity(flightId);
        });
    }
}