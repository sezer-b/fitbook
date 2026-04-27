export interface AuthResponse {
    token: string;
    role: string;
    id: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterUserRequest {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
    phone?: string;
}