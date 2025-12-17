import { describe, it, expect, vi } from 'vitest';
import { Service } from '$models/Service';
import { getServices } from '$services/serviceService';
import { GET } from '$utils/apiFetch';
import { parsePagedResponse } from '$models/PagedList';
import type { PagedResult } from '$models/PagedList';

vi.mock('$utils/apiFetch', () => ({
    GET: vi.fn()
}));

vi.mock('$models/PagedList', async () => {
    const actual = await vi.importActual<any>('$models/PagedList');
    return {
        ...actual,
        parsePagedResponse: vi.fn()
    };
});

vi.spyOn(Service, 'fromJson').mockImplementation((obj: any) => obj as Service);


describe('getServices', () => {

    it('should build query params and return parsed services', async () => {

        const mockedResponse = {
            items: [{ serviceId: 1, serviceName: 'Corte' }],
            page: 1,
            totalPages: 1
        };

        const parsedResult: PagedResult<Service> = {
            items: [{ serviceId: 1, serviceName: 'Corte' } as Service],
            page: 1,
            totalPages: 1
        };

        (GET as any).mockResolvedValue(mockedResponse);
        (parsePagedResponse as any).mockReturnValue(parsedResult);

        const result = await getServices({
            businessId: 2,
            rating: 5,
            orderFilters: 'rate_desc',
            page: 1
        });

        expect(GET).toHaveBeenCalledTimes(1);

        expect(GET).toHaveBeenCalledWith(
            'services?businessId=2&rating=5&orderFilters=rate_desc&page=1',
            expect.objectContaining({
                contentType: 'service-list'
            })
        );

        expect(parsePagedResponse).toHaveBeenCalledWith(mockedResponse, Service);
        expect(result).toEqual(parsedResult);
    });
});
