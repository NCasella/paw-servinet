package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.AppointmentStatus;
import ar.edu.itba.paw.model.exceptions.InvalidAppointmentStatusException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Optional;

public class AppointmentStatusDTO {

    private AppointmentStatus status;

    public AppointmentStatusDTO() {
        // Jackson necesita este constructor vacío
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = AppointmentStatus.toEnum(status);
    }

    @JsonProperty("status")
    public String getStatus() {
        return Optional.ofNullable(status)
                .orElseThrow(InvalidAppointmentStatusException::new)
                .getValue();
    }

    public AppointmentStatus getStatusEnum() {
        return status;
    }

    public boolean isConfirmed() {
        return status == AppointmentStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return status == AppointmentStatus.CANCELLED;
    }

    public boolean isPending() {
        return status == AppointmentStatus.PENDING;
    }
    public boolean hasValidStatus() {
        return status != null;
    }

    public boolean hasFinished() { return status == AppointmentStatus.FINISHED; }
}
