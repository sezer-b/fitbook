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
    selector: 'app-login',
    standalone: true,
    imports: [
        ReactiveFormsModule, RouterLink,
        MatButtonToggleModule, MatFormFieldModule, MatInputModule,
        MatButtonModule, MatCardModule, MatProgressSpinnerModule
    ],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})
export class LoginComponent {
    private readonly fb = inject(FormBuilder);
    private readonly authService = inject(AuthService);
    private readonly router = inject(Router);
    private readonly route = inject(ActivatedRoute);

    loginAs = signal<'USER' | 'PROVIDER'>(
        this.route.snapshot.queryParamMap.get('role') === 'provider' ? 'PROVIDER' : 'USER'
    );
    loading = signal(false);
    error = signal<string | null>(null);

    form: FormGroup = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required]
    });

    submit() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        this.loading.set(true);
        this.error.set(null);

        const login$ = this.loginAs() === 'USER'
            ? this.authService.loginUser(this.form.value)
            : this.authService.loginProvider(this.form.value);

        login$.subscribe({
            next: () => {
                this.loading.set(false);
                this.router.navigate([this.loginAs() === 'USER' ? '/' : '/provider']);
            },
            error: () => {
                this.loading.set(false);
                this.error.set('Invalid email or password');
            }
        });
    }
}