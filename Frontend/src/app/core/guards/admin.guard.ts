import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Back-office complet : administrateurs uniquement. */
@Injectable({ providedIn: 'root' })
export class AdminGuard {
  constructor(
    private readonly auth: AuthService,
    private readonly router: Router
  ) {}

  canActivate(): boolean | UrlTree {
    if (this.auth.isAdmin()) {
      return true;
    }
    if (this.auth.getRole() === 'DOCTOR') {
      return this.router.createUrlTree(['/cabinet'], {
        queryParams: { info: 'admin_only' }
      });
    }
    return this.router.createUrlTree(['/portail/accueil'], {
      queryParams: { info: 'admin_only' }
    });
  }
}
