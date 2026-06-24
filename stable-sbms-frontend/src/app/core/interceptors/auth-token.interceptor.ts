import { Injectable } from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from '../services/access-control.service';

@Injectable()
export class AuthTokenInterceptor implements HttpInterceptor {

  private static readonly ACCESS_DENIED_NOTICE_KEY = 'sbms_access_denied_notice_at';

  constructor(
    private accessControl: AccessControlService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.accessControl.session?.token;
    const authRequest = token && !request.url.includes('/api/auth/login')
      ? request.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : request;

    return next.handle(authRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !request.url.includes('/api/auth/login')) {
          this.accessControl.clearSession();
          this.router.navigate(['/auth/login']);
          this.showSessionExpiredNotice(error);
        } else if (error.status === 403) {
          this.showAccessDeniedNotice(error);
        }
        return throwError(() => error);
      })
    );
  }

  private showSessionExpiredNotice(error: HttpErrorResponse): void {
    const message = error?.error?.message || 'Your session has expired. Please sign in again.';
    Swal.fire({
      icon: 'warning',
      title: 'Session Expired',
      text: message,
      confirmButtonText: 'Sign In Again'
    });
  }

  private showAccessDeniedNotice(error: HttpErrorResponse): void {
    const now = Date.now();
    const previousShownAt = Number(sessionStorage.getItem(AuthTokenInterceptor.ACCESS_DENIED_NOTICE_KEY) || '0');
    if (now - previousShownAt < 1800) {
      return;
    }
    sessionStorage.setItem(AuthTokenInterceptor.ACCESS_DENIED_NOTICE_KEY, String(now));
    Swal.fire({
      icon: 'warning',
      title: 'Access Denied',
      text: error?.error?.message || 'You do not have permission to perform this action.',
      toast: true,
      timer: 2600,
      timerProgressBar: true,
      position: 'top-end',
      showConfirmButton: false
    });
  }
}
