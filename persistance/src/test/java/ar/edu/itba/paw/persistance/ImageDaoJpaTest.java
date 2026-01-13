package ar.edu.itba.paw.persistance;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.model.ImageModel;
import ar.edu.itba.paw.persistance.config.TestConfig;

@Transactional
@Rollback
@Sql("classpath:sql/schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImageDaoJpaTest {

    private static final byte[] IMAGE_BYTES = new byte[]{0x00, 0x01, 0x02, 0x03};

    @Autowired
    private ImageDaoJpa imageDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testAddImage() {
        ImageModel image = imageDao.addImage(IMAGE_BYTES);
        em.flush();
        em.clear();

        Assert.assertNotNull("The image object SHOULD be returned by the DAO after creation.", image);

        ImageModel persisted = em.find(ImageModel.class, image.getImageId());
        Assert.assertNotNull("The image SHOULD be retrievable from the DB by its ID.", persisted);
        Assert.assertArrayEquals("The image bytes SHOULD be preserved after persistence.",
                IMAGE_BYTES, persisted.getImageBytes());

        long count = em.createQuery("SELECT COUNT(i) FROM ImageModel i", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain exactly 1 image after creation.",
                1L, count);
    }

    @Test
    public void testGetImageByIdFound() {
        em.createNativeQuery("INSERT INTO images (imageid, imageBytes) VALUES (1, X'00010203')").executeUpdate();
        em.flush();
        em.clear();

        Optional<ImageModel> result = imageDao.getImageById(1L);

        Assert.assertTrue("An existing image SHOULD be retrievable by its ID.",
                result.isPresent());
        Assert.assertEquals("The image ID SHOULD be preserved in the database.",
                1L, result.get().getImageId());
    }

    @Test
    public void testGetImageByIdNotFound() {
        Optional<ImageModel> result = imageDao.getImageById(999L);

        Assert.assertFalse("The response for a non-existent ID SHOULD be empty.",
                result.isPresent());
    }

    @Test
    public void testAddMultipleImages() {
        ImageModel image1 = imageDao.addImage(new byte[]{0x01});
        ImageModel image2 = imageDao.addImage(new byte[]{0x02});
        em.flush();
        em.clear();

        Assert.assertNotNull("The first image object SHOULD be returned.", image1);
        Assert.assertNotNull("The second image object SHOULD be returned.", image2);
        Assert.assertNotEquals("The two images SHOULD have different IDs.",
                image1.getImageId(), image2.getImageId());

        long count = em.createQuery("SELECT COUNT(i) FROM ImageModel i", Long.class).getSingleResult();
        Assert.assertEquals("The DB SHOULD contain exactly 2 images after creation.",
                2L, count);
    }

    @Test
    public void testAddEmptyImage() {
        ImageModel image = imageDao.addImage(new byte[]{});
        em.flush();
        em.clear();

        Assert.assertNotNull("The image object SHOULD be returned even with empty bytes.", image);

        ImageModel persisted = em.find(ImageModel.class, image.getImageId());
        Assert.assertNotNull("The image SHOULD be retrievable from the DB.", persisted);
        Assert.assertArrayEquals("The persisted image bytes SHOULD be empty.",
                new byte[]{}, persisted.getImageBytes());
    }
}
