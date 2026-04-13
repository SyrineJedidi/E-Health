import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

/** Routes réservées aux comptes patient (portail). */
@Injectable({ providedIn: 'root' })
export class PatientPortalGuard {
  constructor(
    private readonly auth: AuthService,
    private readonly router: Router
  ) {}

  canActivate(): boolean | UrlTree {
    if (this.auth.isPatient()) {
      return true;
    }
    if (this.auth.getRole() === 'DOCTOR') {
      return this.router.createUrlTree(['/cabinet'], {
        queryParams: { info: 'patient_portal_only' }
      });
    }
    return this.router.createUrlTree(['/admin/dashboard'], {
      queryParams: { info: 'patient_portal_only' }
    });
  }
}
