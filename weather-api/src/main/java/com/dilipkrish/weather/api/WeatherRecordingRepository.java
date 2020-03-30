package com.dilipkrish.weather.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WeatherRecordingRepository extends JpaRepository<WeatherRecording, Measurement> {
    @Query(
            value = "SELECT *" +
                    "FROM public.weather_recording ws " +
                    "WHERE ws.station_id = :stationId " +
                    "AND ws.element = :element",
            nativeQuery = true)
    List<WeatherRecording> findById_StationIdAndId_Element(@Param("stationId") String stationId,
                                                           @Param("element") String element);
}