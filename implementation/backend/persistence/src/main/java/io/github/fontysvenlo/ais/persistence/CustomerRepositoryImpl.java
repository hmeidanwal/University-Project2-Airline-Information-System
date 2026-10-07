package io.github.fontysvenlo.ais.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import io.github.fontysvenlo.ais.datarecords.CustomerData;
import io.github.fontysvenlo.ais.persistence.api.CustomerRepository;
import io.github.fontysvenlo.ais.exceptions.DeletionFailedException;
import io.github.fontysvenlo.ais.exceptions.DuplicateEmailException;
import io.github.fontysvenlo.ais.exceptions.PersistenceException;
import io.github.fontysvenlo.ais.exceptions.RetrievalFailedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Statement;

/**
 * This class knows everything about storing and retrieving customers from the database.
 * At the moment only returns dummy object with an id that is set.
 * Normally it will connect to a database and do all the handling.
 */
class CustomerRepositoryImpl implements CustomerRepository {
    
    private final DataSource db;
    //private final List<CustomerData> customers = new ArrayList<>(Arrays.asList(new CustomerData(1, "John", "Doe", LocalDate.of(2025, 1, 1)))); 
    private final List<CustomerData> customers = new ArrayList<>(); 
    
    public CustomerRepositoryImpl(DBConfig config) {
        this.db = DBProvider.getDataSource(config);
    }

    /**
     * @see CustomerRepository#add(CustomerData)
     */
      @Override
    public CustomerData add(CustomerData customerData) throws DuplicateEmailException, PersistenceException {

        // // SQL query to insert a new customer into the database.
        String sql = "INSERT INTO Customer (Email, FirstName, LastName, BirthDate, Phone) VALUES (?, ?, ?, ?, ?)";
        
        // // Opens a database connection and prepares the SQL query
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) { // No generated keys needed
            
                // Binds values to the SQL ? placeholders using data from the CustomerData object
            stmt.setString(1, customerData.email());    // PK first
            stmt.setString(2, customerData.firstName());
            stmt.setString(3, customerData.lastName());
            stmt.setDate(4, java.sql.Date.valueOf(customerData.dateOfBirth()));
            stmt.setString(5, customerData.phone());
            
            stmt.executeUpdate();
            
            // Just return the original data (no ID needed)
            return customerData; 
            
        } catch (SQLException e) {
            
        if ("23505".equals(e.getSQLState())) { // PostgreSQL duplicate key violation
            throw new DuplicateEmailException(customerData.email());
        }
        throw new PersistenceException("Failed to add customer", e); // wrap other DB errors
        }
    }

    /**
     * @see CustomerRepository#getAll()
     */
    @Override
public List<CustomerData> getAll() {
    // TODO: Implement the actual database storage. This is the actual implementation!
    
    // SQL query to select all customers from the database in a list. 
    String sql = "SELECT Email, FirstName, LastName, BirthDate, Phone FROM Customer";
    List<CustomerData> result = new ArrayList<>();
    
    // Runs the query and stores results in a ResultSet
    try (Connection conn = db.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
            // For each row in the result, a new CustomerData object is created and added to the list.
        while (rs.next()) {
            result.add(new CustomerData(
                rs.getString("FirstName"),
                rs.getString("LastName"),
                rs.getDate("BirthDate").toLocalDate(),
                rs.getString("Email"),
                rs.getString("Phone")
            ));
        }
    } catch (SQLException e) {
        throw new RetrievalFailedException("Customer data cannot be retrieved at the moment due to a temporary server issue. Please try again later!", e);
    }
    
    return result; 
}

@Override
public boolean delete(String email) {
    String sql = "DELETE FROM Customer WHERE Email = ?";
    
    try (Connection conn = db.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, email);
        int rowsAffected = stmt.executeUpdate();
        return rowsAffected > 0;
        
    } catch (SQLException e) {
        
        throw new DeletionFailedException("Failed to delete customer with Email address: " + email, e);
    }
}

}