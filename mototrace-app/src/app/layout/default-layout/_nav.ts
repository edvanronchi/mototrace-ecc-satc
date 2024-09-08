import {INavData} from '@coreui/angular';

export const navItems: INavData[] = [
    {
        title: true,
        name: 'Recursos'
    },
    {
        name: 'Mapa',
        url: '/mapa',
        iconComponent: {name: 'cil-location-pin'}
    },
    {
        name: 'Bloqueio',
        url: '/bloqueio',
        iconComponent: {name: 'cil-lock-locked'}
    },
    {
        title: true,
        name: 'Cadastros'
    },
    {
        name: 'Dispositivos',
        url: '/dispositivos',
        iconComponent: {name: 'cil-router'}
    }
];
