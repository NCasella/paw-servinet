import type {PagedResult} from "$models/PagedList";
import {GET, getNewIdFromPostResponse, PATCH, POST} from "$utils/apiFetch";
import {parsePagedResponse} from "$models/PagedList";
import type {ReviewForm} from "$models/forms/ReviewCreationForm";
import {Review} from "$models/Review";

export async function getServiceReviews(
    serviceId: number,
    filter?: string,
    page?: number
): Promise<PagedResult<Review>> {

    const params = new URLSearchParams();

    params.append("serviceId", String(serviceId));

    if (filter) params.append("filter", filter);
    if (page !== undefined) params.append("page", String(page));

    const response = await GET(
        `reviews?${params.toString()}`,
        { contentType: "review-list" }
    );

    return parsePagedResponse(response, Review);
}

export async function createReview(form: ReviewForm) :Promise<number> {
    const response = await POST("reviews",form, {
        contentType: "review-creation"
    });
    return getNewIdFromPostResponse(response)
}