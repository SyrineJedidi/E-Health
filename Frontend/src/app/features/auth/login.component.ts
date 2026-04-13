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
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginComponent implements OnInit {
  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  errorMessage = '';
  /** Message affiché après redirection par AuthGuard (session requise). */
  authNotice = '';

  private readonly destroyRef = inject(DestroyRef);
  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((q) => {
      this.authNotice = q.get('authRequired') === '1' ? 'Identifiez-vous pour accéder à cette page.' : '';
      this.cdr.markForCheck();
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }
    this.errorMessage = '';
    const { email, password } = this.form.getRawValue();
    this.auth
      .login({ email: email.trim(), password })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
      next: () => {
        const raw = this.route.snapshot.queryParamMap.get('returnUrl');
        void this.router.navigateByUrl(this.resolvePostLoginUrl(raw));
      },
      error: (err: Error) => {
        this.errorMessage = err.message || 'Connexion impossible';
        this.cdr.markForCheck();
      }
    });
  }

  /** Anciens chemins racine → préfixe `/admin` pour compatibilité des favoris. */
  private resolvePostLoginUrl(raw: string | null): string {
    const fallback = this.auth.defaultHomeUrl();
    if (!raw?.trim()) {
      return fallback;
    }
    let u = raw.trim();
    if (u.startsWith('http://') || u.startsWith('https://')) {
      return fallback;
    }
    if (this.auth.getRole() === 'DOCTOR' && u.startsWith('/admin')) {
      return '/cabinet';
    }
    const pairs: readonly { old: string; neu: string }[] = [
      { old: '/dashboard', neu: '/admin/dashboard' },
      { old: '/patients', neu: '/admin/patients' },
      { old: '/ordonnances', neu: '/admin/ordonnances' },
      { old: '/medecins', neu: '/admin/medecins' },
      { old: '/rendez-vous', neu: '/admin/rendez-vous' }
    ];
    for (const { old, neu } of pairs) {
      if (u === old || u.startsWith(old + '/')) {
        return neu + u.slice(old.length);
      }
    }
    return u;
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
