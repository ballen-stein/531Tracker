package com.a531tracker;

import java.math.BigDecimal;

class CalculateWeight {

    public String setAsPounds(int trainingValue, float liftPercent){
        return String.valueOf((int) ( 5 * ( Math.ceil( ( trainingValue * liftPercent ) / 5 ) ) ) );
    }

    public String setAsKilograms(int trainingValue, float liftPercent){
        return String.valueOf(new BigDecimal((trainingValue * liftPercent) / 2.20462).setScale(1, BigDecimal.ROUND_HALF_UP));
    }
}
