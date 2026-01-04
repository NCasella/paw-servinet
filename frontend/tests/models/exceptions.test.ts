import { describe, it, expect } from 'vitest';
import { InvalidUrlParamError } from '../../src/models/exceptions/InvalidUrlParamError';

describe('InvalidUrlParamError', () => {
    it('creates instance with correct message', () => {
        const error = new InvalidUrlParamError('Invalid ID');
        expect(error.message).toBe('Invalid ID');
    });

    it('has name property set to InvalidUrlParamError', () => {
        const error = new InvalidUrlParamError('test');
        expect(error.name).toBe('InvalidUrlParamError');
    });

    it('is instance of Error', () => {
        const error = new InvalidUrlParamError('test');
        expect(error).toBeInstanceOf(Error);
    });

    it('can be caught as Error', () => {
        let caughtError: Error | null = null;
        try {
            throw new InvalidUrlParamError('caught error');
        } catch (e) {
            if (e instanceof Error) {
                caughtError = e;
            }
        }
        expect(caughtError).not.toBeNull();
        expect(caughtError?.message).toBe('caught error');
    });

    it('prototype chain is correct (instanceof works)', () => {
        const error = new InvalidUrlParamError('prototype test');
        expect(error instanceof InvalidUrlParamError).toBe(true);
        expect(error instanceof Error).toBe(true);
        expect(Object.getPrototypeOf(error)).toBe(InvalidUrlParamError.prototype);
    });
});
