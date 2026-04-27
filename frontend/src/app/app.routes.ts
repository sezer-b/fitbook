import {Routes} from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () =>
            import('./features/courses/pages/course-browse/course-browse.component')
                .then(m => m.CourseBrowseComponent)
    },
    {
        path: 'courses/:id',
        loadComponent: () =>
            import('./features/courses/pages/course-detail/course-detail.component')
                .then(m => m.CourseDetailComponent)
    },
    {
        path: 'auth/login',
        loadComponent: () =>
            import('./features/auth/pages/login/login.component')
                .then(m => m.LoginComponent)
    },
    {
        path: 'auth/register',
        loadComponent: () =>
            import('./features/auth/pages/register/register.component')
                .then(m => m.RegisterComponent)
    },
    {
        path: 'provider',
        loadComponent: () =>
            import('./features/provider/pages/dashboard/provider-dashboard.component')
                .then(m => m.ProviderDashboardComponent),
        canActivate: [() => import('./core/guards/provider.guard').then(m => m.providerGuard)]
    },
    {
        path: '**',
        redirectTo: ''
    }
];