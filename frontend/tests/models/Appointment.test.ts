import { describe, it, expect } from 'vitest';
import { Appointment } from '../../src/models/Appointment';
import { AppointmentStatus } from '../../src/models/enums/AppointmentStatus';
import { createResponse } from '../models/modelUtils';

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
