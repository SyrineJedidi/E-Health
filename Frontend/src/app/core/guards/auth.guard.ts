import { Injectable } from '@angular/core';
import { Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard {
  constructor(
    private readonly auth: AuthService,
    private readonly router: Router
  ) {}

  canActivate(_route: unknown, state: RouterStateSnapshot): boolean | UrlTree {
    if (this.auth.isLoggedIn()) {
      return true;
    }
    return this.router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url, authRequired: '1' }
    });
  }
}
