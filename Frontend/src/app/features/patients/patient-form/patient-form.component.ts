import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Patient } from '../../../core/models/patient.model';
import { PatientService } from '../../../core/services/patient.service';
import { sanitizePlainText } from '../../../core/utils/sanitize.util';

@Component({
  selector: 'app-patient-form',
  templateUrl: './patient-form.component.html',
  styleUrl: './patient-form.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PatientFormComponent implements OnInit {
  form: FormGroup;
  isEditMode = false;
  patientId: number | null = null;
  successMessage = '';
  errorMessage = '';
  readonly groupes = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private readonly fb: FormBuilder,
    private readonly patientService: PatientService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {
    this.form = this.fb.group({
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telephone: [''],
      adresse: [''],
      dateNaissance: [''],
      groupeSanguin: ['']
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const path = this.route.snapshot.routeConfig?.path ?? '';
    if (idParam && path.includes('edit')) {
      this.isEditMode = true;
      this.patientId = Number(idParam);
      this.loadPatient(this.patientId);
    }
  }

  /** Charge le patient existant pour l’édition. */
  private loadPatient(id: number): void {
    this.patientService.getPatientById(id).subscribe({
      next: (res) => {
        const p = res.data;
        if (p) {
          this.form.patchValue({
            nom: p.nom,
            prenom: p.prenom,
            email: p.email,
            telephone: p.telephone ?? '',
            adresse: p.adresse ?? '',
            dateNaissance: p.dateNaissance ?? '',
            groupeSanguin: p.groupeSanguin ?? ''
          });
        }
        this.cdr.markForCheck();
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.cdr.markForCheck();
      }
    });
  }

  /** Soumet le formulaire (création ou mise à jour). */
  onSubmit(): void {
    this.successMessage = '';
    this.errorMessage = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }
    const value = this.form.value as Record<string, string>;
    const patient: Patient = {
      nom: sanitizePlainText(value['nom'], 120),
      prenom: sanitizePlainText(value['prenom'], 120),
      email: (value['email'] ?? '').trim().toLowerCase(),
      telephone: sanitizePlainText(value['telephone'], 40) || undefined,
      adresse: sanitizePlainText(value['adresse'], 500) || undefined,
      dateNaissance: (value['dateNaissance'] ?? '').trim() || undefined,
      groupeSanguin: sanitizePlainText(value['groupeSanguin'], 8) || undefined
    };
    if (this.isEditMode && this.patientId != null) {
      this.patientService.updatePatient(this.patientId, patient).subscribe({
        next: (res) => {
          this.successMessage = res.message || 'Patient mis à jour.';
          this.cdr.markForCheck();
          setTimeout(() => void this.router.navigate(['/admin/patients', this.patientId]), 800);
        },
        error: (err: Error) => {
          this.errorMessage = err.message;
          this.cdr.markForCheck();
        }
      });
    } else {
      this.patientService.createPatient(patient).subscribe({
        next: (res) => {
          this.successMessage = res.message || 'Patient créé.';
          this.cdr.markForCheck();
          const newId = res.data?.id;
          if (newId != null) {
            setTimeout(() => void this.router.navigate(['/admin/patients', newId]), 800);
          } else {
            void this.router.navigate(['/admin/patients']);
          }
        },
        error: (err: Error) => {
          this.errorMessage = err.message;
          this.cdr.markForCheck();
        }
      });
    }
  }

  cancel(): void {
    void this.router.navigate(['/admin/patients']);
  }

  fieldError(field: string): string {
    const c = this.form.get(field);
    if (!c || !c.touched || !c.errors) {
      return '';
    }
    if (c.errors['required']) {
      return 'Champ obligatoire';
    }
    if (c.errors['email']) {
      return 'Email invalide';
    }
    return '';
  }
}
