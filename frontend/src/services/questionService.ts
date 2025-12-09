import type {PagedResult} from "$models/PagedList";
import {GET, getNewIdFromPostResponse, PATCH, POST} from "$utils/apiFetch";
import {parsePagedResponse} from "$models/PagedList";
import {Question} from "$models/Question";
import {getCurrentUser} from "$services/userService";
import type {QuestionResponseForm} from "$models/forms/QuestionResponseForm";
import type {QuestionForm} from "$models/forms/QuestionCreationForm";

export async function getServiceQuestions(serviceId: number, page: number) : Promise<PagedResult<Question>> {
    const response = await GET(`questions?serviceId=${serviceId}&page=${page}`,
        {contentType: "question-list"} )

    return parsePagedResponse(response, Question);
}

export async function getRespondentQuestions(page:number) : Promise<PagedResult<Question>> {
    const user = await getCurrentUser().catch( (e) => e)
    const response = await GET(`questions?respondentId=${user.userId}&page=${page}`,
        {contentType: "question-list"} )

    return parsePagedResponse(response, Question);
}

export async function createQuestion(form: QuestionForm) :Promise<number> {
    const response = await POST("questions",form, {
        contentType: "question-creation"
    });
    return getNewIdFromPostResponse(response)
}

export async function respondQuestion(questionId: number, form: QuestionResponseForm) :Promise<void> {
    await PATCH(`questions/${questionId}`,form, {
        contentType: "question-response"
    })
}
