package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.RatingsFilters;
import ar.edu.itba.paw.model.exceptions.RatingNotFoundException;
import ar.edu.itba.paw.model.exceptions.ServiceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("RatingServiceImpl")
public class RatingServiceImpl implements RatingService {

    private final RatingDao ratingDao;
    private final ServiceService serviceService;
    private final BusinessService businessService;
    private static final int PAGE_SIZE=10;
    @Autowired
    public RatingServiceImpl(
            final RatingDao ratingDao,
            final ServiceService serviceService,
            final BusinessService businessService
    ) {
        this.ratingDao = ratingDao;
        this.serviceService = serviceService;
        this.businessService = businessService;
    }

    @Transactional(readOnly = true)
    @Override
    public PagedList<Rating> getAllRatings(int page, RatingsFilters filter) {
        List<Rating> ratings = ratingDao.getAllRatings(page, PAGE_SIZE, filter);
        int ratingsCount = getAllRatingsCount(filter);
        return PagedList.of(ratings, ratingsCount);
    }

    @Transactional(readOnly = true)
    @Override
    public PagedList<Rating> getRatingsByService(long serviceId, int page, RatingsFilters filter) {
        serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        List<Rating> ratings = ratingDao.getRatingsByService(serviceId, page, PAGE_SIZE, filter);
        int ratingsCount = getRatingsCountByService(serviceId, filter);
        return PagedList.of(ratings, ratingsCount);
    }

    @Transactional(readOnly = true)
    @Override
    public int getAllRatingsCount(RatingsFilters filter) {
        return ratingDao.getAllRatingsCount(filter);
    }

    @Transactional(readOnly = true)
    @Override
    public int getRatingsCountByService(long serviceId, RatingsFilters filter) {
        serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        return ratingDao.getRatingsCountByService(serviceId, filter);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Rating> findById(long id) {
        return ratingDao.findById(id);
    }

    @Transactional
    @Override
    public Rating create(long serviceId, long userId, int rating, String comment) {
        serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        return ratingDao.create(serviceId, userId, rating, comment);
    }

    @Transactional(readOnly = true)
    @Override
    public double getRatingsAvg(ar.edu.itba.paw.model.Service service) {
        double avg = service.getRatingAvg();
        avg = Math.round(avg * 10) / 10.0;
        return avg;
    }


    @Transactional(readOnly = true)
    @Override
    public Rating hasAlreadyRated(long userid, long serviceId) {
        Rating rating;
        if(ratingDao.hasAlreadyRated(userid, serviceId).isPresent()) {
            rating = ratingDao.hasAlreadyRated(userid, serviceId).get();
        } else rating = null;
        return rating;
    }

    @Transactional
    @Override
    public void edit(long ratingId, int rating, String comment) {
        findById(ratingId).orElseThrow(RatingNotFoundException::new);
        ratingDao.edit(ratingId, rating, comment);
    }


    @Transactional
    @Override
    public List<Rating> getAllBusinessRatings(long businessId, int page) {
        businessService.findById(businessId).orElseThrow(ServiceNotFoundException::new);
        return ratingDao.getAllBusinessRatings(businessId, page, PAGE_SIZE);
    }

    @Transactional
    @Override
    public List<Rating> getAllBusinessRatingsFiltered(long businessId, int page, RatingsFilters filter) {
        businessService.findById(businessId).orElseThrow(ServiceNotFoundException::new);
        if(filter == null) {
            return getAllBusinessRatings(businessId, page);
        }
        return ratingDao.getAllBusinessRatingsFiltered(businessId, page, PAGE_SIZE, filter);
    }

    @Transactional
    @Override
    public Map<Integer, Double> getRatingsAvgByRate(long serviceId) {
        serviceService.findById(serviceId).orElseThrow(ServiceNotFoundException::new);
        return orderRatings(ratingDao.getRatingsAvgByRate(serviceId));
    }

    @Transactional
    @Override
    public Map<Integer, Double> getBusinessRatingsAvgByRate(long businessId) {
        businessService.findById(businessId).orElseThrow(ServiceNotFoundException::new);
        return orderRatings(ratingDao.getBusinessRatingsAvgByRate(businessId));
    }

    private Map<Integer, Double> orderRatings(List<Object[]> results) {
        Map<Integer, Double> tempRatings = new HashMap<>();
        for (Object[] result : results) {
            Integer rating = (Integer) result[0];
            Double count = ((Long) result[1]).doubleValue();
            tempRatings.put(rating, count);
        }
        Map<Integer, Double> ratings = new LinkedHashMap<>();
        for (int i = 5; i > 0; i--) {
            ratings.put(i, tempRatings.getOrDefault(i, 0.0));
        }
        return ratings;
    }

    @Transactional(readOnly = true)
    @Override
    public int getBusinessRatingsPageCount(Business business) {
        int serviceCount = (int) business.getBusinessRatingCount();
        int pageCount = serviceCount / PAGE_SIZE;
        if(serviceCount % PAGE_SIZE != 0) pageCount++;
        return pageCount;
    }
}
