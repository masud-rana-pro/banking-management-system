import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { SharedModule } from 'src/app/shared/shared.module';
import { VerificationRoutingModule } from './verification-routing.module';

import { VerificationDashboardComponent } from './pages/verification-dashboard/verification-dashboard.component';
import { OtpVerifyComponent } from './pages/otp-verify/otp-verify.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { VerificationLogListComponent } from './pages/verification-log-list/verification-log-list.component';
import { ProviderTestConsoleComponent } from './pages/provider-test-console/provider-test-console.component';

@NgModule({
  declarations: [
    VerificationDashboardComponent,
    OtpVerifyComponent,
    ForgotPasswordComponent,
    ResetPasswordComponent,
    VerificationLogListComponent,
    ProviderTestConsoleComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    SharedModule,
    VerificationRoutingModule
  ]
})
export class VerificationModule {}
