package com.dilipkrish.weather.dataloader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(exclude = {WebFluxAutoConfiguration.class,
        WebMvcAutoConfiguration.class})
public class WeatherDataLoaderApp {

    public static void main(String[] args) {
        SpringApplication.run(WeatherDataLoaderApp.class, args);
    }

}
