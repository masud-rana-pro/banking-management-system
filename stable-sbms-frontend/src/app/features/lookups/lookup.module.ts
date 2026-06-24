import { NgModule } from '@angular/core';

import { SharedModule } from 'src/app/shared/shared.module';
import { LookupRoutingModule } from './lookup-routing.module';
import { LookupDashboardComponent } from './pages/lookup-dashboard/lookup-dashboard.component';
import { LookupTypeListComponent } from './pages/lookup-type-list/lookup-type-list.component';
import { LookupTypeFormComponent } from './pages/lookup-type-form/lookup-type-form.component';
import { LookupTypeViewComponent } from './pages/lookup-type-view/lookup-type-view.component';
import { LookupValueListComponent } from './pages/lookup-value-list/lookup-value-list.component';
import { LookupValueFormComponent } from './pages/lookup-value-form/lookup-value-form.component';
import { LookupValueViewComponent } from './pages/lookup-value-view/lookup-value-view.component';

@NgModule({
  declarations: [
    LookupDashboardComponent,
    LookupTypeListComponent,
    LookupTypeFormComponent,
    LookupTypeViewComponent,
    LookupValueListComponent,
    LookupValueFormComponent,
    LookupValueViewComponent
  ],
  imports: [
    SharedModule,
    LookupRoutingModule
  ]
})
export class LookupModule {}
