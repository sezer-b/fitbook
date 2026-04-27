export interface CourseCard {
    id: string;
    name: string;
    category: string;
    providerName: string;
    city: string;
    country: string;
    distanceKm: number;
    imageUrl: string;
    capacityPerSlot: number;
    slotDurationMinutes: number;
}

export interface SlotDto {
    id: string;
    startTime: string;
    endTime: string;
    availableSpots: number;
    status: string;
}

export interface CourseDetail {
    id: string;
    name: string;
    description: string;
    category: string;
    providerName: string;
    address: string;
    city: string;
    postcode: string;
    country: string;
    distanceKm: number;
    capacityPerSlot: number;
    slotDurationMinutes: number;
    imageUrls: string[];
    slotsByDate: Record<string, SlotDto[]>;
}