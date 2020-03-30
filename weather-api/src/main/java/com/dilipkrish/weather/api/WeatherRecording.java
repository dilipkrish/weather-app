package com.dilipkrish.weather.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
public class WeatherRecording implements Serializable {
    @EmbeddedId
    private Measurement id;

    @Column(name = "measured_value")
    private BigDecimal measuredValue;
    @Column(name = "measurement_flag")
    private String measurementFLag;
    @Column(name = "quality_flag")
    private String qualityFlag;
    @Column(name = "source_flag")
    private String sourceFlag;
}
