package ar.edu.itba.paw.model;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class ImageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "images_imageid_seq")
    @SequenceGenerator(sequenceName = "images_imageid_seq",name="images_imageid_seq",allocationSize = 1)
    private long imageId;
    @Column(nullable = false)
    private byte[] imageBytes;

    protected ImageModel() {}
    public ImageModel(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }
    public ImageModel(long imageId, byte[] imageBytes) {
        this.imageId = imageId;
        this.imageBytes = imageBytes;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }
    public long getImageId() {
        return this.imageId;
    }

    public void setImageBytes(byte[] image) {
        this.imageBytes = image;
    }

}
