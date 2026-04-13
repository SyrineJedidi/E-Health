import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnInit,
  inject
} from '@angular/core';
import { Prescription } from '../../core/models/prescription.model';
import { PrescriptionService } from '../../core/services/prescription.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-patient-prescriptions',
  templateUrl: './patient-prescriptions.component.html',
  styleUrl: './patient-prescriptions.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PatientPrescriptionsComponent implements OnInit {
  prescriptions: Prescription[] = [];
  /** Brouillon du commentaire par id d’ordonnance. */
  commentDraft: Record<number, string> = {};
  savingId: number | null = null;
  loading = true;
  errorMessage = '';

  private readonly prescriptionService = inject(PrescriptionService);
  private readonly toast = inject(ToastService);
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.errorMessage = '';
    this.cdr.markForCheck();
    this.prescriptionService.getMine().subscribe({
      next: (list) => {
        this.prescriptions = list;
        this.commentDraft = {};
        for (const p of list) {
          this.commentDraft[p.id] = p.patientComment ?? '';
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

  saveComment(p: Prescription): void {
    const text = (this.commentDraft[p.id] ?? '').trim();
    this.savingId = p.id;
    this.cdr.markForCheck();
    this.prescriptionService.updatePatientComment(p.id, text).subscribe({
      next: (updated) => {
        const idx = this.prescriptions.findIndex((x) => x.id === updated.id);
        if (idx >= 0) {
          this.prescriptions[idx] = updated;
        }
        this.commentDraft[p.id] = updated.patientComment ?? '';
        this.savingId = null;
        this.toast.success('Message envoyé à votre médecin.');
        this.cdr.markForCheck();
      },
      error: (err: Error) => {
        this.savingId = null;
        this.toast.error(err.message);
        this.cdr.markForCheck();
      }
    });
  }

  medsSummary(p: Prescription): string {
    return p.medications.map((m) => m.name ?? 'Médicament').join(', ');
  }
}
