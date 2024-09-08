import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';

import {CardBodyComponent, CardComponent, CardHeaderComponent, RowComponent, TextColorDirective} from '@coreui/angular';
import {GoogleMap, MapAdvancedMarker, MapMarker, MapPolyline} from "@angular/google-maps";
import {DispositivoService} from "../../service/dispositivo.service";
import {MapaService} from "../../service/mapa.service";
import {forkJoin, Observable} from "rxjs";
import {Dispositivo} from "../../../models/Dispositivo";
import {Cordenada} from "../../../models/Cordenada";
import {CorHexadecimal} from "../../../models/Cor";

@Component({
    templateUrl: 'mapa.component.html',
    standalone: true,
    imports: [TextColorDirective, CardComponent, CardHeaderComponent, CardBodyComponent, RowComponent, GoogleMap, MapMarker, MapAdvancedMarker, MapPolyline]
})
export class MapaComponent implements OnInit, OnDestroy {
    @ViewChild('mapElement', {static: true}) mapElement!: ElementRef;

    intervalId: number | undefined;

    constructor(private dispositivoService: DispositivoService, private mapaService: MapaService) {
    }

    async ngOnInit() {
        await this.initMap();
    }

    ngOnDestroy(): void {
        clearInterval(this.intervalId);
    }

    async buscarDispositivosCordenadas(): Promise<{ dispositivos: Dispositivo[], cordenadas: Cordenada[] }> {
        return new Promise((resolve) => {
            forkJoin({
                dispositivos: this.dispositivoService.buscarTodos(),
                cordenadas: this.mapaService.buscarCordenadas()
            }).subscribe(resultado => resolve(resultado));
        });
    }

    buscarDispositivosUltimasCordenadas(dispositivos: Dispositivo[]): Observable<Cordenada[]> {
        const dispositivosCodigo = dispositivos.map(dispositivo => dispositivo.codigo).join(',');
        return this.mapaService.buscarUltimasCordenadas(dispositivosCodigo);
    }

    async initMap() {
        const {Map} = (await google.maps.importLibrary("maps")) as google.maps.MapsLibrary;
        const {
            AdvancedMarkerElement,
            PinElement
        } = (await google.maps.importLibrary("marker")) as google.maps.MarkerLibrary;

        const map = new Map(this.mapElement.nativeElement, {
            zoom: 4,
            mapId: '4504f8b37365c3d0',
            center: {lat: -12.286562123662781, lng: -49.4714931964791}
        });

        let {dispositivos, cordenadas} = await this.buscarDispositivosCordenadas();
        let ultimosMarcadores: any = {};
        let ultimosPolylines: any = {};
        let ultimosCirculos: any = {};

        if (!dispositivos.length) {
            return;
        }

        if (cordenadas.length) {
            map.setZoom(16);
            map.setCenter({lat: cordenadas[cordenadas.length - 1].latitude, lng: cordenadas[cordenadas.length - 1].longitude});
        }

        const iniciarAtualizacaoMarcador = () => {
            let entrou = false;
            this.intervalId = setInterval(() => {
                this.buscarDispositivosUltimasCordenadas(dispositivos).subscribe(ultimasCordenadas => {
                    if (!ultimasCordenadas.length) {
                        return;
                    }

                    if (!entrou) {
                        map.setZoom(16);
                        map.setCenter({lat: ultimasCordenadas[0].latitude, lng: ultimasCordenadas[0].longitude});
                        entrou = true;
                    }
                    cordenadas.push(...ultimasCordenadas);
                    dispositivos.forEach(processarDispositivosCordenadas);
                })
            }, 4000);
        }

        dispositivos.forEach(processarDispositivosCordenadas);
        iniciarAtualizacaoMarcador();

        function getColorHex(cor: string) {
            return CorHexadecimal[cor as keyof typeof CorHexadecimal];
        }

        function processarDispositivosCordenadas(dispositivo: Dispositivo) {
            const cordenadasDispositivo = cordenadas.filter(cordenada => cordenada.codigoDispositivo === dispositivo.codigo);

            if (!cordenadasDispositivo.length) {
                return;
            }

            const colorHex = getColorHex(dispositivo.cor);
            const ultimaCordenada = cordenadasDispositivo[cordenadasDispositivo.length - 1];

            gerarRotaHistorico(dispositivo, cordenadasDispositivo, colorHex);
            gerarMarcadorUltimaLocalizacao(dispositivo, ultimaCordenada, colorHex);
        }

        function gerarRotaHistorico(dispositivo: Dispositivo, cordenadasDispositivo: Cordenada[], colorHex: string) {
            const path = cordenadasDispositivo.map(cordenada => ({lat: cordenada.latitude, lng: cordenada.longitude}));

            if (ultimosPolylines[dispositivo.codigo]) {
                ultimosPolylines[dispositivo.codigo].setMap(null);
            }

            const polyline = new google.maps.Polyline({
                path: path,
                strokeColor: colorHex,
                strokeWeight: 4,
                strokeOpacity: 0.6,
                map: map
            });

            ultimosPolylines[dispositivo.codigo] = polyline;
        }

        function formatarDataHora(dataHoraArray: number[]): string {
            const [ano, mes, dia, hora, minuto, segundo] = dataHoraArray;

            const date = new Date(ano, mes - 1, dia, hora, minuto, segundo);

            const diaFormatado = String(date.getDate()).padStart(2, '0');
            const mesFormatado = String(date.getMonth() + 1).padStart(2, '0');
            const anoFormatado = date.getFullYear();
            const horasFormatadas = String(date.getHours()).padStart(2, '0');
            const minutosFormatados = String(date.getMinutes()).padStart(2, '0');
            const segundosFormatados = String(date.getSeconds()).padStart(2, '0');

            return `${diaFormatado}/${mesFormatado}/${anoFormatado} ${horasFormatadas}:${minutosFormatados}:${segundosFormatados}`;
        }

        function gerarMarcadorUltimaLocalizacao(dispositivo: Dispositivo, cordenada: Cordenada, colorHex: string) {
            const icon = document.createElement('div_' + dispositivo.codigo);
            icon.innerHTML = `<img src="assets/capacete.png" style="width: 20px; height: 20px;" alt="">`;

            if (ultimosMarcadores[dispositivo.codigo]) {
                ultimosMarcadores[dispositivo.codigo].setMap(null);
            }

            if (ultimosCirculos[dispositivo.codigo]) {
                ultimosCirculos[dispositivo.codigo].setMap(null);
            }

            const pin = new PinElement({
                glyph: icon,
                background: colorHex,
                borderColor: '#000',
                scale: 1.5
            });

            const infoWindow = new google.maps.InfoWindow({
                content: `<div style="color: black; margin: 5px"><strong>Ultima localização: </strong>${formatarDataHora(cordenada.dataHora)}</div>`
            });

            const marker = new AdvancedMarkerElement({
                map,
                title: dispositivo.nome,
                gmpClickable: true,
                content: pin.element,
                position: {lat: cordenada.latitude, lng: cordenada.longitude}
            });

            const circle = new google.maps.Circle({
                strokeColor: colorHex,
                strokeOpacity: 0.7,
                strokeWeight: 2,
                fillColor: colorHex,
                fillOpacity: 0.2,
                map: map,
                center: {lat: cordenada.latitude, lng: cordenada.longitude},
                radius: 50
            });

            marker.addListener('click', () => {
                infoWindow.open(map, marker);
            });

            ultimosMarcadores[dispositivo.codigo] = marker;
            ultimosCirculos[dispositivo.codigo] = circle;
        }
    }
}
