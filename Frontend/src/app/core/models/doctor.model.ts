import { DoctorAvailability } from './doctor-availability.model';

export interface Doctor {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  /** Libellé de la spécialité (réponse API). */
  specialite: string;
  specialtyId?: number;
  telephone?: string;
  service?: string;
  /** Créneaux (plus de CRUD séparé côté API). */
  availabilities?: DoctorAvailability[];
}
