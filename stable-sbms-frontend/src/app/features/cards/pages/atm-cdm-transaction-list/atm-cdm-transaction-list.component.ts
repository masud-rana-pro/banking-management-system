import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { CardTransactionResponse, formatEnumLabel } from '../../models/card.model';
import { CardService } from '../../services/card.service';

@Component({
  selector: 'app-atm-cdm-transaction-list',
  templateUrl: './atm-cdm-transaction-list.component.html',
  styleUrls: ['./atm-cdm-transaction-list.component.scss']
})
export class AtmCdmTransactionListComponent implements OnInit {

  loading = false;
  allItems: CardTransactionResponse[] = [];
  items: CardTransactionResponse[] = [];

  filters = {
    search: '',
    eventType: ''
  };

  page = 1;
  pageSize = 10;
  total = 0;
  customerImageMap: Record<string, string> = {};
  userImageMap: Record<string, string> = {};

  constructor(
    private cardApi: CardService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private userApi: UserApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
    this.loadUsers();
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.cardApi.getAtmCdmTransactions().subscribe({
      next: items => {
        this.allItems = items || [];
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load ATM/CDM card transactions.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.cardRefNo.toLowerCase().includes(keyword)
        || item.maskedCardNo.toLowerCase().includes(keyword)
        || item.customerCode.toLowerCase().includes(keyword)
        || item.customerName.toLowerCase().includes(keyword)
        || item.accountNumber.toLowerCase().includes(keyword);
      const matchesEventType = !this.filters.eventType || item.eventType === this.filters.eventType;
      return matchesKeyword && matchesEventType;
    });
    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.filters = { search: '', eventType: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onPrint(): void {
    window.print();
  }

  onExport(format: string): void {
    const blob = new Blob([JSON.stringify(this.allItems, null, 2)], { type: 'application/json;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `card-atm-cdm-transactions.${format === 'csv' ? 'json' : format}`;
    link.click();
    URL.revokeObjectURL(url);
  }

  openCard(item: CardTransactionResponse): void {
    this.router.navigate(['/cards', item.cardId]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCustomerImageUrl(customerCode?: string | null): string {
    if (!customerCode) {
      return '';
    }
    return this.customerImageMap[customerCode] || '';
  }

  getUserImageUrl(username?: string | null): string {
    const key = (username || '').trim().toLowerCase();
    return key ? this.userImageMap[key] || '' : '';
  }

  private loadCustomers(): void {
    this.customerService.getAll().subscribe({
      next: customers => {
        this.customerImageMap = this.buildCustomerImageMap(customers || []);
      },
      error: () => {
        this.customerImageMap = {};
      }
    });
  }

  private buildCustomerImageMap(customers: CustomerResponse[]): Record<string, string> {
    return customers.reduce((acc, customer) => {
      if (customer.customerCode && customer.profileImageName) {
        acc[customer.customerCode] = this.fileUploadService.resolveImageUrl(customer.profileImageName);
      }
      return acc;
    }, {} as Record<string, string>);
  }

  private loadUsers(): void {
    this.userApi.getAll().subscribe({
      next: users => {
        this.userImageMap = this.buildUserImageMap(users || []);
      },
      error: () => {
        this.userImageMap = {};
      }
    });
  }

  private buildUserImageMap(users: UserResponse[]): Record<string, string> {
    return users.reduce<Record<string, string>>((acc, user) => {
      if (user.username && user.profileImageName) {
        acc[user.username.trim().toLowerCase()] = this.fileUploadService.resolveImageUrl(user.profileImageName);
      }
      return acc;
    }, {});
  }
}
