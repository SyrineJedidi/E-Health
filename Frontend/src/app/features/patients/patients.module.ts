import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../../shared/shared.module';
import { PatientDetailComponent } from './patient-detail/patient-detail.component';
import { PatientDossierComponent } from './patient-dossier/patient-dossier.component';
import { PatientFormComponent } from './patient-form/patient-form.component';
import { PatientListComponent } from './patient-list/patient-list.component';
import { PatientsRoutingModule } from './patients-routing.module';

@NgModule({
  declarations: [
    PatientListComponent,
    PatientFormComponent,
    PatientDetailComponent,
    PatientDossierComponent
  ],
  imports: [CommonModule, ReactiveFormsModule, SharedModule, PatientsRoutingModule]
})
export class PatientsModule {}
