import { NgModule } from '@angular/core';
import { DEFAULT_CURRENCY_CODE, LOCALE_ID } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { AppShellComponent } from './core/layout/app-shell/app-shell.component';
import { HeaderComponent } from './core/layout/header/header.component';
import { SidebarComponent } from './core/layout/sidebar/sidebar.component';
import { FooterComponent } from './core/layout/footer/footer.component';

import { LoginComponent } from './features/auth/login/login.component';
import { ChangePasswordComponent } from './features/auth/change-password/change-password.component';
import { SelfProfileComponent } from './features/auth/self-profile/self-profile.component';

import { SharedModule } from './shared/shared.module';
import { AdminModule } from './features/admin/admin.module';   
import { AuthTokenInterceptor } from './core/interceptors/auth-token.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    AppShellComponent,
    HeaderComponent,
    SidebarComponent,
    FooterComponent,
    LoginComponent,
    ChangePasswordComponent,
    SelfProfileComponent,

   
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    SharedModule,

    AppRoutingModule,
   
    
  ],

  providers: [
    {
      provide: DEFAULT_CURRENCY_CODE,
      useValue: 'BDT'
    },
    {
      provide: LOCALE_ID,
      useValue: 'en-US'
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthTokenInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
