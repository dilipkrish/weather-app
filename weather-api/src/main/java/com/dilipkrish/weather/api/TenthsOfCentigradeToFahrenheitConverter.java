package com.dilipkrish.weather.api;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

@Component
public class TenthsOfCentigradeToFahrenheitConverter implements Function<BigDecimal, BigDecimal> {
    @Override
    public BigDecimal apply(BigDecimal valueInTenthsOfCentigrade) {
        return valueInTenthsOfCentigrade
                .divide(BigDecimal.valueOf(10), RoundingMode.HALF_EVEN) //Since its in 10ths of Centigrade
                .multiply(BigDecimal.valueOf(9))
                .divide(BigDecimal.valueOf(5), RoundingMode.HALF_EVEN)
                .add(BigDecimal.valueOf(32));
    }
}
