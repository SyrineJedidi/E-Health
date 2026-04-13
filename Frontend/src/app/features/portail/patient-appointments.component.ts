import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';
import { DossierMedical, RendezVous } from '../../core/models/patient.model';
import { PatientService } from '../../core/services/patient.service';

@Component({
  selector: 'app-patient-appointments',
  templateUrl: './patient-appointments.component.html',
  styleUrl: './patient-appointments.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PatientAppointmentsComponent implements OnInit {
  dossier: DossierMedical | null = null;
  loading = true;
  errorMessage = '';

  private readonly patientService = inject(PatientService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.patientService.getMyDossier().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.dossier = res.data;
        } else {
          this.errorMessage = res.message ?? 'Impossible de charger le dossier.';
        }
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  get rendezVous(): RendezVous[] {
    return this.dossier?.rendezVous ?? [];
  }
}
