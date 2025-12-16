package ar.edu.itba.paw.webapp.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDTO {

    @Min(value=1,message = "Min.reviewForm.rating")
    @Max(value=5,message = "Max.reviewForm.rating")
    private int rating;

    @Size(max=255,message = "Size.reviewForm.comment")
    private String comment;
}
