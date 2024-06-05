package ar.edu.itba.paw.model;

import javax.naming.directory.InvalidSearchFilterException;
import java.time.LocalDateTime;

public enum DateIntervalFilter {


    LASTWEEK(7,"d"),
    LASTTHIRTYDAYS(30,"m"),
    LASTYEAR(365,"y");

    private final int minusDays;
    private final String id;

    DateIntervalFilter(int minusDays, String id) {
        this.minusDays = minusDays;
        this.id = id;
    }

    public LocalDateTime getEndDate(LocalDateTime startDate) {
        return startDate.minusDays(minusDays);
    }

    public static DateIntervalFilter of(String id){
        return switch (id) {
            case "w" -> LASTWEEK;
            case "m" -> LASTTHIRTYDAYS;
            case "y" -> LASTYEAR;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return id;
    }


}
