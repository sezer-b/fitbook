export interface ScheduleRule {
    dayOfWeek: number;
    startTime: string;
}

export interface ProviderCourse {
    id: string;
    name: string;
    description: string;
    category: string;
    address: string;
    city: string;
    postcode: string;
    country: string;
    capacityPerSlot: number;
    slotDurationMinutes: number;
    scheduleConfig: { rules: ScheduleRule[] };
    startDate: string;
    endDate: string;
    status: string;
    imageUrls: string[];
    createdAt: string;
    updatedAt: string;
}

export interface CreateCourseRequest {
    name: string;
    description?: string;
    categoryId: string;
    address: string;
    city: string;
    postcode?: string;
    country: string;
    capacityPerSlot: number;
    slotDurationMinutes: number;
    scheduleConfig: { rules: ScheduleRule[] };
    startDate: string;
    endDate: string;
    lat: number;
    lng: number;
}

export interface SlotBooking {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    status: string;
    waitlistPosition?: number;
    isGuest: boolean;
    bookedAt: string;
}

export interface Category {
    id: string;
    name: string;
}