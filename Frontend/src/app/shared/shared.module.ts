import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LoaderComponent } from './loader/loader.component';
import { NavbarBackComponent } from './navbar-back/navbar-back.component';
import { NavbarFrontComponent } from './navbar-front/navbar-front.component';
import { SidebarBackComponent } from './sidebar/sidebar.component';

@NgModule({
  declarations: [
    NavbarBackComponent,
    NavbarFrontComponent,
    SidebarBackComponent,
    LoaderComponent
  ],
  imports: [CommonModule, RouterModule],
  exports: [
    NavbarBackComponent,
    NavbarFrontComponent,
    SidebarBackComponent,
    LoaderComponent,
    CommonModule,
    RouterModule
  ]
})
export class SharedModule {}
