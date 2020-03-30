package com.dilipkrish.weather.api;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
@Data
@EqualsAndHashCode
@NoArgsConstructor
@RequiredArgsConstructor
public class Measurement implements Serializable {
    @NonNull
    @Column(name = "station_id")
    private String stationId;

    @NonNull
    @Column(name = "measurement_date")
    private LocalDate measurementDate;

    @NonNull
    private String element;

    @NonNull
    @Column(name = "measurement_time")
    private LocalTime measurementTime;
}
