import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';
import { Patient } from '../../core/models/patient.model';
import { DoctorService } from '../../core/services/doctor.service';

@Component({
  selector: 'app-cabinet-patients',
  templateUrl: './cabinet-patients.component.html',
  styleUrl: './cabinet-patients.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CabinetPatientsComponent implements OnInit {
  patients: Patient[] = [];
  loading = true;
  errorMessage = '';

  private readonly doctorService = inject(DoctorService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.doctorService.getMyPatients().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.patients = res.data;
        } else {
          this.errorMessage = res.message ?? 'Chargement impossible.';
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
}
