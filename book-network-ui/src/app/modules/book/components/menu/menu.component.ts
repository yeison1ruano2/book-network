import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from 'src/app/services/services';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
})
export class MenuComponent implements OnInit {
  usernameActual: string = '';
  constructor(private authenticationService: AuthenticationService) {}
  ngOnInit(): void {
    const linkColor = document.querySelectorAll('.nav-link');
    linkColor.forEach((link) => {
      if (window.location.href.endsWith(link.getAttribute('href') || '')) {
        link.classList.add('active');
      }
      link.addEventListener('click', () => {
        linkColor.forEach((l) => l.classList.remove('active'));
        link.classList.add('active');
      });
    });
    this.getCurrentUsername();
  }
  logout() {
    localStorage.removeItem('token');
    window.location.reload();
  }

  getCurrentUsername() {
    this.usernameActual = '';
    this.authenticationService.getCurrentUsername().subscribe({
      next: (res: any) => {
        this.usernameActual = res.firstname;
      },
    });
  }
}
