import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ForgotPasswordComponent {
  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]]
  });

  successMessage = '';
  resetToken: string | null = null;
  errorMessage = '';
  loading = false;

  private readonly destroyRef = inject(DestroyRef);
  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }
    this.errorMessage = '';
    this.successMessage = '';
    this.resetToken = null;
    this.loading = true;
    this.cdr.markForCheck();
    this.auth
      .forgotPassword(this.form.getRawValue().email)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (res) => {
          this.successMessage = res.message;
          this.resetToken = res.resetToken ?? null;
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: (err: Error) => {
          this.errorMessage = err.message || 'Impossible d’envoyer la demande';
          this.loading = false;
          this.cdr.markForCheck();
        }
      });
  }

  fieldError(field: string): string {
    const c = this.form.get(field);
    if (!c?.errors || !c.touched) {
      return '';
    }
    if (c.errors['required']) {
      return 'Champ obligatoire';
    }
    if (c.errors['email']) {
      return 'Email invalide';
    }
    return '';
  }
}
