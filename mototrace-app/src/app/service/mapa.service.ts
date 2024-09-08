import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Cordenada} from "../../models/Cordenada";

@Injectable({
    providedIn: 'root'
})
export class MapaService {
    private apiUrl: string = 'http://localhost:8091/api/v1/cordenadas';

    constructor(private http: HttpClient) {
    }

    buscarCordenadas() : Observable<Cordenada[]> {
        return this.http.get<Cordenada[]>(this.apiUrl);
    }

    buscarUltimasCordenadas(dispositivosCodigo: string) : Observable<Cordenada[]> {
        return this.http.get<Cordenada[]>(this.apiUrl + '/ultimas-cordenadas', {params: {dispositivosCodigo}});
    }
}
