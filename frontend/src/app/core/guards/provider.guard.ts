import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from '../services/auth.service';

export const providerGuard = () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (auth.role() === 'PROVIDER') return true;
    return router.createUrlTree(['/auth/login']);
};