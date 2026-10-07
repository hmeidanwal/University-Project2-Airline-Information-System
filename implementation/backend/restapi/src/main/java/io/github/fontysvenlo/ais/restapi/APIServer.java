package io.github.fontysvenlo.ais.restapi;

import java.util.Map;

import io.github.fontysvenlo.ais.businesslogic.api.BusinessLogic;
import io.github.fontysvenlo.ais.exceptions.DateOfBirthException;
import io.github.fontysvenlo.ais.exceptions.DeletionFailedException;
import io.github.fontysvenlo.ais.exceptions.DuplicateEmailException;
import io.github.fontysvenlo.ais.exceptions.EmailException;
import io.github.fontysvenlo.ais.exceptions.FirstNameException;
import io.github.fontysvenlo.ais.exceptions.FutureDateOfBirthException;
import io.github.fontysvenlo.ais.exceptions.LastNameException;
import io.github.fontysvenlo.ais.exceptions.PersistenceException;
import io.github.fontysvenlo.ais.exceptions.PhoneException;
import io.github.fontysvenlo.ais.exceptions.RetrievalFailedException;
import io.github.fontysvenlo.ais.exceptions.TooOldBirthDateException;
import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

/**
 * This class is responsible for starting the REST server and defining the
 * routes.
 */
public class APIServer {

    private final BusinessLogic businessLogic;

    /**
     * Initializes the REST API server
     *
     * @param businessLogic the business logic implementation to communicate
     *                      with
     */
    public APIServer(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    /**
     * Starts the REST API server
     * 
     * @param configuration the configuration of the server
     */
    public void start(ServerConfig configuration) {
        var app = Javalin.create(config -> {
            config.router.contextPath = "/api/v1";
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.allowHost("http://localhost:" + configuration.cors(), "127.0.0.1:" + configuration.cors());
                });
            });
            config.router.apiBuilder(() -> {
                // Customer endpoints
                crud("customers/{customer-email}", new CustomerResource(businessLogic.getCustomerManager()));
                
                // Flight search endpoints
                get("flights/search", ctx -> new FlightResource(businessLogic.getFlightManager()).search(ctx)); 
                
                // Booking endpoints
                post("bookings", ctx -> new BookingResource(businessLogic.getBookingManager()).create(ctx));
                
                // Flight capacity endpoints
                get("flights/{flightId}/capacity", ctx -> new BookingResource(businessLogic.getBookingManager()).getFlightCapacity(ctx));
                get("flights/{flightId}/is-full", ctx -> new BookingResource(businessLogic.getBookingManager()).isFlightFull(ctx));
                get("flights/{flightId}/available-seats", ctx -> new BookingResource(businessLogic.getBookingManager()).getAvailableSeatsCount(ctx));
                get("flights/{flightId}/booked-seats", ctx -> new BookingResource(businessLogic.getBookingManager()).getBookedSeats(ctx));
                get("flights/{flightId}/suggest-seat", ctx -> new BookingResource(businessLogic.getBookingManager()).suggestNextSeat(ctx));
            });
        });

    
        // Exception mapping for the data records.
       // Individual Exception Handlers 
       app.exception(FirstNameException.class, (e, ctx) -> {
        ctx.status(422).json(Map.of( // 422 Unprocessable Entity: cannot process the request due to validation errors
            "error", "First name validation failed",
            "message", e.getMessage()
        ));
    });

    app.exception(LastNameException.class, (e, ctx) -> {
        ctx.status(422).json(Map.of(
            "error", "Last name validation failed",
            "message", e.getMessage()
        ));
    });

    app.exception(FutureDateOfBirthException.class, (e, ctx) -> {
            ctx.status(422).json(Map.of(
                "error", "Invalid date of birth",
                "message", e.getMessage()
            ));
        });

        app.exception(TooOldBirthDateException.class, (e, ctx) -> {
            ctx.status(422).json(Map.of(
                "error", "Invalid date of birth",
                "message", e.getMessage(),
                "minYear", 1900  
            ));
        });    

    app.exception(DateOfBirthException.class, (e, ctx) -> {
        ctx.status(422).json(Map.of(
            "error", "Date of birth validation failed",
            "message", e.getMessage()
        ));
    });

    app.exception(EmailException.class, (e, ctx) -> {
        ctx.status(422).json(Map.of(
            "error", "Email validation failed",
            "message", e.getMessage()
        ));
    });

    app.exception(PhoneException.class, (e, ctx) -> {
        ctx.status(422).json(Map.of(
            "error", "Phone number validation failed",
            "message", e.getMessage()
        ));
    });

    app.exception(DuplicateEmailException.class, (e, ctx) -> {
    ctx.status(409).json(Map.of( // 409 Conflict: email already exists
            "error","Email already exists", 
            "message", "Email: " + e.getMessage() + " already exists")); // 409 Conflict
});

app.exception(PersistenceException.class, (e, ctx) -> {
    ctx.status(500).json(Map.of( // 500 Internal Server Error: general database error
            "error", "Database error",
            "message", e.getMessage())); // Internal Server Error
});

app.exception(DeletionFailedException.class, (e, ctx) -> {
    ctx.status(500).json(Map.of(
        "error", "Deletion failed",
        "message", e.getMessage()
    ));
});

app.exception(RetrievalFailedException.class, (e, ctx) -> {
    ctx.status(500).json(Map.of(
        "error", "Failed to retrieve customer data",
        "message", e.getMessage()
    ));
});


    // Fallback for other illegal arguments
    app.exception(IllegalArgumentException.class, (e, ctx) -> {
        ctx.status(400).json(Map.of( // 400 Bad Request: invalid request
            "error", "Invalid request",
            "message", e.getMessage()
        ));
    });

        //Exceptions mapping from the data records. 
        



        app.start(configuration.port());
    }
}



