import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CourseService} from '../../../../core/services/course.service';
import {BookingService} from '../../../../core/services/booking.service';
import {AuthService} from '../../../../core/services/auth.service';
import {CourseDetail, SlotDto} from '../../../../core/models/course.model';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {DecimalPipe} from '@angular/common';

@Component({
    selector: 'app-course-detail',
    standalone: true,
    imports: [
        MatDatepickerModule,
        MatNativeDateModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        DecimalPipe
    ],
    templateUrl: './course-detail.component.html',
    styleUrl: './course-detail.component.scss'
})
export class CourseDetailComponent implements OnInit {
    private readonly route = inject(ActivatedRoute);
    private readonly courseService = inject(CourseService);
    private readonly bookingService = inject(BookingService);
    private readonly authService = inject(AuthService);
    private readonly fb = inject(FormBuilder);

    readonly today = new Date();

    course = signal<CourseDetail | null>(null);
    loading = signal(true);
    error = signal<string | null>(null);
    selectedDate = signal<Date | null>(null);
    selectedSlot = signal<SlotDto | null>(null);
    slotsForSelectedDate = signal<SlotDto[]>([]);
    bookingSuccess = signal(false);
    bookingError = signal<string | null>(null);
    bookingInProgress = signal(false);

    private lat = 0;
    private lng = 0;
    private fromDate = new Date();

    guestForm: FormGroup = this.fb.group({
        guestFirstName: ['', Validators.required],
        guestLastName: ['', Validators.required],
        guestEmail: ['', [Validators.required, Validators.email]],
        guestPhone: ['']
    });

    get isLoggedIn() {
        return this.authService.isLoggedIn();
    }

    get availableDates(): Set<string> {
        const c = this.course();
        if (!c) return new Set();
        return new Set(Object.keys(c.slotsByDate));
    }

    ngOnInit() {
        navigator.geolocation.getCurrentPosition(
            position => {
                this.lat = position.coords.latitude;
                this.lng = position.coords.longitude;
                this.loadCourse();
            },
            () => {
                this.lat = 47.8095;
                this.lng = 13.0550;
                this.loadCourse();
            }
        );
    }

    loadCourse(from?: Date) {
        const id = this.route.snapshot.paramMap.get('id')!;
        const fromStr = (from ?? this.fromDate).toISOString().split('T')[0];

        this.courseService.getDetail(id, this.lat, this.lng, fromStr).subscribe({
            next: course => {
                this.course.set(course);
                this.loading.set(false);
            },
            error: () => {
                this.error.set('Course not found');
                this.loading.set(false);
            }
        });
    }

    dateFilter = (date: Date | null): boolean => {
        if (!date) return false;
        const dateStr = this.toDateString(date);
        return this.availableDates.has(dateStr);
    };

    onDateSelected(date: Date | null) {
        if (!date) return;
        this.selectedDate.set(date);
        this.selectedSlot.set(null);
        const dateStr = this.toDateString(date);
        const slots = this.course()?.slotsByDate[dateStr] ?? [];
        this.slotsForSelectedDate.set(slots);

        const lastDay = new Date(this.fromDate);
        lastDay.setDate(lastDay.getDate() + 25);
        if (date > lastDay) {
            this.fromDate = date;
            this.loadCourse(date);
        }
    }

    onSlotSelected(slot: SlotDto) {
        this.selectedSlot.set(slot);
        this.bookingSuccess.set(false);
        this.bookingError.set(null);
    }

    book() {
        const slot = this.selectedSlot();
        if (!slot) return;

        if (!this.isLoggedIn && this.guestForm.invalid) {
            this.guestForm.markAllAsTouched();
            return;
        }

        this.bookingInProgress.set(true);
        this.bookingError.set(null);

        const request = this.isLoggedIn
            ? {slotId: slot.id}
            : {slotId: slot.id, ...this.guestForm.value};

        this.bookingService.book(request).subscribe({
            next: response => {
                this.bookingSuccess.set(true);
                this.bookingInProgress.set(false);
                this.selectedSlot.set(null);
                this.guestForm.reset();
                this.loadCourse();
            },
            error: () => {
                this.bookingError.set('Booking failed. Please try again.');
                this.bookingInProgress.set(false);
            }
        });
    }

    private toDateString(date: Date): string {
        return date.toISOString().split('T')[0];
    }
}