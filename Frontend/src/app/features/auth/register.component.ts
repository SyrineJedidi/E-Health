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
import { UserRole } from '../../core/models/auth.model';
import { AuthService } from '../../core/services/auth.service';
import { sanitizePlainText } from '../../core/utils/sanitize.util';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegisterComponent {
  readonly roles: UserRole[] = ['PATIENT', 'DOCTOR', 'ADMIN'];

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    firstName: [''],
    lastName: [''],
    role: this.fb.nonNullable.control<UserRole>('PATIENT', Validators.required)
  });

  errorMessage = '';

  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router
  ) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }
    this.errorMessage = '';
    const v = this.form.getRawValue();
    this.auth
      .register({
        email: v.email.trim(),
        password: v.password,
        firstName: sanitizePlainText(v.firstName, 80) || undefined,
        lastName: sanitizePlainText(v.lastName, 80) || undefined,
        role: v.role
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => void this.router.navigateByUrl(this.auth.defaultHomeUrl()),
        error: (err: Error) => {
          this.errorMessage = err.message || 'Inscription impossible';
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
    if (c.errors['minlength']) {
      return 'Au moins 8 caractères';
    }
    return '';
  }
}
