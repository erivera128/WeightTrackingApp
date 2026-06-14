package com.zybooks.weighttrackingemmanuelrivera;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeightMathUtils {

    public static List<Float> calculate7DayMovingAverage(List<WeightTrackerDB.WeightEntry> entries) {
        List<Float> movingAverages = new ArrayList<>();
        if (entries == null || entries.isEmpty()) {
            return movingAverages;
        }

        List<WeightTrackerDB.WeightEntry> chronologicalEntries = new ArrayList<>(entries);
        Collections.reverse(chronologicalEntries);

        for (int i = 0; i < chronologicalEntries.size(); i++) {
            BigDecimal sum = BigDecimal.ZERO;
            int count = 0;

            for (int j = i; j >= 0 && count < 7; j--) {
                sum = sum.add(new BigDecimal(Float.toString(chronologicalEntries.get(j).getValue())));
                count++;
            }

            if (count > 0) {
                BigDecimal average = sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
                movingAverages.add(average.floatValue());
            } else {
                movingAverages.add(0.0f);
            }
        }

        Collections.reverse(movingAverages);
        return movingAverages;
    }
}