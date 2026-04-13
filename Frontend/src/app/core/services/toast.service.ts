import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ToastItem, ToastVariant } from '../models/toast.model';

/**
 * Notifications globales non bloquantes (style SaaS).
 * Les messages ne doivent jamais contenir de données sensibles (tokens, mots de passe).
 */
@Injectable({ providedIn: 'root' })
export class ToastService {
  private seq = 0;
  private readonly items = new BehaviorSubject<readonly ToastItem[]>([]);

  /** Flux des toasts actuellement visibles. */
  get toasts$(): Observable<readonly ToastItem[]> {
    return this.items.asObservable();
  }

  /**
   * Affiche un toast ; disparaît automatiquement après `durationMs` (0 = manuel uniquement).
   */
  show(variant: ToastVariant, message: string, durationMs = 6000): void {
    const id = ++this.seq;
    const next: ToastItem = { id, variant, message };
    this.items.next([...this.items.value, next]);
    if (durationMs > 0) {
      window.setTimeout(() => this.dismiss(id), durationMs);
    }
  }

  /** Raccourci pour une erreur utilisateur. */
  error(message: string, durationMs?: number): void {
    this.show('danger', message, durationMs);
  }

  /** Raccourci pour un succès. */
  success(message: string, durationMs?: number): void {
    this.show('success', message, durationMs);
  }

  /** Raccourci pour un avertissement (ex. connectivité). */
  warning(message: string, durationMs?: number): void {
    this.show('warning', message, durationMs);
  }

  /** Retire un toast par identifiant. */
  dismiss(id: number): void {
    this.items.next(this.items.value.filter((t) => t.id !== id));
  }

  /** Vide tous les toasts. */
  clear(): void {
    this.items.next([]);
  }
}
