import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnInit
} from '@angular/core';
import { Prescription } from '../../core/models/prescription.model';
import { PrescriptionService } from '../../core/services/prescription.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-prescription-list',
  templateUrl: './prescription-list.component.html',
  styleUrl: './prescription-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PrescriptionListComponent implements OnInit {
  prescriptions: Prescription[] = [];
  errorMessage = '';
  loading = true;
  deletingId: number | null = null;

  private readonly cdr = inject(ChangeDetectorRef);
  private readonly toast = inject(ToastService);

  constructor(private readonly prescriptionService: PrescriptionService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.markForCheck();
    this.prescriptionService.getAll().subscribe({
      next: (list) => {
        this.prescriptions = list;
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

  deletePrescription(p: Prescription): void {
    if (!confirm(`Supprimer l’ordonnance n° ${p.id} ? Cette action est définitive.`)) {
      return;
    }
    this.deletingId = p.id;
    this.cdr.markForCheck();
    this.prescriptionService.delete(p.id).subscribe({
      next: () => {
        this.deletingId = null;
        this.toast.success('Ordonnance supprimée.');
        this.load();
      },
      error: (err: Error) => {
        this.deletingId = null;
        this.errorMessage = err.message;
        this.toast.error(err.message);
        this.cdr.markForCheck();
      }
    });
  }
}
