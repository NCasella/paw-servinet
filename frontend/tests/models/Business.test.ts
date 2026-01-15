import { describe, it, expect } from 'vitest';
import { Business } from '../../src/models/Business';
import { createResponse } from '../models/modelUtils';

describe('Business.fromJson', () => {
    const validBusinessData = {
        businessId: 1,
        userId: 100,
        businessName: 'Test Business',
        telephone: '1234567890',
        email: 'test@business.com',
        address: 'Malabia 1020',
        rating: 4.5
    };

    it('returns Business instance for valid JSON', () => {
        const response = createResponse(validBusinessData);
        const result = Business.fromJson(response);

        expect(result).toBeInstanceOf(Business);
        expect(result.businessId).toBe(1);
        expect(result.userId).toBe(100);
        expect(result.businessName).toBe('Test Business');
        expect(result.telephone).toBe('1234567890');
        expect(result.email).toBe('test@business.com');
        expect(result.address).toBe('Malabia 1020');
        expect(result.rating).toBe(4.5);
    });

    it('throws "Invalid Business JSON" on null body', () => {
        const response = createResponse(null);
        expect(() => Business.fromJson(response)).toThrow('Invalid Business JSON');
    });

    it('throws on missing businessId', () => {
        const { businessId, ...data } = validBusinessData;
        const response = createResponse(data);
        expect(() => Business.fromJson(response)).toThrow('Invalid Business JSON');
    });

    it('throws on missing userId', () => {
        const { userId, ...data } = validBusinessData;
        const response = createResponse(data);
        expect(() => Business.fromJson(response)).toThrow('Invalid Business JSON');
    });

    it('throws on missing businessName', () => {
        const { businessName, ...data } = validBusinessData;
        const response = createResponse(data);
        expect(() => Business.fromJson(response)).toThrow('Invalid Business JSON');
    });

    it('throws on missing telephone', () => {
        const { telephone, ...data } = validBusinessData;
        const response = createResponse(data);
        expect(() => Business.fromJson(response)).toThrow('Invalid Business JSON');
    });

    it('throws on missing email', () => {
        const { email, ...data } = validBusinessData;
        const response = createResponse(data);
        expect(() => Business.fromJson(response)).toThrow('Invalid Business JSON');
    });

    it('throws on missing address', () => {
        const { address, ...data } = validBusinessData;
        const response = createResponse(data);
        expect(() => Business.fromJson(response)).toThrow('Invalid Business JSON');
    });

    it('throws on missing rating', () => {
        const { rating, ...data } = validBusinessData;
        const response = createResponse(data);
        expect(() => Business.fromJson(response)).toThrow('Invalid Business JSON');
    });

    it('throws on wrong type for businessId', () => {
        const response = createResponse({ ...validBusinessData, businessId: '1' });
        expect(() => Business.fromJson(response)).toThrow('Invalid Business JSON');
    });
});

describe('Business helper methods', () => {
    const validBusinessData = {
        businessId: 1,
        userId: 100,
        businessName: 'Test Business',
        telephone: '1234567890',
        email: 'test@business.com',
        address: 'Malabia 1020',
        rating: 4.5
    };

    describe('isOwner', () => {
        it('returns true when userId matches business owner', () => {
            const response = createResponse(validBusinessData);
            const business = Business.fromJson(response);

            expect(business.isOwner(100)).toBe(true);
        });

        it('returns false when userId does not match business owner', () => {
            const response = createResponse(validBusinessData);
            const business = Business.fromJson(response);

            expect(business.isOwner(999)).toBe(false);
        });

        it('returns false for userId 0 when owner is different', () => {
            const response = createResponse(validBusinessData);
            const business = Business.fromJson(response);

            expect(business.isOwner(0)).toBe(false);
        });
    });
});
