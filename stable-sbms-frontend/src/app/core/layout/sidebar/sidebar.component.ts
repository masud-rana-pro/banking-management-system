import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { APP_MENU, AppMenuChild, AppMenuItem, AppRole } from 'src/app/core/config/app-menu.config';
import { AccessControlService } from 'src/app/core/services/access-control.service';

interface MenuSection {
  name: string;
  items: AppMenuItem[];
}

const SHOWCASE_SECTION_ORDER: Record<string, number> = {
  GENERAL: 1,
  OPERATIONS: 2,
  ADMINISTRATION: 3
};

const SHOWCASE_ITEM_ORDER: Record<string, number> = {
  Dashboard: 1,
  'Branch Management': 10,
  'Customer Management': 20,
  'KYC Management': 30,
  'Account Management': 40,
  'Transactions Management': 50,
  'Card Management': 60,
  'Deposit Schemes Management': 70,
  'Profit Management': 80,
  'Financing Management': 90,
  'Contracts Management': 100,
  'Shariah Review Management': 110,
  'Zakat & Charity Management': 120,
  'Statements Generation': 130,
  'Reporting & Regulatory Compliance': 140,
  'Workflow Support Management': 150,
  'Verification Management': 160,
  'Security / Audit Management': 170,
  'Notification & Alerts Management': 180,
  'Integration Management': 190,
  'ATM / CDM Management': 200,
  'Calculation Engine Management': 210,
  'User Management': 220,
  'Role Management': 230,
  'Lookup / Config Management': 240
};

const SHOWCASE_CHILD_ORDER: Record<string, Record<string, number>> = {
  'Branch Management': {
    'Branch Dashboard': 1,
    'Branch List': 2,
    'Create Branch': 3,
    'Branch Assignments': 4,
    'Teller Limits': 5,
    'Vault Balance List': 6,
    'Open Vault': 7,
    'Cash Ledger': 8,
    'Inter-Branch Transfer': 9,
    'EOD Summary': 10
  },
  'Customer Management': {
    'Customer Dashboard': 1,
    'Customer List': 2,
    'New Customer': 3,
    'Customer Search': 4,
    'Status Action': 5
  },
  'KYC Management': {
    'KYC Dashboard': 1,
    'KYC List': 2,
    'New KYC Profile': 3,
    'Approval Queue': 4
  },
  'Account Management': {
    'Account Dashboard': 1,
    'Account List': 2,
    'Opening Requests': 3,
    'New Opening Request': 4,
    'Account Types': 5,
    'New Account Type': 6
  },
  'Transactions Management': {
    'Transaction Dashboard': 1,
    'Transaction Journal': 2,
    'Cash Deposit': 3,
    'Cash Withdraw': 4,
    'Fund Transfer': 5,
    'Cheque Clearing': 6,
    'Standing Instructions': 7,
    'New Standing Instruction': 8
  },
  'Card Management': {
    'Card Dashboard': 1,
    'Card List': 2,
    'Issue Card': 3,
    'PIN Events': 4,
    'ATM/CDM Usage': 5
  },
  'Deposit Schemes Management': {
    'Scheme Dashboard': 1,
    'Scheme List': 2,
    'New Scheme': 3,
    'Enrollment List': 4,
    'New Enrollment': 5
  },
  'Profit Management': {
    'Profit Dashboard': 1,
    'Profit Ratios': 2,
    'New Profit Ratio': 3,
    'Profit Schedules': 4,
    'New Profit Schedule': 5,
    'Profit Postings': 6,
    'Run Profit Posting': 7
  },
  'Financing Management': {
    'Financing Dashboard': 1,
    'Product List': 2,
    'New Product': 3,
    'Application List': 4,
    'New Application': 5
  },
  'Contracts Management': {
    'Contract Dashboard': 1,
    'Template List': 2,
    'New Template': 3,
    'Contract List': 4,
    'Generate Contract': 5
  },
  'Shariah Review Management': {
    'Shariah Dashboard': 1,
    'Case List': 2,
    'Correction Queue': 3,
    'Fatwa Certificates': 4,
    'Annual Report': 5
  },
  'Zakat & Charity Management': {
    'Zakat Dashboard': 1,
    'Profile List': 2,
    'Run Zakat': 3,
    'Charity Fund': 4,
    'Beneficiary List': 5,
    'New Beneficiary': 6,
    'Payout List': 7,
    'New Payout': 8
  },
  'Statements Generation': {
    'Statement Dashboard': 1,
    'Customer Requests': 2,
    'New Customer Statement': 3,
    'Branch Requests': 4,
    'New Branch Statement': 5,
    'Export Center': 6
  },
  'Reporting & Regulatory Compliance': {
    'Report Dashboard': 1,
    'Operational Report': 2,
    'Profit Distribution': 3,
    'Management P&L': 4,
    'Expense Register': 5,
    'Trial Balance': 6,
    'Ledger Profit & Loss': 7,
    'Financing Portfolio': 8,
    'PAR Report': 9,
    'Shariah Audit': 10,
    'Branch Report': 11,
    'Export History': 12
  },
  'Workflow Support Management': {
    'Workflow Dashboard': 1,
    'Pending Queue': 2,
    'My Submissions': 3,
    'Workflow History': 4
  },
  'Verification Management': {
    'Verification Dashboard': 1,
    'Verify OTP': 2,
    'Verification Logs': 3,
    'Forgot Password': 4,
    'Reset Password': 5,
    'Provider Test Console': 6
  },
  'Security / Audit Management': {
    'Security Dashboard': 1,
    'Security Events': 2,
    'Suspicious Activity': 3,
    'Audit Logs': 4,
    'Investigation Cases': 5
  },
  'Notification & Alerts Management': {
    'Notification Dashboard': 1,
    'Template List': 2,
    'New Template': 3,
    'Event Rule List': 4,
    'New Event Rule': 5,
    'Delivery Logs': 6,
    'Retry Queue': 7
  },
  'Integration Management': {
    'Integration Dashboard': 1,
    'Provider List': 2,
    'New Provider': 3,
    'Provider Test': 4,
    'Execution Logs': 5
  },
  'ATM / CDM Management': {
    'ATM Dashboard': 1,
    'Terminal List': 2,
    'Cash Bin List': 3,
    'Cash Bin Setup': 4,
    'Replenishment List': 5,
    'New Replenishment': 6,
    'Reconciliation List': 7,
    'New Reconciliation': 8,
    'ATM/CDM Journal': 9
  },
  'Calculation Engine Management': {
    'Calculation Dashboard': 1,
    'Calculation Simulator': 2
  },
  'User Management': {
    'User Dashboard': 1,
    'User List': 2,
    'New User': 3
  },
  'Role Management': {
    'Role Dashboard': 1,
    'Role List': 2,
    'New Role': 3
  },
  'Lookup / Config Management': {
    'Lookup Dashboard': 1,
    'Lookup Types': 2,
    'New Lookup Type': 3,
    'Lookup Values': 4,
    'New Lookup Value': 5
  }
};

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  @Input() collapsed  = false;
  @Input() mobileOpen = false;

  @Output() menuClick = new EventEmitter<void>();

  currentRole: AppRole = 'SYSTEM_ADMIN';

  menuItems: AppMenuItem[] = APP_MENU;
  menuSections: MenuSection[] = [];
  openMenus: { [key: string]: boolean } = {};

  constructor(private accessControl: AccessControlService) {}

  ngOnInit(): void {
    this.accessControl.session$.subscribe(() => {
      this.currentRole = this.accessControl.currentRoleCode as AppRole;
      this.buildMenu();
    });
  }

  buildMenu(): void {
    const filtered = this.menuItems
      .filter(item => item.enabled !== false && this.accessControl.hasPermission(item.permissionCode))
      .map(item => ({
        ...item,
        children: item.children
          ? item.children.filter(c => c.enabled !== false && this.accessControl.hasPermission(c.permissionCode))
              .sort((a, b) => this.getChildOrder(item.label, a) - this.getChildOrder(item.label, b))
          : []
      }))
      .sort((a, b) => this.getItemOrder(a) - this.getItemOrder(b));

    const grouped: { [section: string]: AppMenuItem[] } = {};
    filtered.forEach(item => {
      if (!grouped[item.section]) grouped[item.section] = [];
      grouped[item.section].push(item);
    });

    this.menuSections = Object.keys(grouped)
      .sort((a, b) => this.getSectionOrder(a) - this.getSectionOrder(b))
      .map(section => ({
        name:  section,
        items: grouped[section]
      }));
  }

  toggleMenu(label: string): void {
    this.openMenus[label] = !this.openMenus[label];
  }

  isMenuOpen(label: string): boolean {
    return !!this.openMenus[label];
  }

  onMenuClick(): void {
    this.menuClick.emit();
  }

  trackBySection(_i: number, s: MenuSection):  string { return s.name; }
  trackByItem   (_i: number, item: AppMenuItem): string { return item.label; }
  trackByChild  (_i: number, c: AppMenuChild):   string { return c.label; }

  private getSectionOrder(section: string): number {
    return SHOWCASE_SECTION_ORDER[section] ?? 999;
  }

  private getItemOrder(item: AppMenuItem): number {
    return SHOWCASE_ITEM_ORDER[item.label] ?? 999;
  }

  private getChildOrder(parentLabel: string, child: AppMenuChild): number {
    return SHOWCASE_CHILD_ORDER[parentLabel]?.[child.label] ?? 999;
  }
}
