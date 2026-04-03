import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { LoaderComponent } from './loader/loader.component';
import { NavbarComponent } from './navbar/navbar.component';
import { SidebarComponent } from './sidebar/sidebar.component';

@NgModule({
  declarations: [NavbarComponent, SidebarComponent, LoaderComponent],
  imports: [CommonModule, RouterModule],
  exports: [NavbarComponent, SidebarComponent, LoaderComponent, CommonModule, RouterModule]
})
export class SharedModule {}
