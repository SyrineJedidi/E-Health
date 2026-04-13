import { ChangeDetectionStrategy, Component } from '@angular/core';

/** Coque back-office : navbar gestion + sidebar + contenu. */
@Component({
  selector: 'app-back-office-layout',
  templateUrl: './back-office-layout.component.html',
  styleUrl: './back-office-layout.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BackOfficeLayoutComponent {}
