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
  userId: number;

  constructor(data: {
    appointmentId: number;
    startDate: string;
    endDate: string;
    address: string;
    description: string | null;
    status: AppointmentStatus;
    serviceId: number;
    userId: number
  }) {
    this.appointmentId = data.appointmentId;
    this.startDate = data.startDate;
    this.endDate = data.endDate;
    this.address = data.address;
    this.description = data.description;
    this.status = data.status;
    this.serviceId = data.serviceId;
    this.userId = data.userId;
  }

  static fromJson(response: TResponse): Appointment {
    const obj = response.body;

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

    private get start(): Date {
    return new Date(this.startDate);
  }

  private get end(): Date {
    return new Date(this.endDate);
  }

  private formatDate(     { withYear }: { withYear: boolean } ) {
  const date = new Date(this.startDate);

  const raw = new Intl.DateTimeFormat("en-US", {
    weekday: "short",
    day: "2-digit",
    month: "long",
          ...(withYear ? { year: "numeric" } : {})
  }).format(date);

  return raw.replace(",", "");  // "Sat 20 December"
}

  private formatTime(date: Date): string {
  return new Intl.DateTimeFormat("es-AR", {
    hour: "2-digit",
    minute: "2-digit",
    hourCycle: "h23"   // ⇐ formato 24 hs, sin AM/PM
  }).format(date);
}

  getStartDateString(): string {
    return this.formatDate({ withYear: false });
  }

  /** startDateWithYearString: con año (ej: 07/12/2025) */
  getStartDateWithYearString(): string {
    return this.formatDate( { withYear: true });
  }

  /** startDateTimeString: solo hora (ej: 14:30) */
  getStartDateTimeString(): string {
    return this.formatTime(this.start);
  }

  /** endDateTimeString: solo hora (ej: 15:00) */
  getEndDateTimeString(): string {
    return this.formatTime(this.end);
  }

  /** Tiene duración? (ej: start != end) */
  hasDuration(): boolean {
    return !!this.endDate && this.endDate !== this.startDate;
  }

  /** Si querés mantener compatibilidad con lo que ya usabas */
  formatedDate(): string {
    return this.getStartDateString();
  }

  formatedTime(): string {
    return this.getStartDateTimeString();
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
    typeof obj.serviceId === "number" &&
    typeof obj.userId === "number"

  );
}
