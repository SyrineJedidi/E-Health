import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';
import { environment } from './environments/environment';

platformBrowserDynamic()
  .bootstrapModule(AppModule)
  .catch((err: unknown) => {
    if (!environment.production && err instanceof Error) {
      // Journalisation réservée au développement local
      // eslint-disable-next-line no-console
      console.error(err);
    }
  });
