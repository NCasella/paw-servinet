package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.RatingsFilters;
import ar.edu.itba.paw.model.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RatingService {
    List<Rating> getAllRatings(long serviceid, int page);
    List<Rating> getAllRatingsFiltered(long serviceid, int page, RatingsFilters filter);
    List<Rating> getAllBusinessRatings(long businessId);
    List<Rating> getAllBusinessRatingsFiltered(long businessid, int page, RatingsFilters filter);
    Optional<Rating> findById(long id);
    Rating create(long serviceid, long userid, int rating, String comment);
    double getRatingsAvg(Service serviceid);
    Rating hasAlreadyRated(long userid, long serviceid);
    void edit(long ratingid, int rating, String comment);
    Map<Integer, Double> getRatingsAvgByRate(long serviceId);
    Map<Integer, Double> getBusinessRatingsAvgByRate(long businessId);
}
