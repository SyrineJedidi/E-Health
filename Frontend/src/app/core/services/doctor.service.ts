import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Doctor } from '../models/doctor.model';

@Injectable({
  providedIn: 'root'
})
export class DoctorService {
  private readonly baseUrl = environment.doctorServiceUrl;

  constructor(private readonly http: HttpClient) {}

  /** Liste tous les médecins. */
  getAllDoctors(): Observable<ApiResponse<Doctor[]>> {
    return this.http
      .get<ApiResponse<Doctor[]>>(this.baseUrl)
      .pipe(catchError((err: HttpErrorResponse) => this.handleError(err)));
  }

  /** Détail d'un médecin. */
  getDoctorById(id: number): Observable<ApiResponse<Doctor>> {
    return this.http
      .get<ApiResponse<Doctor>>(`${this.baseUrl}/${id}`)
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
