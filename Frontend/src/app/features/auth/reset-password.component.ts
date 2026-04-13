import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
  OnInit
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ResetPasswordComponent implements OnInit {
  token = '';

  readonly form = this.fb.nonNullable.group({
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required]
  });

  errorMessage = '';
  loading = false;

  private readonly destroyRef = inject(DestroyRef);
  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((q) => {
      this.token = q.get('token')?.trim() ?? '';
      this.cdr.markForCheck();
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }
    const { newPassword, confirmPassword } = this.form.getRawValue();
    if (newPassword !== confirmPassword) {
      this.errorMessage = 'Les deux mots de passe ne correspondent pas';
      this.cdr.markForCheck();
      return;
    }
    if (!this.token) {
      this.errorMessage = 'Lien invalide : jeton manquant dans l’URL.';
      this.cdr.markForCheck();
      return;
    }
    this.errorMessage = '';
    this.loading = true;
    this.cdr.markForCheck();
    this.auth
      .resetPassword(this.token, newPassword)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => void this.router.navigate(['/login'], { queryParams: { reset: 'ok' } }),
        error: (err: Error) => {
          this.errorMessage = err.message || 'Réinitialisation impossible';
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
