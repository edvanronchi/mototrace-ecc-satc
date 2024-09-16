import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Coordenada} from "../../models/Coordenada";
import {URL_GATEWAY_API} from "../constants/constants";

@Injectable({
    providedIn: 'root'
})
export class MapaService {
    private apiUrl: string = URL_GATEWAY_API + '/worker-position/coordenadas';

    constructor(private http: HttpClient) {
    }

    buscarCoordenadas() : Observable<Coordenada[]> {
        return this.http.get<Coordenada[]>(this.apiUrl);
    }

    buscarUltimasCoordenadas(dispositivosCodigo: string) : Observable<Coordenada[]> {
        return this.http.get<Coordenada[]>(this.apiUrl + '/ultimas-coordenadas', {params: {dispositivosCodigo}});
    }
}
