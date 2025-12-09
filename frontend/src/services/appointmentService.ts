import { Appointment } from "$models/Appointment";
import {  type AppointmentStatus, AppointmentView } from "$models/enums/AppointmentStatus";
import type { AppointmentForm } from "$models/forms/AppointmentCreationForm";
import { parsePagedResponse, type PagedResult } from "$models/PagedList";
import type { Service } from "$models/Service";
import { GET, getNewIdFromPostResponse, POST, type TResponse } from "$utils/apiFetch";
import { number } from "zod";
import { getCurrentUser } from "./userService";

export async function createAppointment(form: AppointmentForm) :Promise<number> {
    const user = await getCurrentUser()
    form.userId = user.userId
    
    const response :TResponse = await POST("appointments", form, {
        contentType: "appointment-creation"
    })   
  
    return getNewIdFromPostResponse(response)
}


export async function getAppointmentsPagedList(id: number, appointmentStatus: AppointmentStatus, view: AppointmentView ) : Promise<PagedResult<Appointment>> {
    const response = await GET(`appointments?${view}=${id}`,
                                { contentType: "appointment-list"}    )
    return parsePagedResponse( response, Appointment)
}


export async function getAppointmentById(appointmentId:number) :Promise<Appointment> {
    const response = await GET(`appointments/${appointmentId}`, 
        { contentType: "appointment-info"}
    )

    return Appointment.fromJson(response)
}