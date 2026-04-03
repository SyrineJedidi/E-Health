export interface Appointment {
  id?: number;
  date: string;
  heure: string;
  motif: string;
  statut: string;
  patientId: number;
  medecinId: number;
}
