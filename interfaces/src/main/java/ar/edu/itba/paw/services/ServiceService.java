package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServiceService {

    List<Service> getAllServices();
    Service editServiceName(long serviceId, String name);
    Optional<Service> findById(long id);
    Optional<BasicService> findBasicServiceById(long id);
    Service create(long businessId, String name, String description, boolean homeservice, Neighbourhoods[] neighbourhood, Neighbourhoods[] uniqueNeighbourhood, String location, Categories category, int minimalduration, PricingTypes pricing, String price, boolean additionalCharges, MultipartFile image);
    void editService(long serviceId, String newDescription, int newDuration, PricingTypes newPricingType, String newPrice, boolean newAdditionalCharges);
    void delete(Service service, Business business, boolean sendEmailToBusiness);
    void delete(long serviceId);
    Map<Long,ServiceContactInfo> getServicesContactInfo(Collection<Long> serviceids);
    List<Service> services(int page,String category,String[] location, String rating, String query);
    int getServiceCount(String category, String[] location, String rating, String searchQuery);
    int getPageCount(String category, String[] location, String rating, String searchQuery);
    List<Service> getRecommendedServices();
    List<BasicService> getAllBusinessBasicServices(long businessId);
    List<Service> getAllBusinessServices(long businessId);
    List<String> getAvailableNeighbourhoods(String category);
}
