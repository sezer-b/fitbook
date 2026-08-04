import {Component, inject, OnInit, signal} from '@angular/core';
import {MatTabsModule} from '@angular/material/tabs';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatChipsModule} from '@angular/material/chips';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {ProviderService} from '../../../../core/services/provider.service';
import {CreateCourseDialogComponent} from '../../components/create-course-dialog/create-course-dialog.component';
import {ProviderCourse} from "../../../../core/models/provider.model";
import {NgTemplateOutlet} from "@angular/common";

@Component({
    selector: 'app-provider-dashboard',
    standalone: true,
    imports: [
        MatTabsModule, MatButtonModule, MatIconModule,
        MatCardModule, MatProgressSpinnerModule,
        MatChipsModule, MatDialogModule, NgTemplateOutlet
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
}