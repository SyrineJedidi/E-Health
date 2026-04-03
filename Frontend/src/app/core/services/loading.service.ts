import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

/**
 * Compteur de requêtes HTTP actives pour afficher le loader global.
 */
@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private activeRequests = 0;
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);

  readonly loading$: Observable<boolean> = this.loadingSubject.asObservable();

  /** Indique qu'une requête a démarré. */
  begin(): void {
    this.activeRequests++;
    if (this.activeRequests === 1) {
      this.loadingSubject.next(true);
    }
  }

  /** Indique qu'une requête s'est terminée. */
  end(): void {
    this.activeRequests = Math.max(0, this.activeRequests - 1);
    if (this.activeRequests === 0) {
      this.loadingSubject.next(false);
    }
  }
}
