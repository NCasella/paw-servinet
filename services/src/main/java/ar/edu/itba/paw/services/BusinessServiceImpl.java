package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.exceptions.BusinessNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service("BusinessServiceImpl")
public class BusinessServiceImpl implements BusinessService{

    private final BusinessDao businessDao;
    private final ServiceService serviceService;
    private final UserService userService;
    private final EmailService emailService;
    private final Logger LOGGER = LoggerFactory.getLogger(BusinessServiceImpl.class);

    @Autowired
    public BusinessServiceImpl(final BusinessDao businessDao, final ServiceService serviceService,
                               final UserService userService, final EmailService emailService){
        this.businessDao = businessDao;
        this.serviceService = serviceService;
        this.userService = userService;
        this.emailService = emailService;
    }
    @Transactional(readOnly = true)
    @Override
    public Optional<Business> findById(long id) {
        return businessDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Business> findByBusinessName(String businessName) {
        return businessDao.findByBusinessName(businessName);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Business> findByAdminId(User admin){
        return businessDao.findByUser(admin);
    }

    @Transactional
    @Override
    public Optional<String> getBusinessEmail(long businessid) {
        return businessDao.getBusinessEmail(businessid);
    }

    @Transactional
    @Override
    public void changeBusinessEmail(long businessId, String value){
        businessDao.changeBusinessEmail(businessId,value);
    }
    @Transactional
    @Override
    public void changeBusinessLocation(long businessId,String value){
        businessDao.changeBusinessLocation(businessId,value);
    }
    @Transactional
    @Override
    public void changeBusinessTelephone(long businessId,String value){
        businessDao.changeBusinessTelephone(businessId,value);
    }
    @Transactional
    @Override
    public void deleteBusiness(long businessid){

        final Business business = findById(businessid).orElseThrow(BusinessNotFoundException::new);
        List<Service> servicesList = serviceService.getAllBusinessServices(businessid);
            for ( Service service : servicesList)
                serviceService.delete(service, business,false);

        boolean isStillProvider = businessDao.deleteBusiness(businessid);
        emailService.deletedBusiness(business,getBusinessLocale(business.getUserId()));
        User user = userService.findById(business.getUserId()).orElseThrow(UserNotFoundException::new);
        if (!isStillProvider) {
            userService.revokeProviderRole(user);
        }
    }

    @Transactional
    @Override
    public Business createBusiness(String businessName, long userId, String telephone, String email, String location){
        Business business = businessDao.createBusiness(businessName,userId,telephone,email,location);

        emailService.createdBusiness(business,getBusinessLocale(business.getUserId()));
        User user= userService.findById(userId).orElseThrow(UserNotFoundException::new);
        userService.makeProvider(user);
        return business;
    }

    private String getBusinessLocale( long adminId ){
        return userService.getUserLocale(adminId);
    }

}
