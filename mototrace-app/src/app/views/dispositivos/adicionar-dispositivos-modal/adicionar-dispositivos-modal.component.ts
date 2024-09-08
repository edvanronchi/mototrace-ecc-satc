import {Component, EventEmitter, input, OnInit, Output} from '@angular/core';
import {
    ButtonCloseDirective,
    ButtonDirective, ColComponent, FormControlDirective, FormLabelDirective, FormSelectDirective,
    ModalBodyComponent,
    ModalComponent,
    ModalFooterComponent,
    ModalHeaderComponent,
    ModalTitleDirective, RowComponent,
    ThemeDirective
} from "@coreui/angular";
import {Dispositivo} from "../../../../models/Dispositivo";
import {DispositivoService} from "../../../service/dispositivo.service";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgClass, NgStyle} from "@angular/common";
import {Cor} from "../../../../models/Cor";

@Component({
    selector: 'app-adicionar-dispositivos-modal',
    standalone: true,
    imports: [ButtonDirective, ModalComponent, ModalHeaderComponent, ModalTitleDirective, ThemeDirective, ButtonCloseDirective, ModalBodyComponent, ModalFooterComponent, ColComponent, RowComponent, FormControlDirective, ReactiveFormsModule, FormsModule, FormLabelDirective, FormSelectDirective, NgStyle, NgClass],
    templateUrl: './adicionar-dispositivos-modal.component.html',
    styleUrls: ['./adicionar-dispositivos-modal.component.scss']
})
export class AdicionarDispositivosModalComponent {
    @Output() close: EventEmitter<void> = new EventEmitter<void>();
    @Output() refresh: EventEmitter<void> = new EventEmitter<void>();

    visible: boolean = false;
    isEdit: boolean = false;
    selectedColorClass: string = 'cor-azul';
    dispositivo: Partial<Dispositivo> = {
        cor: Cor.AZUL
    };

    constructor(private dispositivoService: DispositivoService) {
    }

    toggleOpen(id?: number) {
        this.visible = !this.visible;
        this.isEdit = !!id;

        if (id) {
            this.buscar(id)
        }
    }

    updateColorClass(): void {
        if (!this.dispositivo.cor) {
            return;
        }
        this.selectedColorClass = 'cor-' + this.dispositivo.cor.toLocaleLowerCase();
    }

    handleOpenChange(event: any) {
        this.visible = event;
        if (!event) {
            this.closeModal();
        }
    }

    buscar(id: number) {
        this.dispositivoService.buscar(id).subscribe(dispositivo => {
            this.dispositivo = dispositivo;
            this.updateColorClass();
        });
    }

    onSave() {
        console.log(this.dispositivo)
        if (this.isEdit) {
            this.dispositivoService.atualizar(this.dispositivo).subscribe({next: () => this.afterSave()});
            return;
        }
        this.dispositivoService.salvar(this.dispositivo).subscribe({next: () => this.afterSave()});
    }

    afterSave() {
        this.closeModal();
        this.refreshTable();
    }

    closeModal() {
        this.close.emit();
    }

    refreshTable() {
        this.refresh.emit();
    }
}
