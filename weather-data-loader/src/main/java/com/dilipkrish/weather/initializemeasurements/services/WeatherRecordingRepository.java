package com.dilipkrish.weather.initializemeasurements.services;


import com.dilipkrish.weather.initializemeasurements.ghcn.WeatherRecording;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface WeatherRecordingRepository extends CrudRepository<WeatherRecording, UUID> {
}
