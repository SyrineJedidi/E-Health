import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { LoadingService } from '../../core/services/loading.service';

@Component({
  selector: 'app-loader',
  templateUrl: './loader.component.html'
})
export class LoaderComponent {
  readonly loading$: Observable<boolean>;

  constructor(private readonly loadingService: LoadingService) {
    this.loading$ = this.loadingService.loading$;
  }
}
