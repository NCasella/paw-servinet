package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;

public interface BusinessDao {
     Optional<Business> findById(long id);

     Optional<Business> findByBusinessName(String businessName);

     List<Business> findByUser(User user);

     Optional<String> getBusinessEmail(long businessid);
     void changeBusinessLocation(long businessId,String value);
     void changeBusinessTelephone(long businessId, String value);
     void changeBusinessEmail(long businessId, String value);

     boolean deleteBusiness(long businessid);

     Business createBusiness(String businessName, long userId, String telephone, String email, String location);

}
