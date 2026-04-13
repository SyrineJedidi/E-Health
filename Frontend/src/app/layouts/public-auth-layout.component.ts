import { ChangeDetectionStrategy, Component } from '@angular/core';

/** En-tête commun login / inscription (sans navbar métier). */
@Component({
  selector: 'app-public-auth-layout',
  templateUrl: './public-auth-layout.component.html',
  styleUrl: './public-auth-layout.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PublicAuthLayoutComponent {}
