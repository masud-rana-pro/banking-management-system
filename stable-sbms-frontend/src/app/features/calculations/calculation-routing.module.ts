import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { CalculationDashboardComponent } from './pages/calculation-dashboard/calculation-dashboard.component';
import { CalculationSimulatorComponent } from './pages/calculation-simulator/calculation-simulator.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: CalculationDashboardComponent },
  { path: 'simulator', component: CalculationSimulatorComponent, canActivate: [PermissionGuard], data: { permissionCode: 'CALCULATION_SIMULATE' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CalculationRoutingModule {}
