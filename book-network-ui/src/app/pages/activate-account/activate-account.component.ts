import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/services';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.css'],
})
export class ActivateAccountComponent {
  message: string = '';
  isOkay: boolean = true;
  submitted: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthenticationService
  ) {}

  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  }
  confirmAccount(token: string) {
    this.authService
      .confirm({
        token,
      })
      .subscribe({
        next: () => {
          this.message =
            'Tu cuenta fue activada con éxito.\nPuedes iniciar sesió ahora';
          this.submitted = true;
          this.isOkay = true;
        },
        error: () => {
          this.message = 'Tu token expiro o es invalido';
          this.submitted = true;
          this.isOkay = false;
        },
      });
  }

  redirectToLogin() {
    this.router.navigate(['/login']);
  }
}
