import { Component, EventEmitter, HostListener, OnDestroy, OnInit, Output } from '@angular/core';
import { ThemeService } from 'src/app/core/services/theme.service';
import { AccessControlService, AccessSession } from 'src/app/core/services/access-control.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { Router } from '@angular/router';
import { LiveUpdateItem, LiveUpdateService } from 'src/app/core/services/live-update.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { AppLanguage, LanguageService } from 'src/app/core/services/language.service';

interface DropdownItem {
  id?: string;
  title: string;
  text: string;
  icon: string;
  route?: string | null;
  severity?: string;
}

interface LanguageOption {
  code: AppLanguage;
  nativeLabel: string;
  icon: string;
}

interface SearchItem {
  label: string;
  route: string;
  icon: string;
  keywords: string[];
  permissionCode?: string;
}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, OnDestroy {
  @Output() toggleSidebar = new EventEmitter<void>();

  languages: LanguageOption[] = [
    { code: 'en', nativeLabel: 'English',  icon: 'bi bi-globe' },
    { code: 'bn', nativeLabel: 'বাংলা',    icon: 'bi bi-globe' }
  ];

  selectedLanguage: AppLanguage = 'en';
  currentThemeMode: 'light' | 'dark' = 'light';

  user = {
    id: null as number | null,
    username: '',
    name: 'System User',
    role: 'SYSTEM_ADMIN',
    roleName: 'System Administrator',
    branch: 'Head Office',
    profileImageName: '' as string | null,
    avatarUrl: ''
  };

  showProfileMenu      = false;
  showNotificationPanel = false;
  showMessagesPanel    = false;
  showLangMenu         = false;
  showSearchResults    = false;
  unreadNotificationCount = 0;
  unreadMessageCount = 0;
  currentTimeLabel = '';
  currentDateLabel = '';
  searchQuery = '';
  private clockTimer: ReturnType<typeof setInterval> | null = null;

  notifications: DropdownItem[] = [];
  messages: DropdownItem[] = [];
  readonly searchCatalog: SearchItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: 'bi bi-speedometer2', keywords: ['dashboard', 'home'], permissionCode: 'ADMIN_DASHBOARD_ACCESS' },
    { label: 'My Profile', route: '/account/profile', icon: 'bi bi-person-circle', keywords: ['profile', 'me', 'account'] },
    { label: 'Change Password', route: '/account/change-password', icon: 'bi bi-key', keywords: ['password', 'security', 'change password'] },
    { label: 'User Dashboard', route: '/admin/users/dashboard', icon: 'bi bi-people', keywords: ['user', 'staff', 'admin users'], permissionCode: 'USER_MANAGEMENT_ACCESS' },
    { label: 'User List', route: '/admin/users', icon: 'bi bi-person-lines-fill', keywords: ['user list', 'staff list', 'users'], permissionCode: 'USER_VIEW' },
    { label: 'Role Dashboard', route: '/admin/roles/dashboard', icon: 'bi bi-shield-lock', keywords: ['role', 'permission', 'rbac'], permissionCode: 'ROLE_MANAGEMENT_ACCESS' },
    { label: 'Customer Dashboard', route: '/customers/dashboard', icon: 'bi bi-person-vcard', keywords: ['customer', 'customer dashboard'], permissionCode: 'CUSTOMER_MANAGEMENT_ACCESS' },
    { label: 'Customer List', route: '/customers/list', icon: 'bi bi-card-list', keywords: ['customer list', 'customers'], permissionCode: 'CUSTOMER_MANAGEMENT_ACCESS' },
    { label: 'KYC Dashboard', route: '/kyc/dashboard', icon: 'bi bi-patch-check', keywords: ['kyc', 'verification'], permissionCode: 'KYC_MANAGEMENT_ACCESS' },
    { label: 'KYC List', route: '/kyc/list', icon: 'bi bi-ui-checks-grid', keywords: ['kyc list', 'kyc profiles'], permissionCode: 'KYC_MANAGEMENT_ACCESS' },
    { label: 'Account Dashboard', route: '/accounts/dashboard', icon: 'bi bi-bank', keywords: ['account dashboard', 'accounts'], permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' },
    { label: 'Account List', route: '/accounts/list', icon: 'bi bi-bank2', keywords: ['account list', 'accounts'], permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS' },
    { label: 'Transaction Dashboard', route: '/transactions/dashboard', icon: 'bi bi-arrow-left-right', keywords: ['transactions', 'transaction dashboard'], permissionCode: 'TRANSACTIONS_ACCESS' },
    { label: 'Transaction List', route: '/transactions/list', icon: 'bi bi-receipt', keywords: ['transaction list', 'journal'], permissionCode: 'TRANSACTIONS_ACCESS' },
    { label: 'Card Dashboard', route: '/cards/dashboard', icon: 'bi bi-credit-card-2-front', keywords: ['cards', 'card dashboard'], permissionCode: 'CARD_MANAGEMENT_ACCESS' },
    { label: 'Card List', route: '/cards/list', icon: 'bi bi-credit-card', keywords: ['card list', 'cards'], permissionCode: 'CARD_MANAGEMENT_ACCESS' },
    { label: 'Financing Dashboard', route: '/financing/dashboard', icon: 'bi bi-cash-coin', keywords: ['financing', 'loan', 'investment'], permissionCode: 'FINANCING_ACCESS' },
    { label: 'Financing List', route: '/financing/applications/list', icon: 'bi bi-journal-richtext', keywords: ['financing list', 'loan list', 'applications'], permissionCode: 'FINANCING_ACCESS' },
    { label: 'Deposit Scheme Dashboard', route: '/deposit-schemes/dashboard', icon: 'bi bi-piggy-bank', keywords: ['deposit schemes', 'scheme dashboard'], permissionCode: 'DEPOSIT_SCHEMES_ACCESS' },
    { label: 'Profit Dashboard', route: '/profit/dashboard', icon: 'bi bi-graph-up-arrow', keywords: ['profit', 'profit dashboard'], permissionCode: 'PROFIT_MANAGEMENT_ACCESS' },
    { label: 'Reports Dashboard', route: '/reports/dashboard', icon: 'bi bi-bar-chart-line', keywords: ['reports', 'report dashboard'], permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Monthly Closing Ops', route: '/reports/monthly-closing-ops', icon: 'bi bi-calendar2-check', keywords: ['monthly closing', 'closing ops'], permissionCode: 'REPORTING_REGULATORY_ACCESS' },
    { label: 'Branch Dashboard', route: '/branches/dashboard', icon: 'bi bi-building', keywords: ['branch', 'branches'], permissionCode: 'BRANCH_MANAGEMENT_ACCESS' },
    { label: 'ATM Dashboard', route: '/atm/dashboard', icon: 'bi bi-pc-display-horizontal', keywords: ['atm', 'cdm', 'atm dashboard'], permissionCode: 'ATM_CDM_ACCESS' },
    { label: 'Zakat Dashboard', route: '/zakat/dashboard', icon: 'bi bi-heart-pulse', keywords: ['zakat', 'charity'], permissionCode: 'ZAKAT_CHARITY_ACCESS' },
    { label: 'Zakat Profile List', route: '/zakat/profiles/list', icon: 'bi bi-grid-1x2', keywords: ['zakat profiles', 'zakat list'], permissionCode: 'ZAKAT_CHARITY_ACCESS' },
    { label: 'Notifications Dashboard', route: '/notifications/dashboard', icon: 'bi bi-envelope-paper', keywords: ['notifications', 'alerts', 'email'], permissionCode: 'NOTIFICATION_ALERTS_ACCESS' },
    { label: 'Verification Dashboard', route: '/verification/dashboard', icon: 'bi bi-shield-check', keywords: ['verification dashboard', 'otp', 'verification'], permissionCode: 'VERIFICATION_ACCESS' }
  ];

  constructor(
    public themeService: ThemeService,
    private accessControl: AccessControlService,
    private authService: AuthService,
    private router: Router,
    private liveUpdateService: LiveUpdateService,
    private fileUploadService: FileUploadService,
    private userApiService: UserApiService,
    private languageService: LanguageService
  ) {
    this.accessControl.session$.subscribe(session => {
      this.user = this.mapSessionToUser(session);
    });

    this.themeService.currentTheme$.subscribe(theme => {
      this.currentThemeMode = theme;
    });

    this.liveUpdateService.notifications$.subscribe(items => {
      this.notifications = items.map(item => this.toDropdownItem(item));
    });

    this.liveUpdateService.messages$.subscribe(items => {
      this.messages = items.map(item => this.toDropdownItem(item));
    });

    this.liveUpdateService.unreadCount$.subscribe(count => {
      this.unreadNotificationCount = count;
    });

    this.liveUpdateService.unreadMessageCount$.subscribe(count => {
      this.unreadMessageCount = count;
    });

    this.languageService.language$.subscribe(language => {
      this.selectedLanguage = language;
    });
  }

  ngOnInit(): void {
    this.startClock();
    this.refreshSessionProfile();
  }

  @HostListener('document:click')
  onDocumentClick(): void { this.closeAllMenus(); }

  stopEvent(e: MouseEvent): void { e.stopPropagation(); }

  closeAllMenus(): void {
    this.showProfileMenu       = false;
    this.showNotificationPanel = false;
    this.showMessagesPanel     = false;
    this.showLangMenu          = false;
    this.showSearchResults     = false;
  }

  toggleProfileMenu(e: MouseEvent): void {
    e.stopPropagation();
    this.showProfileMenu = !this.showProfileMenu;
    this.showNotificationPanel = false;
    this.showMessagesPanel     = false;
    this.showLangMenu          = false;
  }

  toggleNotifications(e: MouseEvent): void {
    e.stopPropagation();
    this.showNotificationPanel = !this.showNotificationPanel;
    if (this.showNotificationPanel) {
      this.liveUpdateService.markNotificationsSeen();
    }
    this.showProfileMenu  = false;
    this.showMessagesPanel = false;
    this.showLangMenu     = false;
  }

  toggleMessages(e: MouseEvent): void {
    e.stopPropagation();
    this.showMessagesPanel = !this.showMessagesPanel;
    if (this.showMessagesPanel) {
      this.liveUpdateService.markMessagesSeen();
    }
    this.showNotificationPanel = false;
    this.showProfileMenu   = false;
    this.showLangMenu      = false;
  }

  toggleLanguageMenu(e: MouseEvent): void {
    e.stopPropagation();
    this.showLangMenu = !this.showLangMenu;
    this.showMessagesPanel     = false;
    this.showNotificationPanel = false;
    this.showProfileMenu       = false;
  }

  changeLanguage(code: AppLanguage, e: MouseEvent): void {
    e.stopPropagation();
    this.languageService.setLanguage(code);
    this.showLangMenu = false;
  }

  toggleTheme(e: MouseEvent): void {
    e.stopPropagation();
    this.themeService.toggleTheme();
  }

  onSearchFocus(): void {
    this.showSearchResults = true;
  }

  onSearchInput(value: string): void {
    this.searchQuery = value;
    this.showSearchResults = true;
  }

  onSearchKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.showSearchResults = false;
      return;
    }
    if (event.key === 'Enter') {
      event.preventDefault();
      const firstResult = this.filteredSearchResults[0];
      if (firstResult) {
        this.navigateToSearchResult(firstResult);
      }
    }
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.showSearchResults = false;
  }

  openSearchResult(item: SearchItem, event: MouseEvent): void {
    event.stopPropagation();
    this.navigateToSearchResult(item);
  }

  openProfile(e: MouseEvent): void {
    e.stopPropagation();
    this.showProfileMenu = false;
    this.router.navigate(['/account/profile']);
  }

  openSecurity(e: MouseEvent): void {
    e.stopPropagation();
    this.showProfileMenu = false;
    this.router.navigate(['/account/change-password']);
  }

  logout(e: MouseEvent): void {
    e.stopPropagation();
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/auth/login'])
    });
  }

  openNotification(item: DropdownItem, e: MouseEvent): void {
    e.stopPropagation();
    this.showNotificationPanel = false;
    if (item.route) {
      this.router.navigateByUrl(item.route);
    }
  }

  openMessage(item: DropdownItem, e: MouseEvent): void {
    e.stopPropagation();
    this.showMessagesPanel = false;
    if (item.route) {
      this.router.navigateByUrl(item.route);
    }
  }

  get themeToggleTitle(): string {
    return this.currentThemeMode === 'dark' ? 'Switch to light mode' : 'Switch to dark mode';
  }

  get themeIconClass(): string {
    return this.currentThemeMode === 'dark' ? 'bi-sun' : 'bi-moon-stars';
  }

  get filteredSearchResults(): SearchItem[] {
    const query = this.searchQuery.trim().toLowerCase();
    const allowed = this.searchCatalog.filter(item => !item.permissionCode || this.accessControl.hasPermission(item.permissionCode));
    if (!query) {
      return allowed.slice(0, 8);
    }
    return allowed
      .filter(item =>
        item.label.toLowerCase().includes(query)
        || item.route.toLowerCase().includes(query)
        || item.keywords.some(keyword => keyword.toLowerCase().includes(query)))
      .slice(0, 8);
  }

  private toDropdownItem(item: LiveUpdateItem): DropdownItem {
    return {
      id: item.id,
      title: item.title,
      text: item.message,
      icon: this.resolveNotificationIcon(item.category, item.severity),
      route: item.route,
      severity: item.severity
    };
  }

  private resolveNotificationIcon(category?: string, severity?: string): string {
    const normalizedCategory = (category || '').toUpperCase();
    if (normalizedCategory === 'REPORT') return 'bi bi-file-earmark-bar-graph';
    if (normalizedCategory === 'VERIFICATION') return 'bi bi-shield-check';
    if (normalizedCategory === 'NOTIFICATION') return 'bi bi-bell';
    if ((severity || '').toUpperCase() === 'WARNING') return 'bi bi-exclamation-triangle';
    return 'bi bi-info-circle';
  }

  getUserInitials(): string {
    const base = (this.user.name || this.user.username || 'SU').trim();
    const parts = base.split(/\s+/).filter(Boolean);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return base.substring(0, 2).toUpperCase();
  }

  onAvatarError(): void {
    this.user.avatarUrl = '';
  }

  private refreshSessionProfile(): void {
    const currentSession = this.accessControl.session;
    if (!currentSession?.token) {
      return;
    }

    this.authService.me().subscribe({
      next: session => {
        const existing = this.accessControl.session;
        if (!existing) {
          return;
        }
        this.accessControl.setSession({
          ...session,
          rememberMe: existing.rememberMe
        });
        this.refreshHeaderImageFromUserApi(session.userId);
      },
      error: () => {
        this.refreshHeaderImageFromUserApi(currentSession.userId);
      }
    });
  }

  private refreshHeaderImageFromUserApi(userId?: number | null): void {
    if (!userId) {
      return;
    }

    this.userApiService.getById(userId).subscribe({
      next: user => {
        const existing = this.accessControl.session;
        if (!existing || existing.userId !== user.id) {
          return;
        }
        this.accessControl.setSession({
          ...existing,
          username: user.username || existing.username,
          fullName: user.fullName || existing.fullName,
          profileImageName: user.profileImageName || null,
          branchId: user.branchId ?? existing.branchId ?? null,
          branchName: user.branchName || existing.branchName || null,
          roleId: user.roleId ?? existing.roleId,
          roleCode: user.roleCode || existing.roleCode,
          roleName: user.roleName || existing.roleName,
          rememberMe: existing.rememberMe
        });
      },
      error: () => {
        // Keep current header state if user lookup fails.
      }
    });
  }

  private mapSessionToUser(session: AccessSession | null) {
    const profileImageName = session?.profileImageName || '';
    return {
      id: session?.userId || null,
      username: session?.username || '',
      name: session?.fullName || session?.username || 'System User',
      role: session?.roleCode || 'NO_ROLE',
      roleName: session?.roleName || 'No assigned role',
      branch: session?.branchName || 'Head Office',
      profileImageName,
      avatarUrl: this.buildAvatarUrl(profileImageName)
    };
  }

  private buildAvatarUrl(profileImageName?: string | null): string {
    if (!profileImageName) {
      return '';
    }
    const baseUrl = this.fileUploadService.resolveImageUrl(profileImageName);
    const versionToken = encodeURIComponent(profileImageName);
    return `${baseUrl}?v=${versionToken}`;
  }

  private navigateToSearchResult(item: SearchItem): void {
    this.searchQuery = '';
    this.showSearchResults = false;
    this.router.navigateByUrl(item.route);
  }
  ngOnDestroy(): void {
    if (this.clockTimer) {
      clearInterval(this.clockTimer);
      this.clockTimer = null;
    }
  }

  private startClock(): void {
    this.updateClock();
    this.clockTimer = setInterval(() => this.updateClock(), 1000);
  }

  private updateClock(): void {
    const now = new Date();
    this.currentTimeLabel = new Intl.DateTimeFormat('en-US', {
      hour: 'numeric',
      minute: '2-digit',
      hour12: true
    }).format(now);
    this.currentDateLabel = new Intl.DateTimeFormat('en-GB', {
      weekday: 'short',
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    }).format(now);
  }
}
