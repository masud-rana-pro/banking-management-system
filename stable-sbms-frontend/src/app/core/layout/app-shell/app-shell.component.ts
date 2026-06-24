import { Component, OnInit, HostListener } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { RouteFormModalService } from '../../services/route-form-modal.service';

@Component({
  selector: 'app-shell',
  templateUrl: './app-shell.component.html',
  styleUrls: ['./app-shell.component.scss']
})
export class AppShellComponent implements OnInit {

  sidebarCollapsed = false;
  mobileSidebarOpen = false;
  private readonly embeddedHost = window.location.search.includes('embed=1');
  embeddedMode = this.embeddedHost;

  private readonly SIDEBAR_WIDTH     = '272px';
  private readonly SIDEBAR_COLLAPSED = '72px';

  constructor(
    private readonly router: Router,
    private readonly routeFormModal: RouteFormModalService
  ) {}

  ngOnInit(): void {
    this.routeFormModal.initialize();
    this.updateEmbeddedMode();
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.updateEmbeddedMode();
        this.notifyModalHostWhenComplete();
      });

    this.updateCssVar();
    // Collapse by default on tablet
    if (window.innerWidth < 1280 && window.innerWidth >= 992) {
      this.sidebarCollapsed = true;
      this.updateCssVar();
    }
  }

  onToggleSidebar(): void {
    if (window.innerWidth <= 991) {
      this.mobileSidebarOpen = !this.mobileSidebarOpen;
    } else {
      this.sidebarCollapsed = !this.sidebarCollapsed;
      this.updateCssVar();
    }
  }

  onSidebarMenuClick(): void {
    if (window.innerWidth <= 991) {
      this.mobileSidebarOpen = false;
    }
  }

  closeMobileSidebar(): void {
    this.mobileSidebarOpen = false;
  }

  @HostListener('window:resize', ['$event'])
  onResize(): void {
    if (window.innerWidth > 991) {
      this.mobileSidebarOpen = false;
    }
    this.updateCssVar();
  }

  private updateCssVar(): void {
    const root = document.documentElement;
    const collapsed = this.sidebarCollapsed;
    root.style.setProperty(
      '--current-sidebar-width',
      collapsed ? this.SIDEBAR_COLLAPSED : this.SIDEBAR_WIDTH
    );
  }

  private updateEmbeddedMode(): void {
    this.embeddedMode = this.embeddedHost || this.router.url.includes('embed=1');
  }

  private notifyModalHostWhenComplete(): void {
    if (!this.embeddedHost || this.routeFormModal.isFormRoute(this.router.url)) {
      return;
    }

    window.parent.postMessage({ type: 'sbms-route-form-done' }, window.location.origin);
  }
}
