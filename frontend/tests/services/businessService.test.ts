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
        vi.mocked(POST).mockResolvedValue({});
        vi.mocked(getNewIdFromPostResponse).mockReturnValue(42);

        const form = {} as any;
        const id = await businessService.createBusiness(form);

        expect(POST).toHaveBeenCalledWith('businesses', form, { contentType: 'business-creation' });
        expect(id).toBe(42);
    });

    it('getUserBusinesses calls GET and parses result', async () => {
        const response = { items: [], page: 1 };
        vi.mocked(GET).mockResolvedValue(response);
        vi.mocked(parsePagedResponse).mockReturnValue(response);

        const result = await businessService.getUserBusinesses(10, 2);

        expect(GET).toHaveBeenCalledWith('businesses?ownerId=10&page=2', { contentType: 'business-list' });
        expect(parsePagedResponse).toHaveBeenCalledWith(response, Business);
        expect(result).toBe(response);
    });

    it('getCurrentUserBusinesses calls GET with current user', async () => {
        vi.mocked(getCurrentUser).mockResolvedValue({ userId: 5 });
        const response = { items: [], page: 1 };
        vi.mocked(GET).mockResolvedValue(response);
        vi.mocked(parsePagedResponse).mockReturnValue(response);

        const result = await businessService.getCurrentUserBusinesses(1);

        expect(GET).toHaveBeenCalledWith('businesses?ownerId=5&page=1', { contentType: 'business-list' });
        expect(result).toBe(response);
    });

    it('getBusinessById calls GET and returns Business.fromJson', async () => {
        const mockBusiness = { id: 1 };
        vi.mocked(GET).mockResolvedValue(mockBusiness);

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
        vi.mocked(serviceService.getAllBusinessServices).mockResolvedValue([1, 2]);

        const review1: Review = { id: 1 } as any;
        const review2: Review = { id: 2 } as any;

        vi.mocked(reviewsService.getAllServiceReviews)
            .mockResolvedValueOnce([review1])
            .mockResolvedValueOnce([review2]);

        const result = await businessService.getBusinessReviews(5);

        expect(serviceService.getAllBusinessServices).toHaveBeenCalledWith(5);
        expect(reviewsService.getAllServiceReviews).toHaveBeenCalledTimes(2);
        expect(result).toEqual([review1, review2]);
    });

    it('getServiceBusinesses maps services to businesses', async () => {
        const services = [
            { serviceId: 1, businessId: 10 },
            { serviceId: 2, businessId: 20 },
            { serviceId: 3, businessId: 10 }  // Same business
        ] as Service[];

        const business10 = { businessId: 10, businessName: 'B10' };
        const business20 = { businessId: 20, businessName: 'B20' };

        vi.mocked(GET).mockResolvedValueOnce(business10).mockResolvedValueOnce(business20);
        vi.spyOn(Business, 'fromJson')
            .mockReturnValueOnce(business10 as any)
            .mockReturnValueOnce(business20 as any);

        const result = await businessService.getServiceBusinesses(services);

        expect(result.size).toBe(2);  // Only unique businessIds
        expect(result.get(10)).toEqual(business10);
        expect(result.get(20)).toEqual(business20);
    });

});

