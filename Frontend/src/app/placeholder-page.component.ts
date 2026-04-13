import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router } from '@angular/router';

/** Page temporaire pour les sections non encore branchées à l’API. */
@Component({
  selector: 'app-placeholder-page',
  templateUrl: './placeholder-page.component.html',
  styleUrl: './placeholder-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PlaceholderPageComponent {
  private readonly router = inject(Router);

  get backLink(): string {
    return this.router.url.includes('/admin/') ? '/admin/dashboard' : '/portail/accueil';
  }

  get backLabel(): string {
    return this.router.url.includes('/admin/')
      ? 'Retour au tableau de bord'
      : 'Retour à l’accueil portail';
  }
}
