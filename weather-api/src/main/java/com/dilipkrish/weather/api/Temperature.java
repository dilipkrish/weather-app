package com.dilipkrish.weather.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Temperature {
    @NonNull
    private String stationId;
    @NonNull
    private LocalDate date;
    @NonNull
    private BigDecimal high;
    @NonNull
    private BigDecimal low;
}
