import * as businessService from '$services/businessService';
import * as serviceService from '$services/serviceService';
import * as reviewsService from '$services/reviewsService';
import { GET, POST, DELETE, PATCH, getNewIdFromPostResponse } from '$utils/apiFetch';
import { parsePagedResponse } from '$models/PagedList';
import type { Review } from '$models/Review';
import type { Service } from '$models/Service';
import { beforeEach, describe, expect, it, vi } from "vitest";
import { getCurrentUser } from "$services/userService";
import {Business} from "$models/Business";

vi.mock('$utils/apiFetch', async (importOriginal) => {
    const actual = await importOriginal<typeof import('$utils/apiFetch')>();
    return {
        ...actual,
        GET: vi.fn(),
        POST: vi.fn(),
        DELETE: vi.fn(),
        PATCH: vi.fn(),
        getNewIdFromPostResponse: vi.fn(),
    };
});

vi.mock('$models/PagedList', async (importOriginal) => {
    const actual = await importOriginal<any>('$models/PagedList');
    return {
        ...actual,
        parsePagedResponse: vi.fn()
    };
});

vi.mock('$services/userService', () => ({
    getCurrentUser: vi.fn()
}));

vi.mock('$services/serviceService', () => ({
    getAllBusinessServices: vi.fn()
}));

vi.mock('$services/reviewsService', () => ({
    getAllServiceReviews: vi.fn()
}));

beforeEach(() => {
    vi.clearAllMocks();
});

describe('businessService', () => {

    it('createBusiness calls POST and returns new id', async () => {
        (POST as any).mockResolvedValue({});
        (getNewIdFromPostResponse as any).mockReturnValue(42);

        const form = {} as any;
        const id = await businessService.createBusiness(form);

        expect(POST).toHaveBeenCalledWith('businesses', form, { contentType: 'business-creation' });
        expect(id).toBe(42);
    });

    it('getUserBusinesses calls GET and parses result', async () => {
        const response = { items: [], page: 1 };
        (GET as any).mockResolvedValue(response);
        (parsePagedResponse as any).mockReturnValue(response);

        const result = await businessService.getUserBusinesses(10, 2);

        expect(GET).toHaveBeenCalledWith('businesses?ownerId=10&page=2', { contentType: 'business-list' });
        expect(parsePagedResponse).toHaveBeenCalledWith(response, Business);
        expect(result).toBe(response);
    });

    it('getCurrentUserBusinesses calls GET with current user', async () => {
        (getCurrentUser as any).mockResolvedValue({ userId: 5 });
        const response = { items: [], page: 1 };
        (GET as any).mockResolvedValue(response);
        (parsePagedResponse as any).mockReturnValue(response);

        const result = await businessService.getCurrentUserBusinesses(1);

        expect(GET).toHaveBeenCalledWith('businesses?ownerId=5&page=1', { contentType: 'business-list' });
        expect(result).toBe(response);
    });

    it('getBusinessById calls GET and returns Business.fromJson', async () => {
        const mockBusiness = { id: 1 };
        (GET as any).mockResolvedValue(mockBusiness);

        vi.spyOn(Business, 'fromJson').mockImplementation((json) => ({
            businessId: json.id
        }));

        const result = await businessService.getBusinessById(1);

        expect(GET).toHaveBeenCalledWith('businesses/1', expect.objectContaining({ contentType: 'business-info' }));
        expect(result.businessId).toBe(1);
    });

    it('deleteBusiness calls DELETE', async () => {
        await businessService.deleteBusiness(3);
        expect(DELETE).toHaveBeenCalledWith('businesses/3');
    });

    it('updateBusiness calls PATCH', async () => {
        const form = {} as any;
        await businessService.updateBusiness(4, form);
        expect(PATCH).toHaveBeenCalledWith('businesses/4', form, { contentType: 'business-update' });
    });

    it('getBusinessReviews fetches all service reviews', async () => {
        (serviceService.getAllBusinessServices as any).mockResolvedValue([1, 2]);

        const review1: Review = { id: 1 } as any;
        const review2: Review = { id: 2 } as any;

        (reviewsService.getAllServiceReviews as any)
            .mockResolvedValueOnce([review1])
            .mockResolvedValueOnce([review2]);

        const result = await businessService.getBusinessReviews(5);

        expect(serviceService.getAllBusinessServices).toHaveBeenCalledWith(5);
        expect(reviewsService.getAllServiceReviews).toHaveBeenCalledTimes(2);
        expect(result).toEqual([review1, review2]);
    });

});

