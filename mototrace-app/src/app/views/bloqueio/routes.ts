import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./bloqueio.component').then(m => m.BloqueioComponent)
  }
];

