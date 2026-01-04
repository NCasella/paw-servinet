import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
    uploadImage,
    getImage,
    getProfileImage
} from '$services/imageService';
import { GET, POST, getNewIdFromPostResponse } from '$utils/apiFetch';

vi.mock('$utils/apiFetch', async (importOriginal) => {
    const actual = await importOriginal<typeof import('$utils/apiFetch')>();

    return {
        ...actual,
        GET: vi.fn(),
        POST: vi.fn(),
        getNewIdFromPostResponse: vi.fn(),
    };
});

vi.mock('$app/paths', () => ({
    base: ''
}));

vi.mock('$lib/images/default.jpeg', () => ({
    default: 'mocked-service-fallback.jpeg'
}));

vi.mock('$lib/images/profile_default.png', () => ({
    default: 'mocked-user-fallback.png'
}));

const mockCreateObjectURL = vi.fn();
globalThis.URL.createObjectURL = mockCreateObjectURL;

beforeEach(() => {
    vi.resetModules();
    vi.clearAllMocks();
});

describe('uploadImage', () => {

    it('creates FormData and posts', async () => {
        const mockResponse = { headers: new Headers(), body: {} };
        vi.mocked(POST).mockResolvedValue(mockResponse);
        vi.mocked(getNewIdFromPostResponse).mockReturnValue(42);

        const file = new File(['test'], 'test.png', { type: 'image/png' });

        await uploadImage(file);

        expect(POST).toHaveBeenCalledTimes(1);
        expect(POST).toHaveBeenCalledWith(
            'images',
            expect.any(FormData),
            expect.objectContaining({ genericContentType: 'multipart/form-data' })
        );
    });

    it('returns new image ID from response', async () => {
        const mockResponse = { headers: new Headers(), body: {} };
        vi.mocked(POST).mockResolvedValue(mockResponse);
        vi.mocked(getNewIdFromPostResponse).mockReturnValue(123);

        const file = new File(['test'], 'test.png', { type: 'image/png' });

        const result = await uploadImage(file);

        expect(getNewIdFromPostResponse).toHaveBeenCalledWith(mockResponse);
        expect(result).toBe(123);
    });
});

describe('getImage', () => {

    it('returns fallback for invalid imageId (0 or negative)', async () => {
        const resultZero = await getImage(0);
        const resultNegative = await getImage(-1);

        expect(resultZero).toBe('mocked-service-fallback.jpeg');
        expect(resultNegative).toBe('mocked-service-fallback.jpeg');
        expect(GET).not.toHaveBeenCalled();
    });

    it('returns fallback for null imageId', async () => {
        const result = await getImage(null as any);

        expect(result).toBe('mocked-service-fallback.jpeg');
        expect(GET).not.toHaveBeenCalled();
    });

    it('fetches blob and creates object URL', async () => {
        const mockBlob = new Blob(['image data'], { type: 'image/jpeg' });
        vi.mocked(GET).mockResolvedValue(mockBlob);
        mockCreateObjectURL.mockReturnValue('blob:http://localhost/image-url');

        const result = await getImage(5);

        expect(GET).toHaveBeenCalledWith(
            'images/5',
            expect.objectContaining({
                binary: true,
                genericContentType: 'image/jpeg'
            })
        );
        expect(mockCreateObjectURL).toHaveBeenCalledWith(mockBlob);
        expect(result).toBe('blob:http://localhost/image-url');
    });

    it('returns fallback on fetch error', async () => {
        vi.mocked(GET).mockRejectedValue(new Error('Network error'));

        const result = await getImage(10);

        expect(GET).toHaveBeenCalledTimes(1);
        expect(result).toBe('mocked-service-fallback.jpeg');
    });
});

describe('getProfileImage', () => {

    it('returns user fallback for invalid imageId', async () => {
        const resultZero = await getProfileImage(0);
        const resultNegative = await getProfileImage(-5);

        expect(resultZero).toBe('mocked-user-fallback.png');
        expect(resultNegative).toBe('mocked-user-fallback.png');
        expect(GET).not.toHaveBeenCalled();
    });

    it('fetches blob and creates object URL', async () => {
        const mockBlob = new Blob(['profile image data'], { type: 'image/jpeg' });
        vi.mocked(GET).mockResolvedValue(mockBlob);
        mockCreateObjectURL.mockReturnValue('blob:http://localhost/profile-url');

        const result = await getProfileImage(7);

        expect(GET).toHaveBeenCalledWith(
            'images/7',
            expect.objectContaining({
                binary: true,
                genericContentType: 'image/jpeg'
            })
        );
        expect(mockCreateObjectURL).toHaveBeenCalledWith(mockBlob);
        expect(result).toBe('blob:http://localhost/profile-url');
    });

    it('returns user fallback on fetch error', async () => {
        vi.mocked(GET).mockRejectedValue(new Error('Server error'));

        const result = await getProfileImage(15);

        expect(GET).toHaveBeenCalledTimes(1);
        expect(result).toBe('mocked-user-fallback.png');
    });
});