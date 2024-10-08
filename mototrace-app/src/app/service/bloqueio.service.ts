import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Dispositivo} from "../../models/Dispositivo";
import {URL_GATEWAY_API} from "../constants/constants";

@Injectable({
    providedIn: 'root'
})
export class BloqueioService {
    private apiUrl: string = URL_GATEWAY_API + '/worker-position/dispositivo-comunicacao';

    constructor(private http: HttpClient) {
    }

    bloquear(codigoDispositivo: string) : Observable<Dispositivo> {
        return this.http.post<Dispositivo>(this.apiUrl + '/bloquear', { codigoDispositivo });
    }

    desbloquear(codigoDispositivo: string) : Observable<Dispositivo> {
        return this.http.post<Dispositivo>(this.apiUrl + '/desbloquear', { codigoDispositivo });
    }

    ativarNotificacao(codigoDispositivo: string) : Observable<Dispositivo> {
        return this.http.post<Dispositivo>(this.apiUrl + '/ativar-notificacao', { codigoDispositivo });
    }

    desativarNotificacao(codigoDispositivo: string) : Observable<Dispositivo> {
        return this.http.post<Dispositivo>(this.apiUrl + '/desativar-notificacao', { codigoDispositivo });
    }
}
