import { Component } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { CONTRACT_TYPE_OPTIONS, ContractTemplateRequest } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-contract-template-create',
  templateUrl: './contract-template-create.component.html',
  styleUrls: ['./contract-template-create.component.scss']
})
export class ContractTemplateCreateComponent {

  saving = false;
  contractTypes = CONTRACT_TYPE_OPTIONS;
  form: ContractTemplateRequest = {
    templateCode: '',
    templateName: '',
    contractType: '',
    versionNo: 1,
    templateBody: 'Contract No: {{contractNo}}\nCustomer: {{customerName}} ({{customerCode}})\nReference: {{referenceModule}} / {{referenceId}}\nDate: {{today}}\n\nTerms:\n1. This {{contractType}} contract is generated from template {{templateName}}.\n2. Customer agrees to the applicable clauses and obligations.\n3. Final contract will be locked after customer and shariah signatures.'
  };

  constructor(
    private contractService: ContractService,
    private router: Router
  ) {}

  save(generateAfterSave = false): void {
    this.saving = true;
    this.contractService.createTemplate(this.form).subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', 'Contract template created successfully.', 'success');
        if (generateAfterSave) {
          this.router.navigate(['/contracts/generate'], { queryParams: { templateId: data.id } });
          return;
        }
        this.router.navigate(['/contracts/templates', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to create contract template.', 'error');
      }
    });
  }
}
