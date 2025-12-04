package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Business;
import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.RatingsFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("RatingServiceImpl")
public class RatingServiceImpl implements RatingService {

    private final RatingDao ratingDao;
    private static final int PAGE_SIZE=10;
    @Autowired
    public RatingServiceImpl(final RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    @Transactional(readOnly = true)
    @Override
    public PagedList<Rating> getAllRatings(long serviceId, int page, RatingsFilters filter) {
        List<Rating> ratings = ratingDao.getAllRatings(serviceId, page, PAGE_SIZE, filter);
        int ratingsCount = getAllRatingsCount(serviceId, filter);
        return PagedList.of(ratings, ratingsCount);
    }

    @Transactional(readOnly = true)
    @Override
    public int getAllRatingsCount(long serviceId, RatingsFilters filter) {
        return ratingDao.getAllRatingsCount(serviceId, filter);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Rating> findById(long id) {
        return ratingDao.findById(id);
    }

    @Transactional
    @Override
    public Rating create(long serviceid, long userid, int rating, String comment) {
        return ratingDao.create(serviceid, userid, rating, comment);
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
    public Rating hasAlreadyRated(long userid, long serviceid) {
        Rating rating;
        if(ratingDao.hasAlreadyRated(userid, serviceid).isPresent()) {
            rating = ratingDao.hasAlreadyRated(userid, serviceid).get();
        } else rating = null;
        return rating;
    }

    @Transactional
    @Override
    public void edit(long ratingid, int rating, String comment) {
        ratingDao.edit(ratingid, rating, comment);
    }


    @Transactional
    @Override
    public List<Rating> getAllBusinessRatings(long businessId, int page) {
        return ratingDao.getAllBusinessRatings(businessId, page, 10);
    }

    @Transactional
    @Override
    public List<Rating> getAllBusinessRatingsFiltered(long businessid, int page, RatingsFilters filter) {
        if(filter == null) {
            return getAllBusinessRatings(businessid, page);
        }
        return ratingDao.getAllBusinessRatingsFiltered(businessid, page, 10, filter);
    }

    @Transactional
    @Override
    public Map<Integer, Double> getRatingsAvgByRate(long serviceId) {
        return orderRatings(ratingDao.getRatingsAvgByRate(serviceId));
    }

    @Transactional
    @Override
    public Map<Integer, Double> getBusinessRatingsAvgByRate(long businessId) {
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
