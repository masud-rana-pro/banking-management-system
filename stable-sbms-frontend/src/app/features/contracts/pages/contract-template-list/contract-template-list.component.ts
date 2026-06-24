import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { ContractTemplateResponse, formatEnumLabel } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-contract-template-list',
  templateUrl: './contract-template-list.component.html',
  styleUrls: ['./contract-template-list.component.scss']
})
export class ContractTemplateListComponent implements OnInit {

  loading = false;
  allItems: ContractTemplateResponse[] = [];
  items: ContractTemplateResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    search: '',
    contractType: '',
    status: ''
  };

  constructor(
    private contractService: ContractService,
    private router: Router,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.contractService.getTemplates().subscribe({
      next: data => {
        this.allItems = data;
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load contract templates.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.templateCode.toLowerCase().includes(keyword)
        || item.templateName.toLowerCase().includes(keyword)
        || item.templateBody.toLowerCase().includes(keyword);
      const matchesType = !this.filters.contractType || item.contractType === this.filters.contractType;
      const matchesStatus = !this.filters.status || item.status === this.filters.status;
      return matchesKeyword && matchesType && matchesStatus;
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
    this.filters = { search: '', contractType: '', status: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  onView(item: ContractTemplateResponse): void {
    this.router.navigate(['/contracts/templates', item.id]);
  }

  onEdit(item: ContractTemplateResponse): void {
    this.router.navigate(['/contracts/templates', item.id, 'edit']);
  }

  onGenerate(item: ContractTemplateResponse): void {
    this.router.navigate(['/contracts/generate'], { queryParams: { templateId: item.id } });
  }

  toggleArchive(item: ContractTemplateResponse): void {
    const request$ = item.status === 'ARCHIVED'
      ? this.contractService.restoreTemplate(item.id)
      : this.contractService.archiveTemplate(item.id);

    request$.subscribe({
      next: () => {
        Swal.fire('Success', `Contract template ${item.status === 'ARCHIVED' ? 'restored' : 'archived'} successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to change template status.', 'error');
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  get activeCount(): number {
    return this.allItems.filter(item => item.status === 'ACTIVE').length;
  }

  get archivedCount(): number {
    return this.allItems.filter(item => item.status === 'ARCHIVED').length;
  }

  get generatedCount(): number {
    return this.allItems.reduce((sum, item) => sum + (item.generatedContractCount || 0), 0);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
