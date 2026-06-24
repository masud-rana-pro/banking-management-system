import {
  AfterViewInit,
  Directive,
  ElementRef,
  OnDestroy,
  Renderer2
} from '@angular/core';
import { Subscription } from 'rxjs';
import { TableActionDisplayMode, TableActionModeService } from '../services/table-action-mode.service';

@Directive({
  selector: '.table-actions, .sbms-table-actions, td'
})
export class TableActionMenuDirective implements AfterViewInit, OnDestroy {
  private menuButton?: HTMLElement;
  private menuPanel?: HTMLElement;
  private menuItems?: HTMLElement;
  private observer?: MutationObserver;
  private modeSub?: Subscription;
  private documentClickUnlisten?: () => void;
  private windowResizeUnlisten?: () => void;
  private globalScrollHandler?: EventListener;
  private rebuildTimer?: number;

  constructor(
    private readonly elementRef: ElementRef<HTMLElement>,
    private readonly renderer: Renderer2,
    private readonly actionMode: TableActionModeService
  ) {}

  ngAfterViewInit(): void {
    this.rebuildMenu();
    this.modeSub = this.actionMode.mode$.subscribe(mode => this.applyMode(mode));
    this.documentClickUnlisten = this.renderer.listen('document', 'click', (event: MouseEvent) => {
      const target = event.target as Node;
      if (!this.host.contains(target) && !this.menuPanel?.contains(target)) {
        this.closeMenu();
      }
    });
    this.windowResizeUnlisten = this.renderer.listen('window', 'resize', () => this.closeMenu());
    this.globalScrollHandler = (event: Event) => {
      const target = event.target as Node | null;
      if (target && this.menuPanel?.contains(target)) {
        return;
      }
      this.closeMenu();
    };
    window.addEventListener('scroll', this.globalScrollHandler, true);
    this.observer = new MutationObserver(() => this.scheduleRebuild());
    this.startObserving();
  }

  ngOnDestroy(): void {
    this.modeSub?.unsubscribe();
    this.observer?.disconnect();
    this.documentClickUnlisten?.();
    this.windowResizeUnlisten?.();
    if (this.globalScrollHandler) {
      window.removeEventListener('scroll', this.globalScrollHandler, true);
    }
    if (this.rebuildTimer) {
      window.clearTimeout(this.rebuildTimer);
    }
  }

  private get host(): HTMLElement {
    return this.elementRef.nativeElement;
  }

  private scheduleRebuild(): void {
    if (this.rebuildTimer) {
      window.clearTimeout(this.rebuildTimer);
    }
    this.rebuildTimer = window.setTimeout(() => this.rebuildMenu(), 60);
  }

  private rebuildMenu(): void {
    const sourceButtons = this.getSourceButtons();
    if (!sourceButtons.length) {
      this.renderer.removeClass(this.host, 'sbms-enhanced-table-actions');
      this.renderer.removeClass(this.host, 'is-menu-mode');
      this.renderer.removeClass(this.host, 'is-inline-mode');
      this.removeMenu();
      this.startObserving();
      return;
    }

    this.observer?.disconnect();
    this.renderer.addClass(this.host, 'sbms-enhanced-table-actions');
    this.ensureHeaderToggle();
    this.ensureMenuShell();
    this.renderer.setProperty(this.menuItems, 'innerHTML', '');

    sourceButtons.forEach((button, index) => {
      const item = this.renderer.createElement('button') as HTMLButtonElement;
      const actionName = this.resolveActionName(button, index);
      const tone = this.resolveTone(button);
      const disabled = this.isDisabled(button);

      this.renderer.setAttribute(item, 'type', 'button');
      this.renderer.addClass(item, 'sbms-action-menu-item');
      this.renderer.addClass(item, `tone-${tone}`);
      this.renderer.setProperty(item, 'disabled', disabled);

      const icon = this.renderer.createElement('i') as HTMLElement;
      this.copyIconClasses(button, icon);
      const label = this.renderer.createElement('span') as HTMLElement;
      this.renderer.setProperty(label, 'textContent', actionName);

      this.renderer.appendChild(item, icon);
      this.renderer.appendChild(item, label);
      this.renderer.listen(item, 'click', (event: MouseEvent) => {
        event.stopPropagation();
        if (!disabled) {
          this.closeMenu();
          button.click();
        }
      });
      this.renderer.appendChild(this.menuItems, item);
    });

    this.applyMode(this.actionMode.mode);
    this.startObserving();
  }

  private startObserving(): void {
    this.observer?.observe(this.host, { childList: true, subtree: true, attributes: true, attributeFilter: ['disabled', 'title', 'class'] });
  }

  private ensureMenuShell(): void {
    if (!this.menuButton) {
      this.menuButton = this.renderer.createElement('button') as HTMLButtonElement;
      this.renderer.setAttribute(this.menuButton, 'type', 'button');
      this.renderer.setAttribute(this.menuButton, 'title', 'More actions');
      this.renderer.addClass(this.menuButton, 'sbms-action-menu-trigger');
      const icon = this.renderer.createElement('i');
      this.renderer.addClass(icon, 'bi');
      this.renderer.addClass(icon, 'bi-three-dots-vertical');
      this.renderer.appendChild(this.menuButton, icon);
      this.renderer.listen(this.menuButton, 'click', (event: MouseEvent) => {
        event.stopPropagation();
        this.toggleMenu();
      });
      this.renderer.appendChild(this.host, this.menuButton);
    }

    if (!this.menuPanel) {
      this.menuPanel = this.renderer.createElement('div') as HTMLElement;
      this.renderer.addClass(this.menuPanel, 'sbms-action-menu-panel');
      this.renderer.appendChild(document.body, this.menuPanel);
    }

    if (!this.menuItems) {
      this.menuItems = this.renderer.createElement('div') as HTMLElement;
      this.renderer.addClass(this.menuItems, 'sbms-action-menu-items');
      this.renderer.appendChild(this.menuPanel, this.menuItems);
    }
  }

  private removeMenu(): void {
    if (this.menuButton) {
      this.renderer.removeChild(this.host, this.menuButton);
      this.menuButton = undefined;
    }
    if (this.menuPanel) {
      this.renderer.removeChild(document.body, this.menuPanel);
      this.menuPanel = undefined;
      this.menuItems = undefined;
    }
  }

  private getSourceButtons(): HTMLElement[] {
    const isExplicitActionHost = this.host.classList.contains('table-actions') || this.host.classList.contains('sbms-table-actions');
    const isActionColumnCell = this.isActionColumnCell();

    if (!isExplicitActionHost && isActionColumnCell && this.host.querySelector(':scope .table-actions, :scope .sbms-table-actions')) {
      return [];
    }

    const selector = isActionColumnCell
      ? ':scope button, :scope a, :scope [role="button"]'
      : ':scope > button, :scope > a, :scope > [role="button"]';

    return Array.from(this.host.querySelectorAll(selector))
      .filter(action =>
        !action.classList.contains('sbms-action-menu-trigger') &&
        !action.classList.contains('sbms-action-menu-item') &&
        (isExplicitActionHost || isActionColumnCell || action.classList.contains('action-btn'))
      ) as HTMLElement[];
  }

  private isActionColumnCell(): boolean {
    if (this.host.tagName.toLowerCase() !== 'td') {
      return false;
    }

    const table = this.host.closest('table');
    const row = this.host.closest('tr');
    if (!table || !row) {
      return false;
    }

    const cells = Array.from(row.children).filter(cell => cell.tagName.toLowerCase() === 'td');
    const cellIndex = cells.indexOf(this.host);
    if (cellIndex < 0) {
      return false;
    }

    const headerCells = Array.from(table.querySelectorAll('thead tr:last-child th')) as HTMLTableCellElement[];
    let expandedIndex = 0;
    for (const header of headerCells) {
      const span = Math.max(1, header.colSpan || 1);
      const headerText = (header.textContent || '').trim();
      if (cellIndex >= expandedIndex && cellIndex < expandedIndex + span) {
        return /actions?/i.test(headerText);
      }
      expandedIndex += span;
    }

    return false;
  }

  private applyMode(mode: TableActionDisplayMode): void {
    const menuMode = mode === 'menu';
    if (menuMode) {
      this.renderer.addClass(this.host, 'is-menu-mode');
      this.renderer.removeClass(this.host, 'is-inline-mode');
    } else {
      this.renderer.removeClass(this.host, 'is-menu-mode');
      this.renderer.addClass(this.host, 'is-inline-mode');
    }
    if (!menuMode) {
      this.closeMenu();
    }
    this.updateHeaderToggleState(mode);
  }

  private toggleMenu(): void {
    if (!this.menuPanel) return;
    const open = this.menuPanel.classList.contains('open');
    document.querySelectorAll('.sbms-action-menu-panel.open').forEach(panel => panel.classList.remove('open'));
    if (!open) {
      this.positionMenu();
      this.menuPanel.classList.add('open');
    }
  }

  private closeMenu(): void {
    this.menuPanel?.classList.remove('open', 'open-upward');
  }

  private positionMenu(): void {
    if (!this.menuPanel || !this.menuButton) return;
    const rect = this.menuButton.getBoundingClientRect();
    this.renderer.setStyle(this.menuPanel, 'display', 'grid');
    this.renderer.setStyle(this.menuPanel, 'visibility', 'hidden');
    this.renderer.setStyle(this.menuPanel, 'pointer-events', 'none');

    const maxPanelHeight = Math.min(420, Math.max(220, window.innerHeight - 48));
    const panelWidth = Math.min(Math.max(this.menuPanel.offsetWidth || 218, 218), Math.min(232, window.innerWidth - 24));
    const panelHeight = Math.min(this.menuPanel.scrollHeight || 220, maxPanelHeight);
    const left = Math.max(12, Math.min(window.innerWidth - panelWidth - 12, rect.right - panelWidth));
    const bottomTop = rect.bottom + 8;
    const topTop = rect.top - panelHeight - 8;
    const hasBottomSpace = bottomTop + panelHeight <= window.innerHeight - 12;
    const top = hasBottomSpace
      ? bottomTop
      : Math.max(12, topTop);

    this.renderer.setStyle(this.menuPanel, 'width', `${panelWidth}px`);
    this.renderer.setStyle(this.menuPanel, 'max-height', `${maxPanelHeight}px`);
    this.renderer.setStyle(this.menuPanel, 'left', `${left}px`);
    this.renderer.setStyle(this.menuPanel, 'top', `${top}px`);
    this.renderer.removeStyle(this.menuPanel, 'display');
    this.renderer.removeStyle(this.menuPanel, 'visibility');
    this.renderer.removeStyle(this.menuPanel, 'pointer-events');
    this.menuPanel.classList.toggle('open-upward', !hasBottomSpace);
  }

  private ensureHeaderToggle(): void {
    const table = this.host.closest('table');
    if (!table || table.classList.contains('sbms-action-toggle-ready')) {
      return;
    }
    const headers = Array.from(table.querySelectorAll('thead th')) as HTMLElement[];
    const actionHeader = headers.find(th => /actions?/i.test((th.textContent || '').trim()));
    if (!actionHeader) {
      return;
    }

    table.classList.add('sbms-action-toggle-ready');
    actionHeader.classList.add('sbms-action-header');

    const label = this.renderer.createElement('span') as HTMLElement;
    this.renderer.addClass(label, 'sbms-action-header-label');
    this.renderer.setProperty(label, 'textContent', (actionHeader.textContent || 'Actions').trim());
    this.renderer.setProperty(actionHeader, 'innerHTML', '');
    this.renderer.appendChild(actionHeader, label);

    const toggle = this.renderer.createElement('button') as HTMLButtonElement;
    this.renderer.setAttribute(toggle, 'type', 'button');
    this.renderer.setAttribute(toggle, 'title', 'Toggle action display mode');
    this.renderer.addClass(toggle, 'sbms-action-mode-toggle');
    toggle.dataset['sbmsActionModeToggle'] = 'true';
    this.renderer.listen(toggle, 'click', (event: MouseEvent) => {
      event.stopPropagation();
      this.actionMode.toggle();
    });
    this.renderer.appendChild(actionHeader, toggle);
    this.updateHeaderToggleState(this.actionMode.mode);
  }

  private updateHeaderToggleState(mode: TableActionDisplayMode): void {
    const table = this.host.closest('table');
    const toggle = table?.querySelector('[data-sbms-action-mode-toggle="true"]') as HTMLElement | null;
    if (!toggle) {
      return;
    }
    toggle.classList.toggle('menu-mode', mode === 'menu');
    toggle.classList.toggle('inline-mode', mode === 'inline');
    toggle.innerHTML = mode === 'menu'
      ? '<i class="bi bi-three-dots-vertical"></i><span>Menu</span>'
      : '<i class="bi bi-grid-3x3-gap"></i><span>Icons</span>';
  }

  private resolveActionName(button: HTMLElement, index: number): string {
    const title = button.getAttribute('title') || button.getAttribute('aria-label') || button.textContent || '';
    const normalized = title.replace(/\s+/g, ' ').trim();
    if (normalized) return normalized;

    const classList = Array.from(button.classList);
    const known = classList.find(cls => !['btn', 'btn-sm', 'action-btn', 'disabled'].includes(cls));
    return known ? this.toTitleCase(known) : `Action ${index + 1}`;
  }

  private resolveTone(button: HTMLElement): string {
    const classes = Array.from(button.classList).join(' ').toLowerCase();
    if (/delete|danger|trash|remove|reject|archive|block/.test(classes)) return 'danger';
    if (/approve|success|activate|restore|check|verify/.test(classes)) return 'success';
    if (/warning|return|undo|pending/.test(classes)) return 'warning';
    if (/edit|primary|pencil/.test(classes)) return 'primary';
    if (/view|preview|print|download|info|schedule|report/.test(classes)) return 'info';
    return 'neutral';
  }

  private copyIconClasses(button: HTMLElement, target: HTMLElement): void {
    const sourceIcon = button.querySelector('i, .taka-icon');
    if (sourceIcon) {
      Array.from(sourceIcon.classList).forEach(cls => this.renderer.addClass(target, cls));
      if (sourceIcon.classList.contains('taka-icon')) {
        this.renderer.setProperty(target, 'textContent', sourceIcon.textContent || '\u09F3');
      }
      return;
    }
    this.renderer.addClass(target, 'bi');
    this.renderer.addClass(target, 'bi-circle');
  }

  private toTitleCase(value: string): string {
    return value
      .replace(/[-_]+/g, ' ')
      .replace(/\b\w/g, char => char.toUpperCase());
  }

  private isDisabled(action: HTMLElement): boolean {
    return action.hasAttribute('disabled') ||
      action.getAttribute('aria-disabled') === 'true' ||
      action.classList.contains('disabled');
  }
}
