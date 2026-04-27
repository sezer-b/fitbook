import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BookingResponse, CreateBookingRequest} from '../models/booking.model';

@Injectable({providedIn: 'root'})
export class BookingService {
    private readonly http = inject(HttpClient);

    book(request: CreateBookingRequest) {
        return this.http.post<BookingResponse>('/api/bookings', request);
    }

    cancel(id: string) {
        return this.http.delete(`/api/bookings/${id}`);
    }

    getMyBookings() {
        return this.http.get<any[]>('/api/bookings/me');
    }
}