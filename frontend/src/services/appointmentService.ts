import { Appointment } from "$models/Appointment";
import {  AppointmentStatus, AppointmentView } from "$models/enums/AppointmentStatus";
import type { AppointmentForm } from "$models/forms/AppointmentCreationForm";
import { parsePagedResponse, type PagedResult } from "$models/PagedList";
import { GET, getNewIdFromPostResponse, PATCH, POST, type TResponse } from "$utils/apiFetch";
import { getCurrentUser } from "./userService";

export async function createAppointment(form: AppointmentForm) :Promise<number> {
    const user = await getCurrentUser()
    const payload = { ...form, userId: user.userId };  // Clone instead of mutate

    const response :TResponse = await POST("appointments", payload, {
        contentType: "appointment-creation"
    })

    return getNewIdFromPostResponse(response)
}


export async function getAppointmentsPagedList(id: number, appointmentStatus: AppointmentStatus, view: AppointmentView, pageNum:number ) : Promise<PagedResult<Appointment>> {
    const response = await GET(`appointments?${view}=${id}&status=${appointmentStatus}&page=${pageNum}`,
                                { contentType: "appointment-list"}    )
    return parsePagedResponse( response, Appointment)
}


export async function getAppointmentById(appointmentId:number) :Promise<Appointment> {
    const response = await GET(`appointments/${appointmentId}`, 
        { contentType: "appointment-info"}
    )

    return Appointment.fromJson(response)
}


export async function cancelAppointment(appoinmentId: number) {
    return changeAppointmentStatus(appoinmentId, AppointmentStatus.CANCELLED)
}

export async function denyAppointment(appoinmentId: number) {
    return changeAppointmentStatus(appoinmentId, AppointmentStatus.DENIED)
}

export async function confirmAppointment(appoinmentId: number) {
    return changeAppointmentStatus(appoinmentId, AppointmentStatus.CONFIRMED)
}

export async function changeAppointmentStatus(appoinmentId:number, newStatus:AppointmentStatus) {
    if ( newStatus == AppointmentStatus.FINISHED)
        throw Error("Appointment status can't be changed to finished")

    const response = await PATCH(`appointments/${appoinmentId}`, { status: newStatus },
        { contentType: "appointment-status"}
    )

    return response
}