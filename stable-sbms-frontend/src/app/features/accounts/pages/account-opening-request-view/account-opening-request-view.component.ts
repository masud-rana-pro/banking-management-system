import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { AccountOpeningRequestResponse, formatEnumLabel } from '../../models/account.model';
import { AccountHolderImageService } from '../../services/account-holder-image.service';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-opening-request-view',
  templateUrl: './account-opening-request-view.component.html',
  styleUrls: ['./account-opening-request-view.component.scss']
})
export class AccountOpeningRequestViewComponent implements OnInit {

  loading = false;
  id: number | null = null;
  item: AccountOpeningRequestResponse | null = null;
  branches: BranchResponse[] = [];
  customerImageMap: Record<number, string> = {};
  customerImageByCode: Record<string, string> = {};
  customerImageByName: Record<string, string> = {};
  resolvedImageName = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountApi: AccountService,
    private branchApi: BranchApiService,
    private fileUploadService: FileUploadService,
    private customerApi: CustomerService,
    private accountHolderImage: AccountHolderImageService
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
    forkJoin({
      item: this.accountApi.getOpeningRequestById(id),
      branches: this.branchApi.getAll().pipe(catchError(() => of([]))),
      customers: this.customerApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ item, branches, customers }) => {
        this.item = item;
        this.branches = branches || [];
        this.customerImageMap = this.accountHolderImage.buildCustomerImageMap(customers || []);
        this.customerImageByCode = this.accountHolderImage.buildCustomerImageByCode(customers || []);
        this.customerImageByName = this.accountHolderImage.buildCustomerImageByName(customers || []);
        this.resolvedImageName = this.accountHolderImage.resolveOpeningRequestImageName(
          item,
          this.customerImageMap,
          this.customerImageByCode,
          this.customerImageByName
        );
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load opening request.', 'error');
      }
    });
  }

  print(): void {
    if (!this.id) return;
    window.open(this.accountApi.getOpeningRequestDocumentPreviewUrl(this.id), '_blank');
  }

  previewDocument(): void {
    if (!this.id) return;
    window.open(this.accountApi.getOpeningRequestDocumentPreviewUrl(this.id), '_blank');
  }

  downloadDocument(): void {
    if (!this.id) return;
    window.open(this.accountApi.getOpeningRequestDocumentDownloadUrl(this.id), '_blank');
  }

  edit(): void {
    if (!this.id) return;
    this.router.navigate(['/accounts/opening-requests', this.id, 'edit']);
  }

  review(): void {
    if (!this.id) return;
    this.router.navigate(['/accounts/opening-requests', this.id, 'review']);
  }

  openCustomer(): void {
    if (!this.item) return;
    this.router.navigate(['/customers', this.item.customerId]);
  }

  openType(): void {
    if (!this.item) return;
    this.router.navigate(['/accounts/account-types', this.item.accountTypeId]);
  }

  openAccount(): void {
    if (!this.item?.accountId) return;
    this.router.navigate(['/accounts', this.item.accountId]);
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) {
      return 'Unassigned Branch';
    }
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getImageUrl(fileName?: string | null): string {
    return this.fileUploadService.resolveImageUrl(fileName);
  }

  getResolvedImageUrl(): string {
    return this.fileUploadService.resolveImageUrl(this.resolvedImageName);
  }

  openApplicantPreview(fileName?: string | null): void {
    const url = this.getImageUrl(fileName);
    if (url) {
      window.open(url, '_blank');
    }
  }
}
