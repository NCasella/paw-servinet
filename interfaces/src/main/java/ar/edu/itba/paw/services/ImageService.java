package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.ImageModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService {
    Optional<ImageModel> getImageById(long id);
    ImageModel addImage(byte[] imageBytes);
}
