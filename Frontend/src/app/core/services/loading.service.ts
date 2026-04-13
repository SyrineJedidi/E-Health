import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

/**
 * Compteur global des requêtes HTTP en cours (utilisé par l’intercepteur et le composant loader).
 */
@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private activeRequests = 0;
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);

  /** Observable : `true` dès qu’au moins une requête est active. */
  readonly loading$: Observable<boolean> = this.loadingSubject.asObservable();

  /** Appelé par l’intercepteur au départ d’une requête sortante. */
  begin(): void {
    this.activeRequests++;
    if (this.activeRequests === 1) {
      // Différer pour éviter NG0100 (ExpressionChangedAfterItHasBeenChecked) : begin()
      // est appelé depuis l'intercepteur HTTP pendant le même cycle de CD que le clic.
      queueMicrotask(() => this.loadingSubject.next(true));
    }
  }

  /** Appelé par l’intercepteur à la fin (succès ou erreur) d’une requête. */
  end(): void {
    this.activeRequests = Math.max(0, this.activeRequests - 1);
    if (this.activeRequests === 0) {
      queueMicrotask(() => this.loadingSubject.next(false));
    }
  }
}
