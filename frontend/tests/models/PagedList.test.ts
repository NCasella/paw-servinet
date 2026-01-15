import { describe, it, expect } from 'vitest';
import { parsePagedResponse, isLastPage, type PagedResult } from '../../src/models/PagedList';
import { createResponse } from '../models/modelUtils';

describe('PagedList', () => {
    // Simple test class that implements FromJsonStatic
    class TestItem {
        id: number;
        name: string;

        constructor(data: { id: number; name: string }) {
            this.id = data.id;
            this.name = data.name;
        }

        static fromJson(response: { body: any }): TestItem {
            return new TestItem(response.body);
        }
    }

    describe('parsePagedResponse', () => {
        it('transforms items correctly', () => {
            const headers = new Headers();
            headers.set('Link', '<http://api/items?page=2>; rel="next", <5>; rel="total"');

            const response = createResponse([
                { id: 1, name: 'Item 1' },
                { id: 2, name: 'Item 2' },
                { id: 3, name: 'Item 3' }
            ], headers);

            const result = parsePagedResponse(response, TestItem);

            expect(result.items).toHaveLength(3);
            expect(result.items[0]).toBeInstanceOf(TestItem);
            expect(result.items[0].id).toBe(1);
            expect(result.items[0].name).toBe('Item 1');
            expect(result.items[1].id).toBe(2);
            expect(result.items[2].id).toBe(3);
        });

        it('handles empty array response', () => {
            const headers = new Headers();
            headers.set('Link', '<1>; rel="total"');

            const response = createResponse([], headers);

            const result = parsePagedResponse(response, TestItem);

            expect(result.items).toHaveLength(0);
            expect(result.items).toEqual([]);
        });

        it('handles non-array response (coerces to empty)', () => {
            const headers = new Headers();

            const response = createResponse({ notAnArray: true }, headers);

            const result = parsePagedResponse(response, TestItem);

            expect(result.items).toHaveLength(0);
            expect(result.items).toEqual([]);
        });

        it('handles null body (coerces to empty)', () => {
            const headers = new Headers();

            const response = createResponse(null, headers);

            const result = parsePagedResponse(response, TestItem);

            expect(result.items).toHaveLength(0);
        });

        it('parses pagination links from header', () => {
            const headers = new Headers();
            headers.set('Link', '<http://api/items?page=2>; rel="next", <http://api/items?page=1>; rel="prev", <http://api/items?page=1>; rel="first", <http://api/items?page=5>; rel="last", <5>; rel="total"');

            const response = createResponse([{ id: 1, name: 'Item' }], headers);

            const result = parsePagedResponse(response, TestItem);

            expect(result.links.total).toBe(5);
            expect(result.links.next).toBe(2);
            expect(result.links.prev).toBe(1);
            expect(result.links.first).toBe(1);
            expect(result.links.last).toBe(5);
        });

        it('handles missing Link header', () => {
            const headers = new Headers();

            const response = createResponse([{ id: 1, name: 'Item' }], headers);

            const result = parsePagedResponse(response, TestItem);

            expect(result.links).toEqual({});
        });
    });

    describe('isLastPage', () => {
        it('returns true when pageNum >= total', () => {
            const pagedResult: PagedResult<TestItem> = {
                items: [],
                links: { total: 5 }
            };

            expect(isLastPage(5, pagedResult)).toBe(true);
            expect(isLastPage(6, pagedResult)).toBe(true);
        });

        it('returns false when pageNum < total', () => {
            const pagedResult: PagedResult<TestItem> = {
                items: [],
                links: { total: 5 }
            };

            expect(isLastPage(1, pagedResult)).toBe(false);
            expect(isLastPage(4, pagedResult)).toBe(false);
        });

        it('defaults total to 1 when not provided', () => {
            const pagedResult: PagedResult<TestItem> = {
                items: [],
                links: {}
            };

            expect(isLastPage(1, pagedResult)).toBe(true);
            expect(isLastPage(0, pagedResult)).toBe(false);
        });
    });
});
