import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { Business } from '../../src/models/Business';
import { User } from '../../src/models/User';
import { Appointment } from '../../src/models/Appointment';
import { Review } from '../../src/models/Review';
import { Question } from '../../src/models/Question';
import { Service } from '../../src/models/Service';
import { UserContactInfo } from '../../src/models/ContactInfo';
import { parsePagedResponse, isLastPage, type PagedResult } from '../../src/models/PagedList';
import { AppointmentStatus } from '../../src/models/enums/AppointmentStatus';

// Helper to create TResponse object
function createResponse(body: any, headers: Headers = new Headers()): { body: any; headers: Headers } {
    return { body, headers };
}

// ============================================================
// 1. Business Tests
// ============================================================
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

// ============================================================
// 1b. Business Helper Methods Tests
// ============================================================
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

// ============================================================
// 2. User Tests
// ============================================================
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

// ============================================================
// 2b. User Helper Methods Tests
// ============================================================
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

// ============================================================
// 3. Appointment Tests
// ============================================================
describe('Appointment.fromJson', () => {
    const validAppointmentData = {
        appointmentId: 1,
        startDate: '2025-01-15T10:00:00Z',
        endDate: '2025-01-15T11:00:00Z',
        address: '456 Service St',
        description: 'Test appointment',
        status: AppointmentStatus.CONFIRMED,
        serviceId: 10,
        userId: 100
    };

    it('returns Appointment instance for valid JSON', () => {
        const response = createResponse(validAppointmentData);
        const result = Appointment.fromJson(response);

        expect(result).toBeInstanceOf(Appointment);
        expect(result.appointmentId).toBe(1);
        expect(result.startDate).toBe('2025-01-15T10:00:00Z');
        expect(result.endDate).toBe('2025-01-15T11:00:00Z');
        expect(result.address).toBe('456 Service St');
        expect(result.description).toBe('Test appointment');
        expect(result.status).toBe(AppointmentStatus.CONFIRMED);
        expect(result.serviceId).toBe(10);
        expect(result.userId).toBe(100);
    });

    it('throws "Invalid Appointment JSON" on null body', () => {
        const response = createResponse(null);
        expect(() => Appointment.fromJson(response)).toThrow('Invalid Appointment JSON');
    });

    it('handles nullable description field (null)', () => {
        const dataWithNullDesc = { ...validAppointmentData, description: null };
        const response = createResponse(dataWithNullDesc);
        const result = Appointment.fromJson(response);

        expect(result.description).toBeNull();
    });

    it('throws on missing appointmentId', () => {
        const { appointmentId, ...data } = validAppointmentData;
        const response = createResponse(data);
        expect(() => Appointment.fromJson(response)).toThrow('Invalid Appointment JSON');
    });

    it('throws on missing startDate', () => {
        const { startDate, ...data } = validAppointmentData;
        const response = createResponse(data);
        expect(() => Appointment.fromJson(response)).toThrow('Invalid Appointment JSON');
    });

    it('throws on missing endDate', () => {
        const { endDate, ...data } = validAppointmentData;
        const response = createResponse(data);
        expect(() => Appointment.fromJson(response)).toThrow('Invalid Appointment JSON');
    });

    it('throws on missing address', () => {
        const { address, ...data } = validAppointmentData;
        const response = createResponse(data);
        expect(() => Appointment.fromJson(response)).toThrow('Invalid Appointment JSON');
    });

    it('throws on missing status', () => {
        const { status, ...data } = validAppointmentData;
        const response = createResponse(data);
        expect(() => Appointment.fromJson(response)).toThrow('Invalid Appointment JSON');
    });

    it('throws on missing serviceId', () => {
        const { serviceId, ...data } = validAppointmentData;
        const response = createResponse(data);
        expect(() => Appointment.fromJson(response)).toThrow('Invalid Appointment JSON');
    });

    it('throws on missing userId', () => {
        const { userId, ...data } = validAppointmentData;
        const response = createResponse(data);
        expect(() => Appointment.fromJson(response)).toThrow('Invalid Appointment JSON');
    });
});

// ============================================================
// 3b. Appointment Helper Methods Tests
// ============================================================
describe('Appointment helper methods', () => {
    describe('hasFinished', () => {
        it('returns true when startDate is in the past', () => {
            const pastDate = new Date(Date.now() - 1000 * 60 * 60).toISOString(); // 1 hour ago
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: pastDate,
                endDate: pastDate,
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.hasFinished()).toBe(true);
        });

        it('returns false when startDate is in the future', () => {
            const futureDate = new Date(Date.now() + 1000 * 60 * 60).toISOString(); // 1 hour from now
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: futureDate,
                endDate: futureDate,
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.hasFinished()).toBe(false);
        });
    });

    describe('isConfirmed', () => {
        it('returns true when status is CONFIRMED', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T10:00:00Z',
                endDate: '2025-01-15T11:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.isConfirmed()).toBe(true);
        });

        it('returns false when status is PENDING', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T10:00:00Z',
                endDate: '2025-01-15T11:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.PENDING,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.isConfirmed()).toBe(false);
        });

        it('returns false when status is DENIED', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T10:00:00Z',
                endDate: '2025-01-15T11:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.DENIED,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.isConfirmed()).toBe(false);
        });

        it('returns false when status is CANCELLED', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T10:00:00Z',
                endDate: '2025-01-15T11:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CANCELLED,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.isConfirmed()).toBe(false);
        });
    });

    describe('hasDuration', () => {
        it('returns true when startDate and endDate differ', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T10:00:00Z',
                endDate: '2025-01-15T11:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.hasDuration()).toBe(true);
        });

        it('returns false when startDate and endDate are the same', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T10:00:00Z',
                endDate: '2025-01-15T10:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.hasDuration()).toBe(false);
        });
    });

    describe('getStartDateString', () => {
        it('formats date without year', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-12-20T10:00:00Z',
                endDate: '2025-12-20T11:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            const result = appointment.getStartDateString();
            // Should contain weekday, month and day but no year (format: "Sat December 20")
            expect(result).toMatch(/\w+ \w+ \d+/);
            expect(result).not.toContain('2025');
        });
    });

    describe('getStartDateWithYearString', () => {
        it('formats date with year', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-12-20T10:00:00Z',
                endDate: '2025-12-20T11:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            const result = appointment.getStartDateWithYearString();
            // Should contain year
            expect(result).toContain('2025');
        });
    });

    describe('getStartDateTimeString', () => {
        it('formats start time in 24h format', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T14:30:00Z',
                endDate: '2025-01-15T15:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            const result = appointment.getStartDateTimeString();
            // Should be in HH:MM format (24h)
            expect(result).toMatch(/^\d{1,2}:\d{2}$/);
        });
    });

    describe('getEndDateTimeString', () => {
        it('formats end time in 24h format', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T14:30:00Z',
                endDate: '2025-01-15T15:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            const result = appointment.getEndDateTimeString();
            // Should be in HH:MM format (24h)
            expect(result).toMatch(/^\d{1,2}:\d{2}$/);
        });

        it('returns different time from start time when duration exists', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T14:30:00Z',
                endDate: '2025-01-15T16:30:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            const startTime = appointment.getStartDateTimeString();
            const endTime = appointment.getEndDateTimeString();
            expect(startTime).not.toBe(endTime);
        });
    });

    describe('formatedDate (legacy)', () => {
        it('returns same result as getStartDateString', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T10:00:00Z',
                endDate: '2025-01-15T11:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.formatedDate()).toBe(appointment.getStartDateString());
        });
    });

    describe('formatedTime (legacy)', () => {
        it('returns same result as getStartDateTimeString', () => {
            const appointment = new Appointment({
                appointmentId: 1,
                startDate: '2025-01-15T10:00:00Z',
                endDate: '2025-01-15T11:00:00Z',
                address: 'Test',
                description: null,
                status: AppointmentStatus.CONFIRMED,
                serviceId: 1,
                userId: 1
            });

            expect(appointment.formatedTime()).toBe(appointment.getStartDateTimeString());
        });
    });
});

// ============================================================
// 4. Review Tests
// ============================================================
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

// ============================================================
// 5. Question Tests
// ============================================================
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

// ============================================================
// 6. Service Tests
// ============================================================
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

// ============================================================
// 7. UserContactInfo Tests
// ============================================================
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

// ============================================================
// 8. PagedList Tests
// ============================================================
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
