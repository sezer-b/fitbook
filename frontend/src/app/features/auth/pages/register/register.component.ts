import {Component, inject, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {AuthService} from '../../../../core/services/auth.service';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [
        ReactiveFormsModule, RouterLink,
        MatButtonToggleModule, MatFormFieldModule, MatInputModule,
        MatButtonModule, MatCardModule, MatProgressSpinnerModule
    ],
    templateUrl: './register.component.html',
    styleUrl: './register.component.scss'
})
export class RegisterComponent {
    private readonly fb = inject(FormBuilder);
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);
    private readonly route = inject(ActivatedRoute);

    registerAs = signal<'USER' | 'PROVIDER'>(
        this.route.snapshot.queryParamMap.get('role') === 'provider' ? 'PROVIDER' : 'USER'
    );
    loading = signal(false);
    error = signal<string | null>(null);

    userForm: FormGroup = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        phone: ['']
    });

    providerForm: FormGroup = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        businessName: ['', Validators.required],
        description: [''],
        phone: [''],
        website: ['']
    });

    get currentForm() {
        return this.registerAs() === 'USER' ? this.userForm : this.providerForm;
    }

    submit() {
        const form = this.currentForm;
        if (form.invalid) {
            form.markAllAsTouched();
            return;
        }

        this.loading.set(true);
        this.error.set(null);

        const register$ = this.registerAs() === 'USER'
            ? this.authService.registerUser(form.value)
            : this.authService.registerProvider(form.value);

        register$.subscribe({
            next: () => {
                this.loading.set(false);
                this.router.navigate([this.registerAs() === 'USER' ? '/' : '/provider']);
            },
            error: (err) => {
                this.loading.set(false);
                this.error.set(err?.error?.detail ?? 'Registration failed. Please try again.');
            }
        });
    }
}