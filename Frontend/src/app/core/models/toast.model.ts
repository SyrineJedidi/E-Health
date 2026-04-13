/** Type de notification affichée dans la barre de toasts. */
export type ToastVariant = 'success' | 'danger' | 'warning' | 'info';

/** Une notification éphémère pour l’utilisateur. */
export interface ToastItem {
  readonly id: number;
  readonly variant: ToastVariant;
  readonly message: string;
}
