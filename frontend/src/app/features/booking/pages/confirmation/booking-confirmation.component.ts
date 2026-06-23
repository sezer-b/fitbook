import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-booking-confirmation',
    standalone: true,
    imports: [MatCardModule, MatButtonModule, MatIconModule, RouterLink],
    templateUrl: './booking-confirmation.component.html',
    styleUrl: './booking-confirmation.component.scss'
})
export class BookingConfirmationComponent implements OnInit {
    private readonly router = inject(Router);

    status = signal<string | null>(null);
    waitlistPosition = signal<number | null>(null);
    courseName = signal<string | null>(null);
    providerName = signal<string | null>(null);
    slotDate = signal<string | null>(null);
    startTime = signal<string | null>(null);
    endTime = signal<string | null>(null);

    get isWaitlisted() {
        return this.status() === 'WAITLISTED';
    }

    ngOnInit() {
        const state = this.router.getCurrentNavigation()?.extras.state
            ?? history.state;

        if (!state?.courseName) {
            this.router.navigate(['/']);
            return;
        }

        this.status.set(state['status']);
        this.waitlistPosition.set(state['waitlistPosition']);
        this.courseName.set(state['courseName']);
        this.providerName.set(state['providerName']);
        this.slotDate.set(state['slotDate']);
        this.startTime.set(state['startTime']);
        this.endTime.set(state['endTime']);
    }
}