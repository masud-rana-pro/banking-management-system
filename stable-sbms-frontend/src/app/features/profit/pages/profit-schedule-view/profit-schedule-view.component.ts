import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { ProfitPostingResponse, ProfitRatioResponse, ProfitScheduleResponse, formatEnumLabel } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

@Component({
  selector: 'app-profit-schedule-view',
  templateUrl: './profit-schedule-view.component.html',
  styleUrls: ['./profit-schedule-view.component.scss']
})
export class ProfitScheduleViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: ProfitScheduleResponse | null = null;
  ratios: ProfitRatioResponse[] = [];
  postings: ProfitPostingResponse[] = [];
  customer: CustomerResponse | null = null;
  customerImageUrl = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private profitApi: ProfitService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) {
      this.loadData(this.id);
    }
  }

  get postingHistory(): ProfitPostingResponse[] {
    return this.postings.filter(item => item.scheduleId === this.id);
  }

  get currentRatio(): ProfitRatioResponse | null {
    if (!this.item) return null;
    return this.ratios.find(item => item.accountTypeId === this.item?.accountTypeId && item.activeNow && item.status === 'ACTIVE') || null;
  }

  loadData(id: number): void {
    this.loading = true;
    forkJoin({
      schedule: this.profitApi.getScheduleById(id),
      ratios: this.profitApi.getRatios(),
      postings: this.profitApi.getPostings()
    }).subscribe({
      next: ({ schedule, ratios, postings }) => {
        this.item = schedule;
        this.ratios = ratios || [];
        this.postings = postings || [];
        this.loadCustomerProfile(schedule.customerCode);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load profit schedule view.', 'error');
      }
    });
  }

  print(): void {
    window.print();
  }

  openAccount(): void {
    if (!this.item) return;
    this.router.navigate(['/accounts', this.item.accountId]);
  }

  openRatio(): void {
    if (!this.item) return;
    if (this.currentRatio) {
      this.router.navigate(['/profit/ratios', this.currentRatio.id]);
      return;
    }
    this.router.navigate(['/profit/ratios'], { queryParams: { accountTypeId: this.item.accountTypeId } });
  }

  openPosting(id: number): void {
    this.router.navigate(['/profit/postings', id]);
  }

  openPostingRun(): void {
    if (!this.item) return;
    this.router.navigate(['/profit/postings/run'], { queryParams: { scheduleId: this.item.id, accountId: this.item.accountId } });
  }

  openCalculator(): void {
    if (!this.item) return;
    this.router.navigate(['/calculations/simulator'], {
      queryParams: {
        sourceModule: 'PROFIT',
        productType: 'PROFIT_POSTING',
        principalAmount: this.item.currentBalance,
        ratePercent: this.currentRatio?.ratioPercent ?? 0,
        tenureMonths: 12,
        frequency: this.item.profitFrequency,
        scheduleId: this.item.id,
        accountId: this.item.accountId,
        accountTypeId: this.item.accountTypeId,
        sourceName: `${this.item.accountNumber} - ${this.item.customerName}`,
        returnRoute: `/profit/schedules/${this.item.id}`
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  private loadCustomerProfile(customerCode?: string | null): void {
    if (!customerCode) {
      this.customer = null;
      this.customerImageUrl = '';
      return;
    }

    this.customerService.getAll().subscribe({
      next: customers => {
        this.customer = customers.find(item => item.customerCode === customerCode) || null;
        this.customerImageUrl = this.customer?.profileImageName
          ? this.fileUploadService.resolveImageUrl(this.customer.profileImageName)
          : '';
      },
      error: () => {
        this.customer = null;
        this.customerImageUrl = '';
      }
    });
  }
}
