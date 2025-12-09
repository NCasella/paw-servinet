export enum AppointmentStatus {
    CONFIRMED = "confirmed",
    PENDING = "pending",
}

export function getAppointmentStatus(status: any, defaultStatus: AppointmentStatus): AppointmentStatus {
  return Object.values(AppointmentStatus).includes(status)
    ? status
    : defaultStatus;
}


export enum AppointmentView {
    BUSINESS= "businessId",
    USER= "userId:"
} 