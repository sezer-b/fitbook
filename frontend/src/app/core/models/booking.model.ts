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