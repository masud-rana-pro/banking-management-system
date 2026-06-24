import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { CalculationRoutingModule } from './calculation-routing.module';

import { CalculationDashboardComponent } from './pages/calculation-dashboard/calculation-dashboard.component';
import { CalculationSimulatorComponent } from './pages/calculation-simulator/calculation-simulator.component';

@NgModule({
  declarations: [
    CalculationDashboardComponent,
    CalculationSimulatorComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    CalculationRoutingModule
  ]
})
export class CalculationModule {}
