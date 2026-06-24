import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionGuard } from 'src/app/core/guards/permission.guard';

import { VerificationDashboardComponent } from './pages/verification-dashboard/verification-dashboard.component';
import { OtpVerifyComponent } from './pages/otp-verify/otp-verify.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { VerificationLogListComponent } from './pages/verification-log-list/verification-log-list.component';
import { ProviderTestConsoleComponent } from './pages/provider-test-console/provider-test-console.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: VerificationDashboardComponent, canActivate: [PermissionGuard], data: { permissionCode: 'VERIFICATION_ACCESS' } },
  { path: 'otp-verify', component: OtpVerifyComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'logs', component: VerificationLogListComponent, canActivate: [PermissionGuard], data: { permissionCode: 'VERIFICATION_ACCESS' } },
  { path: 'provider-test', component: ProviderTestConsoleComponent, canActivate: [PermissionGuard], data: { permissionCode: 'VERIFICATION_PROVIDER_TEST' } }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class VerificationRoutingModule {}
