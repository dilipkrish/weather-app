package com.dilipkrish.weather.api;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

@CrossOrigin(value = { "*" },
        maxAge = 900
)
@RestController
@RequestMapping("/api/weather/stations")
public class WeatherStationsController {

    public static final String MAPQUEST_API_URL_FORMAT = "https://www.mapquestapi.com";
    private final String apiKey;
    private final int radius;
    private final WeatherStationRepository repository;
    private final WebClient webClient;

    @Autowired
    public WeatherStationsController(
            @Value("${weather.mapquest.api.key}") String apiKey,
            @Value("${weather.location-radius}") int radius,
            WeatherStationRepository repository) {
        this.apiKey = apiKey;
        this.radius = radius;
        this.repository = repository;
        this.webClient = WebClient.builder()
                .baseUrl(MAPQUEST_API_URL_FORMAT)
                .build();
    }

    @GetMapping("/{state}/{city}")
    public Flux<WeatherStation> weatherStations(@PathVariable String state, @PathVariable String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/geocoding/v1/address")
                        .queryParam("key", apiKey)
                        .queryParam("inFormat", "kvp")
                        .queryParam("outFormat", "json")
                        .queryParam("location", String.format("%s, %s", city, state))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(response -> {
                    DocumentContext parsed = JsonPath.parse(response);
                    BigDecimal lat = BigDecimal.valueOf(
                            (double) parsed.read("$['results'][0]['locations'][0]['latLng']['lat']"));
                    BigDecimal lng = BigDecimal.valueOf(
                            (double) parsed.read("$['results'][0]['locations'][0]['latLng']['lng']"));
                    return Flux.fromStream(
                            repository.findStations(
                                    radius,
                                    state,
                                    lng,
                                    lat).stream());
                });

    }
}
