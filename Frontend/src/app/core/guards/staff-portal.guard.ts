import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Page portail « Médecins » : réservée au personnel (aperçu / démo). */
@Injectable({ providedIn: 'root' })
export class StaffPortalGuard {
  constructor(
    private readonly auth: AuthService,
    private readonly router: Router
  ) {}

  canActivate(): boolean | UrlTree {
    if (this.auth.isAdmin()) {
      return true;
    }
    return this.router.createUrlTree(['/portail/accueil'], {
      queryParams: { info: 'staff_feature' }
    });
  }
}
