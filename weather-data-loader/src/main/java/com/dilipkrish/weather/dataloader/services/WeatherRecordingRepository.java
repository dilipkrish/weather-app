package com.dilipkrish.weather.dataloader.services;


import com.dilipkrish.weather.dataloader.ghcn.WeatherRecording;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface WeatherRecordingRepository extends CrudRepository<WeatherRecording, UUID> {
}
