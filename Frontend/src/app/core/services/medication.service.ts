import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MedicationCatalog } from '../models/prescription.model';

@Injectable({ providedIn: 'root' })
export class MedicationService {
  private readonly baseUrl: string =
    environment.medicationServiceUrl ?? '/api/medications';

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<MedicationCatalog[]> {
    return this.http
      .get<MedicationCatalog[]>(this.baseUrl, { observe: 'body' })
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  search(q: string): Observable<MedicationCatalog[]> {
    let params = new HttpParams();
    const trimmed = q?.trim();
    if (trimmed) {
      params = params.set('q', trimmed);
    }
    return this.http
      .get<MedicationCatalog[]>(`${this.baseUrl}/search`, { observe: 'body', params })
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let message = 'Erreur réseau ou serveur';
    const body = error.error;
    if (body && typeof body === 'object') {
      const o = body as Record<string, unknown>;
      if (typeof o['message'] === 'string') {
        message = o['message'] as string;
      }
    } else if (error.message) {
      message = error.message;
    }
    return throwError(() => new Error(message));
  }
}
