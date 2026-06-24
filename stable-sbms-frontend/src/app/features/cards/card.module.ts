import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { CardRoutingModule } from './card-routing.module';

import { CardDashboardComponent } from './pages/card-dashboard/card-dashboard.component';
import { CardListComponent } from './pages/card-list/card-list.component';
import { CardCreateComponent } from './pages/card-create/card-create.component';
import { CardViewComponent } from './pages/card-view/card-view.component';
import { CardActivationComponent } from './pages/card-activation/card-activation.component';
import { CardBlockUnblockComponent } from './pages/card-block-unblock/card-block-unblock.component';
import { PinEventListComponent } from './pages/pin-event-list/pin-event-list.component';
import { AtmCdmTransactionListComponent } from './pages/atm-cdm-transaction-list/atm-cdm-transaction-list.component';

@NgModule({
  declarations: [
    CardDashboardComponent,
    CardListComponent,
    CardCreateComponent,
    CardViewComponent,
    CardActivationComponent,
    CardBlockUnblockComponent,
    PinEventListComponent,
    AtmCdmTransactionListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    CardRoutingModule
  ]
})
export class CardModule {}
