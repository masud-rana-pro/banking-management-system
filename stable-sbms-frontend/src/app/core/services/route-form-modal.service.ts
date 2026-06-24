import { Injectable } from '@angular/core';
import { NavigationExtras, Router, UrlTree } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

export interface RouteFormModalState {
  open: boolean;
  url: string;
  title: string;
}

@Injectable({ providedIn: 'root' })
export class RouteFormModalService {
  private readonly exactModalPaths = [
    '/branches/assignments',
    '/branches/teller-limits',
    '/branches/inter-branch-transfer',
    '/reports/monthly-closing-ops',
    '/reports/management-expenses',
    '/statement/export-center',
    '/verification/otp-verify',
    '/verification/reset-password',
    '/verification/provider-test'
  ];

  private readonly stateSubject = new BehaviorSubject<RouteFormModalState>({
    open: false,
    url: '',
    title: ''
  });

  private initialized = false;
  private originalNavigate?: Router['navigate'];
  private originalNavigateByUrl?: Router['navigateByUrl'];

  readonly state$ = this.stateSubject.asObservable();

  constructor(private readonly router: Router) {}

  initialize(): void {
    if (this.initialized || this.isEmbeddedWindow()) {
      return;
    }

    this.initialized = true;
    this.originalNavigate = this.router.navigate.bind(this.router);
    this.originalNavigateByUrl = this.router.navigateByUrl.bind(this.router);

    this.router.navigate = ((commands: any[], extras?: NavigationExtras) => {
      const tree = this.router.createUrlTree(commands, extras);
      const url = this.router.serializeUrl(tree);
      if (this.shouldOpenAsModal(url)) {
        this.open(url);
        return Promise.resolve(true);
      }
      return this.originalNavigate!(commands, extras);
    }) as Router['navigate'];

    this.router.navigateByUrl = ((url: string | UrlTree, extras?: NavigationExtras) => {
      const serialized = typeof url === 'string' ? url : this.router.serializeUrl(url);
      if (this.shouldOpenAsModal(serialized)) {
        this.open(serialized);
        return Promise.resolve(true);
      }
      return this.originalNavigateByUrl!(url, extras);
    }) as Router['navigateByUrl'];
  }

  open(url: string): void {
    const normalizedUrl = this.withEmbedParam(url);
    this.stateSubject.next({
      open: true,
      url: normalizedUrl,
      title: this.buildTitle(normalizedUrl)
    });
  }

  close(reloadCurrentPage = false): void {
    this.stateSubject.next({ open: false, url: '', title: '' });
    if (reloadCurrentPage) {
      window.setTimeout(() => window.location.reload(), 80);
    }
  }

  isFormRoute(url: string): boolean {
    const normalized = this.normalizePath(url);
    const pathOnly = normalized.split('?')[0];
    if (!normalized || normalized.includes('modal=0')) {
      return false;
    }
    if (/(dashboard|\/list$|\/profiles$|\/templates$|\/logs$|\/history$|\/reports?$|\/view$)/.test(normalized)) {
      return false;
    }
    if (pathOnly === '/shariah/cases' && normalized.includes('create=1')) {
      return true;
    }
    if (this.exactModalPaths.some(path => pathOnly === path || pathOnly.startsWith(`${path}/`))) {
      return true;
    }

    return /(^|\/)(new|create|edit|deposit|withdraw|transfer|cheque-clearing|status-action|request|review|documents|run|open|assign-role|reset-password|lock-unlock|permissions|activate|block-unblock|pin-events|action|generate|sign|calc-run|simulator|provider-test|disburse|repayment|addresses|identities|profit)(\/|$|\?)/.test(normalized) ||
      /\/\d+\/(edit|status|review|documents|reverse|assign-role|reset-password|lock-unlock|permissions|activate|block-unblock|action|sign|disburse|repayment|addresses|identities|profit)(\/|$|\?)/.test(normalized);
  }

  private shouldOpenAsModal(url: string): boolean {
    const normalized = this.normalizePath(url);
    if (!normalized || normalized.includes('embed=1') || normalized.includes('modal=0')) {
      return false;
    }

    return this.isFormRoute(normalized);
  }

  private withEmbedParam(url: string): string {
    const hashIndex = url.indexOf('#');
    const hash = hashIndex >= 0 ? url.slice(hashIndex) : '';
    const base = hashIndex >= 0 ? url.slice(0, hashIndex) : url;
    const separator = base.includes('?') ? '&' : '?';
    return `${base}${separator}embed=1${hash}`;
  }

  private normalizePath(url: string): string {
    return url.split('#')[0].toLowerCase();
  }

  private isEmbeddedWindow(): boolean {
    return window.location.search.includes('embed=1');
  }

  private buildTitle(url: string): string {
    const path = url.split('?')[0].split('#')[0];
    const last = path.split('/').filter(Boolean).pop() || 'Form';
    return last
      .replace(/[-_]+/g, ' ')
      .replace(/\b\w/g, char => char.toUpperCase());
  }
}
