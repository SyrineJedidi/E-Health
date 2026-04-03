import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PatientDetailComponent } from './patient-detail/patient-detail.component';
import { PatientDossierComponent } from './patient-dossier/patient-dossier.component';
import { PatientFormComponent } from './patient-form/patient-form.component';
import { PatientListComponent } from './patient-list/patient-list.component';

const routes: Routes = [
  { path: '', component: PatientListComponent },
  { path: 'new', component: PatientFormComponent },
  { path: ':id/dossier', component: PatientDossierComponent },
  { path: ':id/edit', component: PatientFormComponent },
  { path: ':id', component: PatientDetailComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PatientsRoutingModule {}
