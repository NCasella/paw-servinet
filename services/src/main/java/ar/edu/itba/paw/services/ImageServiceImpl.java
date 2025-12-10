package ar.edu.itba.paw.services;


import ar.edu.itba.paw.model.ImageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service("imageServiceImpl")
public class ImageServiceImpl implements ImageService{

    private static final Logger LOGGER= LoggerFactory.getLogger(ImageServiceImpl.class);
    private final ImageDao imageDao;

    @Autowired
    public ImageServiceImpl(final ImageDao imageDao){
        this.imageDao = imageDao;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ImageModel> getImageById(Long id) {
        if(id==null){
            return Optional.empty();
        }
        return imageDao.getImageById(id);
    }

    @Transactional
    @Override
    public ImageModel addImage( byte[] bytes) {
        return imageDao.addImage(bytes);

    }

}
