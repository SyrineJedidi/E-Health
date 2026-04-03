import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { Patient } from '../../../core/models/patient.model';
import { PatientService } from '../../../core/services/patient.service';

@Component({
  selector: 'app-patient-list',
  templateUrl: './patient-list.component.html'
})
export class PatientListComponent implements OnInit {
  patients: Patient[] = [];
  readonly searchControl: FormControl<string | null>;
  isLoading = false;
  errorMessage = '';

  constructor(
    private readonly patientService: PatientService,
    private readonly router: Router,
    fb: FormBuilder
  ) {
    this.searchControl = fb.control('');
  }

  ngOnInit(): void {
    this.loadPatients();
  }

  /** Recharge la liste depuis l'API. */
  loadPatients(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.patientService.getAllPatients().subscribe({
      next: (res) => {
        this.patients = res.data ?? [];
        this.isLoading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.patients = [];
        this.isLoading = false;
      }
    });
  }

  /** Filtre local sur nom / prénom / email en temps réel. */
  get filteredPatients(): Patient[] {
    const raw = this.searchControl.value;
    const q = (raw ?? '').trim().toLowerCase();
    if (!q) {
      return this.patients;
    }
    return this.patients.filter(
      (p) =>
        p.nom.toLowerCase().includes(q) ||
        p.prenom.toLowerCase().includes(q) ||
        p.email.toLowerCase().includes(q)
    );
  }

  goDetail(id: number): void {
    void this.router.navigate(['/patients', id]);
  }

  goEdit(id: number): void {
    void this.router.navigate(['/patients', id, 'edit']);
  }

  goNew(): void {
    void this.router.navigate(['/patients', 'new']);
  }

  /** Suppression avec confirmation navigateur. */
  deletePatient(id: number, nom: string): void {
    if (!window.confirm(`Supprimer le patient « ${nom} » ?`)) {
      return;
    }
    this.patientService.deletePatient(id).subscribe({
      next: () => this.loadPatients(),
      error: (err: Error) => {
        this.errorMessage = err.message;
      }
    });
  }
}
