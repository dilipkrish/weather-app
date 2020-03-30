package com.dilipkrish.weather.dataloader.ghcn;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class WeatherRecording {
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
