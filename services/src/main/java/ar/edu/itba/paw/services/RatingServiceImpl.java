package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("RatingServiceImpl")
public class RatingServiceImpl implements RatingService {

    private final RatingDao ratingDao;

    @Autowired
    public RatingServiceImpl(final RatingDao ratingDao) {
        this.ratingDao = ratingDao;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Rating> getAllRatings(long serviceid, int page) {
        //TODO: manejar tamaño de pagina
        return ratingDao.getAllRatings(serviceid, page,10);
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
    public double getRatingsAvg(long serviceid) {
        double avg = ratingDao.getRatingsAvg(serviceid);
        avg = Math.round(avg * 10) / 10.0;
        return avg;
    }

    @Transactional(readOnly = true)
    @Override
    public int getRatingsCount(long serviceid) {
        return ratingDao.getRatingsCount(serviceid);
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
    public double getBussinessRatingsAvg(long businessId) {
        return ratingDao.getBussinessRatingsAvg(businessId);
    }

    @Transactional
    @Override
    public List<Rating> getAllBusinessRatings(long businessId) {
        return ratingDao.getAllBusinessRatings(businessId);
    }

    @Transactional
    @Override
    public Map<Integer, Double> getRatingsAvgByRate(long serviceId) {
        return ratingDao.getRatingsAvgByRate(serviceId);
    }
}
