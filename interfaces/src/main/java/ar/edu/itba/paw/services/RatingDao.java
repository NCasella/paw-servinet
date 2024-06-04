package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Rating;
import ar.edu.itba.paw.model.RatingsFilters;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RatingDao {
    List<Rating> getAllRatings(long serviceid, int page, int pageSize);
    List<Rating> getAllRatingsFiltered(long serviceid, int page, int pageSize, RatingsFilters filter);
    List<Rating> getAllBusinessRatings(long businessId, int page, int pageSize);
    List<Rating> getAllBusinessRatingsFiltered(long businessid, int page, int pageSize, RatingsFilters filter);
    Optional<Rating> findById(long id);
    Rating create(long serviceid, long userid, int rating, String comment);
    Optional<Rating> hasAlreadyRated(long userid, long serviceid);
    void edit(long ratingid, int rating, String comment);
    List<Object[]> getRatingsAvgByRate(long serviceId);
    List<Object[]> getBusinessRatingsAvgByRate(long businessId);
}
