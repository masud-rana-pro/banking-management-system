import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { CustomerResponse } from 'src/app/features/customer/models/customer.model';
import { CustomerService } from 'src/app/features/customer/services/customer.service';
import { CONTRACT_TYPE_OPTIONS, ContractGenerateRequest, ContractTemplateResponse, REFERENCE_MODULE_OPTIONS } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-contract-generate',
  templateUrl: './contract-generate.component.html',
  styleUrls: ['./contract-generate.component.scss']
})
export class ContractGenerateComponent implements OnInit {

  loading = false;
  saving = false;
  templates: ContractTemplateResponse[] = [];
  customers: CustomerResponse[] = [];
  selectedCustomer: CustomerResponse | null = null;
  customerImageUrl = '';
  uploadingDocument = false;
  referenceModules = REFERENCE_MODULE_OPTIONS;
  form: ContractGenerateRequest = {
    templateId: null,
    customerId: null,
    referenceModule: '',
    referenceId: null,
    contractText: '',
    supportingDocumentName: '',
    remarks: '',
    generatedBy: 'SYSTEM_GENERATOR'
  };

  constructor(
    private contractService: ContractService,
    private customerService: CustomerService,
    private fileUploadService: FileUploadService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form.templateId = Number(this.route.snapshot.queryParamMap.get('templateId')) || null;
    this.form.customerId = Number(this.route.snapshot.queryParamMap.get('customerId')) || null;
    this.form.referenceModule = this.route.snapshot.queryParamMap.get('referenceModule') || '';
    this.form.referenceId = Number(this.route.snapshot.queryParamMap.get('referenceId')) || null;
    this.loadLookups();
  }

  loadLookups(): void {
    this.loading = true;
    this.contractService.getTemplates().subscribe({
      next: templates => {
        this.templates = templates.filter(item => item.status !== 'ARCHIVED');
        this.customerService.getAll().subscribe({
          next: customers => {
            this.customers = customers.filter(item => item.status !== 'ARCHIVED');
            this.syncSelectedCustomer();
            this.loading = false;
          },
          error: err => {
            console.error(err);
            this.loading = false;
          }
        });
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load contract generate form.', 'error');
      }
    });
  }

  fillFromTemplate(): void {
    const template = this.templates.find(item => item.id === this.form.templateId);
    if (!template || this.form.contractText.trim()) return;
    this.form.contractText = template.templateBody;
  }

  onCustomerChange(): void {
    this.syncSelectedCustomer();
  }

  onSupportingDocumentSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.uploadingDocument = true;
    this.fileUploadService.uploadDocument(file).subscribe({
      next: result => {
        this.form.supportingDocumentName = result.fileName;
        this.uploadingDocument = false;
      },
      error: err => {
        console.error(err);
        this.uploadingDocument = false;
        Swal.fire('Error', err?.error?.message || 'Failed to upload contract supporting document.', 'error');
      }
    });
  }

  previewSupportingDocument(): void {
    if (!this.form.supportingDocumentName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.form.supportingDocumentName), '_blank');
  }

  save(openSign = false): void {
    this.saving = true;
    this.contractService.generate(this.form).subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', 'Contract generated successfully.', 'success');
        this.router.navigate(openSign ? ['/contracts', data.id, 'sign'] : ['/contracts', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to generate contract.', 'error');
      }
    });
  }

  private syncSelectedCustomer(): void {
    this.selectedCustomer = this.customers.find(item => item.id === this.form.customerId) || null;
    this.customerImageUrl = this.selectedCustomer?.profileImageName
      ? this.fileUploadService.resolveImageUrl(this.selectedCustomer.profileImageName)
      : '';
  }
}
