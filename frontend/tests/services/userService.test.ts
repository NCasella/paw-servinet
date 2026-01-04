import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
    getUserInfo,
    createUser,
    getCurrentUser,
    getUserContactInfo,
    requestPasswordRecovery,
    resetPassword,
    updateUser,
    editUserProfilePic,
    getPatchFormWithModifiedFields,
    closeSession,
    currentUserIsProvider,
    getAppointmentClients
} from '$services/userService';
import { GET, POST, PATCH, getNewIdFromPostResponse } from '$utils/apiFetch';
import { getUser, login, logout } from '$stores/userStore';
import { extractUserIdFromToken, extractUserRolesFromToken, removeTokens } from '$services/authenticate';
import { uploadImage } from '$services/imageService';
import { setLanguage, resetLanguage } from '$lib/i18n/i18n';
import { User } from '$models/User';
import { UserContactInfo } from '$models/ContactInfo';
import { UserUpdateForm } from '$models/forms/UserUpdateForm';
import type { Appointment } from '$models/Appointment';

vi.mock('$utils/apiFetch', async (importOriginal) => {
    const actual = await importOriginal<typeof import('$utils/apiFetch')>();
    return {
        ...actual,
        GET: vi.fn(),
        POST: vi.fn(),
        PATCH: vi.fn(),
        getNewIdFromPostResponse: vi.fn(),
    };
});

vi.mock('$stores/userStore', () => ({
    getUser: vi.fn(),
    login: vi.fn(),
    logout: vi.fn(),
    user: { subscribe: vi.fn() }
}));

vi.mock('$services/authenticate', () => ({
    extractUserIdFromToken: vi.fn(),
    extractUserRolesFromToken: vi.fn(),
    removeTokens: vi.fn(),
    loginWithBasicAuth: vi.fn()
}));

vi.mock('$services/imageService', () => ({
    uploadImage: vi.fn()
}));

vi.mock('$lib/i18n/i18n', () => ({
    setLanguage: vi.fn(),
    resetLanguage: vi.fn()
}));

vi.mock('$models/User', async (importOriginal) => {
    const actual = await importOriginal<any>();
    return {
        ...actual,
        User: {
            ...actual.User,
            fromJson: vi.fn()
        }
    };
});

vi.mock('$models/ContactInfo', async (importOriginal) => {
    const actual = await importOriginal<any>();
    return {
        ...actual,
        UserContactInfo: {
            ...actual.UserContactInfo,
            fromJson: vi.fn()
        }
    };
});

beforeEach(() => {
    vi.clearAllMocks();
});

describe('userService', () => {

    describe('getUserInfo', () => {
        it('fetches and returns User', async () => {
            const mockResponse = { body: { userId: 1, fullName: 'Test User', username: 'testuser', email: 'test@test.com', language: 'en' } };
            const mockUser = { userId: 1, fullName: 'Test User', username: 'testuser', setRole: vi.fn() };

            vi.mocked(GET).mockResolvedValue(mockResponse);
            vi.mocked(User.fromJson).mockReturnValue(mockUser);
            vi.mocked(extractUserRolesFromToken).mockReturnValue([]);

            const result = await getUserInfo(1);

            expect(GET).toHaveBeenCalledWith('users/1', { contentType: 'user-info', fetchFn: undefined });
            expect(User.fromJson).toHaveBeenCalledWith(mockResponse);
            expect(mockUser.setRole).toHaveBeenCalledWith(false);
            expect(result).toBe(mockUser);
        });
    });

    describe('createUser', () => {
        it('posts and returns new ID', async () => {
            const mockForm = { email: 'test@test.com', password: 'password123' } as any;
            const mockResponse = {};

            vi.mocked(POST).mockResolvedValue(mockResponse);
            vi.mocked(getNewIdFromPostResponse).mockReturnValue(42);

            const result = await createUser(mockForm);

            expect(POST).toHaveBeenCalledWith('users', mockForm, { contentType: 'user-registration' });
            expect(getNewIdFromPostResponse).toHaveBeenCalledWith(mockResponse);
            expect(result).toBe(42);
        });
    });

    describe('getCurrentUser', () => {
        it('returns cached user from store', async () => {
            const cachedUser = { userId: 5, fullName: 'Cached User' };
            vi.mocked(getUser).mockReturnValue(cachedUser);

            const result = await getCurrentUser();

            expect(getUser).toHaveBeenCalled();
            expect(GET).not.toHaveBeenCalled();
            expect(result).toBe(cachedUser);
        });

        it('fetches from API when not cached', async () => {
            const mockResponse = { body: { userId: 10, fullName: 'API User', username: 'apiuser', email: 'api@test.com', language: 'es' } };
            const mockUser = { userId: 10, fullName: 'API User', language: 'es', setRole: vi.fn() };

            vi.mocked(getUser).mockReturnValue(null);
            vi.mocked(extractUserIdFromToken).mockReturnValue(10);
            vi.mocked(GET).mockResolvedValue(mockResponse);
            vi.mocked(User.fromJson).mockReturnValue(mockUser);
            vi.mocked(extractUserRolesFromToken).mockReturnValue([]);

            const result = await getCurrentUser();

            expect(getUser).toHaveBeenCalled();
            expect(extractUserIdFromToken).toHaveBeenCalled();
            expect(GET).toHaveBeenCalledWith('users/10', { contentType: 'user-info', fetchFn: undefined });
            expect(login).toHaveBeenCalledWith(mockUser);
            expect(setLanguage).toHaveBeenCalledWith('es');
            expect(result).toBe(mockUser);
        });

        it('throws error when no token is present', async () => {
            vi.mocked(getUser).mockReturnValue(null);
            vi.mocked(extractUserIdFromToken).mockReturnValue(null); 

            await expect(getCurrentUser()).rejects.toThrow("Current user not found: auth is missing");
        });
    });

    describe('getUserContactInfo', () => {
        it('fetches contact info', async () => {
            const mockResponse = { body: { userId: 1, username: 'testuser', fullName: 'Test User', email: 'test@test.com', language: 'en', telephone: '+1234567890' } };
            const mockContactInfo = { userId: 1, username: 'testuser', fullName: 'Test User', email: 'test@test.com' };

            vi.mocked(GET).mockResolvedValue(mockResponse);
            vi.mocked(UserContactInfo.fromJson).mockReturnValue(mockContactInfo);

            const result = await getUserContactInfo(1);

            expect(GET).toHaveBeenCalledWith('users/1', { contentType: 'user-contact-info' });
            expect(UserContactInfo.fromJson).toHaveBeenCalledWith(mockResponse);
            expect(result).toBe(mockContactInfo);
        });
    });

    describe('requestPasswordRecovery', () => {
        it('sends POST request', async () => {
            const mockForm = { email: 'test@test.com' };
            const mockResponse = { ok: true };

            vi.mocked(POST).mockResolvedValue(mockResponse);

            const result = await requestPasswordRecovery(mockForm);

            expect(POST).toHaveBeenCalledWith('users', mockForm, { contentType: 'user-password-recovery-request' });
            expect(result).toBe(mockResponse);
        });
    });

    describe('resetPassword', () => {
        it('sends PATCH with basic auth header', async () => {
            const mockForm = { email: 'test@test.com', code: '123456', password: 'newpassword123' };
            const userId = '42';
            const expectedBasic = btoa(`${mockForm.email}:${mockForm.code}`);

            vi.mocked(PATCH).mockResolvedValue(true);

            const result = await resetPassword(mockForm, userId);

            expect(PATCH).toHaveBeenCalledWith(
                'users/42',
                expect.objectContaining({ password: 'newpassword123' }),
                {
                    contentType: 'user-update',
                    headers: {
                        Authorization: `Basic ${expectedBasic}`
                    }
                }
            );
            expect(result).toBe(true);
        });
    });

    describe('updateUser', () => {
        it('sends PATCH request', async () => {
            const mockForm = new UserUpdateForm({ username: 'newuser', email: 'new@test.com', telephone: '+1234567890', locale: 'en' });

            vi.mocked(PATCH).mockResolvedValue({});

            await updateUser(5, mockForm);

            expect(PATCH).toHaveBeenCalledWith('users/5', mockForm, { contentType: 'user-update' });
        });
    });

    describe('editUserProfilePic', () => {
        it('uploads image then patches user', async () => {
            const mockFile = new File([''], 'profile.png', { type: 'image/png' });
            const uploadedImageId = 99;

            vi.mocked(uploadImage).mockResolvedValue(uploadedImageId);
            vi.mocked(PATCH).mockResolvedValue({});

            await editUserProfilePic(7, mockFile);

            expect(uploadImage).toHaveBeenCalledWith(mockFile);
            expect(PATCH).toHaveBeenCalledWith('users/7', { profilePicId: 99 }, { contentType: 'user-update' });
        });
    });

    describe('getPatchFormWithModifiedFields', () => {
        it('returns only changed fields', () => {
            const originalInfo = {
                userId: 1,
                username: 'olduser',
                fullName: 'Old Name',
                email: 'old@test.com',
                language: 'en',
                telephone: '+1111111111',
                profilePicture: null
            } as unknown as UserContactInfo;

            const updateForm = new UserUpdateForm({
                username: 'newuser',
                email: 'old@test.com',
                telephone: '+2222222222',
                locale: 'en',
                password: ''
            });

            const result = getPatchFormWithModifiedFields(originalInfo, updateForm);

            expect(result.username).toBe('newuser');
            expect(result.email).toBe('');
            expect(result.telephone).toBe('+2222222222');
            expect(result.locale).toBe('');
        });

        it('returns empty form when no fields changed', () => {
            const contactInfo = { username: 'user1', email: 'a@b.com', telephone: '123', language: 'en' } as unknown as UserContactInfo;
            const form = new UserUpdateForm({ username: 'user1', email: 'a@b.com', telephone: '123', locale: 'en', password: '' });

            const result = getPatchFormWithModifiedFields(contactInfo, form);

            expect(result.username).toBe('');
            expect(result.email).toBe('');
        });

        it('returns all fields when all changed', () => {
            const contactInfo = { username: 'old', email: 'old@b.com', telephone: '000', language: 'es' } as unknown as UserContactInfo;
            const form = new UserUpdateForm({ username: 'new', email: 'new@b.com', telephone: '999', locale: 'en', password: '' });

            const result = getPatchFormWithModifiedFields(contactInfo, form);

            expect(result.username).toBe('new');
            expect(result.email).toBe('new@b.com');
            expect(result.telephone).toBe('999');
            expect(result.locale).toBe('en');
        });
    });

    describe('closeSession', () => {
        it('calls logout, resetLanguage, removeTokens', () => {
            closeSession();

            expect(logout).toHaveBeenCalled();
            expect(resetLanguage).toHaveBeenCalled();
            expect(removeTokens).toHaveBeenCalled();
        });
    });

    describe('currentUserIsProvider', () => {
        it('returns true for ROLE_BUSINESS', () => {
            vi.mocked(extractUserRolesFromToken).mockReturnValue(['ROLE_USER', 'ROLE_BUSINESS']);

            const result = currentUserIsProvider();

            expect(extractUserRolesFromToken).toHaveBeenCalled();
            expect(result).toBe(true);
        });

        it('returns false for regular user', () => {
            vi.mocked(extractUserRolesFromToken).mockReturnValue(['ROLE_USER']);

            const result = currentUserIsProvider();

            expect(extractUserRolesFromToken).toHaveBeenCalled();
            expect(result).toBe(false);
        });
    });

    describe('getAppointmentClients', () => {
        it('maps userIds to contact info', async () => {
            const appointments: Partial<Appointment>[] = [
                { appointmentId: 1, userId: 10 },
                { appointmentId: 2, userId: 20 },
                { appointmentId: 3, userId: 10 }
            ];

            const contactInfo1 = {
                userId: 10,
                username: 'user10',
                fullName: 'User Ten',
                email: 'user10@test.com',
                toContactInfo: () => ({ username: 'user10', fullName: 'User Ten', email: 'user10@test.com' })
            };
            const contactInfo2 = {
                userId: 20,
                username: 'user20',
                fullName: 'User Twenty',
                email: 'user20@test.com',
                toContactInfo: () => ({ username: 'user20', fullName: 'User Twenty', email: 'user20@test.com' })
            };

            vi.mocked(GET)
                .mockResolvedValueOnce({ body: { userId: 10, username: 'user10', fullName: 'User Ten', email: 'user10@test.com' } })
                .mockResolvedValueOnce({ body: { userId: 20, username: 'user20', fullName: 'User Twenty', email: 'user20@test.com' } });

            vi.mocked(UserContactInfo.fromJson)
                .mockReturnValueOnce(contactInfo1)
                .mockReturnValueOnce(contactInfo2);

            const result = await getAppointmentClients(appointments as Appointment[]);

            expect(GET).toHaveBeenCalledTimes(2);
            expect(GET).toHaveBeenCalledWith('users/10', { contentType: 'user-contact-info' });
            expect(GET).toHaveBeenCalledWith('users/20', { contentType: 'user-contact-info' });

            expect(result).toBeInstanceOf(Map);
            expect(result.size).toBe(2);
            expect(result.get(10)).toEqual({ username: 'user10', fullName: 'User Ten', email: 'user10@test.com' });
            expect(result.get(20)).toEqual({ username: 'user20', fullName: 'User Twenty', email: 'user20@test.com' });
        });
    });

});