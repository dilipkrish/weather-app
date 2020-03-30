package com.dilipkrish.weather.api;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;

public interface WeatherStationRepository extends CrudRepository<WeatherStation, String> {
    @Query(
            value = "SELECT *" +
                    "FROM public.weather_station ws " +
                    "WHERE ST_Distance_Sphere(" +
                    "   ST_SetSRID( ST_Point(ws.longitude, ws.latitude), 4326), " +
                    "   ST_MakePoint(:longitude, :latitude)) <= :distance * 1609.34 " +
                    "AND ws.state = :state",
            nativeQuery = true)
    Collection<WeatherStation> findStations(
            @Param("distance") int distance,
            @Param("state") String state,
            @Param("longitude") BigDecimal longitude,
            @Param("latitude") BigDecimal latitude);
}
