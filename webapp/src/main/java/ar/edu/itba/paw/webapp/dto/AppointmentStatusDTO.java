package ar.edu.itba.paw.webapp.dto;

import java.util.Objects;
import java.util.Optional;

public class AppointmentStatusDTO {
    private Boolean confirmed;
    private Boolean canceled;

    private AppointmentStatusDTO(){}

    @Override
    public int hashCode() {
        return Objects.hash(confirmed,canceled);
    }

    public Boolean isCanceled() {
        return canceled!=null && canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Boolean isConfirmed() {
        return confirmed!=null && confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean hasValues() {
        return confirmed!=null || canceled!=null;
    }
}
