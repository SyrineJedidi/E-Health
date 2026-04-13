import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChangePasswordComponent {
  readonly form = this.fb.nonNullable.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required]
  });

  readonly backUrl = this.auth.defaultHomeUrl();

  errorMessage = '';
  loading = false;

  private readonly destroyRef = inject(DestroyRef);
  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
    private readonly toast: ToastService
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }
    const v = this.form.getRawValue();
    if (v.newPassword !== v.confirmPassword) {
      this.errorMessage = 'Les deux nouveaux mots de passe ne correspondent pas';
      this.cdr.markForCheck();
      return;
    }
    this.errorMessage = '';
    this.loading = true;
    this.cdr.markForCheck();
    this.auth
      .changePassword(v.currentPassword, v.newPassword)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.toast.success('Mot de passe modifié avec succès');
          void this.router.navigateByUrl(this.auth.defaultHomeUrl());
        },
        error: (err: Error) => {
          this.errorMessage = err.message || 'Modification impossible';
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
    if (c.errors['minlength']) {
      return 'Au moins 8 caractères';
    }
    return '';
  }
}
