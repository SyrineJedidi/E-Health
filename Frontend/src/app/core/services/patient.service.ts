import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { DossierMedical, Patient } from '../models/patient.model';

/**
 * Accès HTTP au microservice patients (`ApiResponse<T>` encapsulé).
 */
@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private readonly baseUrl = environment.patientServiceUrl;

  constructor(private readonly http: HttpClient) {}

  /** Liste tous les patients. */
  getAllPatients(): Observable<ApiResponse<Patient[]>> {
    return this.http
      .get<ApiResponse<Patient[]>>(this.baseUrl)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Détail d'un patient par identifiant. */
  getPatientById(id: number): Observable<ApiResponse<Patient>> {
    return this.http
      .get<ApiResponse<Patient>>(`${this.baseUrl}/${id}`)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Crée un patient. */
  createPatient(p: Patient): Observable<ApiResponse<Patient>> {
    return this.http
      .post<ApiResponse<Patient>>(this.baseUrl, p)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Met à jour un patient. */
  updatePatient(id: number, p: Patient): Observable<ApiResponse<Patient>> {
    return this.http
      .put<ApiResponse<Patient>>(`${this.baseUrl}/${id}`, p)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Supprime un patient. */
  deletePatient(id: number): Observable<ApiResponse<null>> {
    return this.http
      .delete<ApiResponse<null>>(`${this.baseUrl}/${id}`)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Recherche par nom (paramètre query côté API). */
  searchByNom(nom: string): Observable<ApiResponse<Patient[]>> {
    const params = new HttpParams().set('nom', nom);
    return this.http
      .get<ApiResponse<Patient[]>>(`${this.baseUrl}/search`, { params })
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Dossier médical (patient + rendez-vous). */
  getDossierMedical(id: number): Observable<ApiResponse<DossierMedical>> {
    return this.http
      .get<ApiResponse<DossierMedical>>(`${this.baseUrl}/${id}/dossier`)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Dossier du patient connecté (JWT = email), pour le portail. */
  getMyDossier(): Observable<ApiResponse<DossierMedical>> {
    return this.http
      .get<ApiResponse<DossierMedical>>(`${this.baseUrl}/me/dossier`, { observe: 'body' })
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    const message = this.userFacingMessage(error);
    return throwError(() => new Error(message));
  }

  private userFacingMessage(error: HttpErrorResponse): string {
    if (error.error && typeof error.error === 'object' && 'message' in error.error) {
      return String((error.error as { message: string }).message);
    }
    if (error.status === 0) {
      return 'Serveur inaccessible (réseau ou passerelle arrêtée).';
    }
    if (error.status >= 500) {
      return 'Le service patients est temporairement indisponible.';
    }
    return error.status ? `Erreur ${error.status}` : 'Erreur réseau ou serveur';
  }
}
