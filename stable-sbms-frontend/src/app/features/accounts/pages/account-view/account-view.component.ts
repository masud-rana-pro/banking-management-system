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
  selector: 'app-account-view',
  templateUrl: './account-view.component.html',
  styleUrls: ['./account-view.component.scss']
})
export class AccountViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: AccountResponse | null = null;
  branches: BranchResponse[] = [];
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
        Swal.fire('Error', 'Failed to load account view.', 'error');
      }
    });
  }

  openCustomer(): void {
    if (!this.item) return;
    this.router.navigate(['/customers', this.item.customerId]);
  }

  openRequest(): void {
    if (!this.item?.openingRequestId) return;
    this.router.navigate(['/accounts/opening-requests', this.item.openingRequestId]);
  }

  openType(): void {
    if (!this.item) return;
    this.router.navigate(['/accounts/account-types', this.item.accountTypeId]);
  }

  openStatus(): void {
    if (!this.id) return;
    this.router.navigate(['/accounts', this.id, 'status']);
  }

  openDeposit(): void {
    if (!this.id) return;
    this.router.navigate(['/transactions/deposit'], { queryParams: { accountId: this.id } });
  }

  openWithdraw(): void {
    if (!this.id) return;
    this.router.navigate(['/transactions/withdraw'], { queryParams: { accountId: this.id } });
  }

  openTransfer(): void {
    if (!this.id) return;
    this.router.navigate(['/transactions/transfer'], { queryParams: { fromAccountId: this.id } });
  }

  openProfitSchedule(): void {
    if (!this.id) return;
    this.router.navigate(['/profit/schedules/new'], { queryParams: { accountId: this.id } });
  }

  openProfitPostings(): void {
    if (!this.id) return;
    this.router.navigate(['/profit/postings'], { queryParams: { accountId: this.id } });
  }

  openProfitRatio(): void {
    if (!this.item?.profitRatioId) return;
    this.router.navigate(['/profit/ratios', this.item.profitRatioId]);
  }

  openCardList(): void {
    if (!this.item) return;
    this.router.navigate(['/cards/list'], { queryParams: { accountId: this.item.id, customerId: this.item.customerId } });
  }

  issueCard(): void {
    if (!this.item) return;
    this.router.navigate(['/cards/new'], { queryParams: { accountId: this.item.id, customerId: this.item.customerId } });
  }

  openCustomerStatement(): void {
    if (!this.item) return;
    this.router.navigate(['/statement/customer/request'], { queryParams: { accountId: this.item.id, customerId: this.item.customerId } });
  }

  openStatementList(): void {
    if (!this.item) return;
    this.router.navigate(['/statement/customer/list'], { queryParams: { customerId: this.item.customerId } });
  }

  openDepositSchemeList(): void {
    if (!this.item) return;
    this.router.navigate(['/deposit-schemes/enrollments/list'], { queryParams: { customerId: this.item.customerId, accountId: this.item.id } });
  }

  openDepositSchemeEnrollment(): void {
    if (!this.item) return;
    this.router.navigate(['/deposit-schemes/enrollments/new'], { queryParams: { customerId: this.item.customerId, accountId: this.item.id } });
  }

  openFinancingList(): void {
    if (!this.item) return;
    this.router.navigate(['/financing/applications'], { queryParams: { customerId: this.item.customerId } });
  }

  openFinancingApplication(): void {
    if (!this.item) return;
    this.router.navigate(['/financing/applications/new'], { queryParams: { customerId: this.item.customerId, branchId: this.item.branchId } });
  }

  openZakatProfiles(): void {
    if (!this.item) return;
    this.router.navigate(['/zakat/profiles'], { queryParams: { customerId: this.item.customerId } });
  }

  runZakat(): void {
    if (!this.item) return;
    this.router.navigate(['/zakat/calc-run'], { queryParams: { customerId: this.item.customerId } });
  }

  openContracts(): void {
    if (!this.item) return;
    this.router.navigate(['/contracts/list'], { queryParams: { customerId: this.item.customerId } });
  }

  generateContract(): void {
    if (!this.item) return;
    this.router.navigate(['/contracts/generate'], {
      queryParams: {
        customerId: this.item.customerId,
        referenceModule: 'ACCOUNT_OPENING',
        referenceId: this.item.openingRequestId || this.item.id
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
