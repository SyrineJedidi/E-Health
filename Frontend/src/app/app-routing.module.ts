import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';
import { DoctorCabinetGuard } from './core/guards/doctor-cabinet.guard';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { ChangePasswordComponent } from './features/auth/change-password.component';
import { ForgotPasswordComponent } from './features/auth/forgot-password.component';
import { LoginComponent } from './features/auth/login.component';
import { RegisterComponent } from './features/auth/register.component';
import { ResetPasswordComponent } from './features/auth/reset-password.component';
import { FrontPortalHomeComponent } from './features/portail/front-portal-home.component';
import { PatientAppointmentsComponent } from './features/portail/patient-appointments.component';
import { PatientPrescriptionsComponent } from './features/portail/patient-prescriptions.component';
import { PatientPortalGuard } from './core/guards/patient-portal.guard';
import { StaffPortalGuard } from './core/guards/staff-portal.guard';
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

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'portail/accueil' },
  {
    path: 'login',
    component: PublicAuthLayoutComponent,
    children: [{ path: '', component: LoginComponent }]
  },
  {
    path: 'register',
    component: PublicAuthLayoutComponent,
    children: [{ path: '', component: RegisterComponent }]
  },
  {
    path: 'forgot-password',
    component: PublicAuthLayoutComponent,
    children: [{ path: '', component: ForgotPasswordComponent }]
  },
  {
    path: 'reset-password',
    component: PublicAuthLayoutComponent,
    children: [{ path: '', component: ResetPasswordComponent }]
  },
  {
    path: 'admin',
    component: BackOfficeLayoutComponent,
    canActivate: [AuthGuard, AdminGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardComponent },
      {
        path: 'patients',
        loadChildren: () => import('./features/patients/patients.module').then((m) => m.PatientsModule)
      },
      { path: 'ordonnances/nouvelle', component: PrescriptionFormComponent },
      { path: 'ordonnances/:id/edition', component: PrescriptionFormComponent },
      { path: 'ordonnances', component: PrescriptionListComponent },
      { path: 'medecins', component: DoctorsAdminComponent },
      { path: 'rendez-vous', component: PlaceholderPageComponent },
      { path: 'changer-mot-de-passe', component: ChangePasswordComponent }
    ]
  },
  {
    path: 'portail',
    component: FrontOfficeLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'accueil' },
      { path: 'accueil', component: FrontPortalHomeComponent },
      {
        path: 'rendez-vous',
        component: PatientAppointmentsComponent,
        canActivate: [PatientPortalGuard]
      },
      {
        path: 'ordonnances',
        component: PatientPrescriptionsComponent,
        canActivate: [PatientPortalGuard]
      },
      {
        path: 'medecins',
        component: PlaceholderPageComponent,
        canActivate: [StaffPortalGuard]
      },
      { path: 'changer-mot-de-passe', component: ChangePasswordComponent }
    ]
  },
  {
    path: 'cabinet',
    component: CabinetLayoutComponent,
    canActivate: [AuthGuard, DoctorCabinetGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'accueil' },
      { path: 'accueil', component: CabinetHomeComponent },
      { path: 'patients', component: CabinetPatientsComponent },
      { path: 'rendez-vous', component: CabinetAppointmentsComponent },
      { path: 'ordonnance/nouvelle', component: PrescriptionFormComponent },
      { path: 'changer-mot-de-passe', component: ChangePasswordComponent }
    ]
  },
  { path: '**', redirectTo: 'portail/accueil' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
