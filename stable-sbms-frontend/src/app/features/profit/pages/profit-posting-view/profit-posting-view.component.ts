import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { ProfitPostingResponse, formatEnumLabel } from '../../models/profit.model';
import { ProfitService } from '../../services/profit.service';

@Component({
  selector: 'app-profit-posting-view',
  templateUrl: './profit-posting-view.component.html',
  styleUrls: ['./profit-posting-view.component.scss']
})
export class ProfitPostingViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: ProfitPostingResponse | null = null;
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
      this.load(this.id);
    }
  }

  load(id: number): void {
    this.loading = true;
    this.profitApi.getPostingById(id).subscribe({
      next: item => {
        this.item = item;
        this.loadCustomerProfile(item.customerCode);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load profit posting view.', 'error');
      }
    });
  }

  previewAdvice(): void {
    if (!this.item) return;
    window.open(this.profitApi.getPostingAdvicePreviewUrl(this.item.id), '_blank');
  }

  downloadAdvice(): void {
    if (!this.item) return;
    const link = document.createElement('a');
    link.href = this.profitApi.getPostingAdviceDownloadUrl(this.item.id);
    link.target = '_blank';
    link.rel = 'noopener';
    link.click();
  }

  printAdvice(): void {
    this.previewAdvice();
  }

  openAccount(): void {
    if (!this.item) return;
    this.router.navigate(['/accounts', this.item.accountId]);
  }

  openSchedule(): void {
    if (!this.item) return;
    this.router.navigate(['/profit/schedules', this.item.scheduleId]);
  }

  openRunAgain(): void {
    if (!this.item) return;
    this.router.navigate(['/profit/postings/run'], { queryParams: { scheduleId: this.item.scheduleId, accountId: this.item.accountId } });
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
