package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service("serviceServiceImpl")

public class ServiceServiceImpl implements ServiceService {
    private final ServiceDao serviceDao;
    private final EmailService emailService;
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final BusinessDao businessDao;
    private final ImageService imageService;
    private final Logger LOGGER = LoggerFactory.getLogger(ServiceServiceImpl.class);

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
    public Service create(long businessId, String name, String description, boolean homeservice,
                          Neighbourhoods[] neighbourhood, String location, Categories category, int minimalduration,
                          PricingTypes pricing, String price, boolean additionalCharges, MultipartFile image) throws IOException {
        Business business = businessDao.findById( businessId).orElseThrow(BusinessNotFoundException::new);

        long imageId = image.isEmpty()? 0 : imageService.addImage(image.getBytes()).getImageId();

        Service service = serviceDao.create(business.getBusinessid(), name, description, homeservice,location,neighbourhood, category,minimalduration ,pricing, price, additionalCharges,imageId);
        emailService.createdService(service, business);
        return service;
    }

    @Transactional
    @Override
    public Service edit(long serviceid, String field, String newvalue) {
        return serviceDao.edit(serviceid, field, newvalue);
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
        if (!optionalService.isPresent())
            return;
        final Service service = optionalService.get();
        final Business business = businessDao.findById(service.getBusinessid()).get();

        delete(service,business);
    }

    @Transactional
    @Override
    public void delete(Service service, Business business) {

        List<Appointment> appointmentList = appointmentService.getAllUpcomingServiceAppointments(service.getId());
             for ( Appointment appointment : appointmentList){
                 User client = userService.findById( appointment.getUserid()).get();

                if (appointment.getConfirmed())
                    emailService.cancelledAppointment(appointment,service,business,client,true);
                else
                    emailService.deniedAppointment(appointment,service,business,client,true);
             }
        serviceDao.delete(service.getId());
        emailService.deletedService(service,business);
    }


    @Transactional(readOnly = true)
    @Override
    public List<Service> services(int page, String category, String[] location, String rating, String query) {
        int ratingNum = Ratings.getMinValueByName(rating);
        return serviceDao.getServicesFilteredBy(page, category, location, ratingNum, query);
    }

    @Transactional(readOnly = true)
    @Override
    public int getServiceCount(String category, String[] location, String rating, String searchQuery) {
        int ratingNum = Ratings.getMinValueByName(rating);
        return serviceDao.getServiceCount(category, location, ratingNum, searchQuery);
    }

    @Transactional(readOnly = true)
    @Override
    public int getPageCount(String category, String[] location, String rating, String searchQuery) {
        int serviceCount = getServiceCount(category, location, rating, searchQuery);
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
    public List<Service> getAllBusinessServices(long businessid){
        return serviceDao.getAllBusinessServices(businessid);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Service> getRecommendedServices() {
        return serviceDao.getRecommendedServices();
    }
}
