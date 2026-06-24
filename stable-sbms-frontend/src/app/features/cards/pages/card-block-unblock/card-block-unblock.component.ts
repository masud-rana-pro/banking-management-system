import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { CardResponse, CardWorkflowActionRequest, formatEnumLabel } from '../../models/card.model';
import { CardService } from '../../services/card.service';

@Component({
  selector: 'app-card-block-unblock',
  templateUrl: './card-block-unblock.component.html',
  styleUrls: ['./card-block-unblock.component.scss']
})
export class CardBlockUnblockComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: CardResponse | null = null;
  customer: CustomerResponse | null = null;
  customerImageUrl = '';
  model: CardWorkflowActionRequest = {
    blockReason: '',
    remarks: '',
    performedBy: 'SYSTEM',
    maskedCardNo: '',
    expiryDate: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private cardApi: CardService,
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

  get isBlocked(): boolean {
    return this.item?.cardStatus === 'BLOCKED';
  }

  load(id: number): void {
    this.loading = true;
    this.cardApi.getById(id).subscribe({
      next: item => {
        this.item = item;
        this.loadCustomerProfile(item.customerCode);
        this.model.blockReason = item.blockReason || '';
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load block/unblock workflow.', 'error');
      }
    });
  }

  submit(): void {
    if (!this.id || !this.item) return;
    if (!this.isBlocked && !this.model.blockReason.trim()) {
      Swal.fire('Validation', 'Block reason is required.', 'warning');
      return;
    }
    const action$ = this.isBlocked
      ? this.cardApi.unblock(this.id, this.model)
      : this.cardApi.block(this.id, this.model);

    action$.subscribe({
      next: card => {
        Swal.fire('Success', this.isBlocked ? 'Card unblocked successfully.' : 'Card blocked successfully.', 'success');
        this.router.navigate(['/cards', card.id]);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to update card status.', 'error')
    });
  }

  openView(): void {
    if (!this.id) return;
    this.router.navigate(['/cards', this.id]);
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
