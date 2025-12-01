package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.AppointmentStatus;
import ar.edu.itba.paw.model.exceptions.InvalidAppointmentStatusException;

import java.util.Optional;

public class AppointmentStatusDTO {

    private AppointmentStatus status;

    public AppointmentStatusDTO() {
    }

    public void setStatus(String status) {
        this.status = AppointmentStatus.toEnum(status);
    }

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
