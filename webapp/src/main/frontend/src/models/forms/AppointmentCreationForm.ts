export class AppointmentForm {
  startDate: string;
  address: string;
  description: string;
  neighbourhood: string;
  serviceId: number;
  userId?: number

   constructor(data: {
    startdate?: string;
    address?: string;
    description?: string;
    neighbourhood?: string;
    serviceId: number;
  }) {
    this.startDate = data.startdate ?? "";
    this.address = data.address ?? "";
    this.description = data.description ?? "";
    this.neighbourhood = data.neighbourhood ?? "";
    this.serviceId = data.serviceId;
  }

  validateAppointmentForm(): AppointmentFormErrors {
    const result = AppointmentFormSchema.safeParse(this);
    const errors: AppointmentFormErrors = {};

    if (!result.success) {
      for (const issue of result.error.issues) {
        const field = issue.path[0] as keyof AppointmentFormData;

        // Usamos solo el primer error por campo
        if (!errors[field]) {
          errors[field] = issue.message as AppointmentFormErrorKey;
        }
      }
    }

    return errors;
  }
}


import { z } from "zod";

const MAX_LEN = 255;

export type AppointmentFormErrorKey =
  | "NotEmpty.appointmentForm.location"
  | "Size.appointmentForm.location"
  | "NotNull.appointmentForm.date"
  | "FutureDate.appointmentForm.date"
  | "Size.appointmentForm.description"; //todo "Size.appointmentForm.description": "The description cannot exceed 255 characters"
  // barrio no se chequea xq le tiro las opciones

export const AppointmentFormSchema = z.object({
  address: z
    .string()
    .trim()
    .min(1, { message: "NotEmpty.appointmentForm.location" })
    .max(MAX_LEN, { message: "Size.appointmentForm.location" }),

  startDate: z
    .string()
    // campo no nulo / no vacío
    .refine(
      (v) => v !== null && v !== undefined && v.trim() !== "",
      { message: "NotNull.appointmentForm.date" }
    )
    // fecha en el futuro
    .refine(
      (v) => {
        const d = new Date(v); // "YYYY-MM-DDTHH:mm" de <input type="datetime-local">
        return !Number.isNaN(d.getTime()) && d.getTime() > Date.now();
      },
      { message: "FutureDate.appointmentForm.date" }
    ),

  description: z
    .string()
    .max(MAX_LEN, { message: "Size.appointmentForm.description" }),
});

export type AppointmentFormData = z.infer<typeof AppointmentFormSchema>;
export type AppointmentFormErrors = {
  [K in keyof AppointmentFormData]?: AppointmentFormErrorKey;
};
