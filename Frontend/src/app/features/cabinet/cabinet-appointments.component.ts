import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';
import { RendezVous } from '../../core/models/patient.model';
import { DoctorService } from '../../core/services/doctor.service';

@Component({
  selector: 'app-cabinet-appointments',
  templateUrl: './cabinet-appointments.component.html',
  styleUrl: './cabinet-appointments.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CabinetAppointmentsComponent implements OnInit {
  list: RendezVous[] = [];
  loading = true;
  errorMessage = '';

  private readonly doctorService = inject(DoctorService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.doctorService.getMyAppointments().subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.list = res.data;
        } else {
          this.errorMessage = res.message ?? '';
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
