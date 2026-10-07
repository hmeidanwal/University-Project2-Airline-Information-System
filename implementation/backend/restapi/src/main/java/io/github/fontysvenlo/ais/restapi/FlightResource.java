package io.github.fontysvenlo.ais.restapi;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fontysvenlo.ais.businesslogic.api.FlightManager;
import io.github.fontysvenlo.ais.datarecords.FlightData;
import io.javalin.http.Context;

public class FlightResource {
    private static final Logger logger = LoggerFactory.getLogger(FlightResource.class);
    private final FlightManager flightManager;

    public FlightResource(FlightManager flightManager) {
        if (flightManager == null) {
            throw new IllegalArgumentException("FlightManager cannot be null");
        }
        this.flightManager = flightManager;
    }

    public void search(Context ctx) {
        
            String departure = ctx.queryParam("departure");
            String arrival = ctx.queryParam("arrival");
            String datetime = ctx.queryParam("datetime");

            logger.info("Received search request with parameters: departure={}, arrival={}, date={}", departure, arrival, datetime);

            if (departure == null || arrival == null || datetime == null) {
                String error = "Missing required parameters: departure, arrival, date";
                logger.error(error);
                ctx.status(400).json(Map.of("error", error));
                return;
            }

            List<List<FlightData>> flights = flightManager.search(departure, arrival, datetime);
            logger.info("Found {} flights matching the criteria", flights.size());
            
            if (flights.isEmpty()) {
                ctx.json(List.of()); // Return empty array instead of null
            } else {
                ctx.json(flights);
            }
        
    }
} 