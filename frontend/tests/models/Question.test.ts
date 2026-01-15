import { describe, it, expect } from 'vitest';
import { Question } from '../../src/models/Question';
import { createResponse } from '../models/modelUtils';

describe('Question.fromJson', () => {
    const validQuestionData = {
        questionId: 1,
        serviceId: 10,
        userId: 100,
        question: 'What are your hours?',
        response: 'We are open 9-5',
        date: '2025-01-15',
        links: {
            self: 'http://api/questions/1',
            service: 'http://api/services/10',
            user: 'http://api/users/100'
        }
    };

    it('returns Question instance for valid JSON', () => {
        const response = createResponse(validQuestionData);
        const result = Question.fromJson(response);

        expect(result).toBeInstanceOf(Question);
        expect(result.questionId).toBe(1);
        expect(result.serviceId).toBe(10);
        expect(result.userId).toBe(100);
        expect(result.question).toBe('What are your hours?');
        expect(result.response).toBe('We are open 9-5');
        expect(result.date).toBe('2025-01-15');
    });

    it('throws on null body', () => {
        const response = createResponse(null);
        expect(() => Question.fromJson(response)).toThrow('Invalid Question JSON');
    });

    it('handles nullable response field (null)', () => {
        const dataWithNullResponse = { ...validQuestionData, response: null };
        const response = createResponse(dataWithNullResponse);
        const result = Question.fromJson(response);

        expect(result.response).toBeNull();
    });

    it('handles nullable response field (undefined)', () => {
        const { response: _, ...dataWithoutResponse } = validQuestionData;
        const dataWithUndefinedResponse = { ...dataWithoutResponse, response: undefined };
        const response = createResponse(dataWithUndefinedResponse);
        const result = Question.fromJson(response);

        expect(result.response).toBeUndefined();
    });

    it('handles response field as string', () => {
        const dataWithStringResponse = { ...validQuestionData, response: 'This is a response' };
        const response = createResponse(dataWithStringResponse);
        const result = Question.fromJson(response);

        expect(result.response).toBe('This is a response');
    });

    it('throws on missing questionId', () => {
        const { questionId, ...data } = validQuestionData;
        const response = createResponse(data);
        expect(() => Question.fromJson(response)).toThrow('Invalid Question JSON');
    });

    it('throws on missing question', () => {
        const { question, ...data } = validQuestionData;
        const response = createResponse(data);
        expect(() => Question.fromJson(response)).toThrow('Invalid Question JSON');
    });

    it('throws on missing links', () => {
        const { links, ...data } = validQuestionData;
        const response = createResponse(data);
        expect(() => Question.fromJson(response)).toThrow('Invalid Question JSON');
    });
});
