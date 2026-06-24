import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { BranchApiService } from '../../../branch/services/branch-api.service';
import { BranchResponse } from '../../../branch/models/branch.model';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { CardEventLogResponse, CardPinEventResponse, CardResponse, formatEnumLabel } from '../../models/card.model';
import { CardService } from '../../services/card.service';

@Component({
  selector: 'app-card-view',
  templateUrl: './card-view.component.html',
  styleUrls: ['./card-view.component.scss']
})
export class CardViewComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: CardResponse | null = null;
  events: CardEventLogResponse[] = [];
  pinEvents: CardPinEventResponse[] = [];
  branches: BranchResponse[] = [];
  customerProfileImageName = '';
  userImageMap: Record<string, string> = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private cardApi: CardService,
    private branchApi: BranchApiService,
    private customerApi: CustomerService,
    private fileUploadService: FileUploadService,
    private userApi: UserApiService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) {
      this.load(this.id);
    }
  }

  load(id: number): void {
    this.loading = true;
    forkJoin({
      item: this.cardApi.getById(id),
      events: this.cardApi.getEvents(id).pipe(catchError(() => of([]))),
      pinEvents: this.cardApi.getPinEvents(id).pipe(catchError(() => of([]))),
      branches: this.branchApi.getAll().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ item, events, pinEvents, branches }) => {
        this.item = item;
        this.events = events;
        this.pinEvents = pinEvents;
        this.branches = branches || [];
        this.customerApi.getById(item.customerId).pipe(catchError(() => of(null as CustomerResponse | null))).subscribe({
          next: customer => {
            this.customerProfileImageName = customer?.profileImageName || '';
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
        Swal.fire('Error', 'Failed to load card view.', 'error');
      }
    });
  }

  print(): void {
    window.print();
  }

  openCustomer(): void {
    if (!this.item) return;
    this.router.navigate(['/customers', this.item.customerId]);
  }

  openAccount(): void {
    if (!this.item) return;
    this.router.navigate(['/accounts', this.item.accountId]);
  }

  edit(): void {
    if (!this.item) return;
    this.router.navigate(['/cards', this.item.id, 'edit']);
  }

  activation(): void {
    if (!this.item) return;
    this.router.navigate(['/cards', this.item.id, 'activate']);
  }

  blockUnblock(): void {
    if (!this.item) return;
    this.router.navigate(['/cards', this.item.id, 'block-unblock']);
  }

  pinEventsPage(): void {
    if (!this.item) return;
    this.router.navigate(['/cards', this.item.id, 'pin-events']);
  }

  usagePage(): void {
    this.router.navigate(['/cards/atm-cdm-transactions/list']);
  }

  toggleArchive(): void {
    if (!this.item) return;
    const restoring = this.item.status === 'ARCHIVED';
    const action$ = restoring ? this.cardApi.restore(this.item.id) : this.cardApi.archive(this.item.id);
    action$.subscribe({
      next: () => {
        Swal.fire('Success', restoring ? 'Card restored successfully.' : 'Card archived successfully.', 'success');
        if (this.id) this.load(this.id);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to update card.', 'error')
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

  getUserImageUrl(username?: string | null): string {
    const key = (username || '').trim().toLowerCase();
    return key ? this.userImageMap[key] || '' : '';
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

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }

  canAny(permissionCodes: string[]): boolean {
    return this.accessControl.hasAnyPermission(permissionCodes);
  }
}
