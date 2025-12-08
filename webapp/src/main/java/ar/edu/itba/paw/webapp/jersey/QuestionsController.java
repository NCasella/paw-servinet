package ar.edu.itba.paw.webapp.jersey;

import ar.edu.itba.paw.model.PagedList;
import ar.edu.itba.paw.model.Question;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.exceptions.InvalidFilterException;
import ar.edu.itba.paw.model.exceptions.QuestionNotFoundException;
import ar.edu.itba.paw.model.exceptions.UserNotFoundException;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.auth.ServinetAuthControl;
import ar.edu.itba.paw.webapp.dto.input.QuestionCreationDTO;
import ar.edu.itba.paw.webapp.dto.input.QuestionResponseDTO;
import ar.edu.itba.paw.webapp.dto.output.QuestionDto;
import ar.edu.itba.paw.webapp.mediaType.CustomMediaTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/questions")
@Component
public class QuestionsController {

    @Context
    private UriInfo uriInfo;
    @Context
    private Request request;
    private final QuestionService questionService;
    private final ServinetAuthControl authControl;

    @Autowired
    public QuestionsController(
            QuestionService questionService,
            ServinetAuthControl authControl
    ) {
        this.authControl = authControl;
        this.questionService = questionService;
    }

    @OPTIONS
    public Response getSupportedMimeTypesForQuestions() {
        return Response.ok()
                .header("Allow", "GET, POST, OPTIONS")
                .header("Accept", CustomMediaTypes.QUESTION_LIST)
                .header("Accept-Post", CustomMediaTypes.QUESTION_CREATION)
                .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                .build();
    }

    @GET
    @Produces(value = CustomMediaTypes.QUESTION_LIST)
    public Response getQuestions(
            @QueryParam("serviceId") final Long serviceId,
            @QueryParam("respondentId") final Long respondentId,
            @QueryParam("page") @DefaultValue("1") final int page
    ){
        if (serviceId != null && respondentId != null)
            throw new InvalidFilterException("Cannot filter by both serviceId and businessId simultaneously.");

        PagedList<Question> pagedList;
        if(serviceId != null) {
            pagedList = questionService.getQuestionsByService(serviceId, page);
        } else if(respondentId != null) {
            pagedList = questionService.getQuestionsToRespond(respondentId, page);
        } else {
            pagedList = questionService.getAllQuestions(page);
        }

        List<QuestionDto> dtoList = pagedList.getList().stream()
                .map(q -> QuestionDto.fromQuestion(q, uriInfo))
                .toList();

        return PagedListResponse.generate(
                dtoList,
                page,
                pagedList.getTotalElements(),
                uriInfo,
                QuestionDto.class,
                request
        );
    }

    @POST
    @Consumes(value = CustomMediaTypes.QUESTION_CREATION)
    public Response createQuestion(@Valid final QuestionCreationDTO questionCreationDto) {
        User currentUser = authControl.getCurrentUser().orElseThrow(UserNotFoundException::new);
        Question question = questionService.create(
                questionCreationDto.getServiceId(),
                currentUser.getUserId(),
                questionCreationDto.getQuestion()
        );
        return Response.created(
                uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(question.getId()))
                        .build()
        ).build();
    }


    @Path("/{questionId}")
    @OPTIONS
    public Response getSupportedMimeTypesForQuestion() {
        return Response.ok()
                .header("Allow", "GET, PATCH, OPTIONS")
                .header("Accept", CustomMediaTypes.QUESTION_INFO)
                .header("Accept-Patch", CustomMediaTypes.QUESTION_RESPONSE)
                .header("Access-Control-Allow-Methods", "GET, PATCH, OPTIONS")
                .build();
    }

    @GET
    @Path("/{questionId}")
    @Produces(value = CustomMediaTypes.QUESTION_INFO)
    public Response getQuestionById(
            @PathParam("questionId") final long questionId
    ){
        Question question = questionService.findById(questionId).orElseThrow(QuestionNotFoundException::new);
        return ConditionalCache.cacheResponse(request, QuestionDto.fromQuestion(question, uriInfo)).build();
    }

    @PATCH
    @Path("/{questionId}")
    @Consumes(value = CustomMediaTypes.QUESTION_RESPONSE)
    public Response updateQuestionResponse(
            @PathParam("questionId") final long questionId,
            @Valid final QuestionResponseDTO questionResponseDTO
    ){
        questionService.addResponse(questionId, questionResponseDTO.getResponse());
        return Response.noContent().build();
    }
}
