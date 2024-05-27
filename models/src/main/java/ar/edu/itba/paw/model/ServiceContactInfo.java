package ar.edu.itba.paw.model;

import java.util.Objects;


public class ServiceContactInfo {

    private final long serviceId;
    //@JoinColumn(name = "servicename", nullable = false)
    private final String serviceName;

    //@JoinColumn(name = "businessEmail")
    private final String businessEmail;

    //@JoinColumn(name = "businessTelephone")
    private final String businessTelephone;

    public ServiceContactInfo(long serviceId, String serviceName, String businessEmail, String businessTelephone ) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.businessEmail = businessEmail;
        this.businessTelephone = businessTelephone;
    }


    public long getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public String getBusinessTelephone() {
        return businessTelephone;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ServiceContactInfo s &&
                serviceId == s.serviceId);
    }

}
