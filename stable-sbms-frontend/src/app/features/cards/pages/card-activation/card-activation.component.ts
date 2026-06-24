import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from '../../../customer/models/customer.model';
import { CustomerService } from '../../../customer/services/customer.service';
import { CardPinEventRequest, CardPinEventType, CardResponse, CardWorkflowActionRequest, formatEnumLabel } from '../../models/card.model';
import { CardService } from '../../services/card.service';

@Component({
  selector: 'app-card-activation',
  templateUrl: './card-activation.component.html',
  styleUrls: ['./card-activation.component.scss']
})
export class CardActivationComponent implements OnInit {

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
  recordPinEvent = true;

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

  load(id: number): void {
    this.loading = true;
    this.cardApi.getById(id).subscribe({
      next: item => {
        this.item = item;
        this.loadCustomerProfile(item.customerCode);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load card activation page.', 'error');
      }
    });
  }

  activate(): void {
    if (!this.id) return;
    this.cardApi.activate(this.id, this.model).subscribe({
      next: card => {
        if (this.recordPinEvent) {
          const pinRequest: CardPinEventRequest = {
            eventType: 'PIN_GENERATED',
            performedBy: this.model.performedBy || 'SYSTEM'
          };
          this.cardApi.addPinEvent(card.id, pinRequest).subscribe({
            next: () => {
              Swal.fire('Success', 'Card activated and PIN generated event recorded successfully.', 'success');
              this.router.navigate(['/cards', card.id]);
            },
            error: () => {
              Swal.fire('Success', 'Card activated successfully.', 'success');
              this.router.navigate(['/cards', card.id]);
            }
          });
          return;
        }

        Swal.fire('Success', 'Card activated successfully.', 'success');
        this.router.navigate(['/cards', card.id]);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to activate card.', 'error')
    });
  }

  openView(): void {
    if (!this.id) return;
    this.router.navigate(['/cards', this.id]);
  }

  openPinEvents(): void {
    if (!this.id) return;
    this.router.navigate(['/cards', this.id, 'pin-events']);
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

  protected readonly CardPinEventType = {} as Record<string, CardPinEventType>;
}
