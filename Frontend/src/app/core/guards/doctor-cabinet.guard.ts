import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Espace médecin (cabinet) : rôle DOCTOR uniquement. */
@Injectable({ providedIn: 'root' })
export class DoctorCabinetGuard {
  constructor(
    private readonly auth: AuthService,
    private readonly router: Router
  ) {}

  canActivate(): boolean | UrlTree {
    if (this.auth.getRole() === 'DOCTOR') {
      return true;
    }
    if (this.auth.isAdmin()) {
      return this.router.createUrlTree(['/admin/dashboard']);
    }
    return this.router.createUrlTree(['/portail/accueil'], {
      queryParams: { info: 'doctor_only' }
    });
  }
}
