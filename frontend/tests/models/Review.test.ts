import { describe, it, expect } from 'vitest';
import { Review } from '../../src/models/Review';
import { createResponse } from '../models/modelUtils';

describe('Review.fromJson', () => {
    const validReviewData = {
        ratingId: 1,
        serviceId: 10,
        userId: 100,
        rating: 5,
        comment: 'Great service!',
        date: '2025-01-15',
        links: {
            user: 'http://api/users/100',
            service: 'http://api/services/10',
            self: 'http://api/reviews/1'
        }
    };

    it('returns Review instance for valid JSON', () => {
        const response = createResponse(validReviewData);
        const result = Review.fromJson(response);

        expect(result).toBeInstanceOf(Review);
        expect(result.ratingId).toBe(1);
        expect(result.serviceId).toBe(10);
        expect(result.userId).toBe(100);
        expect(result.rating).toBe(5);
        expect(result.comment).toBe('Great service!');
        expect(result.date).toBe('2025-01-15');
        expect(result.links.user).toBe('http://api/users/100');
        expect(result.links.service).toBe('http://api/services/10');
        expect(result.links.self).toBe('http://api/reviews/1');
    });

    it('throws on null body', () => {
        const response = createResponse(null);
        expect(() => Review.fromJson(response)).toThrow('Invalid Review JSON');
    });

    it('throws on missing links object', () => {
        const { links, ...data } = validReviewData;
        const response = createResponse(data);
        expect(() => Review.fromJson(response)).toThrow('Invalid Review JSON');
    });

    it('throws on missing links.user', () => {
        const data = { ...validReviewData, links: { service: 'http://api/services/10', self: 'http://api/reviews/1' } };
        const response = createResponse(data);
        expect(() => Review.fromJson(response)).toThrow('Invalid Review JSON');
    });

    it('throws on missing links.service', () => {
        const data = { ...validReviewData, links: { user: 'http://api/users/100', self: 'http://api/reviews/1' } };
        const response = createResponse(data);
        expect(() => Review.fromJson(response)).toThrow('Invalid Review JSON');
    });

    it('throws on missing links.self', () => {
        const data = { ...validReviewData, links: { user: 'http://api/users/100', service: 'http://api/services/10' } };
        const response = createResponse(data);
        expect(() => Review.fromJson(response)).toThrow('Invalid Review JSON');
    });

    it('throws on missing ratingId', () => {
        const { ratingId, ...data } = validReviewData;
        const response = createResponse(data);
        expect(() => Review.fromJson(response)).toThrow('Invalid Review JSON');
    });

    it('throws on missing rating', () => {
        const { rating, ...data } = validReviewData;
        const response = createResponse(data);
        expect(() => Review.fromJson(response)).toThrow('Invalid Review JSON');
    });

    it('throws on missing comment', () => {
        const { comment, ...data } = validReviewData;
        const response = createResponse(data);
        expect(() => Review.fromJson(response)).toThrow('Invalid Review JSON');
    });
});
