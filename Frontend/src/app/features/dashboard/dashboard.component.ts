import { Component, OnInit } from '@angular/core';
import { DoctorService } from '../../core/services/doctor.service';
import { PatientService } from '../../core/services/patient.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  totalPatients = 0;
  totalDoctors = 0;
  readonly staticAppointments = 12;
  readonly staticPending = 5;
  loadError = '';

  constructor(
    private readonly patientService: PatientService,
    private readonly doctorService: DoctorService
  ) {}

  ngOnInit(): void {
    this.patientService.getAllPatients().subscribe({
      next: (res) => {
        this.totalPatients = res.data?.length ?? 0;
      },
      error: (err: Error) => {
        this.loadError = err.message;
        this.totalPatients = 0;
      }
    });
    this.doctorService.getAllDoctors().subscribe({
      next: (res) => {
        this.totalDoctors = res.data?.length ?? 0;
      },
      error: () => {
        this.totalDoctors = 0;
      }
    });
  }
}
