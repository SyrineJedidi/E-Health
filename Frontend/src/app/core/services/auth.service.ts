import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, catchError, map, tap, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  AuthResponse,
  ForgotPasswordResponse,
  LoginRequest,
  RegisterRequest,
  UserRole
} from '../models/auth.model';

const TOKEN_KEY = 'ehealth_jwt';
const ROLE_KEY = 'ehealth_role';

/**
 * Authentification JWT (session `sessionStorage`), état de connexion réactif.
 * Le rôle est stocké séparément pour le routage front-office / back-office.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly base = environment.authBaseUrl;

  private readonly authState = new BehaviorSubject<boolean>(this.hasToken());

  readonly loggedIn$ = this.authState.asObservable();

  constructor(private readonly http: HttpClient) {}

  hasToken(): boolean {
    return !!sessionStorage.getItem(TOKEN_KEY);
  }

  getToken(): string | null {
    return sessionStorage.getItem(TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return this.hasToken();
  }

  /** Rôle issu de la dernière connexion / inscription (chaîne API). */
  getRole(): UserRole | null {
    const r = sessionStorage.getItem(ROLE_KEY);
    if (r === 'PATIENT' || r === 'DOCTOR' || r === 'ADMIN') {
      return r;
    }
    return null;
  }

  /** Médecin ou administrateur → accès back-office. */
  isStaff(): boolean {
    const r = this.getRole();
    return r === 'DOCTOR' || r === 'ADMIN';
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  /** Patient uniquement. */
  isPatient(): boolean {
    return this.getRole() === 'PATIENT';
  }

  /** Cible par défaut après connexion selon le rôle. */
  defaultHomeUrl(): string {
    const r = this.getRole();
    if (r === 'ADMIN') {
      return '/admin/dashboard';
    }
    if (r === 'DOCTOR') {
      return '/cabinet';
    }
    return '/portail/accueil';
  }

  login(body: LoginRequest): Observable<void> {
    return this.http.post<AuthResponse>(`${this.base}/login`, body).pipe(
      tap((res) => this.persistSession(res)),
      map(() => undefined),
      catchError((err) => this.mapAuthError(err))
    );
  }

  register(body: RegisterRequest): Observable<void> {
    return this.http.post<AuthResponse>(`${this.base}/register`, body).pipe(
      tap((res) => this.persistSession(res)),
      map(() => undefined),
      catchError((err) => this.mapAuthError(err))
    );
  }

  me(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${this.base}/me`);
  }

  forgotPassword(email: string): Observable<ForgotPasswordResponse> {
    return this.http
      .post<ForgotPasswordResponse>(`${this.base}/forgot-password`, { email: email.trim() })
      .pipe(catchError((err) => this.mapAuthError(err)));
  }

  resetPassword(token: string, newPassword: string): Observable<void> {
    return this.http
      .post<{ message: string }>(`${this.base}/reset-password`, { token: token.trim(), newPassword })
      .pipe(
        map(() => undefined),
        catchError((err) => this.mapAuthError(err))
      );
  }

  changePassword(currentPassword: string, newPassword: string): Observable<void> {
    return this.http
      .post<{ message: string }>(`${this.base}/change-password`, {
        currentPassword,
        newPassword
      })
      .pipe(
        map(() => undefined),
        catchError((err) => this.mapAuthError(err))
      );
  }

  logout(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(ROLE_KEY);
    this.authState.next(false);
  }

  private persistSession(res: AuthResponse): void {
    if (res.token) {
      sessionStorage.setItem(TOKEN_KEY, res.token);
      this.authState.next(true);
    }
    if (res.role) {
      sessionStorage.setItem(ROLE_KEY, res.role);
    } else {
      sessionStorage.removeItem(ROLE_KEY);
    }
  }

  private mapAuthError(err: HttpErrorResponse) {
    let msg = 'Erreur réseau ou serveur';
    if (err.error && typeof err.error === 'object' && 'message' in err.error) {
      msg = String((err.error as { message: unknown }).message);
    } else if (err.status === 401) {
      msg = 'Email ou mot de passe incorrect';
    } else if (err.status === 409) {
      msg = 'Un compte existe déjà avec cet email';
    } else if (err.status === 503) {
      msg =
        'Service indisponible (vérifiez que MongoDB est démarré sur localhost:27017).';
    } else if (err.status === 400) {
      msg =
        err.error && typeof err.error === 'object' && 'message' in err.error
          ? String((err.error as { message: unknown }).message)
          : 'Requête invalide';
    }
    return throwError(() => new Error(msg));
  }
}
