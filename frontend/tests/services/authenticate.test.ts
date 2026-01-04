import { beforeEach, describe, expect, it, vi } from "vitest";
import * as authenticate from '$services/authenticate';

vi.mock('$stores/auth', () => ({
    setTokens: vi.fn(),
    clearTokens: vi.fn(),
    getAccessToken: vi.fn(),
    auth: {}
}));

vi.mock('jwt-decode', () => ({
    jwtDecode: vi.fn()
}));

import { setTokens, clearTokens, getAccessToken } from '$stores/auth';
import { jwtDecode } from 'jwt-decode';

const mockFetch = vi.fn();
global.fetch = mockFetch;

beforeEach(() => {
    vi.clearAllMocks();
});

describe('authenticate', () => {

    describe('loginWithBasicAuth', () => {

        it('stores tokens on successful response', async () => {
            const mockHeaders = new Headers();
            mockHeaders.set('Authorization-Access-Token', 'Bearer access-token-123');
            mockHeaders.set('authorization-refresh-token', 'Bearer refresh-token-456');

            mockFetch.mockResolvedValue({
                ok: true,
                headers: mockHeaders
            });

            const result = await authenticate.loginWithBasicAuth('user@test.com', 'password123');

            expect(result).toBe(true);
            expect(mockFetch).toHaveBeenCalledTimes(1);
            // Verify the fetch call contains the correct method and Basic auth header
            const fetchCall = mockFetch.mock.calls[0];
            expect(fetchCall[1].method).toBe('GET');
            expect(fetchCall[1].headers.Authorization).toContain('Basic ');
            expect(setTokens).toHaveBeenCalledWith({
                accessToken: 'access-token-123',
                refreshToken: 'refresh-token-456'
            });
        });

        it('returns false on failed response', async () => {
            mockFetch.mockResolvedValue({
                ok: false,
                status: 401
            });

            const result = await authenticate.loginWithBasicAuth('user@test.com', 'wrongpassword');

            expect(result).toBe(false);
            expect(setTokens).not.toHaveBeenCalled();
        });

    });

    describe('removeTokens', () => {

        it('calls clearTokens', () => {
            authenticate.removeTokens();

            expect(clearTokens).toHaveBeenCalled();
        });

    });

    describe('extractUserIdFromToken', () => {

        it('decodes JWT and returns id', () => {
            vi.mocked(getAccessToken).mockReturnValue('valid-token');
            vi.mocked(jwtDecode).mockReturnValue({
                sub: 'user@test.com',
                id: 42,
                roles: ['ROLE_USER']
            });

            const result = authenticate.extractUserIdFromToken();

            expect(getAccessToken).toHaveBeenCalled();
            expect(jwtDecode).toHaveBeenCalledWith('valid-token');
            expect(result).toBe(42);
        });

        it('throws error when no token', () => {
            vi.mocked(getAccessToken).mockReturnValue(null);

            expect(() => authenticate.extractUserIdFromToken()).toThrow('Token not found');
        });

    });

    describe('extractUserRolesFromToken', () => {

        it('extracts roles array', () => {
            vi.mocked(getAccessToken).mockReturnValue('valid-token');
            vi.mocked(jwtDecode).mockReturnValue({
                sub: 'user@test.com',
                id: 42,
                roles: ['ROLE_USER', 'ROLE_ADMIN']
            });

            const result = authenticate.extractUserRolesFromToken();

            expect(getAccessToken).toHaveBeenCalled();
            expect(jwtDecode).toHaveBeenCalledWith('valid-token');
            expect(result).toEqual(['ROLE_USER', 'ROLE_ADMIN']);
        });

        it('returns empty array on invalid token', () => {
            vi.mocked(getAccessToken).mockReturnValue('invalid-token');
            vi.mocked(jwtDecode).mockImplementation(() => {
                throw new Error('Invalid token');
            });

            const result = authenticate.extractUserRolesFromToken();

            expect(result).toEqual([]);
        });

    });

    describe('isResettingPassword', () => {

        it('returns true when ROLE_PASSWORD_RESET present', () => {
            vi.mocked(getAccessToken).mockReturnValue('valid-token');
            vi.mocked(jwtDecode).mockReturnValue({
                sub: 'user@test.com',
                id: 42,
                roles: ['ROLE_USER', 'ROLE_PASSWORD_RESET']
            });

            const result = authenticate.isResettingPassword();

            expect(result).toBe(true);
        });

        it('returns false when role not present', () => {
            vi.mocked(getAccessToken).mockReturnValue('valid-token');
            vi.mocked(jwtDecode).mockReturnValue({
                sub: 'user@test.com',
                id: 42,
                roles: ['ROLE_USER']
            });

            const result = authenticate.isResettingPassword();

            expect(result).toBe(false);
        });

    });

});