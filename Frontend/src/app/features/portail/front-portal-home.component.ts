import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
  OnInit
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-front-portal-home',
  templateUrl: './front-portal-home.component.html',
  styleUrl: './front-portal-home.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FrontPortalHomeComponent implements OnInit {
  infoMessage = '';

  private readonly route = inject(ActivatedRoute);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);

  constructor(readonly auth: AuthService) {}

  ngOnInit(): void {
    this.route.queryParamMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((q) => {
      const info = q.get('info');
      this.infoMessage =
        info === 'staff_only'
          ? 'L’espace professionnel est réservé aux médecins et administrateurs.'
          : info === 'staff_feature'
            ? 'Cette page du portail est réservée aux administrateurs.'
            : info === 'doctor_only'
              ? 'Cette page est réservée aux médecins (cabinet).'
              : info === 'admin_only'
                ? 'Cette page est réservée aux administrateurs.'
                : '';
      this.cdr.markForCheck();
    });
  }
}
