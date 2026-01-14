package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.ImageModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceImplTest {

    private static final long IMAGE_ID = 1L;
    private static final byte[] IMAGE_BYTES = new byte[]{0x00, 0x01, 0x02};

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private ImageDao imageDao;

    @Test
    public void testGetImageByIdNull() {
        Optional<ImageModel> result = imageService.getImageById(null);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isPresent());
        Mockito.verify(imageDao, Mockito.never()).getImageById(Mockito.anyLong());
    }

    @Test
    public void testGetImageByIdNotFound() {
        Mockito.when(imageDao.getImageById(IMAGE_ID)).thenReturn(Optional.empty());

        Optional<ImageModel> result = imageService.getImageById(IMAGE_ID);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isPresent());
        Mockito.verify(imageDao).getImageById(IMAGE_ID);
    }

    @Test
    public void testGetImageByIdFound() {
        ImageModel imageModel = new ImageModel(IMAGE_ID, IMAGE_BYTES);
        Mockito.when(imageDao.getImageById(IMAGE_ID)).thenReturn(Optional.of(imageModel));

        Optional<ImageModel> result = imageService.getImageById(IMAGE_ID);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(IMAGE_ID, result.get().getImageId());
        Assert.assertArrayEquals(IMAGE_BYTES, result.get().getImageBytes());
    }

    @Test
    public void testAddImage() {
        ImageModel imageModel = new ImageModel(IMAGE_ID, IMAGE_BYTES);
        Mockito.when(imageDao.addImage(IMAGE_BYTES)).thenReturn(imageModel);

        ImageModel result = imageService.addImage(IMAGE_BYTES);

        Assert.assertNotNull(result);
        Assert.assertEquals(IMAGE_ID, result.getImageId());
        Assert.assertArrayEquals(IMAGE_BYTES, result.getImageBytes());
        Mockito.verify(imageDao).addImage(IMAGE_BYTES);
    }
}
