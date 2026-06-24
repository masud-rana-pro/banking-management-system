import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { TableExportService } from 'src/app/core/services/table-export.service';
import { CharityBeneficiaryResponse, formatEnumLabel } from '../../models/zakat.model';
import { ZakatService } from '../../services/zakat.service';

@Component({
  selector: 'app-beneficiary-list',
  templateUrl: './beneficiary-list.component.html',
  styleUrls: ['./beneficiary-list.component.scss']
})
export class BeneficiaryListComponent implements OnInit {

  loading = false;
  allItems: CharityBeneficiaryResponse[] = [];
  items: CharityBeneficiaryResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  search = '';

  constructor(
    private zakatService: ZakatService,
    private router: Router,
    private fileUploadService: FileUploadService,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.zakatService.getBeneficiaries().subscribe({
      next: data => {
        this.allItems = data;
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load charity beneficiaries.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.search.trim().toLowerCase();
    const filtered = this.allItems.filter(item => !keyword
      || item.beneficiaryCode.toLowerCase().includes(keyword)
      || item.beneficiaryName.toLowerCase().includes(keyword)
      || String(item.mobile || '').toLowerCase().includes(keyword));
    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.search = '';
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openCreate(): void {
    this.router.navigate(['/zakat/beneficiaries/new']);
  }

  openEdit(item: CharityBeneficiaryResponse): void {
    this.router.navigate(['/zakat/beneficiaries', item.id, 'edit']);
  }

  openPayout(item?: CharityBeneficiaryResponse): void {
    this.router.navigate(['/zakat/payouts/new'], {
      queryParams: item ? { beneficiaryId: item.id } : {}
    });
  }

  toggleArchive(item: CharityBeneficiaryResponse): void {
    const request$ = item.status === 'ARCHIVED'
      ? this.zakatService.restoreBeneficiary(item.id)
      : this.zakatService.archiveBeneficiary(item.id);
    request$.subscribe({
      next: () => {
        Swal.fire('Success', `Beneficiary ${item.status === 'ARCHIVED' ? 'restored' : 'archived'} successfully.`, 'success');
        this.load();
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to change beneficiary status.', 'error');
      }
    });
  }

  onPrint(): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No beneficiary data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Charity Beneficiary List',
      'Al-Barakah Shariah Banking Management System',
      ['Beneficiary Code', 'Beneficiary Name', 'Mobile', 'Status', 'Proof'],
      data.map(item => [
        item.beneficiaryCode,
        item.beneficiaryName,
        item.mobile || '',
        item.status,
        item.proofDocumentName ? 'Available' : 'Not Available'
      ])
    );
  }

  onExport(type: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No beneficiary data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Charity Beneficiaries',
      'charity-beneficiary-list',
      ['Beneficiary Code', 'Beneficiary Name', 'Mobile', 'Status', 'Proof'],
      data.map(item => [
        item.beneficiaryCode,
        item.beneficiaryName,
        item.mobile || '',
        item.status,
        item.proofDocumentName ? 'Available' : 'Not Available'
      ]),
      type as 'csv' | 'excel' | 'pdf'
    );
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  previewProof(item: CharityBeneficiaryResponse): void {
    if (!item.proofDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(item.proofDocumentName), '_blank');
  }

  private getExportableData(): CharityBeneficiaryResponse[] {
    const keyword = this.search.trim().toLowerCase();
    return this.allItems.filter(item => !keyword
      || item.beneficiaryCode.toLowerCase().includes(keyword)
      || item.beneficiaryName.toLowerCase().includes(keyword)
      || String(item.mobile || '').toLowerCase().includes(keyword));
  }
}
