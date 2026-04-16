import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { DayOfWeek } from '../models/doctor-availability.model';
import { Doctor } from '../models/doctor.model';
import { Specialty } from '../models/specialty.model';
import { Patient, RendezVous } from '../models/patient.model';

export interface DoctorCreatePayload {
  nom: string;
  prenom: string;
  email: string;
  specialtyId: number;
  telephone?: string;
  service?: string;
  department?: string;
  /** Créneaux envoyés avec la création du médecin. */
  availabilities?: AvailabilityPayload[];
}

export interface DoctorUpdatePayload {
  nom?: string;
  prenom?: string;
  email?: string;
  specialtyId?: number;
  telephone?: string;
  service?: string;
  department?: string;
  active?: boolean;
  /** Si défini (y compris []), remplace tous les créneaux. */
  availabilities?: AvailabilityPayload[];
}

export interface AvailabilityPayload {
  dayOfWeek: DayOfWeek;
  heureDebut: string;
  heureFin: string;
}

/**
 * Microservice médecins (passerelle + JWT).
 */
@Injectable({
  providedIn: 'root'
})
export class DoctorService {
  private readonly baseUrl = environment.doctorServiceUrl;
  private readonly specUrl = environment.specialtiesServiceUrl;

  constructor(private readonly http: HttpClient) {}

  getAllDoctors(): Observable<ApiResponse<Doctor[]>> {
    return this.http
      .get<ApiResponse<Doctor[]>>(this.baseUrl)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Fiche médecin du compte connecté (email JWT = email annuaire). */
  getMe(): Observable<ApiResponse<Doctor>> {
    return this.http
      .get<ApiResponse<Doctor>>(`${this.baseUrl}/me`, { observe: 'body' })
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Patients liés (ordonnances + rendez-vous) au médecin connecté. */
  getMyPatients(): Observable<ApiResponse<Patient[]>> {
    return this.http
      .get<ApiResponse<Patient[]>>(`${this.baseUrl}/me/patients`, { observe: 'body' })
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Rendez-vous du médecin connecté (vide si service RDV indisponible). */
  getMyAppointments(): Observable<ApiResponse<RendezVous[]>> {
    return this.http
      .get<ApiResponse<RendezVous[]>>(`${this.baseUrl}/me/rendez-vous`, { observe: 'body' })
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  getDoctorById(id: number): Observable<ApiResponse<Doctor>> {
    return this.http
      .get<ApiResponse<Doctor>>(`${this.baseUrl}/${id}`)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  createDoctor(body: DoctorCreatePayload): Observable<ApiResponse<Doctor>> {
    return this.http
      .post<ApiResponse<Doctor>>(this.baseUrl, body)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  updateDoctor(id: number, body: DoctorUpdatePayload): Observable<ApiResponse<Doctor>> {
    return this.http
      .put<ApiResponse<Doctor>>(`${this.baseUrl}/${id}`, body)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  deleteDoctor(id: number): Observable<ApiResponse<void>> {
    return this.http
      .delete<ApiResponse<void>>(`${this.baseUrl}/${id}`)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Agrégation : liste des patients via patient-service (même JWT). */
  getPatientsForStaff(): Observable<ApiResponse<Patient[]>> {
    return this.http
      .get<ApiResponse<Patient[]>>(`${this.baseUrl}/patients`)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Référentiel lecture seule (choix dans le formulaire médecin). */
  getSpecialties(): Observable<ApiResponse<Specialty[]>> {
    return this.http
      .get<ApiResponse<Specialty[]>>(this.specUrl)
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
      return 'Le service médecins est temporairement indisponible.';
    }
    return error.status ? `Erreur ${error.status}` : 'Erreur réseau ou serveur';
  }
}
