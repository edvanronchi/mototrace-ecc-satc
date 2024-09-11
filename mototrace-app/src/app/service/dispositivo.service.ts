import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Dispositivo} from "../../models/Dispositivo";
import {API_URL_DISPOSITIVOS} from "../constants/constants";

@Injectable({
    providedIn: 'root'
})
export class DispositivoService {
    private apiUrl: string = API_URL_DISPOSITIVOS + '/dispositivos';

    constructor(private http: HttpClient) {
    }

    buscarTodos() : Observable<Dispositivo[]> {
        return this.http.get<Dispositivo[]>(this.apiUrl);
    }

    buscar(id: number) : Observable<Dispositivo> {
        return this.http.get<Dispositivo>(`${this.apiUrl}/${id}`);
    }

    salvar(dispositivo: Partial<Dispositivo>) : Observable<Dispositivo> {
        return this.http.post<Dispositivo>(this.apiUrl, dispositivo);
    }

    atualizar(dispositivo: Partial<Dispositivo>) : Observable<Dispositivo> {
        return this.http.put<Dispositivo>(`${this.apiUrl}/${dispositivo.id}`, dispositivo);
    }

    remover(id: number) : Observable<Dispositivo> {
        return this.http.delete<Dispositivo>(`${this.apiUrl}/${id}`);
    }
}
