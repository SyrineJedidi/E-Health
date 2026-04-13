import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-navbar-back',
  templateUrl: './navbar-back.component.html',
  styleUrl: './navbar-back.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarBackComponent {
  readonly loggedIn$ = this.auth.loggedIn$;
  /** Menu hamburger (mobile) — géré par Angular pour fiabilité avec le routeur. */
  navOpen = false;

  private readonly cdr = inject(ChangeDetectorRef);

  constructor(
    private readonly auth: AuthService,
    private readonly router: Router,
    destroyRef: DestroyRef
  ) {
    this.router.events
      .pipe(
        filter((e): e is NavigationEnd => e instanceof NavigationEnd),
        takeUntilDestroyed(destroyRef)
      )
      .subscribe(() => {
        this.navOpen = false;
        this.cdr.markForCheck();
      });
  }

  toggleNav(): void {
    this.navOpen = !this.navOpen;
    this.cdr.markForCheck();
  }

  logout(): void {
    this.auth.logout();
    void this.router.navigate(['/login']);
  }
}
