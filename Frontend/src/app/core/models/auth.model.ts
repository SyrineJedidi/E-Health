export interface AuthResponse {
  token: string | null;
  tokenType: string | null;
  userId: string | null;
  email: string | null;
  firstName: string | null;
  lastName: string | null;
  role: string | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export type UserRole = 'PATIENT' | 'DOCTOR' | 'ADMIN';

export interface RegisterRequest {
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  role: UserRole;
}

export interface ForgotPasswordResponse {
  message: string;
  resetToken?: string | null;
}
