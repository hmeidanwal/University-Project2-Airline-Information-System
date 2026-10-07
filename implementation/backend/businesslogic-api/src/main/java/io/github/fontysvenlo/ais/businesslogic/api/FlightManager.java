package io.github.fontysvenlo.ais.businesslogic.api;

import java.util.List;

import io.github.fontysvenlo.ais.datarecords.FlightData;

public interface FlightManager {

    public List<List<FlightData>> search(String departure, String arrival, String date);

}
