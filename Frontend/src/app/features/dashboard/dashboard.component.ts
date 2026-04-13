import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiResponse } from '../../core/models/api-response.model';
import { Doctor } from '../../core/models/doctor.model';
import { Patient } from '../../core/models/patient.model';
import { DoctorService } from '../../core/services/doctor.service';
import { PatientService } from '../../core/services/patient.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {
  totalPatients = 0;
  totalDoctors = 0;
  readonly staticAppointments = 12;
  readonly staticPending = 5;
  /** Message discret si une API est hors service (pas le texte brut HTTP). */
  apiNotice = '';
  loading = true;

  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private readonly patientService: PatientService,
    private readonly doctorService: DoctorService
  ) {}

  ngOnInit(): void {
    forkJoin({
      patients: this.patientService.getAllPatients().pipe(
        catchError(() => of(null as ApiResponse<Patient[]> | null))
      ),
      doctors: this.doctorService.getAllDoctors().pipe(
        catchError(() => of(null as ApiResponse<Doctor[]> | null))
      )
    }).subscribe({
      next: ({ patients, doctors }) => {
        this.totalPatients = patients?.data?.length ?? 0;
        this.totalDoctors = doctors?.data?.length ?? 0;
        const pFail = patients === null;
        const dFail = doctors === null;
        if (pFail && dFail) {
          this.apiNotice =
            'Les indicateurs patients et médecins ne sont pas disponibles pour le moment. Vérifiez que la passerelle API (souvent le port 8085), Eureka et les microservices sont démarrés, puis actualisez la page.';
        } else if (pFail) {
          this.apiNotice =
            'Le total patients n’a pas pu être chargé (service ou passerelle indisponible). Les autres indicateurs s’affichent normalement.';
        } else if (dFail) {
          this.apiNotice =
            'Le total médecins n’a pas pu être chargé (service ou passerelle indisponible). Les autres indicateurs s’affichent normalement.';
        } else {
          this.apiNotice = '';
        }
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }
}
