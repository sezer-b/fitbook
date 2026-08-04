import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { BookingService } from '../../../../core/services/booking.service';
import { BookingHistory } from '../../../../core/models/booking.model';

@Component({
    selector: 'app-my-bookings',
    standalone: true,
    imports: [
        RouterLink, MatCardModule, MatButtonModule,
        MatIconModule, MatProgressSpinnerModule,
        MatChipsModule, MatTabsModule
    ],
    templateUrl: './my-bookings.component.html',
    styleUrl: './my-bookings.component.scss'
})
export class MyBookingsComponent implements OnInit {
    private readonly bookingService = inject(BookingService);

    bookings = signal<BookingHistory[]>([]);
    loading = signal(true);
    cancellingId = signal<string | null>(null);

    get upcoming() {
        const today = new Date().toISOString().split('T')[0];
        return this.bookings().filter(b =>
            b.slotDate >= today && b.status !== 'CANCELLED'
        );
    }

    get past() {
        const today = new Date().toISOString().split('T')[0];
        return this.bookings().filter(b =>
            b.slotDate < today || b.status === 'CANCELLED'
        );
    }

    ngOnInit() {
        this.load();
    }

    load() {
        this.bookingService.getMyBookings().subscribe({
            next: bookings => {
                this.bookings.set(bookings);
                this.loading.set(false);
            },
            error: () => this.loading.set(false)
        });
    }

    cancel(bookingId: string) {
        this.cancellingId.set(bookingId);
        this.bookingService.cancel(bookingId).subscribe({
            next: () => {
                this.cancellingId.set(null);
                this.load();
            },
            error: () => this.cancellingId.set(null)
        });
    }
}