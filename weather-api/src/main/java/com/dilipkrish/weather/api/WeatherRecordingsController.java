package com.dilipkrish.weather.api;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@CrossOrigin(value = { "*" },
        maxAge = 900
)
@RestController
@RequestMapping("/api/weather")
public class WeatherRecordingsController {

    private final WeatherRecordingRepository repository;
    private final TenthsOfCentigradeToFahrenheitConverter fahrenheitConverter;

    public WeatherRecordingsController(
            WeatherRecordingRepository repository,
            TenthsOfCentigradeToFahrenheitConverter fahrenheitConverter) {
        this.repository = repository;
        this.fahrenheitConverter = fahrenheitConverter;
    }

    @GetMapping("/temperatures/{stationId}")
    public Flux<Temperature> temperatures(@PathVariable String stationId) {
        List<WeatherRecording> readings = new ArrayList<>(repository.findById_StationIdAndId_Element(stationId, "TMAX"));
        readings.addAll(repository.findById_StationIdAndId_Element(stationId, "TMIN"));

        // create the map
        Map<LocalDate, List<WeatherRecording>> multimap = new HashMap<>();
        readings.forEach(each -> {
            List<WeatherRecording> readingsOnDay =
                    multimap.computeIfAbsent(each.getId().getMeasurementDate(), k -> new ArrayList<>());
            readingsOnDay.add(each);
        });
        return Flux.fromStream(multimap.entrySet().stream())
                .map(entry -> new Temperature(
                        entry.getValue().stream()
                                .findFirst()
                                .map(r -> r.getId().getStationId())
                                .orElse(""),
                        entry.getKey(),
                        extractElementValue(entry.getValue(), "TMAX"),
                        extractElementValue(entry.getValue(),"TMIN"),
                        extractElementValue(entry.getValue(),"TAVG")));
    }

    private BigDecimal extractElementValue(List<WeatherRecording> readings, String elementName) {
        return readings.stream()
                .filter(r -> Objects.equals(r.getId().getElement(), elementName))
                .findFirst()
                .map(WeatherRecording::getMeasuredValue)
                .map(fahrenheitConverter)
                .orElse(BigDecimal.ZERO);
    }

//    private Example<WeatherRecording> example(String stationId, String element) {
//        WeatherRecording probe = new WeatherRecording();
//        Measurement id = new Measurement();
//        id.setElement(element);
//        id.setStationId(stationId);
//        probe.setId(id);
//        return Example.of(probe);
//    }
}
