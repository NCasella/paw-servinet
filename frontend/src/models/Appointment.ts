import type { TResponse } from "$utils/apiFetch";
import { AppointmentStatus } from "./enums/AppointmentStatus";

export class Appointment {
  appointmentId: number;
  startDate: string;     // Te conviene dejarlos como string ISO y convertir afuera si querés
  endDate: string;
  address: string;
  description: string | null;
  status: AppointmentStatus;
  serviceId: number;

  constructor(data: {
    appointmentId: number;
    startDate: string;
    endDate: string;
    address: string;
    description: string | null;
    status: AppointmentStatus;
    serviceId: number
  }) {
    this.appointmentId = data.appointmentId;
    this.startDate = data.startDate;
    this.endDate = data.endDate;
    this.address = data.address;
    this.description = data.description;
    this.status = data.status;
    this.serviceId = data.serviceId;
  }

  static fromJson(response: TResponse): Appointment {
    const obj = response.body;
    console.log("Appointment fromJson:", obj);

    if (isAppointment(obj)) {
      return new Appointment(obj);
    }

    throw new Error("Invalid Appointment JSON");
  }
  hasFinished(): boolean {
    const start = new Date(this.startDate).getTime(); 
    return start < Date.now();
  }

  isConfirmed() :boolean {
    return this.status == AppointmentStatus.CONFIRMED
  }

  formatedDate() :Date {
    return new Date(this.startDate); 
  }
}

function isAppointment(obj: any): obj is Appointment {
  return (
    obj &&
    typeof obj.appointmentId === "number" &&
    typeof obj.startDate === "string" &&
    typeof obj.endDate === "string" &&
    typeof obj.address === "string" &&
    (typeof obj.description === "string" || obj.description == null) &&
    typeof obj.status === "string" &&
    typeof obj.serviceId === "number"

  );
}

