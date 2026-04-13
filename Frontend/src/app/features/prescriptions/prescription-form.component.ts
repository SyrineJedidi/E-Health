import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnInit
} from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { Doctor } from '../../core/models/doctor.model';
import { Patient } from '../../core/models/patient.model';
import { PrescriptionStatus } from '../../core/models/prescription.model';
import { MedicationCatalog } from '../../core/models/prescription.model';
import { DoctorService } from '../../core/services/doctor.service';
import { MedicationService } from '../../core/services/medication.service';
import { PatientService } from '../../core/services/patient.service';
import { PrescriptionService } from '../../core/services/prescription.service';

@Component({
  selector: 'app-prescription-form',
  templateUrl: './prescription-form.component.html',
  styleUrl: './prescription-form.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PrescriptionFormComponent implements OnInit {
  readonly form: FormGroup;
  readonly statusOptions: PrescriptionStatus[] = ['ACTIVE', 'EXPIRED', 'CANCELLED', 'DISPENSED'];

  patients: Patient[] = [];
  doctors: Doctor[] = [];
  medicationCatalog: MedicationCatalog[] = [];
  refsLoading = true;
  prescriptionLoading = false;
  saving = false;
  errorMessage = '';
  isEditMode = false;
  /** Formulaire simplifié sous /cabinet (médecin connecté). */
  cabinetMode = false;
  private editId: number | null = null;

  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly prescriptionService: PrescriptionService,
    private readonly patientService: PatientService,
    private readonly doctorService: DoctorService,
    private readonly medicationService: MedicationService
  ) {
    this.form = this.fb.group({
      patientId: [null as number | null, Validators.required],
      doctorId: [null as number | null, Validators.required],
      status: ['ACTIVE' as PrescriptionStatus, Validators.required],
      medications: this.fb.array([this.createMedicationGroup()])
    });
  }

  get medications(): FormArray {
    return this.form.get('medications') as FormArray;
  }

  ngOnInit(): void {
    this.cabinetMode = this.router.url.includes('/cabinet');

    const idStr = this.route.snapshot.paramMap.get('id');
    if (idStr != null && idStr !== '') {
      const n = Number(idStr);
      if (!Number.isNaN(n)) {
        this.isEditMode = true;
        this.editId = n;
      }
    }

    const meds$ = this.medicationService.getAll().pipe(catchError(() => of([] as MedicationCatalog[])));

    if (this.cabinetMode) {
      forkJoin({
        me: this.doctorService.getMe(),
        patients: this.doctorService.getMyPatients(),
        meds: meds$
      }).subscribe({
        next: ({ me, patients, meds }) => {
          const d = me.data;
          if (d) {
            this.doctors = [d];
            this.form.patchValue({ doctorId: d.id });
          } else {
            this.errorMessage = me.message ?? 'Profil médecin introuvable.';
          }
          this.patients = patients.data ?? [];
          this.medicationCatalog = meds;
          this.refsLoading = false;
          if (this.isEditMode && this.editId != null) {
            this.loadPrescription(this.editId);
          }
          this.cdr.markForCheck();
        },
        error: (err: Error) => {
          this.errorMessage = err.message;
          this.refsLoading = false;
          this.cdr.markForCheck();
        }
      });
      return;
    }

    forkJoin({
      patients: this.patientService.getAllPatients(),
      doctors: this.doctorService.getAllDoctors(),
      meds: meds$
    }).subscribe({
      next: ({ patients, doctors, meds }) => {
        this.patients = patients.data ?? [];
        this.doctors = doctors.data ?? [];
        this.medicationCatalog = meds;
        this.refsLoading = false;
        if (this.isEditMode && this.editId != null) {
          this.loadPrescription(this.editId);
        }
        this.cdr.markForCheck();
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.refsLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  createMedicationGroup(): FormGroup {
    return this.fb.group({
      id: [null as number | null],
      medicationId: [null as number | null],
      name: [''],
      dosage: ['', Validators.required],
      frequency: ['', Validators.required],
      durationDays: [null as number | null],
      instructions: ['']
    });
  }

  onMedicationCatalogChange(index: number): void {
    const g = this.medications.at(index);
    if (!g) {
      return;
    }
    const id = g.get('medicationId')?.value as number | null;
    if (id == null) {
      this.cdr.markForCheck();
      return;
    }
    const med = this.medicationCatalog.find((m) => m.id === id);
    g.patchValue({
      name: med?.name ?? g.get('name')?.value
    });
    this.cdr.markForCheck();
  }

  addMedication(): void {
    this.medications.push(this.createMedicationGroup());
    this.cdr.markForCheck();
  }

  removeMedication(index: number): void {
    if (this.medications.length <= 1) {
      return;
    }
    this.medications.removeAt(index);
    this.cdr.markForCheck();
  }

  private loadPrescription(id: number): void {
    this.prescriptionLoading = true;
    this.errorMessage = '';
    this.cdr.markForCheck();
    this.prescriptionService.getById(id).subscribe({
      next: (p) => {
        while (this.medications.length) {
          this.medications.removeAt(0);
        }
        this.form.patchValue({
          patientId: p.patientId,
          doctorId: p.doctorId,
          status: p.status
        });
        for (const m of p.medications) {
          this.medications.push(
            this.fb.group({
              id: [m.id ?? null],
              medicationId: [m.medicationId ?? null],
              name: [m.name ?? ''],
              dosage: [m.dosage, Validators.required],
              frequency: [m.frequency, Validators.required],
              durationDays: [m.durationDays ?? null],
              instructions: [m.instructions ?? '']
            })
          );
        }
        if (this.medications.length === 0) {
          this.medications.push(this.createMedicationGroup());
        }
        this.prescriptionLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.prescriptionLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  submit(): void {
    for (let i = 0; i < this.medications.length; i++) {
      const g = this.medications.at(i);
      const mid = g.get('medicationId')?.value as number | null;
      const nm = (g.get('name')?.value as string)?.trim();
      if (!mid && !nm) {
        this.errorMessage = `Ligne ${i + 1} : choisissez un médicament du catalogue ou saisissez un nom.`;
        this.cdr.markForCheck();
        return;
      }
    }
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }
    this.saving = true;
    this.errorMessage = '';
    this.cdr.markForCheck();
    const v = this.form.getRawValue() as {
      patientId: number;
      doctorId: number;
      status: PrescriptionStatus;
      medications: Array<{
        id: number | null;
        medicationId: number | null;
        name: string;
        dosage: string;
        frequency: string;
        durationDays: number | null;
        instructions: string;
      }>;
    };
    const payload = {
      patientId: v.patientId,
      doctorId: v.doctorId,
      status: v.status,
      medications: v.medications.map((m) => {
        const medicationId = m.medicationId ?? null;
        const name = (m.name ?? '').trim();
        return {
          ...(m.id != null ? { id: m.id } : {}),
          ...(medicationId != null ? { medicationId } : {}),
          ...(!medicationId && name ? { name } : {}),
          dosage: m.dosage.trim(),
          frequency: m.frequency.trim(),
          ...(m.durationDays != null ? { durationDays: m.durationDays } : {}),
          instructions: m.instructions?.trim() || null
        };
      })
    };

    const req =
      this.isEditMode && this.editId != null
        ? this.prescriptionService.update(this.editId, payload)
        : this.prescriptionService.create(payload);

    const afterSave = this.cabinetMode ? '/cabinet' : '/admin/ordonnances';
    req.subscribe({
      next: () => void this.router.navigateByUrl(afterSave),
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.saving = false;
        this.cdr.markForCheck();
      }
    });
  }

  cancel(): void {
    void this.router.navigateByUrl(this.cabinetMode ? '/cabinet' : '/admin/ordonnances');
  }
}
