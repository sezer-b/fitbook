import {Component, inject, OnInit, signal} from '@angular/core';
import {NgTemplateOutlet} from '@angular/common';
import {MatTabsModule} from '@angular/material/tabs';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {MatTableModule} from '@angular/material/table';
import {ProviderService} from '../../../../core/services/provider.service';
import {CreateCourseDialogComponent} from '../../components/create-course-dialog/create-course-dialog.component';
import {CourseImage, ProviderCourse, SlotBooking} from "../../../../core/models/provider.model";
import {CourseSlot} from "../../../../core/models/course.model";

@Component({
    selector: 'app-provider-dashboard',
    standalone: true,
    imports: [
        NgTemplateOutlet, MatTabsModule, MatButtonModule, MatIconModule,
        MatCardModule, MatProgressSpinnerModule, MatDialogModule, MatTableModule
    ],
    templateUrl: './provider-dashboard.component.html',
    styleUrl: './provider-dashboard.component.scss'
})
export class ProviderDashboardComponent implements OnInit {
    private readonly providerService = inject(ProviderService);
    private readonly dialog = inject(MatDialog);

    courses = signal<ProviderCourse[]>([]);
    loading = signal(true);
    actionInProgress = signal<string | null>(null);

    viewingCourse = signal<ProviderCourse | null>(null);
    slots = signal<CourseSlot[]>([]);
    slotsLoading = signal(false);
    selectedSlot = signal<CourseSlot | null>(null);
    slotBookings = signal<SlotBooking[]>([]);
    slotBookingsLoading = signal(false);

    get draftCourses() {
        return this.courses().filter(c => c.status === 'DRAFT');
    }

    get activeCourses() {
        return this.courses().filter(c => c.status === 'ACTIVE');
    }

    get cancelledCourses() {
        return this.courses().filter(c => c.status === 'CANCELLED');
    }

    ngOnInit() {
        this.load();
    }

    load() {
        this.providerService.getMyCourses().subscribe({
            next: courses => {
                this.courses.set(courses);
                this.loading.set(false);
            },
            error: () => this.loading.set(false)
        });
    }

    openCreateDialog() {
        const ref = this.dialog.open(CreateCourseDialogComponent, {
            width: '700px',
            maxHeight: '90vh'
        });
        ref.afterClosed().subscribe(created => {
            if (created) this.load();
        });
    }

    publish(course: ProviderCourse) {
        this.actionInProgress.set(course.id);
        this.providerService.publishCourse(course.id).subscribe({
            next: () => {
                this.actionInProgress.set(null);
                this.load();
            },
            error: () => this.actionInProgress.set(null)
        });
    }

    cancel(course: ProviderCourse) {
        this.actionInProgress.set(course.id);
        this.providerService.cancelCourse(course.id).subscribe({
            next: () => {
                this.actionInProgress.set(null);
                this.load();
            },
            error: () => this.actionInProgress.set(null)
        });
    }

    uploadImage(course: ProviderCourse, event: Event) {
        const file = (event.target as HTMLInputElement).files?.[0];
        if (!file) return;
        this.providerService.uploadImage(course.id, file).subscribe({
            next: () => this.load(),
            error: () => {
            }
        });
    }

    deleteImage(course: ProviderCourse, image: CourseImage) {
        this.providerService.deleteImage(course.id, image.id).subscribe({
            next: () => this.load(),
            error: () => {
            }
        });
    }

    viewSlots(course: ProviderCourse) {
        this.viewingCourse.set(course);
        this.selectedSlot.set(null);
        this.slotBookings.set([]);
        this.slotsLoading.set(true);
        this.providerService.getCourseSlots(course.id).subscribe({
            next: slots => {
                this.slots.set(slots);
                this.slotsLoading.set(false);
            },
            error: () => this.slotsLoading.set(false)
        });
    }

    selectSlot(slot: CourseSlot) {
        this.selectedSlot.set(slot);
        this.slotBookingsLoading.set(true);
        this.providerService.getSlotBookings(slot.id).subscribe({
            next: bookings => {
                this.slotBookings.set(bookings);
                this.slotBookingsLoading.set(false);
            },
            error: () => this.slotBookingsLoading.set(false)
        });
    }

    backToCourses() {
        this.viewingCourse.set(null);
        this.slots.set([]);
        this.selectedSlot.set(null);
        this.slotBookings.set([]);
    }

    backToSlots() {
        this.selectedSlot.set(null);
        this.slotBookings.set([]);
    }
}