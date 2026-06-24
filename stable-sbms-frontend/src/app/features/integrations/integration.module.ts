import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { IntegrationRoutingModule } from './integration-routing.module';

import { IntegrationDashboardComponent } from './pages/integration-dashboard/integration-dashboard.component';
import { ProviderListComponent } from './pages/provider-list/provider-list.component';
import { ProviderCreateComponent } from './pages/provider-create/provider-create.component';
import { ProviderEditComponent } from './pages/provider-edit/provider-edit.component';
import { ProviderViewComponent } from './pages/provider-view/provider-view.component';
import { IntegrationLogListComponent } from './pages/integration-log-list/integration-log-list.component';
import { IntegrationLogViewComponent } from './pages/integration-log-view/integration-log-view.component';
import { ProviderTestComponent } from './pages/provider-test/provider-test.component';

@NgModule({
  declarations: [
    IntegrationDashboardComponent,
    ProviderListComponent,
    ProviderCreateComponent,
    ProviderEditComponent,
    ProviderViewComponent,
    IntegrationLogListComponent,
    IntegrationLogViewComponent,
    ProviderTestComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    IntegrationRoutingModule
  ]
})
export class IntegrationModule {}
