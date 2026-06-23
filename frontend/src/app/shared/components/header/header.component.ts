import {Component, inject} from '@angular/core';
import {AuthService} from "../../../core/services/auth.service";
import {MatToolbar} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {RouterLink} from "@angular/router";
import {MatButton} from "@angular/material/button";

@Component({
    selector: 'app-header',
    standalone: true,
    imports: [
        MatToolbar,
        MatIcon,
        MatMenuTrigger,
        MatMenu,
        MatMenuItem,
        RouterLink,
        MatButton
    ],
    templateUrl: './header.component.html',
    styleUrl: './header.component.scss',
})
export class HeaderComponent {
    readonly authService = inject(AuthService);

    get isLoggedIn() {
        return this.authService.isLoggedIn();
    }

    get role() {
        return this.authService.role();
    }

    logout() {
        this.authService.logout();
    }
}
