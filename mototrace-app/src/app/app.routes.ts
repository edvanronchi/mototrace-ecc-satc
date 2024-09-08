import {Routes} from '@angular/router';
import {DefaultLayoutComponent} from './layout';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'mapa',
        pathMatch: 'full'
    },
    {
        path: '',
        component: DefaultLayoutComponent,
        data: {
            title: 'Home'
        },
        children: [
            {
                path: 'dispositivos',
                loadChildren: () => import('./views/dispositivos/routes').then((m) => m.routes)
            },
            {
                path: 'mapa',
                loadChildren: () => import('./views/mapa/routes').then((m) => m.routes)
            },
            {
                path: 'bloqueio',
                loadChildren: () => import('./views/bloqueio/routes').then((m) => m.routes)
            },
        ]
    },
    {
        path: '404',
        loadComponent: () => import('./views/pages/page404/page404.component').then(m => m.Page404Component),
        data: {
            title: 'Page 404'
        }
    },
    {
        path: '500',
        loadComponent: () => import('./views/pages/page500/page500.component').then(m => m.Page500Component),
        data: {
            title: 'Page 500'
        }
    },
    {
        path: 'login',
        loadComponent: () => import('./views/pages/login/login.component').then(m => m.LoginComponent),
        data: {
            title: 'Login Page'
        }
    },
    {
        path: 'register',
        loadComponent: () => import('./views/pages/register/register.component').then(m => m.RegisterComponent),
        data: {
            title: 'Register Page'
        }
    },
    {path: '**', redirectTo: '404'}
];
