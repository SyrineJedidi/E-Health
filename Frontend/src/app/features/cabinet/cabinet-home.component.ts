import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-cabinet-home',
  templateUrl: './cabinet-home.component.html',
  styleUrl: './cabinet-home.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CabinetHomeComponent {}
