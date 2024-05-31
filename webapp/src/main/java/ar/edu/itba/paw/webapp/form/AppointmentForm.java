package ar.edu.itba.paw.webapp.form;


import ar.edu.itba.paw.webapp.validation.FutureDate;


import javax.validation.constraints.*;
import java.time.LocalDateTime;


public class AppointmentForm {
    private String neighbourhood;
    @Size(max = 255)
    private String description;

    @NotEmpty
    @Size(max = 255)
    private String location;

    @FutureDate
    private String date;

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date==null?null:date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.parse(date);
    }


}