import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { FinancingApplicationResponse, FinancingDashboardSummaryResponse, FinancingProductMetricResponse, formatEnumLabel } from '../../models/financing.model';
import { FinancingService } from '../../services/financing.service';

interface DashboardBarItem {
  id?: number;
  label: string;
  value: number;
  note: string;
  share: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

interface DashboardBandItem {
  label: string;
  value: number;
  note: string;
  share: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

interface DashboardLegendItem {
  label: string;
  value: number;
  note: string;
  color: string;
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

interface DashboardLinePoint {
  label: string;
  fullLabel: string;
  value: number;
  note: string;
  x: number;
  y: number;
  applicationId?: number;
}

interface DashboardExposureColumn {
  id?: number;
  label: string;
  shortLabel: string;
  requestedAmount: number;
  outstandingAmount: number;
  requestedHeight: number;
  outstandingHeight: number;
  note: string;
}

interface DashboardHeatCell {
  label: string;
  value: number;
  intensity: number;
}

interface DashboardHeatRow {
  label: string;
  cells: DashboardHeatCell[];
}

@Component({
  selector: 'app-financing-dashboard',
  templateUrl: './financing-dashboard.component.html',
  styleUrls: ['./financing-dashboard.component.scss']
})
export class FinancingDashboardComponent implements OnInit {

  loading = false;
  summary: FinancingDashboardSummaryResponse | null = null;
  approvalRate = 0;
  averageTicket = 0;
  productBars: DashboardBarItem[] = [];
  funnelBands: DashboardBandItem[] = [];
  recentAmountBars: DashboardBarItem[] = [];
  riskRows: Array<{ label: string; value: string }> = [];
  pipelineLegend: DashboardLegendItem[] = [];
  productColumns: DashboardColumnItem[] = [];
  productAxisTicks: DashboardAxisTick[] = [];
  applicationTrendPoints: DashboardLinePoint[] = [];
  exposureColumns: DashboardExposureColumn[] = [];
  exposureAxisTicks: DashboardAxisTick[] = [];
  portfolioHeatRows: DashboardHeatRow[] = [];
  financingPipelineGradient = 'conic-gradient(#14b8a6 0deg 360deg)';
  customerImageMap: Record<number, string> = {};
  selectedApplicationId?: number;

  constructor(
    private financingService: FinancingService,
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
    forkJoin({
      summary: this.financingService.getDashboardSummary(),
      applications: this.financingService.getApplications()
    }).subscribe({
      next: ({ summary, applications }) => {
        try {
          const safeApplications = Array.isArray(applications) ? applications : [];
          const recentApplications = (Array.isArray(summary?.recentApplications) && summary.recentApplications.length)
            ? summary.recentApplications
            : safeApplications.slice(0, 12);
          const financingByProduct = (Array.isArray(summary?.financingByProduct) && summary.financingByProduct.length)
            ? summary.financingByProduct
            : this.buildProductMetricsFromApplications(safeApplications);

          this.summary = {
            ...summary,
            financingByProduct,
            recentApplications
          };
          this.prepareDashboard(this.summary);
        } catch (err) {
          console.error('Failed to prepare financing dashboard view model', err);
          this.summary = {
            pendingApplications: Number(summary?.pendingApplications || 0),
            approvedApplications: Number(summary?.approvedApplications || 0),
            disbursedAmount: Number(summary?.disbursedAmount || 0),
            overdueInstallments: Number(summary?.overdueInstallments || 0),
            financingByProduct: this.buildProductMetricsFromApplications(Array.isArray(applications) ? applications : []),
            charityLateFeeAmount: Number(summary?.charityLateFeeAmount || 0),
            recentApplications: Array.isArray(applications) ? applications.slice(0, 12) : []
          };
          this.prepareDashboard(this.summary);
          Swal.fire('Warning', 'Financing dashboard loaded with partial analytics. The board view has been stabilized.', 'warning');
        } finally {
          this.loading = false;
        }
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load financing dashboard.', 'error');
      }
    });
  }

  private buildProductMetricsFromApplications(applications: FinancingApplicationResponse[]): FinancingProductMetricResponse[] {
    const counts = new Map<string, number>();
    for (const item of applications || []) {
      const key = item.productName || 'Financing';
      counts.set(key, (counts.get(key) || 0) + 1);
    }
    return Array.from(counts.entries())
      .map(([productName, applicationCount]) => ({ productName, applicationCount }))
      .sort((a, b) => b.applicationCount - a.applicationCount);
  }

  openProducts(): void {
    this.router.navigate(['/financing/products']);
  }

  openApplications(): void {
    this.router.navigate(['/financing/applications']);
  }

  openRecoveryQueue(): void {
    this.router.navigate(['/financing/applications'], {
      queryParams: {
        overdue: 1
      }
    });
  }

  openRecoveryReport(): void {
    this.router.navigate(['/reports/loan-recovery']);
  }

  openNewApplication(): void {
    this.router.navigate(['/financing/applications/new']);
  }

  openView(id?: number): void {
    if (!id) return;
    this.router.navigate(['/financing/applications', id]);
  }

  getCustomerImageUrl(customerId?: number | null): string {
    if (!customerId) {
      return '';
    }
    return this.customerImageMap[customerId] || '';
  }

  getMetricShare(item: FinancingProductMetricResponse): number {
    const total = this.summary?.financingByProduct.reduce((sum, entry) => sum + (entry.applicationCount || 0), 0) || 0;
    if (!total) return 0;
    return Math.max(8, Math.round(((item.applicationCount || 0) / total) * 100));
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get selectedApplication(): FinancingDashboardSummaryResponse['recentApplications'][number] | null {
    if (!this.summary?.recentApplications?.length) {
      return null;
    }
    return this.summary.recentApplications.find(item => item.id === this.selectedApplicationId) || this.summary.recentApplications[0];
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  selectTrendPoint(point: DashboardLinePoint): void {
    this.selectedApplicationId = point.applicationId;
  }

  selectExposureColumn(column: DashboardExposureColumn): void {
    this.selectedApplicationId = column.id;
  }

  selectApplication(id?: number): void {
    if (!id) {
      return;
    }
    this.selectedApplicationId = id;
  }

  trendPointX(index: number): number {
    const total = Math.max(this.applicationTrendPoints.length - 1, 1);
    return 28 + (index / total) * 464;
  }

  trendPointY(value: number): number {
    const maxValue = Math.max(...this.applicationTrendPoints.map(item => item.value), 1);
    return 184 - (value / maxValue) * 142;
  }

  get applicationTrendPath(): string {
    return this.applicationTrendPoints
      .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
      .join(' ');
  }

  get applicationTrendAreaPath(): string {
    if (!this.applicationTrendPoints.length) {
      return '';
    }
    const line = this.applicationTrendPath;
    const lastPoint = this.applicationTrendPoints[this.applicationTrendPoints.length - 1];
    const firstPoint = this.applicationTrendPoints[0];
    return `${line} L ${lastPoint.x} 196 L ${firstPoint.x} 196 Z`;
  }

  private prepareDashboard(data: FinancingDashboardSummaryResponse): void {
    const productMetrics = Array.isArray(data.financingByProduct) ? data.financingByProduct : [];
    const recentApplications = Array.isArray(data.recentApplications) ? data.recentApplications : [];
    const totalPipeline = Number(data.pendingApplications || 0) + Number(data.approvedApplications || 0);
    this.approvalRate = totalPipeline ? Math.round(((data.approvedApplications || 0) / totalPipeline) * 100) : 0;

    const disbursedRecent = recentApplications.filter(item => item.applicationStatus === 'DISBURSED' || item.applicationStatus === 'ACTIVE').length;
    const totalRequested = recentApplications.reduce((sum, item) => sum + Number(item.requestedAmount || 0), 0);
    this.averageTicket = recentApplications.length ? totalRequested / recentApplications.length : 0;

    this.funnelBands = [
      {
        label: 'Pending Pipeline',
        value: data.pendingApplications || 0,
        note: 'Applications waiting for review, verification or approval',
        share: this.getShare(data.pendingApplications || 0, totalPipeline || 1),
        tone: 'warning'
      },
      {
        label: 'Approved Queue',
        value: data.approvedApplications || 0,
        note: 'Cases cleared for the next disbursement decision',
        share: this.getShare(data.approvedApplications || 0, totalPipeline || 1),
        tone: 'success'
      },
      {
        label: 'Recent Disbursed / Active',
        value: disbursedRecent,
        note: 'Latest financed files already moved into live book',
        share: this.getShare(disbursedRecent, recentApplications.length || 1),
        tone: 'primary'
      },
      {
        label: 'Overdue Installments',
        value: data.overdueInstallments || 0,
        note: 'Repayment schedule pressure requiring collection attention',
        share: this.getShare(data.overdueInstallments || 0, Math.max(data.overdueInstallments || 0, totalPipeline || 1)),
        tone: 'danger'
      }
    ];
    this.pipelineLegend = [
      { label: 'Pending', value: data.pendingApplications || 0, note: 'review and verification queue', color: '#f59e0b' },
      { label: 'Approved', value: data.approvedApplications || 0, note: 'ready for execution', color: '#22c55e' },
      { label: 'Overdue', value: data.overdueInstallments || 0, note: 'collection pressure', color: '#ef4444' },
      { label: 'Live Book', value: disbursedRecent, note: 'recent disbursed or active files', color: '#14b8a6' }
    ];
    this.financingPipelineGradient = this.buildDonutGradient(this.pipelineLegend);

    const tones: Array<DashboardBarItem['tone']> = ['primary', 'success', 'warning', 'info', 'danger'];
    const productTotal = productMetrics.reduce((sum, entry) => sum + Number(entry.applicationCount || 0), 0);
    const productMax = Math.max(...productMetrics.map(item => Number(item.applicationCount || 0)), 1);
    this.productBars = productMetrics.map((item, index) => ({
      label: item.productName,
      value: Number(item.applicationCount || 0),
      note: `${this.getShare(Number(item.applicationCount || 0), productTotal || 1)}% of visible product demand`,
      share: this.getShare(Number(item.applicationCount || 0), productMax),
      tone: tones[index % tones.length]
    }));
    this.productColumns = productMetrics.slice(0, 6).map((item, index) => ({
      label: item.productName,
      shortLabel: this.buildShortLabel(item.productName),
      value: Number(item.applicationCount || 0),
      height: this.getShare(Number(item.applicationCount || 0), productMax),
      tone: tones[index % tones.length]
    }));
    this.productAxisTicks = this.buildAxisTicks(productMax);

    const topRequested = [...recentApplications]
      .sort((a, b) => Number(b.requestedAmount || 0) - Number(a.requestedAmount || 0))
      .slice(0, 5);
    const requestedMax = Math.max(...topRequested.map(item => Number(item.requestedAmount || 0)), 1);
    this.recentAmountBars = topRequested.map((item, index) => ({
      id: item.id,
      label: item.applicationNo,
      value: Number(item.requestedAmount || 0),
      note: `${item.customerName} | ${this.getLabel(item.applicationStatus)}`,
      share: this.getShare(Number(item.requestedAmount || 0), requestedMax),
      tone: tones[index % tones.length]
    }));

    const chronologicalApplications = [...recentApplications]
      .sort((a, b) => new Date(a.createdAt || a.submittedAt || '').getTime() - new Date(b.createdAt || b.submittedAt || '').getTime())
      .slice(-6);
    this.applicationTrendPoints = chronologicalApplications.map((item, index) => ({
      label: this.buildDateLabel(item.createdAt || item.submittedAt),
      fullLabel: item.applicationNo,
      value: Number(item.requestedAmount || 0),
      note: `${item.customerName} | ${this.getLabel(item.applicationStatus)}`,
      x: this.trendPointX(index),
      y: 0,
      applicationId: item.id
    }));
    this.applicationTrendPoints = this.applicationTrendPoints.map((item, index) => ({
      ...item,
      x: this.trendPointX(index),
      y: this.trendPointY(item.value)
    }));

    const topExposure = [...recentApplications]
      .sort((a, b) => Number(b.totalOutstandingAmount || 0) - Number(a.totalOutstandingAmount || 0))
      .slice(0, 5);
    const exposureMax = Math.max(...topExposure.map(item => Math.max(Number(item.requestedAmount || 0), Number(item.totalOutstandingAmount || 0))), 1);
    this.exposureColumns = topExposure.map(item => ({
      id: item.id,
      label: item.applicationNo,
      shortLabel: this.buildShortLabel(item.applicationNo),
      requestedAmount: Number(item.requestedAmount || 0),
      outstandingAmount: Number(item.totalOutstandingAmount || 0),
      requestedHeight: this.getShare(Number(item.requestedAmount || 0), exposureMax),
      outstandingHeight: this.getShare(Number(item.totalOutstandingAmount || 0), exposureMax),
      note: `${item.customerName} | ${this.getLabel(item.applicationStatus)}`
    }));
    this.exposureAxisTicks = this.buildAxisTicks(exposureMax);

    const statusBuckets = [
      {
        label: 'In Review',
        matcher: (status?: string) => ['SUBMITTED', 'DOC_CHECK', 'ASSET_VERIFIED', 'SHARIAH_REVIEW'].includes(String(status || ''))
      },
      {
        label: 'Approved',
        matcher: (status?: string) => status === 'APPROVED'
      },
      {
        label: 'Live',
        matcher: (status?: string) => ['DISBURSED', 'ACTIVE', 'CLOSED'].includes(String(status || ''))
      },
      {
        label: 'Exception',
        matcher: (status?: string) => ['DRAFT', 'RETURNED', 'REJECTED'].includes(String(status || ''))
      }
    ];
    const typeLabels = Array.from(new Set(recentApplications.map(item => this.getLabel(item.financingType)).filter(Boolean)));
    const heatBase = typeLabels.length ? typeLabels : ['Murabaha', 'Ijarah', 'Musharaka'];
    const rawHeatRows = heatBase.map(typeLabel => ({
      label: typeLabel,
      cells: statusBuckets.map(bucket => ({
        label: bucket.label,
        value: recentApplications.filter(item => this.getLabel(item.financingType) === typeLabel && bucket.matcher(item.applicationStatus)).length,
        intensity: 0
      }))
    }));
    const maxHeatValue = Math.max(...rawHeatRows.flatMap(row => row.cells.map(cell => cell.value)), 1);
    this.portfolioHeatRows = rawHeatRows.map(row => ({
      label: row.label,
      cells: row.cells.map(cell => ({
        ...cell,
        intensity: Math.max(18, Math.round((cell.value / maxHeatValue) * 100))
      }))
    }));

    this.riskRows = [
      { label: 'Approval Rate', value: `${this.approvalRate}%` },
      { label: 'Average Ticket', value: this.averageTicket.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) },
      { label: 'Charity Late Fee Pool', value: Number(data.charityLateFeeAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) },
      { label: 'Disbursed Amount', value: Number(data.disbursedAmount || 0).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }) }
    ];

    this.selectedApplicationId = recentApplications[0]?.id;
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

  private buildShortLabel(label: string): string {
    const alphaNumeric = label.replace(/[^A-Za-z0-9 ]/g, ' ').trim();
    if (/^[A-Za-z]{2,}\d+/i.test(alphaNumeric.replace(/\s+/g, ''))) {
      return alphaNumeric.slice(-4).toUpperCase();
    }
    return label
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(part => part[0])
      .join('')
      .toUpperCase() || 'FN';
  }

  private buildDateLabel(value?: string): string {
    if (!value) {
      return 'File';
    }
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return 'File';
    }
    return date.toLocaleDateString(undefined, { month: 'short', day: 'numeric' });
  }

  private buildAxisTicks(maxValue: number): DashboardAxisTick[] {
    const safeMax = Math.max(1, Math.ceil(maxValue));
    return [0, 25, 50, 75, 100].map(percent => ({
      label: `${Math.round((safeMax * percent) / 100)}`,
      bottom: percent
    }));
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
