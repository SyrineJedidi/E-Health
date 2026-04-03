export interface Patient {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  telephone?: string;
  adresse?: string;
  dateNaissance?: string;
  groupeSanguin?: string;
  createdAt?: string;
}

export interface RendezVous {
  id: number;
  date: string;
  heure: string;
  motif: string;
  statut: string;
  patientId: number;
  medecinId: number;
}

export interface DossierMedical {
  patient: Patient;
  rendezVous: RendezVous[];
  message: string;
}
