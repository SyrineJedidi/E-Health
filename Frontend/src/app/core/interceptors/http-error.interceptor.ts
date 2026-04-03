import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, finalize, throwError } from 'rxjs';
import { LoadingService } from '../services/loading.service';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private readonly loading: LoadingService) {}

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
        if (error.status === 0) {
          console.error('Server unreachable');
        } else if (error.status === 404) {
          console.error('Resource not found');
        } else if (error.status === 500) {
          console.error('Internal server error');
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
