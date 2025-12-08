import type { AppointmentForm } from "$models/forms/AppointmentCreationForm";
import { getNewIdFromPostResponse, POST, type TResponse } from "$utils/apiFetch";
import { getCurrentUser } from "./userService";

export async function createAppointment(form: AppointmentForm) :Promise<number> {
    const user = await getCurrentUser()
    form.userId = user.userId
    
    const response :TResponse = await POST("appointments", form, {
        contentType: "appointment-creation"
    })   
  
    return getNewIdFromPostResponse(response)
}

