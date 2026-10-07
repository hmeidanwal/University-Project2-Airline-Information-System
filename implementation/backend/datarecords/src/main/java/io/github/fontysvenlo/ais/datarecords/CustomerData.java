package io.github.fontysvenlo.ais.datarecords;

import java.time.LocalDate;

/**
 * Data carrier for CustomerData.
 * A record is not mutable. Getter methods (e.g. firstName(), not getFirstName()),
 * hashCode(), equals() end toString available for free.

 * @param firstName the first name of the customer
 * @param lastName the last name of the customer
 * @param dateOfBirth the date of birth of the customer
 * @param email the email address of the customer (primary key)
 * @param phone the phone number of the customer
 */
public record CustomerData(String firstName, String lastName, LocalDate dateOfBirth, String email, String phone) { }


