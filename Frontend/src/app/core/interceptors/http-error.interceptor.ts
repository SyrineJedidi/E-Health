import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { LoadingService } from '../services/loading.service';
import { ToastService } from '../services/toast.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(
    private readonly loading: LoadingService,
    private readonly injector: Injector,
    private readonly toast: ToastService
  ) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    this.loading.begin();

    const withJsonBody = ['POST', 'PUT', 'PATCH'].includes(req.method);
    // Le dev server Angular (Vite) réécrit les GET vers index.html si Accept contient */* (défaut HttpClient) : JSON attendu → erreur de parsing.
    const isApiCall = this.isApiPath(req.url);
    const headers: Record<string, string> = {};
    if (isApiCall) {
      headers['Accept'] = 'application/json';
    }
    if (withJsonBody) {
      headers['Content-Type'] = 'application/json';
    }
    const outgoing =
      Object.keys(headers).length > 0 ? req.clone({ setHeaders: headers }) : req;

    return next.handle(outgoing).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          const auth = this.injector.get(AuthService);
          const router = this.injector.get(Router);
          auth.logout();
          void router.navigate(['/login'], { queryParams: { returnUrl: router.url } });
        } else if (error.status === 0) {
          this.toast.warning(
            'Serveur inaccessible. Vérifiez la passerelle API et que les microservices sont démarrés.'
          );
        } else if (error.status === 500) {
          // Les échecs GET (listes, dashboard) sont gérés dans les composants ; éviter toast + bannière.
          if (req.method !== 'GET') {
            this.toast.error('Erreur serveur. Réessayez plus tard ou contactez le support.');
          }
        }
        return throwError(() => error);
      }),
      finalize(() => this.loading.end())
    );
  }

  private isApiPath(url: string): boolean {
    try {
      const path = url.startsWith('http') ? new URL(url).pathname : url.split('?')[0];
      return path.startsWith('/api');
    } catch {
      return url.includes('/api/');
    }
  }
}
