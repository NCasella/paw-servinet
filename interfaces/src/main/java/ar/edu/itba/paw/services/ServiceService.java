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
    Service create(long businessId, String name, String description, boolean homeservice, Neighbourhoods[] neighbourhood, String location, Categories category, int minimalduration, PricingTypes pricing, String price, boolean additionalCharges, MultipartFile image);
    void editService(long serviceId, String newDescription, int newDuration, PricingTypes newPricingType, String newPrice, boolean newAdditionalCharges);
    void delete(Service service, Business business, boolean sendEmailToBusiness);
    void delete(long serviceId);
    Map<Long,ServiceContactInfo> getServicesContactInfo(Collection<Long> serviceids);
    PagedList<Service> getServices(int page, Categories category, Neighbourhoods[] neighbourhoods, int rating, String query, ServicesOrderFilters orderFilters, Boolean homeServiceFilter, Long businessId);
    int getServiceCount(Categories category, Neighbourhoods[] neighbourhoods, int rating, String searchQuery, Boolean homeServiceCount, Long businessId);
    int getPageCount(Categories category, Neighbourhoods[] neighbourhoods, int rating, String searchQuery, Boolean homeServiceCount, Long businessId);
    List<Service> getRecommendedServices();
    List<BasicService> getAllBusinessBasicServices(long businessId);
    List<BasicService> getAllUserBasicServices(User user);
    List<Service> getAllBusinessServices(long businessId);
    List<String> getAvailableNeighbourhoods(Categories category);
}
