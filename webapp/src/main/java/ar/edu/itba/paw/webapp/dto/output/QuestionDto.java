package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.webapp.dto.output.links.QuestionLinks;
import ar.edu.itba.paw.webapp.jersey.PathUrls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {
    private long questionId;
    private long serviceId;
    private long userId;
    private String question;
    private String response;
    private LocalDate date;

    private QuestionLinks links;

    public static QuestionDto fromQuestion(Question question, UriInfo uriInfo) {

        URI selfUri = uriInfo.getBaseUriBuilder()
                .path(PathUrls.QUESTIONS_URL.getUrl())
                .path(String.valueOf(question.getId()))
                .build();

        return QuestionDto.builder()
                .questionId(question.getId())
                .serviceId(question.getServiceid())
                .userId(question.getUserid())
                .question(question.getQuestion())
                .response(question.getResponse())
                .date(question.getDate())
                .links(
                    QuestionLinks.builder()
                            .self(selfUri)
                            .build()
                )
                .build();
    }
}
