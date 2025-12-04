package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RatingService {
    PagedList<Rating> getAllRatings(long serviceid, int page, RatingsFilters filter);
    int getAllRatingsCount(long serviceId, RatingsFilters filter);
    List<Rating> getAllBusinessRatings(long businessId, int page);
    List<Rating> getAllBusinessRatingsFiltered(long businessid, int page, RatingsFilters filter);
    Optional<Rating> findById(long id);
    Rating create(long serviceid, long userid, int rating, String comment);
    double getRatingsAvg(Service serviceid);
    Rating hasAlreadyRated(long userid, long serviceid);
    void edit(long ratingid, int rating, String comment);
    Map<Integer, Double> getRatingsAvgByRate(long serviceId);
    Map<Integer, Double> getBusinessRatingsAvgByRate(long businessId);
    int getBusinessRatingsPageCount(Business business);
}
