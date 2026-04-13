import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';
import { HttpErrorInterceptor } from './core/interceptors/http-error.interceptor';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ChangePasswordComponent } from './features/auth/change-password.component';
import { ForgotPasswordComponent } from './features/auth/forgot-password.component';
import { LoginComponent } from './features/auth/login.component';
import { RegisterComponent } from './features/auth/register.component';
import { ResetPasswordComponent } from './features/auth/reset-password.component';
import { FrontPortalHomeComponent } from './features/portail/front-portal-home.component';
import { PatientAppointmentsComponent } from './features/portail/patient-appointments.component';
import { PatientPrescriptionsComponent } from './features/portail/patient-prescriptions.component';
import { PrescriptionFormComponent } from './features/prescriptions/prescription-form.component';
import { PrescriptionListComponent } from './features/prescriptions/prescription-list.component';
import { BackOfficeLayoutComponent } from './layouts/back-office-layout.component';
import { FrontOfficeLayoutComponent } from './layouts/front-office-layout.component';
import { PublicAuthLayoutComponent } from './layouts/public-auth-layout.component';
import { PlaceholderPageComponent } from './placeholder-page.component';
import { DoctorsAdminComponent } from './features/doctors/doctors-admin.component';
import { CabinetLayoutComponent } from './layouts/cabinet-layout.component';
import { CabinetHomeComponent } from './features/cabinet/cabinet-home.component';
import { CabinetPatientsComponent } from './features/cabinet/cabinet-patients.component';
import { CabinetAppointmentsComponent } from './features/cabinet/cabinet-appointments.component';
import { SharedModule } from './shared/shared.module';
import { ToastContainerComponent } from './shared/toast-container/toast-container.component';

@NgModule({
  declarations: [
    AppComponent,
    PublicAuthLayoutComponent,
    BackOfficeLayoutComponent,
    FrontOfficeLayoutComponent,
    DashboardComponent,
    FrontPortalHomeComponent,
    PatientAppointmentsComponent,
    PatientPrescriptionsComponent,
    PlaceholderPageComponent,
    LoginComponent,
    RegisterComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    ChangePasswordComponent,
    PrescriptionListComponent,
    PrescriptionFormComponent,
    DoctorsAdminComponent,
    CabinetLayoutComponent,
    CabinetHomeComponent,
    CabinetPatientsComponent,
    CabinetAppointmentsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    SharedModule,
    ToastContainerComponent
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
