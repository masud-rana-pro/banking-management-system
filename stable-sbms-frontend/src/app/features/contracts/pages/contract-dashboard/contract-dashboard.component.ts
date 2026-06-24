import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { ContractDashboardSummaryResponse, formatEnumLabel } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

interface DashboardLegendItem {
  label: string;
  value: number;
  note: string;
  color: string;
}

interface DashboardBandItem {
  label: string;
  value: number;
  note: string;
  share: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

interface DashboardColumnItem {
  label: string;
  shortLabel: string;
  value: number;
  height: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

interface DashboardAxisTick {
  label: string;
  bottom: number;
}

@Component({
  selector: 'app-contract-dashboard',
  templateUrl: './contract-dashboard.component.html',
  styleUrls: ['./contract-dashboard.component.scss']
})
export class ContractDashboardComponent implements OnInit {

  loading = false;
  summary: ContractDashboardSummaryResponse | null = null;
  contractLegend: DashboardLegendItem[] = [];
  contractBands: DashboardBandItem[] = [];
  typeColumns: DashboardColumnItem[] = [];
  templateColumns: DashboardColumnItem[] = [];
  typeAxisTicks: DashboardAxisTick[] = [];
  templateAxisTicks: DashboardAxisTick[] = [];
  contractRows: Array<{ label: string; value: string }> = [];
  contractGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  customerImageMap: Record<number, string> = {};

  constructor(
    private contractService: ContractService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.load();
  }

  load(): void {
    this.loading = true;
    this.contractService.getDashboardSummary().subscribe({
      next: data => {
        this.summary = data;
        this.prepareDashboard(data);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load contract dashboard.', 'error');
      }
    });
  }

  openTemplates(): void {
    this.router.navigate(['/contracts/templates']);
  }

  openContracts(): void {
    this.router.navigate(['/contracts/list']);
  }

  openGenerate(): void {
    this.router.navigate(['/contracts/generate']);
  }

  viewContract(id?: number): void {
    if (!id) return;
    this.router.navigate(['/contracts', id]);
  }

  getCustomerImageUrl(customerId?: number | null): string {
    if (!customerId) {
      return '';
    }
    return this.customerImageMap[customerId] || '';
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  private prepareDashboard(summary: ContractDashboardSummaryResponse): void {
    const totalContracts = Math.max(summary.totalContracts, 1);
    this.contractLegend = [
      { label: 'Draft', value: summary.draftContracts, note: 'contracts still under preparation', color: '#3b82f6' },
      { label: 'Pending Sign', value: summary.pendingSignatures, note: 'documents waiting for signatures', color: '#f59e0b' },
      { label: 'Active / Locked', value: summary.activeLockedContracts, note: 'finalized documents in force', color: '#22c55e' },
      { label: 'Version Trail', value: summary.contractVersions, note: 'revision history and amendments', color: '#8b5cf6' }
    ];
    this.contractGradient = this.buildDonutGradient(this.contractLegend);

    this.contractBands = [
      {
        label: 'Signature Queue',
        value: summary.pendingSignatures,
        note: 'documents needing customer or shariah sign-off',
        share: this.getShare(summary.pendingSignatures, totalContracts),
        tone: 'warning'
      },
      {
        label: 'Locked Book',
        value: summary.activeLockedContracts,
        note: 'contracts already activated in the legal book',
        share: this.getShare(summary.activeLockedContracts, totalContracts),
        tone: 'success'
      },
      {
        label: 'Draft Backlog',
        value: summary.draftContracts,
        note: 'templates generated but not finalized yet',
        share: this.getShare(summary.draftContracts, totalContracts),
        tone: 'info'
      },
      {
        label: 'Version Footprint',
        value: summary.contractVersions,
        note: `${summary.recentTemplates.length} template references in the current shortlist`,
        share: this.getShare(summary.contractVersions, Math.max(summary.contractVersions, totalContracts)),
        tone: 'primary'
      }
    ];

    const typeMap = new Map<string, number>();
    summary.recentContracts.forEach(item => {
      const key = this.getLabel(item.contractType);
      typeMap.set(key, (typeMap.get(key) || 0) + 1);
    });
    const typeEntries = Array.from(typeMap.entries()).sort((a, b) => b[1] - a[1]).slice(0, 6);
    const maxType = Math.max(...typeEntries.map(([, value]) => value), 1);
    const tones: Array<DashboardColumnItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    this.typeColumns = typeEntries.map(([label, value], index) => ({
      label,
      shortLabel: this.toChartLabel(label),
      value,
      height: this.getShare(value, maxType),
      tone: tones[index % tones.length]
    }));
    this.typeAxisTicks = this.buildAxisTicks(maxType);

    const templateEntries = [...summary.recentTemplates]
      .sort((a, b) => b.generatedContractCount - a.generatedContractCount)
      .slice(0, 6);
    const maxTemplate = Math.max(...templateEntries.map(item => item.generatedContractCount), 1);
    this.templateColumns = templateEntries.map((item, index) => ({
      label: item.templateName,
      shortLabel: this.toChartLabel(item.templateName),
      value: item.generatedContractCount,
      height: this.getShare(item.generatedContractCount, maxTemplate),
      tone: tones[index % tones.length]
    }));
    this.templateAxisTicks = this.buildAxisTicks(maxTemplate);

    this.contractRows = [
      { label: 'Recent Contracts', value: `${summary.recentContracts.length}` },
      { label: 'Recent Templates', value: `${summary.recentTemplates.length}` },
      { label: 'Pending Signatures', value: `${summary.pendingSignatures}` },
      { label: 'Version Trail', value: `${summary.contractVersions}` }
    ];
  }

  private getShare(value: number, total: number): number {
    if (!total) {
      return 0;
    }
    return Math.max(6, Math.round((value / total) * 100));
  }

  private buildDonutGradient(items: DashboardLegendItem[]): string {
    const total = items.reduce((sum, item) => sum + item.value, 0);
    if (!total) {
      return 'conic-gradient(#cbd5e1 0deg 360deg)';
    }

    let cursor = 0;
    const segments = items.map(item => {
      const sweep = (item.value / total) * 360;
      const start = cursor;
      const end = cursor + sweep;
      cursor = end;
      return `${item.color} ${start}deg ${end}deg`;
    });

    return `conic-gradient(${segments.join(', ')})`;
  }

  private buildAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, Math.ceil(maxValue));
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${Math.round((safeMax * percent) / 100)}`,
      bottom: percent
    }));
  }

  private toChartLabel(value: string): string {
    const compact = value.replace(/\s+/g, ' ').trim();
    if (!compact) {
      return 'Type';
    }

    const words = compact.split(' ');
    const readable = words.slice(0, 2).join(' ');
    return readable.length <= 14 ? readable : readable.slice(0, 14).trim();
  }

  private loadCustomers(): void {
    this.customerService.getAll().subscribe({
      next: customers => {
        this.customerImageMap = this.buildCustomerImageMap(customers);
      },
      error: () => {
        this.customerImageMap = {};
      }
    });
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, customer) => {
      if (customer.id && customer.profileImageName) {
        acc[customer.id] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<number, string>);
  }
}
