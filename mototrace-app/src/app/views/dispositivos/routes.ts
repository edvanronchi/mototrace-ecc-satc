import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./dispositivos.component').then(m => m.DispositivosComponent)
  }
];

