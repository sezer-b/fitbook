import {Component, inject, OnInit, signal} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule, provideNativeDateAdapter} from '@angular/material/core';
import {ProviderService} from '../../../../core/services/provider.service';
import {Category} from "../../../../core/models/provider.model";

@Component({
    selector: 'app-create-course-dialog',
    standalone: true,
    imports: [
        ReactiveFormsModule, MatDialogModule, MatFormFieldModule,
        MatInputModule, MatSelectModule, MatButtonModule,
        MatIconModule, MatProgressSpinnerModule,
        MatDatepickerModule, MatNativeDateModule
    ],
    providers: [provideNativeDateAdapter()],
    templateUrl: './create-course-dialog.component.html',
    styleUrl: './create-course-dialog.component.scss'
})
export class CreateCourseDialogComponent implements OnInit {
    private readonly fb = inject(FormBuilder);
    private readonly providerService = inject(ProviderService);
    private readonly dialogRef = inject(MatDialogRef<CreateCourseDialogComponent>);

    categories = signal<Category[]>([]);
    loading = signal(false);
    error = signal<string | null>(null);

    readonly days = [
        {value: 1, label: 'Monday'},
        {value: 2, label: 'Tuesday'},
        {value: 3, label: 'Wednesday'},
        {value: 4, label: 'Thursday'},
        {value: 5, label: 'Friday'},
        {value: 6, label: 'Saturday'},
        {value: 7, label: 'Sunday'}
    ];

    form: FormGroup = this.fb.group({
        name: ['', Validators.required],
        description: [''],
        categoryId: ['', Validators.required],
        address: ['', Validators.required],
        city: ['', Validators.required],
        postcode: [''],
        country: ['', Validators.required],
        capacityPerSlot: [10, [Validators.required, Validators.min(1)]],
        slotDurationMinutes: [60, [Validators.required, Validators.min(5)]],
        startDate: [null, Validators.required],
        endDate: [null, Validators.required],
        lat: [null, Validators.required],
        lng: [null, Validators.required],
        rules: this.fb.array([this.createRule()])
    });

    get rules(): FormArray {
        return this.form.get('rules') as FormArray;
    }

    ngOnInit() {
        this.providerService.getCategories().subscribe(cats => this.categories.set(cats));
        this.detectLocation();
    }

    detectLocation() {
        navigator.geolocation.getCurrentPosition(position => {
            this.form.patchValue({
                lat: position.coords.latitude,
                lng: position.coords.longitude
            });
        });
    }

    createRule() {
        return this.fb.group({
            dayOfWeek: [1, Validators.required],
            startTime: ['09:00', Validators.required]
        });
    }

    addRule() {
        this.rules.push(this.createRule());
    }

    removeRule(index: number) {
        if (this.rules.length > 1) this.rules.removeAt(index);
    }

    submit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        this.loading.set(true);
        this.error.set(null);

        const value = this.form.value;
        const request = {
            ...value,
            startDate: this.toDateStr(value.startDate),
            endDate: this.toDateStr(value.endDate),
            scheduleConfig: {rules: value.rules}
        };

        this.providerService.createCourse(request).subscribe({
            next: course => {
                this.loading.set(false);
                this.dialogRef.close(course);
            },
            error: err => {
                this.loading.set(false);
                this.error.set(err?.error?.detail ?? 'Failed to create course');
            }
        });
    }

    private toDateStr(date: Date): string {
        return date.toISOString().split('T')[0];
    }
}