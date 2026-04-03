import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { PlaceholderPageComponent } from './placeholder-page.component';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'dashboard', component: DashboardComponent },
  {
    path: 'patients',
    loadChildren: () => import('./features/patients/patients.module').then((m) => m.PatientsModule)
  },
  { path: 'medecins', component: PlaceholderPageComponent },
  { path: 'rendez-vous', component: PlaceholderPageComponent },
  { path: '**', redirectTo: 'dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
