package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.ImageModel;
import ar.edu.itba.paw.services.ImageDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

public class ImageDaoJpa implements ImageDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<ImageModel> getImageById(long id)  {
        return Optional.of(em.find(ImageModel.class, id));
    }

    @Override
    public ImageModel addImage( byte[] image){
        ImageModel newImage = new ImageModel(image);
        em.persist(newImage);
        return newImage;
    }

}
