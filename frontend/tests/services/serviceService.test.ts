import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
    createService,
    deleteService,
    getServices,
    updateService
} from '$services/serviceService';
import {GET, POST, DELETE, PATCH, getNewIdFromPostResponse} from '$utils/apiFetch';
import {isLastPage, parsePagedResponse} from '$models/PagedList';
import type { PagedResult } from '$models/PagedList';
import type { Service } from '$models/Service';
import {uploadImage} from "$services/imageService";

vi.mock('$utils/apiFetch', async (importOriginal) => {
    const actual = await importOriginal<typeof import('$utils/apiFetch')>();

    return {
        ...actual,
        GET: vi.fn(),
        POST: vi.fn(),
        DELETE: vi.fn(),
        PATCH: vi.fn(),
    };
});


vi.mock('$models/PagedList', async () => {
    const actual = await vi.importActual<any>('$models/PagedList');
    return {
        ...actual,
        parsePagedResponse: vi.fn()
    };
});

vi.mock('$services/imageService', () => ({
    uploadImage: vi.fn()
}));

vi.mock('$utils/postResponseUtils', () => ({
    getNewIdFromPostResponse: vi.fn()
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

        (GET as any).mockResolvedValue(backendResponse);
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
            expect.objectContaining({ contentType: 'service-list' })
        );

        expect(parsePagedResponse).toHaveBeenCalledWith(backendResponse, expect.any(Function));
        expect(result).toEqual(parsedResult);
    });

    it('builds correct query and returns parsed result when only page is provided', async () => {
        const parsedResult = { items: [], page: 2 } as PagedResult<Service>;

        (GET as any).mockResolvedValue({});
        (parsePagedResponse as any).mockReturnValue(parsedResult);

        const result = await getServices({ page: 2 });

        expect(GET).toHaveBeenCalledWith(
            'services?page=2',
            expect.any(Object)
        );

        expect(result).toEqual(parsedResult);
    });


    it('propagates backend errors (400 / 500)', async () => {
        const error = new Error('Bad Request');
        (GET as any).mockRejectedValue(error);

        await expect(
            getServices({ rating: 999, page: 1 })
        ).rejects.toThrow('Bad Request');

        expect(GET).toHaveBeenCalledOnce();
    });

    it('does not include undefined or null filters in query', async () => {
        (GET as any).mockResolvedValue({
            items: [],
            page: 1,
        });

        (parsePagedResponse as any).mockReturnValue({
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
        (POST as any).mockResolvedValue({});
        vi.spyOn(
            await import('$utils/apiFetch'),
            'getNewIdFromPostResponse'
        ).mockReturnValue(99);

        const id = await createService({} as any, null);

        expect(POST).toHaveBeenCalled();
        expect(id).toBe(99);
    });

    it('createService with image uploads image first', async () => {
        (uploadImage as any).mockResolvedValue(5);
        (POST as any).mockResolvedValue({});
        (getNewIdFromPostResponse as any).mockReturnValue(10);

        const form: any = {};
        const file = new File([], 'img.png');

        const id = await createService(form, file);

        expect(uploadImage).toHaveBeenCalledWith(file);
        expect(form.imageId).toBe(5);
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
