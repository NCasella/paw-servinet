import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
    getServiceQuestions,
    getRespondentQuestions,
    createQuestion,
    respondQuestion
} from '$services/questionService';
import { GET, POST, PATCH, getNewIdFromPostResponse } from '$utils/apiFetch';
import { parsePagedResponse } from '$models/PagedList';
import type { PagedResult } from '$models/PagedList';
import { Question } from '$models/Question';
import { getCurrentUser } from '$services/userService';
import type { QuestionResponseForm } from '$models/forms/QuestionResponseForm';
import type { QuestionForm } from '$models/forms/QuestionCreationForm';

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

vi.mock('$models/PagedList', async (importOriginal) => {
    const actual = await importOriginal<any>();
    return {
        ...actual,
        parsePagedResponse: vi.fn()
    };
});

vi.mock('$services/userService', () => ({
    getCurrentUser: vi.fn()
}));

beforeEach(() => {
    vi.clearAllMocks();
});

describe('questionService', () => {

    describe('getServiceQuestions', () => {

        it('fetches questions with correct query parameters', async () => {
            const backendResponse = {
                items: [{ questionId: 1, question: 'Test question' }],
                page: 1,
            };
            const parsedResult = {
                items: backendResponse.items as unknown as Question[],
                page: 1,
            } as PagedResult<Question>;

            vi.mocked(GET).mockResolvedValue(backendResponse);
            vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

            const result = await getServiceQuestions(5, 2);

            expect(GET).toHaveBeenCalledTimes(1);
            expect(GET).toHaveBeenCalledWith(
                'questions?serviceId=5&page=2',
                { contentType: 'question-list' }
            );
            expect(result).toEqual(parsedResult);
        });

        it('calls parsePagedResponse with Question class', async () => {
            const backendResponse = {
                items: [],
                page: 1,
            };
            const parsedResult = { items: [], page: 1 } as PagedResult<Question>;

            vi.mocked(GET).mockResolvedValue(backendResponse);
            vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

            await getServiceQuestions(1, 1);

            expect(parsePagedResponse).toHaveBeenCalledWith(backendResponse, Question);
        });

    });

    describe('getRespondentQuestions', () => {

        it('gets current user and builds query with respondentId', async () => {
            const mockUser = { userId: 10 };
            const backendResponse = {
                items: [{ questionId: 1 }],
                page: 1,
            };
            const parsedResult = {
                items: backendResponse.items as unknown as Question[],
                page: 1,
            } as PagedResult<Question>;

            vi.mocked(getCurrentUser).mockResolvedValue(mockUser);
            vi.mocked(GET).mockResolvedValue(backendResponse);
            vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

            const result = await getRespondentQuestions(3);

            expect(getCurrentUser).toHaveBeenCalledTimes(1);
            expect(GET).toHaveBeenCalledWith(
                'questions?respondentId=10&page=3',
                { contentType: 'question-list' }
            );
            expect(parsePagedResponse).toHaveBeenCalledWith(backendResponse, Question);
            expect(result).toEqual(parsedResult);
        });

        it('handles getCurrentUser error gracefully', async () => {
            const error = new Error('User not found');
            const backendResponse = { items: [], page: 1 };
            const parsedResult = { items: [], page: 1 } as PagedResult<Question>;

            vi.mocked(getCurrentUser).mockRejectedValue(error);
            vi.mocked(GET).mockResolvedValue(backendResponse);
            vi.mocked(parsePagedResponse).mockReturnValue(parsedResult);

            const result = await getRespondentQuestions(1);

            expect(getCurrentUser).toHaveBeenCalledTimes(1);
            // The catch returns the error itself, which becomes the 'user' object
            expect(GET).toHaveBeenCalledWith(
                'questions?respondentId=undefined&page=1',
                { contentType: 'question-list' }
            );
            expect(result).toEqual(parsedResult);
        });

    });

    describe('createQuestion', () => {

        it('posts question and returns new ID', async () => {
            const form: QuestionForm = {
                serviceId: 5,
                question: 'What are your hours?'
            } as QuestionForm;

            vi.mocked(POST).mockResolvedValue({});
            vi.mocked(getNewIdFromPostResponse).mockReturnValue(42);

            const id = await createQuestion(form);

            expect(POST).toHaveBeenCalledWith(
                'questions',
                form,
                { contentType: 'question-creation' }
            );
            expect(getNewIdFromPostResponse).toHaveBeenCalled();
            expect(id).toBe(42);
        });

    });

    describe('respondQuestion', () => {

        it('patches question with response form', async () => {
            const form: QuestionResponseForm = {
                response: 'We are open 9-5'
            } as QuestionResponseForm;

            vi.mocked(PATCH).mockResolvedValue({});

            await respondQuestion(7, form);

            expect(PATCH).toHaveBeenCalledWith(
                'questions/7',
                form,
                { contentType: 'question-response' }
            );
        });

    });

});
