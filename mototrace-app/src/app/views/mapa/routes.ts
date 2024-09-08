import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./mapa.component').then(m => m.MapaComponent)
  }
];

