package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Rating;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RatingService {
    List<Rating> getAllRatings(long serviceid, int page);
    List<Rating> getAllBusinessRatings(long businessId);
    Optional<Rating> findById(long id);
    Rating create(long serviceid, long userid, int rating, String comment);
    double getRatingsAvg(long serviceid);
    int getRatingsCount(long serviceid);
    double getBussinessRatingsAvg(long businessId);
    Rating hasAlreadyRated(long userid, long serviceid);
    void edit(long ratingid, int rating, String comment);
    Map<Integer, Double> getRatingsAvgByRate(long serviceId);
    Map<Integer, Double> getBusinessRatingsAvgByRate(long businessId);
    int getBusinessRatingsCount(long businessId);
}
