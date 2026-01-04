import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
    getServiceReviews,
    createReview,
    updateReview,
    getAllServiceReviews
} from '$services/reviewsService';
import { GET, POST, PATCH, getNewIdFromPostResponse } from '$utils/apiFetch';
import { isLastPage, parsePagedResponse } from '$models/PagedList';
import type { PagedResult } from '$models/PagedList';
import { Review } from '$models/Review';

vi.mock('$utils/apiFetch', async (importOriginal) => {
    const actual = await importOriginal<typeof import('$utils/apiFetch')>();

    return {
        ...actual,
        GET: vi.fn(),
        POST: vi.fn(),
        PATCH: vi.fn(),
        getNewIdFromPostResponse: vi.fn()
    };
});

vi.mock('$models/PagedList', async () => {
    const actual = await vi.importActual<any>('$models/PagedList');
    return {
        ...actual,
        parsePagedResponse: vi.fn(),
        isLastPage: vi.fn()
    };
});

vi.mock('$models/Review', () => ({
    Review: vi.fn()
}));

beforeEach(() => {
    vi.resetModules();
    vi.clearAllMocks();
});

describe('getServiceReviews', () => {
    it('fetches reviews with serviceId only', async () => {
        const backendResponse = {
            items: [{ ratingId: 1, rating: 5 }],
            links: {}
        };

        const parsedResult = {
            items: backendResponse.items as unknown as Review[],
            links: {}
        } as PagedResult<Review>;

        vi.mocked(GET).mockResolvedValue(backendResponse);
        vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

        const result = await getServiceReviews(123);

        expect(GET).toHaveBeenCalledTimes(1);
        expect(GET).toHaveBeenCalledWith(
            'reviews?serviceId=123',
            expect.objectContaining({ contentType: 'review-list' })
        );

        expect(parsePagedResponse).toHaveBeenCalledWith(backendResponse, expect.any(Function));
        expect(result).toEqual(parsedResult);
    });

    it('fetches reviews with filter parameter', async () => {
        const parsedResult = { items: [], links: {} } as PagedResult<Review>;

        vi.mocked(GET).mockResolvedValue({});
        vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

        const result = await getServiceReviews(123, 'recent');

        expect(GET).toHaveBeenCalledWith(
            'reviews?serviceId=123&filter=recent',
            expect.any(Object)
        );

        expect(result).toEqual(parsedResult);
    });

    it('fetches reviews with page parameter', async () => {
        const parsedResult = { items: [], links: {} } as PagedResult<Review>;

        vi.mocked(GET).mockResolvedValue({});
        vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

        const result = await getServiceReviews(123, undefined, 2);

        expect(GET).toHaveBeenCalledWith(
            'reviews?serviceId=123&page=2',
            expect.any(Object)
        );

        expect(result).toEqual(parsedResult);
    });

    it('fetches reviews with all parameters', async () => {
        const parsedResult = { items: [], links: {} } as PagedResult<Review>;

        vi.mocked(GET).mockResolvedValue({});
        vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

        const result = await getServiceReviews(456, 'top_rated', 3);

        expect(GET).toHaveBeenCalledWith(
            'reviews?serviceId=456&filter=top_rated&page=3',
            expect.objectContaining({ contentType: 'review-list' })
        );

        expect(result).toEqual(parsedResult);
    });
});

describe('createReview', () => {
    it('posts review and returns new ID', async () => {
        const form = { serviceId: 1, rating: 5, comment: 'Great service!' };

        vi.mocked(POST).mockResolvedValue({});
        vi.mocked(getNewIdFromPostResponse).mockReturnValue(42);

        const id = await createReview(form as any);

        expect(POST).toHaveBeenCalledWith(
            'reviews',
            form,
            expect.objectContaining({ contentType: 'review-creation' })
        );
        expect(getNewIdFromPostResponse).toHaveBeenCalled();
        expect(id).toBe(42);
    });
});

describe('updateReview', () => {
    it('patches review with correct URL', async () => {
        const form = { rating: 4, comment: 'Updated comment' };

        vi.mocked(PATCH).mockResolvedValue({});

        await updateReview(15, form);

        expect(PATCH).toHaveBeenCalledWith(
            'reviews/15',
            form,
            expect.objectContaining({ contentType: 'review-update' })
        );
    });
});

describe('getAllServiceReviews', () => {
    it('iterates through multiple pages', async () => {
        const review1 = { ratingId: 1, rating: 5 } as unknown as Review;
        const review2 = { ratingId: 2, rating: 4 } as unknown as Review;
        const review3 = { ratingId: 3, rating: 3 } as unknown as Review;

        const page1Result = {
            items: [review1, review2],
            links: { total: 2 }
        } as PagedResult<Review>;

        const page2Result = {
            items: [review3],
            links: { total: 2 }
        } as PagedResult<Review>;

        vi.mocked(GET).mockResolvedValue({});
        vi.mocked(parsePagedResponse)
            .mockReturnValueOnce(page1Result)
            .mockReturnValueOnce(page2Result);

        vi.mocked(isLastPage)
            .mockReturnValueOnce(false)
            .mockReturnValueOnce(true);

        const result = await getAllServiceReviews(123);

        expect(GET).toHaveBeenCalledTimes(2);
        expect(GET).toHaveBeenNthCalledWith(
            1,
            'reviews?serviceId=123&page=1',
            expect.any(Object)
        );
        expect(GET).toHaveBeenNthCalledWith(
            2,
            'reviews?serviceId=123&page=2',
            expect.any(Object)
        );

        expect(result).toEqual([review1, review2, review3]);
    });

    it('returns empty array for no reviews', async () => {
        const emptyResult = {
            items: [],
            links: { total: 1 }
        } as PagedResult<Review>;

        vi.mocked(GET).mockResolvedValue({});
        vi.mocked(parsePagedResponse).mockReturnValue(emptyResult);
        vi.mocked(isLastPage).mockReturnValue(true);

        const result = await getAllServiceReviews(999);

        expect(GET).toHaveBeenCalledTimes(1);
        expect(result).toEqual([]);
    });

    it('stops at last page', async () => {
        const review1 = { ratingId: 1 } as unknown as Review;

        const singlePageResult = {
            items: [review1],
            links: { total: 1 }
        } as PagedResult<Review>;

        vi.mocked(GET).mockResolvedValue({});
        vi.mocked(parsePagedResponse).mockReturnValue(singlePageResult);
        vi.mocked(isLastPage).mockReturnValue(true);

        const result = await getAllServiceReviews(123);

        expect(GET).toHaveBeenCalledTimes(1);
        expect(isLastPage).toHaveBeenCalledWith(1, singlePageResult);
        expect(result).toEqual([review1]);
    });
});