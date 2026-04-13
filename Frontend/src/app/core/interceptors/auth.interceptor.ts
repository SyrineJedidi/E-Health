import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly injector: Injector) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    if (!this.requiresAuth(req.url)) {
      return next.handle(req);
    }
    const token = this.injector.get(AuthService).getToken();
    if (!token) {
      return next.handle(req);
    }
    const authReq = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next.handle(authReq);
  }

  private requiresAuth(url: string): boolean {
    const path = url.startsWith('http') ? new URL(url).pathname : url.split('?')[0];
    // Auth : seules ces routes sont publiques sans jeton
    if (
      path.startsWith('/api/auth/login') ||
      path.startsWith('/api/auth/register') ||
      path.startsWith('/api/auth/forgot-password') ||
      path.startsWith('/api/auth/reset-password')
    ) {
      return false;
    }
    // Tout le reste sous /api (médecins, spécialités, patients, ordonnances, etc.) : envoyer le JWT si présent
    return path.startsWith('/api/');
  }
}
