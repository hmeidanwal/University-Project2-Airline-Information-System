package io.github.fontysvenlo.ais.persistence.api;

import java.util.List;

import io.github.fontysvenlo.ais.datarecords.FlightData;

public interface FlightRepository extends Helpers {

    List<List<FlightData>> search(String departure, String arrival, String datetime);
}
