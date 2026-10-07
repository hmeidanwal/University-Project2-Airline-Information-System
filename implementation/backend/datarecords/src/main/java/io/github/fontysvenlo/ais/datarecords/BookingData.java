package io.github.fontysvenlo.ais.datarecords;

import java.math.BigDecimal;

public record BookingData(
    Integer id,
    Integer flightId,
    BigDecimal price,
    Boolean luggage,
    Boolean food,
    String classType,
    String seatNumber,
    String customerEmail,
    String customerName,
    Boolean paid
    // Optionals:
    // Integer discountId,
    // Integer employeeId
) {} 