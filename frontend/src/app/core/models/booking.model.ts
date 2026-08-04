export interface CreateBookingRequest {
    slotId: string;
    guestFirstName?: string;
    guestLastName?: string;
    guestEmail?: string;
    guestPhone?: string;
}

export interface BookingResponse {
    id: string;
    slotId: string;
    status: string;
    waitlistPosition?: number;
    bookedAt: string;
}

export interface BookingHistory {
    id: string;
    slotId: string;
    courseName: string;
    providerName: string;
    slotDate: string;
    startTime: string;
    endTime: string;
    status: string;
    waitlistPosition?: number;
    bookedAt: string;
}