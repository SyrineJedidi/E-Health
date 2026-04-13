import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DossierMedical, Patient, RendezVous } from '../../../core/models/patient.model';
import { PatientService } from '../../../core/services/patient.service';

@Component({
  selector: 'app-patient-dossier',
  templateUrl: './patient-dossier.component.html',
  styleUrl: './patient-dossier.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PatientDossierComponent implements OnInit {
  dossier: DossierMedical | null = null;
  errorMessage = '';
  isLoading = false;

  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly patientService: PatientService
  ) {}

  ngOnInit(): void {
    const idStr = this.route.snapshot.paramMap.get('id');
    const id = idStr ? Number(idStr) : NaN;
    if (Number.isNaN(id)) {
      void this.router.navigate(['/admin/patients']);
      return;
    }
    this.isLoading = true;
    this.cdr.markForCheck();
    this.patientService.getDossierMedical(id).subscribe({
      next: (res) => {
        this.dossier = res.data ?? null;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.dossier = null;
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  get patient(): Patient | null {
    return this.dossier?.patient ?? null;
  }

  get rendezVous(): RendezVous[] {
    return this.dossier?.rendezVous ?? [];
  }

  goBack(): void {
    if (this.patient?.id != null) {
      void this.router.navigate(['/admin/patients', this.patient.id]);
    } else {
      void this.router.navigate(['/admin/patients']);
    }
  }

  /** Classe Bootstrap pour le badge selon le statut. */
  badgeClass(statut: string): string {
    const s = statut.toUpperCase();
    if (s.includes('CONFIRME') || s === 'CONFIRMÉ' || s === 'CONFIRME') {
      return 'bg-success';
    }
    if (s.includes('ATTENT') || s.includes('EN_ATTENTE')) {
      return 'bg-warning text-dark';
    }
    if (s.includes('ANNU')) {
      return 'bg-danger';
    }
    return 'bg-secondary';
  }
}
