package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.ImageModel;

import java.util.Objects;

public class ImageDto {

    private byte[] imageContent;

    public static ImageDto fromImage(ImageModel image){
    ImageDto imageDto=new ImageDto();
    imageDto.setImageContent(image.getImageBytes());
    return imageDto;
    }
    private ImageDto(){}

    public byte[] getImageContent() {
        return imageContent;
    }
    public void setImageContent(byte[] imageContent){
        this.imageContent= imageContent.clone();
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(imageContent);
    }
}
