import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';
import { DayOfWeek, DoctorAvailability } from '../../core/models/doctor-availability.model';
import { Doctor } from '../../core/models/doctor.model';
import { Patient } from '../../core/models/patient.model';
import { Specialty } from '../../core/models/specialty.model';
import { ToastService } from '../../core/services/toast.service';
import { AvailabilityPayload, DoctorService } from '../../core/services/doctor.service';

type AdminTab = 'doctors' | 'specialties' | 'patients';

@Component({
  selector: 'app-doctors-admin',
  templateUrl: './doctors-admin.component.html',
  styleUrl: './doctors-admin.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DoctorsAdminComponent implements OnInit {
  activeTab: AdminTab = 'doctors';

  doctors: Doctor[] = [];
  specialties: Specialty[] = [];
  patients: Patient[] = [];

  loadingDoctors = false;
  loadingSpecs = false;
  loadingPatients = false;
  errorDoctors = '';
  errorSpecs = '';
  errorPatients = '';

  /** Formulaire nouveau médecin + créneaux (envoyés avec POST /api/doctors). */
  newDoc = {
    nom: '',
    prenom: '',
    email: '',
    specialtyId: null as number | null,
    telephone: '',
    service: ''
  };

  /** Créneaux à créer avec le médecin (pas de CRUD séparé). */
  pendingAvailabilities: AvailabilityPayload[] = [];
  newSlot = {
    dayOfWeek: 'MONDAY' as DayOfWeek,
    heureDebut: '09:00',
    heureFin: '12:00'
  };

  /** Mise à jour des créneaux d’un médecin existant (PUT). */
  editDoctorId: number | null = null;
  editAvailabilities: AvailabilityPayload[] = [];

  readonly days: { value: DayOfWeek; label: string }[] = [
    { value: 'MONDAY', label: 'Lundi' },
    { value: 'TUESDAY', label: 'Mardi' },
    { value: 'WEDNESDAY', label: 'Mercredi' },
    { value: 'THURSDAY', label: 'Jeudi' },
    { value: 'FRIDAY', label: 'Vendredi' },
    { value: 'SATURDAY', label: 'Samedi' },
    { value: 'SUNDAY', label: 'Dimanche' }
  ];

  private readonly doctorApi = inject(DoctorService);
  private readonly toast = inject(ToastService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.loadDoctors();
    this.loadSpecialties();
  }

  setTab(tab: AdminTab): void {
    this.activeTab = tab;
    if (tab === 'patients' && this.patients.length === 0 && !this.loadingPatients) {
      this.loadPatients();
    }
    this.cdr.markForCheck();
  }

  loadDoctors(): void {
    this.loadingDoctors = true;
    this.errorDoctors = '';
    this.cdr.markForCheck();
    this.doctorApi.getAllDoctors().subscribe({
      next: (res) => {
        this.doctors = res.data ?? [];
        this.loadingDoctors = false;
        this.cdr.markForCheck();
      },
      error: (e: Error) => {
        this.errorDoctors = e.message;
        this.loadingDoctors = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadSpecialties(): void {
    this.loadingSpecs = true;
    this.errorSpecs = '';
    this.cdr.markForCheck();
    this.doctorApi.getSpecialties().subscribe({
      next: (res) => {
        this.specialties = res.data ?? [];
        this.loadingSpecs = false;
        this.cdr.markForCheck();
      },
      error: (e: Error) => {
        this.errorSpecs = e.message;
        this.loadingSpecs = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadPatients(): void {
    this.loadingPatients = true;
    this.errorPatients = '';
    this.cdr.markForCheck();
    this.doctorApi.getPatientsForStaff().subscribe({
      next: (res) => {
        this.patients = res.data ?? [];
        this.loadingPatients = false;
        this.cdr.markForCheck();
      },
      error: (e: Error) => {
        this.errorPatients = e.message;
        this.loadingPatients = false;
        this.cdr.markForCheck();
      }
    });
  }

  addPendingSlot(): void {
    const hd = this.normalizeTime(this.newSlot.heureDebut);
    const hf = this.normalizeTime(this.newSlot.heureFin);
    this.pendingAvailabilities = [
      ...this.pendingAvailabilities,
      { dayOfWeek: this.newSlot.dayOfWeek, heureDebut: hd, heureFin: hf }
    ];
    this.cdr.markForCheck();
  }

  removePendingSlot(index: number): void {
    this.pendingAvailabilities = this.pendingAvailabilities.filter((_, i) => i !== index);
    this.cdr.markForCheck();
  }

  createDoctor(): void {
    if (!this.newDoc.nom.trim() || !this.newDoc.prenom.trim() || !this.newDoc.email.trim()) {
      this.toast.error('Nom, prénom et email sont obligatoires.');
      return;
    }
    if (this.newDoc.specialtyId == null) {
      this.toast.error('Choisissez une spécialité.');
      return;
    }
    const body = {
      nom: this.newDoc.nom.trim(),
      prenom: this.newDoc.prenom.trim(),
      email: this.newDoc.email.trim(),
      specialtyId: this.newDoc.specialtyId,
      telephone: this.newDoc.telephone.trim() || undefined,
      service: this.newDoc.service.trim() || undefined,
      availabilities: this.pendingAvailabilities.length > 0 ? this.pendingAvailabilities : undefined
    };
    this.doctorApi.createDoctor(body).subscribe({
      next: () => {
        this.toast.success('Médecin créé.');
        this.newDoc = {
          nom: '',
          prenom: '',
          email: '',
          specialtyId: null,
          telephone: '',
          service: ''
        };
        this.pendingAvailabilities = [];
        this.loadDoctors();
      },
      error: (e: Error) => this.toast.error(e.message)
    });
  }

  removeDoctor(d: Doctor): void {
    if (!d.id || !confirm(`Supprimer ${d.prenom} ${d.nom} ?`)) {
      return;
    }
    this.doctorApi.deleteDoctor(d.id).subscribe({
      next: () => {
        this.toast.success('Médecin supprimé.');
        this.loadDoctors();
        if (this.editDoctorId === d.id) {
          this.cancelEditAvailabilities();
        }
      },
      error: (e: Error) => this.toast.error(e.message)
    });
  }

  startEditAvailabilities(d: Doctor): void {
    if (!d.id) {
      return;
    }
    this.editDoctorId = d.id;
    this.editAvailabilities = (d.availabilities ?? []).map((a) => ({
      dayOfWeek: a.dayOfWeek as DayOfWeek,
      heureDebut: this.shortTime(a.heureDebut),
      heureFin: this.shortTime(a.heureFin)
    }));
    this.cdr.markForCheck();
  }

  cancelEditAvailabilities(): void {
    this.editDoctorId = null;
    this.editAvailabilities = [];
    this.cdr.markForCheck();
  }

  addEditSlot(): void {
    const hd = this.normalizeTime(this.newSlot.heureDebut);
    const hf = this.normalizeTime(this.newSlot.heureFin);
    this.editAvailabilities = [
      ...this.editAvailabilities,
      { dayOfWeek: this.newSlot.dayOfWeek, heureDebut: hd, heureFin: hf }
    ];
    this.cdr.markForCheck();
  }

  removeEditSlot(index: number): void {
    this.editAvailabilities = this.editAvailabilities.filter((_, i) => i !== index);
    this.cdr.markForCheck();
  }

  saveDoctorAvailabilities(): void {
    if (this.editDoctorId == null) {
      return;
    }
    this.doctorApi
      .updateDoctor(this.editDoctorId, { availabilities: this.editAvailabilities })
      .subscribe({
        next: () => {
          this.toast.success('Disponibilités mises à jour.');
          this.cancelEditAvailabilities();
          this.loadDoctors();
        },
        error: (e: Error) => this.toast.error(e.message)
      });
  }

  dayLabel(day: string): string {
    return this.days.find((d) => d.value === day)?.label ?? day;
  }

  private shortTime(t: string): string {
    if (t.length >= 5) {
      return t.slice(0, 5);
    }
    return t;
  }

  private normalizeTime(t: string): string {
    if (t.length === 5) {
      return `${t}:00`;
    }
    return t;
  }
}
