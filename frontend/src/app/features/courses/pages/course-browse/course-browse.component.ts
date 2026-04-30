import {Component, inject, OnInit, signal} from '@angular/core';
import {CourseService} from '../../../../core/services/course.service';
import {CourseCard} from '../../../../core/models/course.model';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {RouterLink} from '@angular/router';
import {DecimalPipe} from '@angular/common';

@Component({
    selector: 'app-course-browse',
    standalone: true,
    imports: [
        MatProgressSpinnerModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        RouterLink,
        DecimalPipe
    ],
    templateUrl: './course-browse.component.html',
    styleUrl: './course-browse.component.scss'
})
export class CourseBrowseComponent implements OnInit {
    private readonly courseService = inject(CourseService);

    courses = signal<CourseCard[]>([]);
    loading = signal(true);
    error = signal<string | null>(null);
    locationDenied = signal(false);

    ngOnInit() {
        this.getUserLocation();
    }

    getUserLocation() {
        if (!navigator.geolocation) {
            this.error.set('Geolocation is not supported by your browser');
            this.loading.set(false);
            return;
        }

        navigator.geolocation.getCurrentPosition(
            position => {
                // TODO: remove static location later
                // const {latitude, longitude} = position.coords;
                const {latitude, longitude} = {latitude: 47.803824092260015, longitude: 13.044970158003089};
                this.loadCourses(latitude, longitude);
            },
            () => {
                this.locationDenied.set(true);
                this.loading.set(false);
            }
        );
    }

    loadCourses(lat: number, lng: number, radiusKm = 50) {
        this.loading.set(true);
        this.courseService.browse(lat, lng, radiusKm).subscribe({
            next: courses => {
                this.courses.set(courses);
                this.loading.set(false);
            },
            error: () => {
                this.error.set('Failed to load courses');
                this.loading.set(false);
            }
        });
    }
}