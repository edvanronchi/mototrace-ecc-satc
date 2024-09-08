import {Component, OnInit, ViewChild} from '@angular/core';
import {
    ButtonDirective,
    CardBodyComponent,
    CardComponent,
    CardHeaderComponent,
    ColComponent,
    RowComponent,
    TableDirective,
    TextColorDirective
} from '@coreui/angular';
import {IconComponent, IconDirective} from "@coreui/icons-angular";
import {
    AdicionarDispositivosModalComponent
} from "./adicionar-dispositivos-modal/adicionar-dispositivos-modal.component";
import {DispositivoService} from "../../service/dispositivo.service";
import {Dispositivo} from "../../../models/Dispositivo";
import {NgClass, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";

@Component({
    templateUrl: 'dispositivos.component.html',
    styleUrls: ['./dispositivos.component.scss'],
    standalone: true,
    imports: [
        TextColorDirective, CardComponent, CardHeaderComponent, CardBodyComponent, RowComponent, TableDirective, IconComponent, IconDirective, ButtonDirective, ColComponent, AdicionarDispositivosModalComponent, NgClass, NgIf, FormsModule]
})
export class DispositivosComponent implements OnInit {
    @ViewChild(AdicionarDispositivosModalComponent) childComponent!: AdicionarDispositivosModalComponent;

    modalVisible: boolean = true;
    dispositivos: Dispositivo[] = [];
    pesquisa: string = '';

    constructor(private dispositivoService: DispositivoService) {
    }

    ngOnInit(): void {
        this.buscarTodos();
    }

    openModalAdicionarDispositivos(id?: number) {
        this.childComponent.toggleOpen(id);
    }

    destroyModal() {
        this.modalVisible = false;
        setTimeout(() => {
            this.modalVisible = true;
        }, 100);
    }

    getClassCor(cor: string) {
        return `cor-${cor.toLocaleLowerCase()}`;
    }

    remover(id: number) {
        this.dispositivoService.remover(id).subscribe({
                next: () => {
                    this.buscarTodos();
                }
            }
        );
    }

    buscarTodos() {
        this.dispositivoService.buscarTodos().subscribe({
                next: (data) => {
                    this.dispositivos = data;
                }
            }
        );
    }

    filtroDispositivos() {
        return this.dispositivos.filter(dispositivo =>
            dispositivo.nome.toLowerCase().includes(this.pesquisa.toLowerCase())
        );
    }
}
