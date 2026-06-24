import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { AccountOpeningRequestResponse, AccountResponse, formatEnumLabel } from '../../models/account.model';
import { AccountHolderImageService } from '../../services/account-holder-image.service';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-status-action',
  templateUrl: './account-status-action.component.html',
  styleUrls: ['./account-status-action.component.scss']
})
export class AccountStatusActionComponent implements OnInit {

  id: number | null = null;
  loading = false;
  saving = false;
  item: AccountResponse | null = null;
  branches: BranchResponse[] = [];
  remarks = '';
  customerProfileImageName = '';
  customerImageMap: Record<number, string> = {};
  customerImageByCode: Record<string, string> = {};
  customerImageByName: Record<string, string> = {};
  requestImageMap: Record<number, string> = {};
  requestImageByCode: Record<string, string> = {};
  requestImageByName: Record<string, string> = {};
  openingRequests: AccountOpeningRequestResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountApi: AccountService,
    private branchApi: BranchApiService,
    private customerApi: CustomerService,
    private fileUploadService: FileUploadService,
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
      item: this.accountApi.getAccountById(id),
      branches: this.branchApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ item, branches }) => {
        this.item = item;
        this.branches = branches || [];
        this.remarks = item.remarks || '';
        forkJoin({
          customer: this.customerApi.getById(item.customerId).pipe(catchError(() => of(null as CustomerResponse | null))),
          customerMatches: this.customerApi.search(item.customerCode || item.customerName || '').pipe(catchError(() => of([] as CustomerResponse[]))),
          openingRequest: item.openingRequestId
            ? this.accountApi.getOpeningRequestById(item.openingRequestId).pipe(catchError(() => of(null as AccountOpeningRequestResponse | null)))
            : of(null as AccountOpeningRequestResponse | null)
        }).subscribe({
          next: ({ customer, customerMatches, openingRequest }) => {
            const customers = [customer, ...(customerMatches || [])].filter(Boolean) as CustomerResponse[];
            this.customerImageMap = this.accountHolderImage.buildCustomerImageMap(customers);
            this.customerImageByCode = this.accountHolderImage.buildCustomerImageByCode(customers);
            this.customerImageByName = this.accountHolderImage.buildCustomerImageByName(customers);
            this.openingRequests = openingRequest ? [openingRequest] : [];
            this.requestImageMap = this.accountHolderImage.buildRequestImageMap(this.openingRequests);
            this.requestImageByCode = this.accountHolderImage.buildRequestImageByCode(this.openingRequests);
            this.requestImageByName = this.accountHolderImage.buildRequestImageByName(this.openingRequests);
            this.customerProfileImageName = this.accountHolderImage.resolveAccountImageName(
              this.item,
              this.customerImageMap,
              this.customerImageByCode,
              this.customerImageByName,
              this.requestImageMap,
              this.requestImageByCode,
              this.requestImageByName,
              this.openingRequests
            );
            this.loading = false;
          },
          error: () => {
            this.customerProfileImageName = '';
            this.loading = false;
          }
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load account status action page.', 'error');
      }
    });
  }

  openView(): void {
    if (!this.id) return;
    this.router.navigate(['/accounts', this.id]);
  }

  openCustomer(): void {
    if (!this.item) return;
    this.router.navigate(['/customers', this.item.customerId]);
  }

  openRequest(): void {
    if (!this.item?.openingRequestId) return;
    this.router.navigate(['/accounts/opening-requests', this.item.openingRequestId]);
  }

  runAction(action: 'activate' | 'block' | 'freeze' | 'close'): void {
    if (!this.id) return;
    this.saving = true;
    const request = { remarks: this.remarks.trim() };
    const action$ = action === 'activate'
      ? this.accountApi.activateAccount(this.id, request)
      : action === 'block'
        ? this.accountApi.blockAccount(this.id, request)
        : action === 'freeze'
          ? this.accountApi.freezeAccount(this.id, request)
          : this.accountApi.closeAccount(this.id, request);

    action$.subscribe({
      next: () => {
        this.saving = false;
        Swal.fire('Success', `Account ${action}d successfully.`, 'success');
        this.load(this.id!);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || `Failed to ${action} account.`, 'error');
      }
    });
  }

  getBranchName(branchId?: number | null): string {
    if (!branchId || branchId < 1) return 'Unassigned Branch';
    const branch = this.branches.find(item => item.id === branchId);
    return branch ? `${branch.branchCode} - ${branch.branchName}` : `BR-${branchId}`;
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getCustomerImageUrl(): string {
    return this.fileUploadService.resolveImageUrl(this.customerProfileImageName);
  }
}
