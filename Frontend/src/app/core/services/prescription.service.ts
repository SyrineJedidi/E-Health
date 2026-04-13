import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, map, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Prescription, PrescriptionRequestPayload } from '../models/prescription.model';

/**
 * Accès HTTP au microservice ordonnances (via la passerelle `/api/prescriptions`).
 */
@Injectable({ providedIn: 'root' })
export class PrescriptionService {
  private readonly baseUrl = environment.prescriptionServiceUrl;

  constructor(private readonly http: HttpClient) {}

  /** Liste toutes les ordonnances. */
  getAll(): Observable<Prescription[]> {
    return this.http
      .get<Prescription[]>(this.baseUrl, { observe: 'body' })
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Ordonnances du patient connecté (JWT + fiche patient même email). */
  getMine(): Observable<Prescription[]> {
    return this.http
      .get<Prescription[]>(`${this.baseUrl}/me`, { observe: 'body' })
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Détail d’une ordonnance par identifiant. */
  getById(id: number): Observable<Prescription> {
    return this.http
      .get<Prescription>(`${this.baseUrl}/${id}`)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Crée une ordonnance (patient, médecin, médicaments). */
  create(payload: PrescriptionRequestPayload): Observable<Prescription> {
    return this.http
      .post<Prescription>(this.baseUrl, payload)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Met à jour une ordonnance existante. */
  update(id: number, payload: PrescriptionRequestPayload): Observable<Prescription> {
    return this.http
      .put<Prescription>(`${this.baseUrl}/${id}`, payload)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Supprime une ordonnance (réponse 204). */
  delete(id: number): Observable<void> {
    return this.http
      .delete(`${this.baseUrl}/${id}`, { observe: 'response', responseType: 'text' })
      .pipe(
        map(() => undefined),
        catchError((err: HttpErrorResponse) => this.handleError(err))
      );
  }

  /** Commentaire du patient pour le médecin (texte vide = effacement). */
  updatePatientComment(id: number, comment: string): Observable<Prescription> {
    return this.http
      .patch<Prescription>(`${this.baseUrl}/${id}/patient-comment`, { comment }, { observe: 'body' })
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
      const rawErrors = o['errors'];
      if (rawErrors && typeof rawErrors === 'object' && rawErrors !== null) {
        const errs = rawErrors as Record<string, string>;
        const details = Object.values(errs).filter(Boolean).join(' · ');
        if (details) {
          message = message ? `${message} — ${details}` : details;
        }
      }
    } else if (error.message) {
      message = error.message;
    }
    return throwError(() => new Error(message));
  }
}
