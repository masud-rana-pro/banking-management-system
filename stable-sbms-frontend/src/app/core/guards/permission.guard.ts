import { Injectable } from '@angular/core';
import { CanActivate, CanLoad, Route, Router, UrlSegment, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { AccessControlService } from '../services/access-control.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionGuard implements CanActivate, CanLoad {

  constructor(
    private accessControl: AccessControlService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, _state: RouterStateSnapshot): boolean {
    const permission = route.data?.['permissionCode'] as string | undefined;
    const permissions = route.data?.['permissionCodes'] as string[] | undefined;
    return this.check(permission, permissions);
  }

  canLoad(route: Route, _segments: UrlSegment[]): boolean {
    const permission = route.data?.['permissionCode'] as string | undefined;
    const permissions = route.data?.['permissionCodes'] as string[] | undefined;
    return this.check(permission, permissions);
  }

  private check(permissionCode?: string, permissionCodes?: string[]): boolean {
    if (!this.accessControl.session) {
      this.router.navigate(['/auth/login']);
      return false;
    }
    if (this.accessControl.session.mustChangePassword) {
      this.router.navigate(['/auth/change-password']);
      return false;
    }
    if (permissionCodes && permissionCodes.length > 0) {
      if (this.accessControl.hasAnyPermission(permissionCodes)) return true;
    } else if (this.accessControl.hasPermission(permissionCode)) {
      return true;
    }
    this.router.navigate([this.accessControl.getLandingRoute()]);
    return false;
  }
}
