package com.dilipkrish.weather.api;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class WeatherStation {
    @Id
    @Column(name= "station_id")
    private String stationId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal elevation;
    private String state;
    private String name;
    @Column(name= "gsn_flag")
    private String gsnFlag;
    @Column(name= "hcn_crn_flag")
    private String hcnCrnFlag;
    @Column(name = "wmo_id")
    private String wmoId;
}
