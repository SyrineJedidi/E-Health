export type PrescriptionStatus = 'ACTIVE' | 'EXPIRED' | 'CANCELLED' | 'DISPENSED';

export type MedicationForm = 'TABLET' | 'CAPSULE' | 'SYRUP' | 'INJECTION' | 'OTHER';

/** Entrée du catalogue (GET /api/medications). */
export interface MedicationCatalog {
  id: number;
  name: string;
  dosage?: string | null;
  form: MedicationForm;
  description?: string | null;
}

/** Ligne d’ordonnance (tableau `medications` dans les requêtes/réponses). */
export interface MedicationDto {
  id?: number | null;
  medicationId?: number | null;
  name?: string | null;
  dosage: string;
  frequency: string;
  durationDays?: number | null;
  instructions?: string | null;
  form?: MedicationForm | null;
}

export interface PrescriptionRequestPayload {
  patientId: number;
  doctorId: number;
  status?: PrescriptionStatus | null;
  expirationDate?: string | null;
  diagnosis?: string | null;
  notes?: string | null;
  medications: MedicationDto[];
}

export interface Prescription {
  id: number;
  patientId: number;
  doctorId: number;
  issuedAt: string;
  expirationDate?: string | null;
  diagnosis?: string | null;
  notes?: string | null;
  /** Message laissé par le patient pour son médecin. */
  patientComment?: string | null;
  status: PrescriptionStatus;
  medications: MedicationDto[];
}
