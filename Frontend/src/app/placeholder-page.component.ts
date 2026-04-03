import { Component } from '@angular/core';

/** Page temporaire pour les sections non encore branchées à l'API. */
@Component({
  selector: 'app-placeholder-page',
  template: `
    <div class="p-4">
      <h2 class="text-primary">Page en construction</h2>
      <p class="text-muted">Cette section sera connectée au microservice correspondant.</p>
      <a routerLink="/dashboard" class="btn btn-outline-primary">Retour au tableau de bord</a>
    </div>
  `
})
export class PlaceholderPageComponent {}
