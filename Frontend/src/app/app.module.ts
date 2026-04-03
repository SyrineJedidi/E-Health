import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpErrorInterceptor } from './core/interceptors/http-error.interceptor';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { PlaceholderPageComponent } from './placeholder-page.component';
import { SharedModule } from './shared/shared.module';

@NgModule({
  declarations: [AppComponent, DashboardComponent, PlaceholderPageComponent],
  imports: [BrowserModule, HttpClientModule, AppRoutingModule, SharedModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
