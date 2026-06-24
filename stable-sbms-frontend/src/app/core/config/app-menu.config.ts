export type AppRole = string;

export interface AppMenuChild {
  label: string;
  route?: string;
  roles?: AppRole[];
  enabled?: boolean;
  permissionCode?: string;
}

export interface AppMenuItem {
  section: string;
  label: string;
  icon: string;
  route?: string;
  roles?: AppRole[];
  enabled?: boolean;
  permissionCode?: string;
  children?: AppMenuChild[];
}

const OPERATIONAL_ROLES: AppRole[] = [
  'TELLER',
  'OPERATIONS_OFFICER',
  'BRANCH_MANAGER',
  'SYSTEM_ADMIN'
];

export const APP_MENU: AppMenuItem[] = [
  {
    section: 'GENERAL',
    label: 'Dashboard',
    icon: 'bi bi-speedometer2',
    route: '/dashboard',
    roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'ADMIN_DASHBOARD_ACCESS',
    enabled: true
  },

  {
    section: 'ADMINISTRATION',
    label: 'Role Management',
    icon: 'bi bi-shield-lock',
    roles: ['SYSTEM_ADMIN'],
    permissionCode: 'ROLE_MANAGEMENT_ACCESS',
    enabled: true,
    children: [
      { label: 'Role Dashboard', route: '/admin/roles/dashboard', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'ROLE_MANAGEMENT_ACCESS' },
      { label: 'Role List', route: '/admin/roles', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'ROLE_VIEW' },
      { label: 'New Role', route: '/admin/roles/new', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'ROLE_CREATE' }
    ]
  },
  {
    section: 'ADMINISTRATION',
    label: 'User Management',
    icon: 'bi bi-people',
    roles: ['SYSTEM_ADMIN'],
    permissionCode: 'USER_MANAGEMENT_ACCESS',
    enabled: true,
    children: [
      { label: 'User Dashboard', route: '/admin/users/dashboard', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'USER_MANAGEMENT_ACCESS' },
      { label: 'User List', route: '/admin/users', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'USER_VIEW' },
      { label: 'New User', route: '/admin/users/new', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'USER_CREATE' }
    ]
  },
  {
    section: 'ADMINISTRATION',
    label: 'Lookup / Config Management',
    icon: 'bi bi-sliders',
    roles: ['SYSTEM_ADMIN'],
    permissionCode: 'LOOKUP_CONFIG_ACCESS',
    enabled: true,
    children: [
      { label: 'Lookup Dashboard', route: '/lookups/dashboard', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'LOOKUP_CONFIG_ACCESS' },
      { label: 'Lookup Types', route: '/lookups/types', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'LOOKUP_CONFIG_ACCESS' },
      { label: 'New Lookup Type', route: '/lookups/types/new', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'LOOKUP_CONFIG_ACCESS' },
      { label: 'Lookup Values', route: '/lookups/values', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'LOOKUP_CONFIG_ACCESS' },
      { label: 'New Lookup Value', route: '/lookups/values/new', roles: ['SYSTEM_ADMIN'], enabled: true, permissionCode: 'LOOKUP_CONFIG_ACCESS' }
    ]
  },

  {
    section: 'OPERATIONS',
    label: 'Branch Management',
    icon: 'bi bi-building',
    roles: OPERATIONAL_ROLES,
    permissionCode: 'BRANCH_MANAGEMENT_ACCESS',
    enabled: true,
children: [
  { label: 'Branch Dashboard', route: '/branches/dashboard', roles: OPERATIONAL_ROLES, enabled: true },
  { label: 'Branch List', route: '/branches/list', roles: OPERATIONAL_ROLES, enabled: true },
  { label: 'Create Branch', route: '/branches/new', roles: ['BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'BRANCH_CREATE' },

  { label: 'Branch Assignments', route: '/branches/assignments', roles: OPERATIONAL_ROLES, enabled: true },
  { label: 'Teller Limits', route: '/branches/teller-limits', roles: OPERATIONAL_ROLES, enabled: true },

  { label: 'Vault Balance List', route: '/branches/vault', roles: OPERATIONAL_ROLES, enabled: true },
  { label: 'Open Vault', route: '/branches/vault/open', roles: ['BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'BRANCH_VAULT_MANAGE' },
  { label: 'Cash Ledger', route: '/branches/cash-ledger', roles: OPERATIONAL_ROLES, enabled: true },

  { label: 'Inter-Branch Transfer', route: '/branches/inter-branch-transfer', roles: OPERATIONAL_ROLES, enabled: true },
  { label: 'EOD Summary', route: '/branches/eod-summary', roles: ['BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'BRANCH_MANAGEMENT_ACCESS' }
]
  },

  {
    section: 'OPERATIONS',
    label: 'ATM / CDM Management',
    icon: 'bi bi-credit-card',
    roles: OPERATIONAL_ROLES,
    permissionCode: 'ATM_CDM_ACCESS',
    enabled: true,
    children: [
      { label: 'ATM Dashboard', route: '/atm/dashboard', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'Terminal List', route: '/atm/terminals', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'Cash Bin List', route: '/atm/cash-bins', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'Cash Bin Setup', route: '/atm/cash-bin/new', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'ATM_CASH_BIN_CREATE' },
      { label: 'Replenishment List', route: '/atm/replenishments', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'New Replenishment', route: '/atm/replenishment/new', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'ATM_REPLENISHMENT_CREATE' },
      { label: 'Reconciliation List', route: '/atm/reconciliations', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'New Reconciliation', route: '/atm/reconciliation/new', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'ATM_RECONCILIATION_CREATE' },
      { label: 'ATM/CDM Journal', route: '/atm/device-journal', roles: OPERATIONAL_ROLES, enabled: true }
    ]
  },

  {
    section: 'OPERATIONS',
    label: 'Customer Management',
    icon: 'bi bi-person-badge',
    route: '/customers/dashboard',
    roles: OPERATIONAL_ROLES,
    permissionCode: 'CUSTOMER_MANAGEMENT_ACCESS',
    enabled: true,
    children: [
      { label: 'Customer Dashboard', route: '/customers/dashboard', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'Customer List', route: '/customers/list', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'New Customer', route: '/customers/new', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'CUSTOMER_CREATE' },
      { label: 'Customer Search', route: '/customers/search', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'Status Action', route: '/customers/status-action', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'CUSTOMER_ACTIVATE' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'KYC Management',
    icon: 'bi bi-shield-check',
    route: '/kyc/dashboard',
    roles: OPERATIONAL_ROLES,
    permissionCode: 'KYC_MANAGEMENT_ACCESS',
    enabled: true,
    children: [
      { label: 'KYC Dashboard', route: '/kyc/dashboard', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'KYC List', route: '/kyc/list', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'New KYC Profile', route: '/kyc/new', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'KYC_CREATE' },
      { label: 'Approval Queue', route: '/kyc/approval-queue', roles: OPERATIONAL_ROLES, enabled: true }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Account Management',
    icon: 'bi bi-journal-bookmark',
    route: '/accounts/dashboard',
    roles: OPERATIONAL_ROLES,
    permissionCode: 'ACCOUNT_MANAGEMENT_ACCESS',
    enabled: true,
    children: [
      { label: 'Account Dashboard', route: '/accounts/dashboard', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'Account List', route: '/accounts/list', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'Account Types', route: '/accounts/account-types', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'New Account Type', route: '/accounts/account-types/new', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'ACCOUNT_TYPE_CREATE' },
      { label: 'Opening Requests', route: '/accounts/opening-requests', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'New Opening Request', route: '/accounts/opening-requests/new', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'ACCOUNT_REQUEST_CREATE' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Transactions Management',
    icon: 'bi bi-arrow-left-right',
    route: '/transactions/dashboard',
    roles: OPERATIONAL_ROLES,
    permissionCode: 'TRANSACTIONS_ACCESS',
    enabled: true,
    children: [
      { label: 'Transaction Dashboard', route: '/transactions/dashboard', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'Transaction Journal', route: '/transactions/list', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'Cash Deposit', route: '/transactions/deposit', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'TRANSACTION_DEPOSIT' },
      { label: 'Cash Withdraw', route: '/transactions/withdraw', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'TRANSACTION_WITHDRAW' },
      { label: 'Fund Transfer', route: '/transactions/transfer', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'TRANSACTION_TRANSFER' },
      { label: 'Cheque Clearing', route: '/transactions/cheque-clearing', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'TRANSACTION_CHEQUE_CLEARING' },
      { label: 'Standing Instructions', route: '/transactions/standing-instructions', roles: OPERATIONAL_ROLES, enabled: true },
      { label: 'New Standing Instruction', route: '/transactions/standing-instructions/new', roles: OPERATIONAL_ROLES, enabled: true, permissionCode: 'TRANSACTION_STANDING_INSTRUCTION_CREATE' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Profit Management',
    icon: 'bi bi-graph-up-arrow',
    route: '/profit/dashboard',
    roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'PROFIT_MANAGEMENT_ACCESS',
    enabled: true,
    children: [
      { label: 'Profit Dashboard', route: '/profit/dashboard', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Profit Ratios', route: '/profit/ratios', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Profit Ratio', route: '/profit/ratios/new', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'PROFIT_RATIO_CREATE' },
      { label: 'Profit Schedules', route: '/profit/schedules', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Profit Schedule', route: '/profit/schedules/new', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'PROFIT_SCHEDULE_CREATE' },
      { label: 'Profit Postings', route: '/profit/postings', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Run Profit Posting', route: '/profit/postings/run', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'PROFIT_POSTING_RUN' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Card Management',
    icon: 'bi bi-credit-card-2-front',
    route: '/cards/dashboard',
    roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'CARD_MANAGEMENT_ACCESS',
    enabled: true,
    children: [
      { label: 'Card Dashboard', route: '/cards/dashboard', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Card List', route: '/cards/list', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Issue Card', route: '/cards/new', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'CARD_CREATE' },
      { label: 'PIN Events', route: '/cards/list', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'ATM/CDM Usage', route: '/cards/atm-cdm-transactions/list', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true }
    ]
  },

  {
    section: 'OPERATIONS',
    label: 'Statements Generation',
    icon: 'bi bi-file-earmark-text',
    route: '/statement/dashboard',
    roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'STATEMENTS_ACCESS',
    enabled: true,
    children: [
      { label: 'Statement Dashboard', route: '/statement/dashboard', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Customer Requests', route: '/statement/customer/list', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Customer Statement', route: '/statement/customer/request', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'STATEMENT_CUSTOMER_REQUEST' },
      { label: 'Branch Requests', route: '/statement/branch/list', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Branch Statement', route: '/statement/branch/request', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'STATEMENT_BRANCH_REQUEST' },
      { label: 'Export Center', route: '/statement/export-center', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Deposit Schemes Management',
    icon: 'bi bi-piggy-bank',
    route: '/deposit-schemes/dashboard',
    roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'DEPOSIT_SCHEMES_ACCESS',
    enabled: true,
    children: [
      { label: 'Scheme Dashboard', route: '/deposit-schemes/dashboard', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Scheme List', route: '/deposit-schemes/list', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Scheme', route: '/deposit-schemes/new', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'DEPOSIT_SCHEME_CREATE' },
      { label: 'Enrollment List', route: '/deposit-schemes/enrollments/list', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Enrollment', route: '/deposit-schemes/enrollments/new', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'DEPOSIT_SCHEME_ENROLLMENT_CREATE' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Financing Management',
    icon: 'bi bi-cash-coin',
    route: '/financing/dashboard',
    roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'FINANCING_ACCESS',
    enabled: true,
    children: [
      { label: 'Financing Dashboard', route: '/financing/dashboard', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Product List', route: '/financing/products', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Product', route: '/financing/products/new', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'FINANCING_PRODUCT_CREATE' },
      { label: 'Application List', route: '/financing/applications', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Application', route: '/financing/applications/new', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'FINANCING_APPLICATION_CREATE' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Contracts Management',
    icon: 'bi bi-file-earmark-richtext',
    route: '/contracts/dashboard',
    roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'CONTRACTS_ACCESS',
    enabled: true,
    children: [
      { label: 'Contract Dashboard', route: '/contracts/dashboard', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Template List', route: '/contracts/templates', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Template', route: '/contracts/templates/new', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'CONTRACT_TEMPLATE_CREATE' },
      { label: 'Contract List', route: '/contracts/list', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Generate Contract', route: '/contracts/generate', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'CONTRACT_GENERATE' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Shariah Review Management',
    icon: 'bi bi-journal-check',
    route: '/shariah/dashboard',
    roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'SHARIAH_REVIEW_ACCESS',
    enabled: true,
    children: [
      { label: 'Shariah Dashboard', route: '/shariah/dashboard', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Case List', route: '/shariah/cases', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Correction Queue', route: '/shariah/correction-queue', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Fatwa Certificates', route: '/shariah/fatwa-certificates', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Annual Report', route: '/shariah/annual-report', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Zakat & Charity Management',
    icon: 'bi bi-cash-stack',
    route: '/zakat/dashboard',
    roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'ZAKAT_CHARITY_ACCESS',
    enabled: true,
    children: [
      { label: 'Zakat Dashboard', route: '/zakat/dashboard', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Profile List', route: '/zakat/profiles', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Run Zakat', route: '/zakat/calc-run', roles: ['CUSTOMER', 'TELLER', 'OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'ZAKAT_CALCULATE' },
      { label: 'Charity Fund', route: '/zakat/charity-fund', roles: ['OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Beneficiary List', route: '/zakat/beneficiaries', roles: ['OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Beneficiary', route: '/zakat/beneficiaries/new', roles: ['OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'CHARITY_BENEFICIARY_CREATE' },
      { label: 'Payout List', route: '/zakat/payouts', roles: ['OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Payout', route: '/zakat/payouts/new', roles: ['OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'CHARITY_PAYOUT_CREATE' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Notification & Alerts Management',
    icon: 'bi bi-bell',
    route: '/notifications/dashboard',
    roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'NOTIFICATION_ALERTS_ACCESS',
    enabled: true,
    children: [
      { label: 'Notification Dashboard', route: '/notifications/dashboard', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Template List', route: '/notifications/templates', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Template', route: '/notifications/templates/new', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'NOTIFICATION_TEMPLATE_CREATE' },
      { label: 'Event Rule List', route: '/notifications/event-rules', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Event Rule', route: '/notifications/event-rules/new', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'NOTIFICATION_EVENT_RULE_CREATE' },
      { label: 'Delivery Logs', route: '/notifications/logs', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Retry Queue', route: '/notifications/retry-queue', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Integration Management',
    icon: 'bi bi-diagram-3',
    route: '/integrations/dashboard',
    roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'INTEGRATION_MANAGEMENT_ACCESS',
    enabled: true,
    children: [
      { label: 'Integration Dashboard', route: '/integrations/dashboard', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Provider List', route: '/integrations/providers', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'New Provider', route: '/integrations/providers/new', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'INTEGRATION_PROVIDER_CREATE' },
      { label: 'Execution Logs', route: '/integrations/logs', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Provider Test', route: '/integrations/provider-test', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'INTEGRATION_PROVIDER_TEST' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Reporting & Regulatory Compliance',
    icon: 'bi bi-bar-chart-line',
    route: '/reports/dashboard',
    roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'REPORTING_REGULATORY_ACCESS',
    enabled: true,
    children: [
      { label: 'Report Dashboard', route: '/reports/dashboard', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Operational Report', route: '/reports/operational', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Profit Distribution', route: '/reports/profit-distribution', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Management P&L', route: '/reports/management-pl', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Expense Register', route: '/reports/management-expenses', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Trial Balance', route: '/reports/trial-balance', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Ledger Profit & Loss', route: '/reports/ledger-profit-loss', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Financing Portfolio', route: '/reports/financing-portfolio', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'PAR Report', route: '/reports/par', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Shariah Audit', route: '/reports/shariah-audit', roles: ['OPERATIONS_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Branch Report', route: '/reports/branch', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Export History', route: '/reports/export-history', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Security / Audit Management',
    icon: 'bi bi-shield-exclamation',
    route: '/security/dashboard',
    roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'SECURITY_AUDIT_ACCESS',
    enabled: true,
    children: [
      { label: 'Security Dashboard', route: '/security/dashboard', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Security Events', route: '/security/events', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Suspicious Activity', route: '/security/suspicious-activities', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Audit Logs', route: '/security/audit-logs', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Investigation Cases', route: '/security/investigation-cases', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Workflow Support Management',
    icon: 'bi bi-diagram-2',
    route: '/workflow/dashboard',
    roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'WORKFLOW_SUPPORT_ACCESS',
    enabled: true,
    children: [
      { label: 'Workflow Dashboard', route: '/workflow/dashboard', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Workflow History', route: '/workflow/history', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Pending Queue', route: '/workflow/pending', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'My Submissions', route: '/workflow/my-submissions', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Verification Management',
    icon: 'bi bi-shield-lock',
    route: '/verification/dashboard',
    roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'VERIFICATION_ACCESS',
    enabled: true,
    children: [
      { label: 'Verification Dashboard', route: '/verification/dashboard', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Verification Logs', route: '/verification/logs', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Verify OTP', route: '/verification/otp-verify', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Forgot Password', route: '/verification/forgot-password', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Reset Password', route: '/verification/reset-password', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Provider Test Console', route: '/verification/provider-test', roles: ['OPERATIONS_OFFICER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'VERIFICATION_PROVIDER_TEST' }
    ]
  },
  {
    section: 'OPERATIONS',
    label: 'Calculation Engine Management',
    icon: 'bi bi-calculator',
    route: '/calculations/dashboard',
    roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'],
    permissionCode: 'CALCULATION_ENGINE_ACCESS',
    enabled: true,
    children: [
      { label: 'Calculation Dashboard', route: '/calculations/dashboard', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true },
      { label: 'Calculation Simulator', route: '/calculations/simulator', roles: ['OPERATIONS_OFFICER', 'INVESTMENT_OFFICER', 'SHARIAH_BOARD_MEMBER', 'BRANCH_MANAGER', 'SYSTEM_ADMIN'], enabled: true, permissionCode: 'CALCULATION_SIMULATE' }
    ]
  }
];
