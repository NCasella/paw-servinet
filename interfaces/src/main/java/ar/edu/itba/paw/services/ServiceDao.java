package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ServiceDao {

    List<Service> getAllServices();
    Optional<Service> findById(long id);
    Optional<BasicService> findBasicServiceById(long id);

    Service create(Business business, String name, String description, boolean homeservice, String location, Neighbourhoods[] neighbourhoods, Categories category, int minimalduration, PricingTypes pricing, String price, boolean additionalCharges, Long imageId);
    Service editServiceName(long serviceid, String newvalue);
    void editService(long serviceId, String newDescription, int newDuration, PricingTypes newPricingType, String newPrice, boolean newAdditionalCharges);

    List<ServiceContactInfo> getServicesContactInfo(Collection<Long> serviceids);
    List<BasicService> getAllBusinessBasicServices(long businessId);
    List<Service> getAllBusinessServices(long businessId);

    void delete(long serviceid);
    List<Service> getServicesFilteredBy(int page, String category, String[] neighbourhoods, int rating, String query);
    int getServiceCount(String category, String[] neighbourhoods, int rating, String query);
    List<Service> getRecommendedServices();

    List<String> getAvailableNeighbourhoods();
    List<String> getAvailableNeighbourhoodsByCategory(String category);

}
