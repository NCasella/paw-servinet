package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;

@org.springframework.stereotype.Service("serviceServiceImpl")

public class ServiceServiceImpl implements ServiceService {
    private final ServiceDao serviceDao;
    private final EmailService emailService;
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final BusinessDao businessDao;
    private final ImageService imageService;


    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceServiceImpl.class);
    @Autowired
    public ServiceServiceImpl(final ServiceDao serviceDao, final EmailService emailService,
                              final AppointmentService appointmentService, final UserService userService,
                              final ImageService imageService, final BusinessDao businessDao) {
        this.serviceDao = serviceDao;
        this.emailService = emailService;
        this.appointmentService = appointmentService;
        this.userService = userService;
        this.businessDao = businessDao;
        this.imageService = imageService;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Service> findById(long id) {
        return serviceDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<BasicService> findBasicServiceById(long id) {
        return serviceDao.findBasicServiceById(id);
    }

    @Transactional
    @Override
    public Service create(long businessId, String name, String description, boolean homeservice,
                          Neighbourhoods[] neighbourhood, Neighbourhoods[] uniqueNeighbourhood, String location, Categories category, int minimalduration,
                          PricingTypes pricing, String price, boolean additionalCharges, MultipartFile image) {
        Business business = businessDao.findById( businessId).orElseThrow(BusinessNotFoundException::new);

        Long imageId=null;
        if(!image.isEmpty()){
            ImageModel maybeImage = imageService.addImage(image);
            if (maybeImage != null) {
                imageId = maybeImage.getImageId();
            }
        }

        Service service = serviceDao.create(business, name, description, homeservice, homeservice? "":location, homeservice? neighbourhood:uniqueNeighbourhood, category, minimalduration ,pricing, price, additionalCharges, imageId);
        emailService.createdService(service, business, business.getOwnedBy().getLocale());
        return service;
    }

    @Transactional
    @Override
    public Service editServiceName(long serviceid, String newvalue) {
        return serviceDao.editServiceName(serviceid,newvalue);
    }

    @Transactional
    @Override
    public Map<Long,ServiceContactInfo> getServicesContactInfo(Collection<Long> serviceids){
        Map<Long,ServiceContactInfo> result = new HashMap<>();
        List<ServiceContactInfo> l =  serviceDao.getServicesContactInfo(serviceids);
        for ( ServiceContactInfo s : l ){
            result.putIfAbsent(s.getServiceId(), s);
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Service> getAllServices() {
        return serviceDao.getAllServices();
    }

    @Transactional
    @Override
    public void delete(long serviceId) {

        Optional<Service> optionalService = findById(serviceId);
        if (optionalService.isEmpty())
            return;
        final Service service = optionalService.get();
        final Business business = businessDao.findById(service.getBusinessid()).orElseThrow(UserNotFoundException::new);

        delete(service,business,true);
    }

    @Transactional
    @Override
    public void delete(Service service, Business business, boolean sendEmailToBusiness) {
        String businessLocale = business.getOwnedBy().getLocale();
        List<Appointment> appointmentList = appointmentService.getAllUpcomingServiceAppointments(service.getId());
             for ( Appointment appointment : appointmentList){
                 User client = userService.findById( appointment.getUserid()).orElseThrow(UserNotFoundException::new);

                if (appointment.getConfirmed())
                    emailService.cancelledAppointment(appointment,service,business,client,true,businessLocale);
                else
                    emailService.deniedAppointment(appointment,service,business,client,true,businessLocale);
             }
        serviceDao.delete(service.getId());
         if (sendEmailToBusiness)
            emailService.deletedService(service,business,businessLocale);
    }


    @Transactional(readOnly = true)
    @Override
    public PagedList<Service> getServices(int page, String category, String[] location, String rating, String query, ServicesOrderFilters orderFilter,Boolean homeServiceFilter, Long businessId) {
        int ratingNum = Ratings.getMinValueByName(rating);
        List<Service> services = serviceDao.getServicesFilteredBy(page, category, location, ratingNum, query, orderFilter,homeServiceFilter, businessId);
        int serviceCount = getServiceCount(category, location, rating, query,homeServiceFilter, businessId);
        return PagedList.of(services, serviceCount);
    }

    @Transactional(readOnly = true)
    @Override
    public int getServiceCount(String category, String[] location, String rating, String searchQuery,Boolean homeServiceFilter, Long businessId) {
        int ratingNum = Ratings.getMinValueByName(rating);
        return serviceDao.getServiceCount(category, location, ratingNum, searchQuery,homeServiceFilter, businessId);
    }

    @Transactional(readOnly = true)
    @Override
    public int getPageCount(String category, String[] location, String rating, String searchQuery,Boolean homeServiceCount, Long businessId) {
        int serviceCount = getServiceCount(category, location, rating, searchQuery,homeServiceCount, businessId);
        int pageCount = serviceCount / 10;
        if(serviceCount % 10 != 0) pageCount++;
        return pageCount;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BasicService> getAllBusinessBasicServices(long businessId) {
        return serviceDao.getAllBusinessBasicServices(businessId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BasicService> getAllUserBasicServices(User user) {
        List<BasicService> services = new ArrayList<>();
        for(Business business : user.getBusinessOwned()) {
            services.addAll(getAllBusinessBasicServices(business.getBusinessid()));
        }
        return services;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Service> getAllBusinessServices(long businessid){
        return serviceDao.getAllBusinessServices(businessid);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Service> getRecommendedServices() {
        return serviceDao.getRecommendedServices();
    }

    @Transactional
    @Override
    public void editService(long serviceId, String newDescription, int newDuration, PricingTypes newPricingType, String newPrice, boolean newAdditionalCharges) {
        serviceDao.editService(serviceId, newDescription, newDuration, newPricingType, newPrice, newAdditionalCharges);
    }

    @Transactional
    @Override
    public List<String> getAvailableNeighbourhoods(String category) {
        if(category == null) {
            return serviceDao.getAvailableNeighbourhoods();
        }
        return serviceDao.getAvailableNeighbourhoodsByCategory(category);
    }

}
