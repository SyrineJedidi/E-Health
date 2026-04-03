import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Patient } from '../../../core/models/patient.model';
import { PatientService } from '../../../core/services/patient.service';

@Component({
  selector: 'app-patient-detail',
  templateUrl: './patient-detail.component.html'
})
export class PatientDetailComponent implements OnInit {
  patient: Patient | null = null;
  errorMessage = '';
  isLoading = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly patientService: PatientService
  ) {}

  ngOnInit(): void {
    const idStr = this.route.snapshot.paramMap.get('id');
    const id = idStr ? Number(idStr) : NaN;
    if (Number.isNaN(id)) {
      void this.router.navigate(['/patients']);
      return;
    }
    this.isLoading = true;
    this.patientService.getPatientById(id).subscribe({
      next: (res) => {
        this.patient = res.data ?? null;
        this.isLoading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.patient = null;
        this.isLoading = false;
      }
    });
  }

  goEdit(): void {
    if (this.patient?.id != null) {
      void this.router.navigate(['/patients', this.patient.id, 'edit']);
    }
  }

  goDossier(): void {
    if (this.patient?.id != null) {
      void this.router.navigate(['/patients', this.patient.id, 'dossier']);
    }
  }

  goBack(): void {
    void this.router.navigate(['/patients']);
  }

  delete(): void {
    if (!this.patient?.id) {
      return;
    }
    if (!window.confirm(`Supprimer ${this.patient.prenom} ${this.patient.nom} ?`)) {
      return;
    }
    this.patientService.deletePatient(this.patient.id).subscribe({
      next: () => void this.router.navigate(['/patients']),
      error: (err: Error) => {
        this.errorMessage = err.message;
      }
    });
  }
}
