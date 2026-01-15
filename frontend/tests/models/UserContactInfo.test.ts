import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { UserContactInfo } from '../../src/models/ContactInfo';
import { createResponse } from '../models/modelUtils';

describe('UserContactInfo.fromJson', () => {
    // Mock console.log to prevent output during tests
    let consoleSpy: ReturnType<typeof vi.spyOn>;

    beforeEach(() => {
        consoleSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
    });

    afterEach(() => {
        consoleSpy.mockRestore();
    });

    const validContactInfoData = {
        userId: 1,
        username: 'johndoe',
        fullName: 'John Doe',
        email: 'john@example.com',
        language: 'en',
        telephone: '1234567890',
        links: {
            profilePic: 'http://example.com/pic.jpg'
        }
    };

    it('returns UserContactInfo instance for valid JSON', () => {
        const response = createResponse(validContactInfoData);
        const result = UserContactInfo.fromJson(response);

        expect(result).toBeInstanceOf(UserContactInfo);
        expect(result.userId).toBe(1);
        expect(result.username).toBe('johndoe');
        expect(result.fullName).toBe('John Doe');
        expect(result.email).toBe('john@example.com');
        expect(result.language).toBe('en');
        expect(result.telephone).toBe('1234567890');
        expect(result.profilePicture).toBe('http://example.com/pic.jpg');
    });

    it('throws on null body', () => {
        const response = createResponse(null);
        expect(() => UserContactInfo.fromJson(response)).toThrow('Invalid ContactInfo JSON');
    });

    it('throws on missing username', () => {
        const { username, ...data } = validContactInfoData;
        const response = createResponse(data);
        expect(() => UserContactInfo.fromJson(response)).toThrow('Invalid ContactInfo JSON');
    });

    it('throws on missing fullName', () => {
        const { fullName, ...data } = validContactInfoData;
        const response = createResponse(data);
        expect(() => UserContactInfo.fromJson(response)).toThrow('Invalid ContactInfo JSON');
    });

    it('throws on missing email', () => {
        const { email, ...data } = validContactInfoData;
        const response = createResponse(data);
        expect(() => UserContactInfo.fromJson(response)).toThrow('Invalid ContactInfo JSON');
    });

    it('handles missing profilePic in links (sets to null)', () => {
        const dataWithoutPic = { ...validContactInfoData, links: {} };
        const response = createResponse(dataWithoutPic);
        const result = UserContactInfo.fromJson(response);

        expect(result.profilePicture).toBeNull();
    });

    it('handles missing links object (sets profilePicture to null)', () => {
        const { links, ...dataWithoutLinks } = validContactInfoData;
        const response = createResponse(dataWithoutLinks);
        const result = UserContactInfo.fromJson(response);

        expect(result.profilePicture).toBeNull();
    });
});
