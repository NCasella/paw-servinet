import { describe, it, expect, vi, beforeEach } from 'vitest';

// Mock $stores/auth
vi.mock('$stores/auth', () => ({
    getAccessToken: vi.fn(),
    getRefreshToken: vi.fn(),
    setTokens: vi.fn()
}));

// Mock $services/authenticate
vi.mock('$services/authenticate', () => ({
    removeTokens: vi.fn()
}));

// Mock $app/paths
vi.mock('$app/paths', () => ({
    base: ''
}));

import {
    apiFetch,
    GET,
    POST,
    PATCH,
    PUT,
    DELETE,
    getNewIdFromPostResponse,
    FetchError,
    isFetchError,
    API_BASE_URL
} from '$utils/apiFetch';
import { getAccessToken, getRefreshToken, setTokens } from '$stores/auth';
import { removeTokens } from '$services/authenticate';

// Helper to create mock Response
function createMockResponse(options: {
    status?: number;
    body?: any;
    headers?: Record<string, string>;
}): Response {
    const { status = 200, body = {}, headers = {} } = options;
    const headersObj = new Headers(headers);
    return {
        ok: status >= 200 && status < 300,
        status,
        headers: headersObj,
        text: vi.fn().mockResolvedValue(JSON.stringify(body)),
        json: vi.fn().mockResolvedValue(body),
        blob: vi.fn().mockResolvedValue(new Blob(['test']))
    } as unknown as Response;
}

// Mock window.location
const mockLocation = { href: '' };
Object.defineProperty(globalThis, 'window', {
    value: { location: mockLocation },
    writable: true
});

beforeEach(() => {
    vi.clearAllMocks();
    mockLocation.href = '';
});

describe('apiFetch', () => {

    describe('GET', () => {
        it('makes GET request with correct URL', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ body: { data: 'test' } }));
            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue(null);

            await GET('/users/1', { withAuth: false, fetchFn: mockFetch });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.stringContaining('/users/1'),
                expect.objectContaining({
                    method: 'GET'
                })
            );
        });
    });

    describe('POST', () => {
        it('sends JSON body with content type header', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ body: { id: 1 } }));
            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue(null);

            const body = { name: 'Test', email: 'test@test.com' };
            await POST('/users', body, { withAuth: false, fetchFn: mockFetch });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.stringContaining('/users'),
                expect.objectContaining({
                    method: 'POST',
                    body: JSON.stringify(body),
                    headers: expect.objectContaining({
                        'Content-Type': 'application/json'
                    })
                })
            );
        });
    });

    describe('PATCH', () => {
        it('sends partial update', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ body: {} }));
            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue(null);

            const body = { name: 'Updated Name' };
            await PATCH('/users/1', body, { withAuth: false, fetchFn: mockFetch });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.stringContaining('/users/1'),
                expect.objectContaining({
                    method: 'PATCH',
                    body: JSON.stringify(body)
                })
            );
        });
    });

    describe('PUT', () => {
        it('sends PUT request with body', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ body: {} }));
            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue(null);

            const body = { name: 'Full Update', email: 'new@test.com' };
            await PUT('/users/1', body, { withAuth: false, fetchFn: mockFetch });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.stringContaining('/users/1'),
                expect.objectContaining({
                    method: 'PUT',
                    body: JSON.stringify(body)
                })
            );
        });
    });

    describe('DELETE', () => {
        it('makes DELETE request', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ status: 204 }));
            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue(null);

            await DELETE('/users/1', { withAuth: false, fetchFn: mockFetch });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.stringContaining('/users/1'),
                expect.objectContaining({
                    method: 'DELETE'
                })
            );
        });
    });

    describe('apiFetch authentication', () => {
        it('adds Bearer token when withAuth=true', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ body: {} }));
            vi.mocked(getAccessToken).mockReturnValue('test-access-token');
            vi.mocked(getRefreshToken).mockReturnValue('test-refresh-token');

            await apiFetch('/users/1', { withAuth: true, fetchFn: mockFetch });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.any(String),
                expect.objectContaining({
                    headers: expect.objectContaining({
                        'Authorization': 'Bearer test-access-token'
                    })
                })
            );
        });

        it('uses refresh token when access token is null', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ body: {} }));
            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue('test-refresh-token');

            await apiFetch('/users/1', { withAuth: true, fetchFn: mockFetch });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.any(String),
                expect.objectContaining({
                    headers: expect.objectContaining({
                        'Authorization-Refresh-Token': 'Bearer test-refresh-token'
                    })
                })
            );
        });

        it('retries with refresh token on 401', async () => {
            const unauthorizedResponse = createMockResponse({ status: 401 });
            const successResponse = createMockResponse({ body: { data: 'success' } });
            const mockFetch = vi.fn()
                .mockResolvedValueOnce(unauthorizedResponse)
                .mockResolvedValueOnce(successResponse);

            vi.mocked(getAccessToken).mockReturnValue('expired-access-token');
            vi.mocked(getRefreshToken).mockReturnValue('valid-refresh-token');

            await apiFetch('/users/1', { withAuth: true, fetchFn: mockFetch });

            expect(mockFetch).toHaveBeenCalledTimes(2);
            expect(setTokens).toHaveBeenCalledWith({ accessToken: null });
            expect(mockFetch).toHaveBeenLastCalledWith(
                expect.any(String),
                expect.objectContaining({
                    headers: expect.objectContaining({
                        'Authorization-Refresh-Token': 'Bearer valid-refresh-token'
                    })
                })
            );
        });

        it('updates stored token from response header', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({
                body: {},
                headers: { 'Authorization': 'Bearer new-access-token' }
            }));
            vi.mocked(getAccessToken).mockReturnValue('old-access-token');
            vi.mocked(getRefreshToken).mockReturnValue(null);

            await apiFetch('/users/1', { withAuth: true, fetchFn: mockFetch });

            expect(setTokens).toHaveBeenCalledWith({ accessToken: 'new-access-token' });
        });

        it('redirects to login on 401 when using refresh token', async () => {
            const unauthorizedResponse = createMockResponse({ status: 401 });
            const mockFetch = vi.fn().mockResolvedValue(unauthorizedResponse);

            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue('invalid-refresh-token');

            await expect(apiFetch('/users/1', { withAuth: true, fetchFn: mockFetch }))
                .rejects.toThrow('Unauthorized');

            expect(removeTokens).toHaveBeenCalled();
            expect(mockLocation.href).toBe('/login');
        });

        it('redirects to login on double 401 (access token fails, then refresh token fails)', async () => {
            const unauthorizedResponse = createMockResponse({ status: 401 });
            const mockFetch = vi.fn()
                .mockResolvedValueOnce(unauthorizedResponse)  // First call with access token fails
                .mockResolvedValueOnce(unauthorizedResponse); // Retry with refresh token also fails

            vi.mocked(getAccessToken).mockReturnValue('expired-access-token');
            vi.mocked(getRefreshToken).mockReturnValue('also-expired-refresh-token');

            await expect(apiFetch('/users/1', { withAuth: true, fetchFn: mockFetch }))
                .rejects.toThrow();

            expect(mockFetch).toHaveBeenCalledTimes(2);
            expect(removeTokens).toHaveBeenCalled();
            expect(mockLocation.href).toBe('/login');
        });
    });

    describe('apiFetch body handling', () => {
        it('handles FormData without JSON serialization', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ body: {} }));
            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue(null);

            const formData = new FormData();
            formData.append('file', new Blob(['test']), 'test.txt');

            await apiFetch('/upload', {
                method: 'POST',
                body: formData,
                withAuth: false,
                fetchFn: mockFetch
            });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.any(String),
                expect.objectContaining({
                    body: formData
                })
            );
            // Should not have Content-Type header set (browser sets it for FormData)
            const callHeaders = mockFetch.mock.calls[0][1].headers;
            expect(callHeaders['Content-Type']).toBeUndefined();
        });

        it('returns blob for binary=true', async () => {
            const testBlob = new Blob(['binary content']);
            const mockResponse = {
                ok: true,
                status: 200,
                headers: new Headers(),
                blob: vi.fn().mockResolvedValue(testBlob)
            } as unknown as Response;
            const mockFetch = vi.fn().mockResolvedValue(mockResponse);

            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue(null);

            const result = await apiFetch('/image.png', {
                binary: true,
                withAuth: false,
                fetchFn: mockFetch
            });

            expect(mockResponse.blob).toHaveBeenCalled();
            expect(result).toBe(testBlob);
        });
    });

    describe('apiFetch content type', () => {
        it('uses vendor-specific content type format', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ body: {} }));
            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue(null);

            await apiFetch('/users', {
                method: 'POST',
                body: { name: 'test' },
                contentType: 'user-registration',
                withAuth: false,
                fetchFn: mockFetch
            });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.any(String),
                expect.objectContaining({
                    headers: expect.objectContaining({
                        'Content-Type': 'application/vnd.servinet.user-registration.v1+json'
                    })
                })
            );
        });

        it('uses Accept header for GET requests with contentType', async () => {
            const mockFetch = vi.fn().mockResolvedValue(createMockResponse({ body: {} }));
            vi.mocked(getAccessToken).mockReturnValue(null);
            vi.mocked(getRefreshToken).mockReturnValue(null);

            await GET('/users/1', {
                contentType: 'user-info',
                withAuth: false,
                fetchFn: mockFetch
            });

            expect(mockFetch).toHaveBeenCalledWith(
                expect.any(String),
                expect.objectContaining({
                    headers: expect.objectContaining({
                        'Accept': 'application/vnd.servinet.user-info.v1+json'
                    })
                })
            );
        });
    });

});

describe('getNewIdFromPostResponse', () => {
    it('extracts ID from Location header', () => {
        const mockHeaders = new Headers({
            'Location': 'http://localhost:8080/api/users/42'
        });
        const response = { headers: mockHeaders, body: '' };

        const id = getNewIdFromPostResponse(response);

        expect(id).toBe(42);
    });

    it('throws when Location header missing', () => {
        const mockHeaders = new Headers();
        const response = { headers: mockHeaders, body: '' };

        expect(() => getNewIdFromPostResponse(response)).toThrow('id not found from POST');
    });
});

describe('FetchError', () => {
    it('has status property', () => {
        const error = new FetchError(404, 'Not found');

        expect(error.status).toBe(404);
        expect(error.message).toBe('Not found');
        expect(error).toBeInstanceOf(Error);
    });
});

describe('isFetchError', () => {
    it('returns true for FetchError', () => {
        const error = new FetchError(500, 'Server error');

        expect(isFetchError(error)).toBe(true);
    });

    it('returns false for regular Error', () => {
        const error = new Error('Regular error');

        expect(isFetchError(error)).toBe(false);
    });

    it('returns false for null', () => {
        expect(isFetchError(null)).toBe(false);
    });

    it('returns false for undefined', () => {
        expect(isFetchError(undefined)).toBe(false);
    });

    it('returns true for object with status property', () => {
        const errorLike = { status: 400, message: 'Bad request' };

        expect(isFetchError(errorLike)).toBe(true);
    });
});

describe('apiFetch error handling', () => {
    it('handles malformed JSON response gracefully', async () => {
        const mockFetch = vi.fn().mockResolvedValue({
            ok: true,
            status: 200,
            headers: new Headers(),
            text: () => Promise.resolve('not valid json {'),
        });
        vi.mocked(getAccessToken).mockReturnValue('token');
        vi.mocked(getRefreshToken).mockReturnValue(null);

        const result = await GET('test', { fetchFn: mockFetch });

        expect(result.body).toBeNull();  // JSON.parse failed, returns null
    });
});
