import * as appointmentService from '$services/appointmentService';
import { GET, POST, PATCH, getNewIdFromPostResponse } from '$utils/apiFetch';
import { parsePagedResponse } from '$models/PagedList';
import { beforeEach, describe, expect, it, vi } from "vitest";
import { getCurrentUser } from "$services/userService";
import { Appointment } from "$models/Appointment";
import { AppointmentStatus, AppointmentView } from "$models/enums/AppointmentStatus";
import { get } from 'node:http';

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
    const actual = await importOriginal<any>('$models/PagedList');
    return {
        ...actual,
        parsePagedResponse: vi.fn()
    };
});

vi.mock('$services/userService', () => ({
    getCurrentUser: vi.fn()
}));

vi.mock('$models/Appointment', () => ({
    Appointment: {
        fromJson: vi.fn()
    }
}));

beforeEach(() => {
    vi.clearAllMocks();
});

describe('appointmentService', () => {

    it('createAppointment gets current user and creates appointment with userId', async () => {
        vi.mocked(getCurrentUser).mockResolvedValue({ userId: 10 });
        vi.mocked(POST).mockResolvedValue({});
        vi.mocked(getNewIdFromPostResponse).mockReturnValue(42);

        const form = { serviceId: 1, startDate: '2026-01-01' } as any;
        await appointmentService.createAppointment(form);

        expect(getCurrentUser).toHaveBeenCalled();
        expect(POST).toHaveBeenCalledWith('appointments', { ...form, userId: 10 }, { contentType: 'appointment-creation' });
    });

    it('createAppointment returns new appointment ID', async () => {
        vi.mocked(getCurrentUser).mockResolvedValue({ userId: 10 });
        vi.mocked(POST).mockResolvedValue({});
        vi.mocked(getNewIdFromPostResponse).mockReturnValue(99);

        const form = {} as any;
        const id = await appointmentService.createAppointment(form);

        expect(getNewIdFromPostResponse).toHaveBeenCalled();
        expect(id).toBe(99);
    });

    it('getAppointmentsPagedList builds correct query with status and view', async () => {
        const response = { items: [], page: 1 };
        vi.mocked(GET).mockResolvedValue(response);
        vi.mocked(parsePagedResponse).mockReturnValue(response);

        await appointmentService.getAppointmentsPagedList(
            5,
            AppointmentStatus.PENDING,
            AppointmentView.BUSINESS,
            2
        );

        expect(GET).toHaveBeenCalledWith(
            'appointments?businessId=5&status=pending&page=2',
            { contentType: 'appointment-list' }
        );
    });

    it('getAppointmentsPagedList parses response correctly', async () => {
        const response = { items: [], page: 1 };
        vi.mocked(GET).mockResolvedValue(response);
        vi.mocked(parsePagedResponse).mockReturnValue(response);

        const result = await appointmentService.getAppointmentsPagedList(
            3,
            AppointmentStatus.CONFIRMED,
            AppointmentView.USER,
            1
        );

        expect(parsePagedResponse).toHaveBeenCalledWith(response, Appointment);
        expect(result).toBe(response);
    });

    it('getAppointmentById fetches and returns Appointment', async () => {
        const mockAppointmentData = { appointmentId: 7 };
        const mockAppointment = { appointmentId: 7 } as any;
        vi.mocked(GET).mockResolvedValue(mockAppointmentData);
        vi.mocked(Appointment.fromJson).mockReturnValue(mockAppointment);

        const result = await appointmentService.getAppointmentById(7);

        expect(GET).toHaveBeenCalledWith('appointments/7', { contentType: 'appointment-info' });
        expect(Appointment.fromJson).toHaveBeenCalledWith(mockAppointmentData);
        expect(result).toBe(mockAppointment);
    });

    it('cancelAppointment patches with CANCELLED status', async () => {
        vi.mocked(PATCH).mockResolvedValue({});

        await appointmentService.cancelAppointment(15);

        expect(PATCH).toHaveBeenCalledWith(
            'appointments/15',
            { status: AppointmentStatus.CANCELLED },
            { contentType: 'appointment-status' }
        );
    });

    it('confirmAppointment patches with CONFIRMED status', async () => {
        vi.mocked(PATCH).mockResolvedValue({});

        await appointmentService.confirmAppointment(20);

        expect(PATCH).toHaveBeenCalledWith(
            'appointments/20',
            { status: AppointmentStatus.CONFIRMED },
            { contentType: 'appointment-status' }
        );
    });

    it('denyAppointment patches with DENIED status', async () => {
        vi.mocked(PATCH).mockResolvedValue({});

        await appointmentService.denyAppointment(25);

        expect(PATCH).toHaveBeenCalledWith(
            'appointments/25',
            { status: AppointmentStatus.DENIED },
            { contentType: 'appointment-status' }
        );
    });

    it('changeAppointmentStatus throws error for FINISHED status', async () => {
        await expect(
            appointmentService.changeAppointmentStatus(1, AppointmentStatus.FINISHED)
        ).rejects.toThrow("Appointment status can't be changed to finished");

        expect(PATCH).not.toHaveBeenCalled();
    });

    describe('getAppointmentsPagedList edge cases', () => {
        it('handles page 0', async () => {
            vi.mocked(GET).mockResolvedValue({});
            vi.mocked(parsePagedResponse).mockReturnValue({ items: [], links: {} } as any);

            await appointmentService.getAppointmentsPagedList(1, AppointmentStatus.PENDING, AppointmentView.USER, 0);

            expect(GET).toHaveBeenCalledWith(
                'appointments?userId=1&status=pending&page=0',
                expect.any(Object)
            );
        });
    });

});