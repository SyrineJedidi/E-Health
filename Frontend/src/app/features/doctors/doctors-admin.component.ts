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
import { DoctorService } from '../../core/services/doctor.service';

type AdminTab = 'doctors' | 'specialties' | 'patients' | 'availabilities';

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
  availabilities: DoctorAvailability[] = [];

  loadingDoctors = false;
  loadingSpecs = false;
  loadingPatients = false;
  loadingAvail = false;
  errorDoctors = '';
  errorSpecs = '';
  errorPatients = '';
  errorAvail = '';

  /** Formulaire nouveau médecin */
  newDoc = {
    nom: '',
    prenom: '',
    email: '',
    specialtyId: null as number | null,
    telephone: '',
    service: ''
  };

  editingSpec: Specialty | null = null;
  specForm = { code: '', label: '', description: '' };

  selectedDoctorId: number | null = null;
  newSlot = {
    dayOfWeek: 'MONDAY' as DayOfWeek,
    heureDebut: '09:00',
    heureFin: '12:00'
  };

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
    if (tab === 'availabilities' && this.selectedDoctorId && this.availabilities.length === 0) {
      this.loadAvailabilities();
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

  loadAvailabilities(): void {
    if (this.selectedDoctorId == null) {
      return;
    }
    this.loadingAvail = true;
    this.errorAvail = '';
    this.cdr.markForCheck();
    this.doctorApi.getAvailabilities(this.selectedDoctorId).subscribe({
      next: (res) => {
        this.availabilities = res.data ?? [];
        this.loadingAvail = false;
        this.cdr.markForCheck();
      },
      error: (e: Error) => {
        this.errorAvail = e.message;
        this.loadingAvail = false;
        this.cdr.markForCheck();
      }
    });
  }

  onDoctorForAvailChange(): void {
    this.availabilities = [];
    if (this.selectedDoctorId != null) {
      this.loadAvailabilities();
    }
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
    this.doctorApi
      .createDoctor({
        nom: this.newDoc.nom.trim(),
        prenom: this.newDoc.prenom.trim(),
        email: this.newDoc.email.trim(),
        specialtyId: this.newDoc.specialtyId,
        telephone: this.newDoc.telephone.trim() || undefined,
        service: this.newDoc.service.trim() || undefined
      })
      .subscribe({
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
      },
      error: (e: Error) => this.toast.error(e.message)
    });
  }

  startEditSpec(s: Specialty): void {
    this.editingSpec = s;
    this.specForm = {
      code: s.code,
      label: s.label,
      description: s.description ?? ''
    };
    this.cdr.markForCheck();
  }

  cancelEditSpec(): void {
    this.editingSpec = null;
    this.specForm = { code: '', label: '', description: '' };
    this.cdr.markForCheck();
  }

  saveSpec(): void {
    const body = {
      code: this.specForm.code.trim(),
      label: this.specForm.label.trim(),
      description: this.specForm.description.trim() || undefined
    };
    if (!body.code || !body.label) {
      this.toast.error('Code et libellé sont obligatoires.');
      return;
    }
    const req = this.editingSpec
      ? this.doctorApi.updateSpecialty(this.editingSpec.id, body)
      : this.doctorApi.createSpecialty(body);
    req.subscribe({
      next: () => {
        this.toast.success(this.editingSpec ? 'Spécialité mise à jour.' : 'Spécialité créée.');
        this.cancelEditSpec();
        this.loadSpecialties();
      },
      error: (e: Error) => this.toast.error(e.message)
    });
  }

  removeSpec(s: Specialty): void {
    if (!confirm(`Supprimer la spécialité « ${s.label} » ?`)) {
      return;
    }
    this.doctorApi.deleteSpecialty(s.id).subscribe({
      next: () => {
        this.toast.success('Spécialité supprimée.');
        this.loadSpecialties();
      },
      error: (e: Error) => this.toast.error(e.message)
    });
  }

  addAvailability(): void {
    if (this.selectedDoctorId == null) {
      this.toast.error('Sélectionnez un médecin.');
      return;
    }
    const hd = this.normalizeTime(this.newSlot.heureDebut);
    const hf = this.normalizeTime(this.newSlot.heureFin);
    this.doctorApi
      .createAvailability(this.selectedDoctorId, {
        dayOfWeek: this.newSlot.dayOfWeek,
        heureDebut: hd,
        heureFin: hf
      })
      .subscribe({
        next: () => {
          this.toast.success('Créneau ajouté.');
          this.loadAvailabilities();
        },
        error: (e: Error) => this.toast.error(e.message)
      });
  }

  removeAvailability(slot: DoctorAvailability): void {
    if (this.selectedDoctorId == null || !confirm('Supprimer ce créneau ?')) {
      return;
    }
    this.doctorApi.deleteAvailability(this.selectedDoctorId, slot.id).subscribe({
      next: () => {
        this.toast.success('Créneau supprimé.');
        this.loadAvailabilities();
      },
      error: (e: Error) => this.toast.error(e.message)
    });
  }

  dayLabel(day: string): string {
    return this.days.find((d) => d.value === day)?.label ?? day;
  }

  private normalizeTime(t: string): string {
    if (t.length === 5) {
      return `${t}:00`;
    }
    return t;
  }
}
