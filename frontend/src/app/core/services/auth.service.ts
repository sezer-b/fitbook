import {Injectable, inject, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {tap} from 'rxjs/operators';
import {AuthResponse, LoginRequest, RegisterUserRequest} from '../models/auth.model';

@Injectable({providedIn: 'root'})
export class AuthService {
    private readonly http = inject(HttpClient);
    private readonly router = inject(Router);

    readonly isLoggedIn = signal(!!localStorage.getItem('token'));
    readonly role = signal<string | null>(localStorage.getItem('role'));

    loginUser(request: LoginRequest) {
        return this.http.post<AuthResponse>('/api/auth/users/login', request).pipe(
            tap(response => this.storeAuth(response))
        );
    }

    loginProvider(request: LoginRequest) {
        return this.http.post<AuthResponse>('/api/auth/providers/login', request).pipe(
            tap(response => this.storeAuth(response))
        );
    }

    registerUser(request: RegisterUserRequest) {
        return this.http.post<AuthResponse>('/api/auth/users/register', request).pipe(
            tap(response => this.storeAuth(response))
        );
    }

    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('userId');
        this.isLoggedIn.set(false);
        this.role.set(null);
        this.router.navigate(['/']);
    }

    private storeAuth(response: AuthResponse) {
        localStorage.setItem('token', response.token);
        localStorage.setItem('role', response.role);
        localStorage.setItem('userId', response.id);
        this.isLoggedIn.set(true);
        this.role.set(response.role);
    }
}