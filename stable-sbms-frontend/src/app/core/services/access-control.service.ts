import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface AccessSession {
  token: string;
  userId: number;
  username: string;
  fullName?: string;
  profileImageName?: string | null;
  roleId: number;
  roleCode: string;
  roleName: string;
  branchId?: number | null;
  branchName?: string | null;
  mustChangePassword?: boolean;
  permissions: string[];
  rememberMe?: boolean;
}

const STORAGE_KEY = 'sbms_access_session';
const SESSION_STORAGE_KEY = 'sbms_access_session_runtime';

@Injectable({
  providedIn: 'root'
})
export class AccessControlService {

  private readonly landingRoutePriority: Array<{ permissionCode: string; route: string }> = [
    { permissionCode: 'ADMIN_DASHBOARD_ACCESS', route: '/dashboard' },
    { permissionCode: 'ROLE_MANAGEMENT_ACCESS', route: '/admin/roles/dashboard' },
    { permissionCode: 'USER_MANAGEMENT_ACCESS', route: '/admin/users/dashboard' },
    { permissionCode: 'LOOKUP_CONFIG_ACCESS', route: '/lookups/dashboard' },
    { permissionCode: 'BRANCH_MANAGEMENT_ACCESS', route: '/branches/dashboard' },
    { permissionCode: 'ATM_CDM_ACCESS', route: '/atm/dashboard' },
    { permissionCode: 'CUSTOMER_MANAGEMENT_ACCESS', route: '/customers/dashboard' },
    { permissionCode: 'KYC_MANAGEMENT_ACCESS', route: '/kyc/dashboard' },
    { permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS', route: '/accounts/dashboard' },
    { permissionCode: 'TRANSACTIONS_ACCESS', route: '/transactions/dashboard' },
    { permissionCode: 'PROFIT_MANAGEMENT_ACCESS', route: '/profit/dashboard' },
    { permissionCode: 'CARD_MANAGEMENT_ACCESS', route: '/cards/dashboard' },
    { permissionCode: 'STATEMENTS_ACCESS', route: '/statement/dashboard' },
    { permissionCode: 'DEPOSIT_SCHEMES_ACCESS', route: '/deposit-schemes/dashboard' },
    { permissionCode: 'FINANCING_ACCESS', route: '/financing/dashboard' },
    { permissionCode: 'CONTRACTS_ACCESS', route: '/contracts/dashboard' },
    { permissionCode: 'SHARIAH_REVIEW_ACCESS', route: '/shariah/dashboard' },
    { permissionCode: 'ZAKAT_CHARITY_ACCESS', route: '/zakat/dashboard' },
    { permissionCode: 'NOTIFICATION_ALERTS_ACCESS', route: '/notifications/dashboard' },
    { permissionCode: 'INTEGRATION_MANAGEMENT_ACCESS', route: '/integrations/dashboard' },
    { permissionCode: 'REPORTING_REGULATORY_ACCESS', route: '/reports/dashboard' },
    { permissionCode: 'SECURITY_AUDIT_ACCESS', route: '/security/dashboard' },
    { permissionCode: 'WORKFLOW_SUPPORT_ACCESS', route: '/workflow/dashboard' },
    { permissionCode: 'VERIFICATION_ACCESS', route: '/verification/dashboard' },
    { permissionCode: 'CALCULATION_ENGINE_ACCESS', route: '/calculations/dashboard' }
  ];

  private readonly sessionSubject = new BehaviorSubject<AccessSession | null>(this.loadSession());
  readonly session$ = this.sessionSubject.asObservable();

  get session(): AccessSession | null {
    return this.sessionSubject.value;
  }

  get currentRoleCode(): string {
    return this.session?.roleCode || '';
  }

  setSession(session: AccessSession): void {
    if (session.rememberMe) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
      sessionStorage.removeItem(SESSION_STORAGE_KEY);
    } else {
      sessionStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(session));
      localStorage.removeItem(STORAGE_KEY);
    }
    this.sessionSubject.next(session);
  }

  clearSession(): void {
    localStorage.removeItem(STORAGE_KEY);
    sessionStorage.removeItem(SESSION_STORAGE_KEY);
    this.sessionSubject.next(null);
  }

  hasPermission(permissionCode?: string | null): boolean {
    if (!permissionCode) return true;
    const normalized = permissionCode.trim().toUpperCase();
    const permissions = this.session?.permissions || [];
    return permissions.map(item => item.toUpperCase()).includes(normalized);
  }

  hasAnyPermission(codes: string[]): boolean {
    return codes.some(code => this.hasPermission(code));
  }

  getLandingRoute(): string {
    const permissions = new Set((this.session?.permissions || []).map(item => item.toUpperCase()));
    const match = this.landingRoutePriority.find(item => permissions.has(item.permissionCode));
    return match?.route || '/auth/login';
  }

  private loadSession(): AccessSession | null {
    try {
      const raw = sessionStorage.getItem(SESSION_STORAGE_KEY) || localStorage.getItem(STORAGE_KEY);
      if (!raw) return null;
      const session = JSON.parse(raw) as AccessSession;
      return session?.token && Array.isArray(session.permissions) ? session : null;
    } catch {
      return null;
    }
  }
}
