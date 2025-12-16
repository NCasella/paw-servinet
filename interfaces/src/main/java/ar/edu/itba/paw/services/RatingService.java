package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RatingService {
    PagedList<Rating> getAllRatings(int page, RatingsFilters filter);
    PagedList<Rating> getRatingsByService(long serviceId, int page, RatingsFilters filter);
    int getAllRatingsCount(RatingsFilters filter);
    int getRatingsCountByService(long serviceId, RatingsFilters filter);
    List<Rating> getAllBusinessRatings(long businessId, int page);
    List<Rating> getAllBusinessRatingsFiltered(long businessId, int page, RatingsFilters filter);
    Optional<Rating> findById(long id);
    Rating create(long serviceId, long userid, int rating, String comment);
    double getRatingsAvg(Service serviceId);
    Rating hasAlreadyRated(long userid, long serviceId);
    void edit(long ratingId, int rating, String comment);
    Map<Integer, Double> getRatingsAvgByRate(long serviceId);
    Map<Integer, Double> getBusinessRatingsAvgByRate(long businessId);
    int getBusinessRatingsPageCount(Business business);
}
