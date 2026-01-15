import { describe, it, expect } from 'vitest';
import { Service } from '../../src/models/Service';
import { createResponse } from '../models/modelUtils';

describe('Service.fromJson', () => {
    const validServiceData = {
        additionalCosts: false,
        address: 'Corrientes 2300',
        businessId: 5,
        category: 'LIMPIEZA',
        description: 'Professional cleaning service',
        duration: 60,
        homeService: true,
        links: {
            business: 'http://api/businesses/5',
            questions: 'http://api/services/1/questions',
            reviews: 'http://api/services/1/reviews',
            self: 'http://api/services/1',
            image: 'http://api/images/1'
        },
        neighbourhoods: ['Caballito', 'Palermo'],
        price: '50.00',
        pricingType: 'PER_HOUR',
        rating: 4.8,
        serviceId: 1,
        serviceName: 'Deep Clean',
        imageId: 1
    };

    it('returns Service instance for valid JSON', () => {
        const response = createResponse(validServiceData);
        const result = Service.fromJson(response);

        expect(result).toBeInstanceOf(Service);
        expect(result.serviceId).toBe(1);
        expect(result.serviceName).toBe('Deep Clean');
        expect(result.businessId).toBe(5);
        expect(result.category).toBe('LIMPIEZA');
        expect(result.description).toBe('Professional cleaning service');
        expect(result.duration).toBe(60);
        expect(result.homeService).toBe(true);
        expect(result.address).toBe('Corrientes 2300');
        expect(result.price).toBe('50.00');
        expect(result.rating).toBe(4.8);
        expect(result.neighbourhoods).toEqual(['Caballito', 'Palermo']);
    });

    it('throws on null body', () => {
        const response = createResponse(null);
        expect(() => Service.fromJson(response)).toThrow('Invalid Service JSON');
    });

    it('validates nested links object - throws on missing links', () => {
        const { links, ...data } = validServiceData;
        const response = createResponse(data);
        expect(() => Service.fromJson(response)).toThrow('Invalid Service JSON');
    });

    it('validates nested links object - throws on missing links.business', () => {
        const data = { ...validServiceData, links: { questions: 'q', reviews: 'r', self: 's' } };
        const response = createResponse(data);
        expect(() => Service.fromJson(response)).toThrow('Invalid Service JSON');
    });

    it('validates nested links object - throws on missing links.questions', () => {
        const data = { ...validServiceData, links: { business: 'b', reviews: 'r', self: 's' } };
        const response = createResponse(data);
        expect(() => Service.fromJson(response)).toThrow('Invalid Service JSON');
    });

    it('validates nested links object - throws on missing links.reviews', () => {
        const data = { ...validServiceData, links: { business: 'b', questions: 'q', self: 's' } };
        const response = createResponse(data);
        expect(() => Service.fromJson(response)).toThrow('Invalid Service JSON');
    });

    it('validates nested links object - throws on missing links.self', () => {
        const data = { ...validServiceData, links: { business: 'b', questions: 'q', reviews: 'r' } };
        const response = createResponse(data);
        expect(() => Service.fromJson(response)).toThrow('Invalid Service JSON');
    });

    it('handles nullable address field (null)', () => {
        const dataWithNullAddress = { ...validServiceData, address: null };
        const response = createResponse(dataWithNullAddress);
        const result = Service.fromJson(response);

        expect(result.address).toBeNull();
    });

    it('handles nullable description field (null)', () => {
        const dataWithNullDesc = { ...validServiceData, description: null };
        const response = createResponse(dataWithNullDesc);
        const result = Service.fromJson(response);

        expect(result.description).toBeNull();
    });

    it('handles nullable price field (null)', () => {
        const dataWithNullPrice = { ...validServiceData, price: null };
        const response = createResponse(dataWithNullPrice);
        const result = Service.fromJson(response);

        expect(result.price).toBeNull();
    });

    it('handles undefined price field (defaults to null)', () => {
        const { price, ...dataWithoutPrice } = validServiceData;
        const response = createResponse(dataWithoutPrice);
        const result = Service.fromJson(response);

        expect(result.price).toBeNull();
    });

    it('handles optional image link', () => {
        const { image, ...linksWithoutImage } = validServiceData.links;
        const dataWithoutImageLink = { ...validServiceData, links: linksWithoutImage };
        const response = createResponse(dataWithoutImageLink);
        const result = Service.fromJson(response);

        expect(result.links.image).toBeUndefined();
    });
});
