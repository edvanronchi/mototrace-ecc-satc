import {booleanAttribute, Component, EventEmitter, Input, Output} from '@angular/core';
import {ButtonDirective, SpinnerComponent} from "@coreui/angular";
import {isObservable, Observable} from "rxjs";

@Component({
    selector: 'app-button',
    standalone: true,
    imports: [
        ButtonDirective,
        SpinnerComponent
    ],
    templateUrl: './button.component.html'
})
export class ButtonComponent {
    private _loading: boolean | null = false;

    @Input() set loading(value: boolean | Observable<any> | null) {
        if (isObservable(value)) {
            this._loading = true;
            value.subscribe({error: () => this._loading = false});
        } else {
            this._loading = false;
        }
    }

    @Input() color = 'primary';
    @Output() buttonClick = new EventEmitter<void>();

    onClick() {
        this.buttonClick.emit();
    }

    get isLoading(): boolean {
        return this._loading ?? false;
    }
}
