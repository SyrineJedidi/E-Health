import { ChangeDetectionStrategy, Component } from '@angular/core';

/** Coque front-office (portail patient) : barre supérieure légère, pas de sidebar. */
@Component({
  selector: 'app-front-office-layout',
  templateUrl: './front-office-layout.component.html',
  styleUrl: './front-office-layout.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FrontOfficeLayoutComponent {}
