import { describe, it, expect } from 'vitest';
import { User } from '../../src/models/User';
import { createResponse } from '../models/modelUtils';

describe('User.fromJson', () => {
    const validUserData = {
        userId: 1,
        fullName: 'John Doe',
        username: 'johndoe',
        email: 'john@example.com',
        language: 'en',
        isProvider: false,
        links: {
            profilePic: 'http://example.com/pic.jpg'
        }
    };

    it('returns User instance for valid JSON', () => {
        const response = createResponse(validUserData);
        const result = User.fromJson(response);

        expect(result).toBeInstanceOf(User);
        expect(result.userId).toBe(1);
        expect(result.fullName).toBe('John Doe');
        expect(result.username).toBe('johndoe');
        expect(result.email).toBe('john@example.com');
        expect(result.language).toBe('en');
        expect(result.profilePicture).toBe('http://example.com/pic.jpg');
    });

    it('throws "Invalid User JSON" on null body', () => {
        const response = createResponse(null);
        expect(() => User.fromJson(response)).toThrow('Invalid User JSON');
    });

    it('throws on missing userId', () => {
        const { userId, ...data } = validUserData;
        const response = createResponse(data);
        expect(() => User.fromJson(response)).toThrow('Invalid User JSON');
    });

    it('throws on missing fullName', () => {
        const { fullName, ...data } = validUserData;
        const response = createResponse(data);
        expect(() => User.fromJson(response)).toThrow('Invalid User JSON');
    });

    it('throws on missing username', () => {
        const { username, ...data } = validUserData;
        const response = createResponse(data);
        expect(() => User.fromJson(response)).toThrow('Invalid User JSON');
    });

    it('throws on missing language', () => {
        const { language, ...data } = validUserData;
        const response = createResponse(data);
        expect(() => User.fromJson(response)).toThrow('Invalid User JSON');
    });

    it('handles missing profilePic in links (sets to null)', () => {
        const dataWithoutPic = { ...validUserData, links: {} };
        const response = createResponse(dataWithoutPic);
        const result = User.fromJson(response);

        expect(result.profilePicture).toBeNull();
    });

    it('handles missing links object (sets profilePicture to null)', () => {
        const { links, ...dataWithoutLinks } = validUserData;
        const response = createResponse(dataWithoutLinks);
        const result = User.fromJson(response);

        expect(result.profilePicture).toBeNull();
    });
});

describe('User helper methods', () => {
    const validUserData = {
        userId: 1,
        fullName: 'John Doe',
        username: 'johndoe',
        email: 'john@example.com',
        language: 'en',
        isProvider: false,
        links: {
            profilePic: 'http://example.com/pic.jpg'
        }
    };

    describe('getProfilePicture', () => {
        it('returns profilePicture when it exists', () => {
            const response = createResponse(validUserData);
            const user = User.fromJson(response);

            expect(user.getProfilePicture()).toBe('http://example.com/pic.jpg');
        });

        it('returns default URL when profilePicture is null', () => {
            const dataWithoutPic = { ...validUserData, links: {} };
            const response = createResponse(dataWithoutPic);
            const user = User.fromJson(response);

            expect(user.getProfilePicture()).toBe(
                'https://t4.ftcdn.net/jpg/02/15/84/43/360_F_215844325_ttX9YiIIyeaR7Ne6EaLLjMAmy4GvPC69.jpg'
            );
        });
    });

    describe('setRole', () => {
        it('sets isProvider to true', () => {
            const response = createResponse(validUserData);
            const user = User.fromJson(response);

            expect(user.isProvider).toBe(false);
            user.setRole(true);
            expect(user.isProvider).toBe(true);
        });

        it('sets isProvider to false', () => {
            const response = createResponse(validUserData);
            const user = User.fromJson(response);

            user.setRole(true);
            expect(user.isProvider).toBe(true);
            user.setRole(false);
            expect(user.isProvider).toBe(false);
        });
    });

    describe('getProfilePictureSrc', () => {
        it('returns profilePicture when it exists', () => {
            const response = createResponse(validUserData);
            const user = User.fromJson(response);

            expect(user.getProfilePictureSrc()).toBe('http://example.com/pic.jpg');
        });

        it('returns undefined when profilePicture is null', () => {
            const dataWithoutPic = { ...validUserData, links: {} };
            const response = createResponse(dataWithoutPic);
            const user = User.fromJson(response);

            expect(user.getProfilePictureSrc()).toBeUndefined();
        });
    });

    describe('getFallbackImage', () => {
        it('returns the default user image', () => {
            const fallback = User.getFallbackImage();
            // Should return the imported default image
            expect(fallback).toBeDefined();
        });
    });
});
