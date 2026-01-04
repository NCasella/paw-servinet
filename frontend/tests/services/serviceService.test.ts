import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
    createService,
    deleteService,
    getServices,
    getServiceById,
    getAppointmentServices,
    getAllBusinessServices,
    updateService
} from '$services/serviceService';
import {GET, POST, DELETE, PATCH, getNewIdFromPostResponse} from '$utils/apiFetch';
import {isLastPage, parsePagedResponse} from '$models/PagedList';
import type { PagedResult } from '$models/PagedList';
import { Service } from '$models/Service';
import {uploadImage} from "$services/imageService";
import type { Appointment } from '$models/Appointment';

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


vi.mock('$models/PagedList', async () => {
    const actual = await vi.importActual<any>('$models/PagedList');
    return {
        ...actual,
        parsePagedResponse: vi.fn(),
        isLastPage: vi.fn()
    };
});

vi.mock('$services/imageService', () => ({
    uploadImage: vi.fn()
}));


beforeEach(() => {
    vi.resetModules();
    vi.clearAllMocks();
});

describe('getServices', () => {

    it('builds full query params and returns parsed services', async () => {
        const backendResponse = {
            items: [{ serviceId: 1, serviceName: 'Corte' }],
            page: 1,
        };

        const parsedResult = {
            items: backendResponse.items as Service[],
            page: 1,
        } as unknown as PagedResult<Service>;

        vi.mocked(GET).mockResolvedValue(backendResponse);
        vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

        const result = await getServices({
            businessId: 2,
            rating: 5,
            orderFilters: 'rate_desc',
            page: 1
        });

        expect(GET).toHaveBeenCalledTimes(1);
        expect(GET).toHaveBeenCalledWith(
            'services?businessId=2&rating=5&orderFilters=rate_desc&page=1',
            expect.objectContaining({ contentType: 'service-list' })
        );

        expect(parsePagedResponse).toHaveBeenCalledWith(backendResponse, expect.any(Function));
        expect(result).toEqual(parsedResult);
    });

    it('builds correct query and returns parsed result when only page is provided', async () => {
        const parsedResult = { items: [], page: 2 } as PagedResult<Service>;

        vi.mocked(GET).mockResolvedValue({});
        vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

        const result = await getServices({ page: 2 });

        expect(GET).toHaveBeenCalledWith(
            'services?page=2',
            expect.any(Object)
        );

        expect(result).toEqual(parsedResult);
    });


    it('propagates backend errors (400 / 500)', async () => {
        const error = new Error('Bad Request');
        vi.mocked(GET).mockRejectedValue(error);

        await expect(
            getServices({ rating: 999, page: 1 })
        ).rejects.toThrow('Bad Request');

        expect(GET).toHaveBeenCalledOnce();
    });

    it('does not include undefined or null filters in query', async () => {
        vi.mocked(GET).mockResolvedValue({
            items: [],
            page: 1,
        });

        vi.mocked(parsePagedResponse).mockReturnValue({
            items: [],
            page: 1,
        });

        await getServices({
            businessId: undefined,
            rating: null as any,
            page: 1
        });

        expect(GET).toHaveBeenCalledWith(
            'services?page=1',
            expect.any(Object)
        );
    });


    it('createService without image posts service', async () => {
        vi.mocked(POST).mockResolvedValue({});
        vi.mocked(getNewIdFromPostResponse).mockReturnValue(99);

        const id = await createService({} as any, null);

        expect(POST).toHaveBeenCalled();
        expect(id).toBe(99);
    });

    it('createService with image uploads image first', async () => {
        vi.mocked(uploadImage).mockResolvedValue(5);
        vi.mocked(POST).mockResolvedValue({});
        vi.mocked(getNewIdFromPostResponse).mockReturnValue(10);

        const form: any = { serviceName: 'Test Service' };
        const file = new File([], 'img.png');

        const id = await createService(form, file);

        expect(uploadImage).toHaveBeenCalledWith(file);
        // Verify POST was called with payload containing the uploaded imageId
        expect(POST).toHaveBeenCalledWith(
            'services',
            expect.objectContaining({ serviceName: 'Test Service', imageId: 5 }),
            expect.objectContaining({ contentType: 'service-creation' })
        );
        // Verify form was NOT mutated (immutable pattern)
        expect(form.imageId).toBeUndefined();
        expect(id).toBe(10);
    });

    it('deleteService calls DELETE with correct url', async () => {
        await deleteService(7);
        expect(DELETE).toHaveBeenCalledWith('services/7');
    });

    it('updateService calls PATCH', async () => {
        await updateService(3, {} as any);

        expect(PATCH).toHaveBeenCalledWith(
            'services/3',
            {},
            expect.objectContaining({ contentType: 'service-update' })
        );
    });
});

describe('getServiceById', () => {
    it('calls GET and returns Service from response', async () => {
        const mockServiceData = {
            body: {
                serviceId: 42,
                serviceName: 'Test Service',
                additionalCosts: false,
                address: 'Corrientes 2400',
                businessId: 1,
                category: 'PELUQUERIA',
                description: 'A test service',
                duration: 60,
                homeService: false,
                links: {
                    business: '/businesses/1',
                    questions: '/services/42/questions',
                    reviews: '/services/42/reviews',
                    self: '/services/42'
                },
                neighbourhoods: ['Caballito'],
                price: '50.00',
                pricingType: 'PER_TOTAL',
                rating: 4.5,
                imageId: 1
            }
        };

        vi.mocked(GET).mockResolvedValue(mockServiceData);

        const result = await getServiceById(42);

        expect(GET).toHaveBeenCalledWith(
            'services/42',
            expect.objectContaining({ contentType: 'service-info' })
        );
        expect(result).toBeInstanceOf(Service);
        expect(result.serviceId).toBe(42);
        expect(result.serviceName).toBe('Test Service');
    });
});

describe('getAppointmentServices', () => {
    it('fetches services for unique service IDs and returns a Map', async () => {
        const mockAppointments = [
            { serviceId: 1 } as Appointment,
            { serviceId: 2 } as Appointment,
            { serviceId: 1 } as Appointment // duplicate
        ];

        const mockService1Data = {
            body: {
                serviceId: 1,
                serviceName: 'Service 1',
                additionalCosts: false,
                address: null,
                businessId: 1,
                category: 'PELUQUERIA',
                description: null,
                duration: 30,
                homeService: false,
                links: {
                    business: '/businesses/1',
                    questions: '/services/1/questions',
                    reviews: '/services/1/reviews',
                    self: '/services/1'
                },
                neighbourhoods: [],
                price: '20.00',
                pricingType: 'PER_TOTAL',
                rating: 4,
                imageId: 1
            }
        };

        const mockService2Data = {
            body: {
                serviceId: 2,
                serviceName: 'Service 2',
                additionalCosts: false,
                address: null,
                businessId: 1,
                category: 'BELLEZA',
                description: null,
                duration: 45,
                homeService: false,
                links: {
                    business: '/businesses/1',
                    questions: '/services/2/questions',
                    reviews: '/services/2/reviews',
                    self: '/services/2'
                },
                neighbourhoods: [],
                price: '30.00',
                pricingType: 'PER_TOTAL',
                rating: 5,
                imageId: 2
            }
        };

        vi.mocked(GET)
            .mockResolvedValueOnce(mockService1Data)
            .mockResolvedValueOnce(mockService2Data);

        const result = await getAppointmentServices(mockAppointments);

        // Should only call GET twice (once per unique service ID)
        expect(GET).toHaveBeenCalledTimes(2);
        expect(result).toBeInstanceOf(Map);
        expect(result.size).toBe(2);
        expect(result.get(1)?.serviceName).toBe('Service 1');
        expect(result.get(2)?.serviceName).toBe('Service 2');
    });
});

describe('getAllBusinessServices', () => {
    it('fetches all pages and returns all service IDs', async () => {
        const page1Result = {
            items: [{ serviceId: 1 }, { serviceId: 2 }] as Service[],
            page: 1,
            totalPages: 2
        } as PagedResult<Service>;

        const page2Result = {
            items: [{ serviceId: 3 }] as Service[],
            page: 2,
            totalPages: 2
        } as PagedResult<Service>;

        vi.mocked(GET).mockResolvedValue({});
        vi.mocked(parsePagedResponse)
            .mockReturnValueOnce(page1Result)
            .mockReturnValueOnce(page2Result);
        vi.mocked(isLastPage)
            .mockReturnValueOnce(false) // first page is not last
            .mockReturnValueOnce(true);  // second page is last

        const result = await getAllBusinessServices(5);

        expect(GET).toHaveBeenCalledTimes(2);
        expect(GET).toHaveBeenNthCalledWith(
            1,
            'services?businessId=5&page=1',
            expect.any(Object)
        );
        expect(GET).toHaveBeenNthCalledWith(
            2,
            'services?businessId=5&page=2',
            expect.any(Object)
        );
        expect(isLastPage).toHaveBeenCalledTimes(2);
        expect(result).toEqual([1, 2, 3]);
    });

    it('handles single page result', async () => {
        const singlePageResult = {
            items: [{ serviceId: 10 }] as Service[],
            page: 1,
            totalPages: 1
        } as PagedResult<Service>;

        vi.mocked(GET).mockResolvedValue({});
        vi.mocked(parsePagedResponse).mockReturnValue(singlePageResult);
        vi.mocked(isLastPage).mockReturnValue(true);

        const result = await getAllBusinessServices(3);

        expect(GET).toHaveBeenCalledTimes(1);
        expect(result).toEqual([10]);
    });
});
