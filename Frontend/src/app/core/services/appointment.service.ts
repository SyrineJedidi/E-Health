import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Appointment } from '../models/appointment.model';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private readonly baseUrl = environment.appointmentServiceUrl;

  constructor(private readonly http: HttpClient) {}

  /** Liste tous les rendez-vous. */
  getAllAppointments(): Observable<ApiResponse<Appointment[]>> {
    return this.http
      .get<ApiResponse<Appointment[]>>(this.baseUrl)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Rendez-vous pour un patient. */
  getByPatientId(patientId: number): Observable<ApiResponse<Appointment[]>> {
    return this.http
      .get<ApiResponse<Appointment[]>>(`${this.baseUrl}/patient/${patientId}`)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    const message =
      error.error && typeof error.error === 'object' && 'message' in error.error
        ? String((error.error as { message: string }).message)
        : error.message || 'Erreur réseau ou serveur';
    return throwError(() => new Error(message));
  }
}
