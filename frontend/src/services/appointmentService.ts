import { Appointment } from "$models/Appointment";
import {  type AppointmentStatus, AppointmentView } from "$models/enums/AppointmentStatus";
import type { AppointmentForm } from "$models/forms/AppointmentCreationForm";
import { parsePagedResponse, type PagedResult } from "$models/PagedList";
import { GET, getNewIdFromPostResponse, POST, type TResponse } from "$utils/apiFetch";
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
    const response = await GET(`/appoinments?${AppointmentView}=${id}`,
                                { contentType: "appointment-list"}    )
    return parsePagedResponse( response, Appointment)
}