import type {PagedResult} from "$models/PagedList";
import {GET, getNewIdFromPostResponse, PATCH, POST} from "$utils/apiFetch";
import {isLastPage, parsePagedResponse} from "$models/PagedList";
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

export async function getAllServiceReviews(serviceId: number): Promise<Review[]> {
  const all: Review[] = [];
  let page = 1;
  let pageResult: PagedResult<Review>;

  while (true) {
    pageResult = await getServiceReviews(serviceId, undefined, page);

    all.push(...pageResult.items);

    if (isLastPage(page, pageResult)) break;
    page++;
  }

  return all;
}

export async function updateReview(reviewId: number, form: Partial<ReviewForm>): Promise<void> {
    await PATCH(`reviews/${reviewId}`, form, {
        contentType: "review-update"
    });
}