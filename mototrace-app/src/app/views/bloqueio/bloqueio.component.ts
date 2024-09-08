import {Component, OnInit} from '@angular/core';
import {
    AlertComponent,
    BorderDirective,
    ButtonDirective,
    CalloutComponent,
    CardBodyComponent,
    CardComponent,
    CardHeaderComponent,
    CardTextDirective,
    CardTitleDirective,
    ColComponent,
    RowComponent,
    SpinnerComponent,
    TemplateIdDirective,
    WidgetStatFComponent
} from "@coreui/angular";
import {IconDirective} from "@coreui/icons-angular";
import {NgClass, NgIf, NgOptimizedImage} from "@angular/common";
import {Dispositivo} from "../../../models/Dispositivo";
import {DispositivoService} from "../../service/dispositivo.service";
import {BloqueioService} from "../../service/bloqueio.service";
import {ButtonComponent} from "../../components/button/button.component";
import {finalize} from "rxjs";

@Component({
    selector: 'app-bloqueio',
    standalone: true,
    imports: [
        RowComponent,
        ColComponent,
        WidgetStatFComponent,
        IconDirective,
        CardBodyComponent,
        CardComponent,
        CardHeaderComponent,
        TemplateIdDirective,
        BorderDirective,
        ButtonDirective,
        CardTextDirective,
        CardTitleDirective,
        AlertComponent,
        CalloutComponent,
        NgOptimizedImage,
        NgClass,
        NgIf,
        SpinnerComponent,
        ButtonComponent
    ],
    templateUrl: './bloqueio.component.html',
    styleUrls: ['./bloqueio.component.scss']
})
export class BloqueioComponent implements OnInit {
    dispositivos: Dispositivo[] = [];

    loading = {
        bloquear: new Map<string, any>(),
        desbloquear: new Map<string, any>(),
        ativarNotificacao: new Map<string, any>(),
        desativarNotificacao: new Map<string, any>()
    };

    constructor(private dispositivoService: DispositivoService, private bloqueioService: BloqueioService) {
    }

    ngOnInit(): void {
        this.buscarTodos();
    }

    buscarTodos() {
        this.dispositivoService.buscarTodos().subscribe({
                next: (data) => {
                    this.dispositivos = data;
                }
            }
        );
    }

    getClassCor(cor: string) {
        return `cor-${cor.toLocaleLowerCase()}`;
    }

    bloquear(dispositivo: Dispositivo) {
        const codigo = dispositivo.codigo;
        const promise = this.bloqueioService.bloquear(codigo);
        promise.subscribe({
            next: () => {
                dispositivo.bloqueado = true;
                this.loading.bloquear.set(codigo, false);
            }
        });
        this.loading.bloquear.set(codigo, promise);
    }

    desbloquear(dispositivo: Dispositivo) {
        const codigo = dispositivo.codigo;
        const promise = this.bloqueioService.desbloquear(codigo);
        promise.subscribe({
            next: () => {
                dispositivo.bloqueado = false;
                this.loading.desbloquear.set(codigo, false);
            }
        });
        this.loading.desbloquear.set(codigo, promise);
    }

    ativarNotificacao(dispositivo: Dispositivo) {
        const codigo = dispositivo.codigo;
        const promise = this.bloqueioService.ativarNotificacao(codigo);
        promise.subscribe({
            next: () => {
                dispositivo.notificacaoAtiva = true;
                this.loading.ativarNotificacao.set(codigo, false);
            }
        });
        this.loading.ativarNotificacao.set(codigo, promise);
    }

    desativarNotificacao(dispositivo: Dispositivo) {
        const codigo = dispositivo.codigo;
        const promise = this.bloqueioService.desativarNotificacao(codigo);
        promise.subscribe({
            next: () => {
                dispositivo.notificacaoAtiva = false;
                this.loading.desativarNotificacao.set(codigo, false);
            }
        });
        this.loading.desativarNotificacao.set(codigo, promise);
    }
}
