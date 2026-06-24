import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { CONTRACT_TYPE_OPTIONS, ContractTemplateRequest } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-contract-template-edit',
  templateUrl: './contract-template-edit.component.html',
  styleUrls: ['./contract-template-edit.component.scss']
})
export class ContractTemplateEditComponent implements OnInit {

  id = 0;
  loading = false;
  saving = false;
  contractTypes = CONTRACT_TYPE_OPTIONS;
  form: ContractTemplateRequest = {
    templateCode: '',
    templateName: '',
    contractType: '',
    versionNo: null,
    templateBody: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private contractService: ContractService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.contractService.getTemplateById(this.id).subscribe({
      next: data => {
        this.form = {
          templateCode: data.templateCode,
          templateName: data.templateName,
          contractType: data.contractType,
          versionNo: data.versionNo,
          templateBody: data.templateBody
        };
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load contract template.', 'error');
      }
    });
  }

  save(): void {
    this.saving = true;
    this.contractService.updateTemplate(this.id, this.form).subscribe({
      next: data => {
        this.saving = false;
        Swal.fire('Success', 'Contract template updated successfully.', 'success');
        this.router.navigate(['/contracts/templates', data.id]);
      },
      error: err => {
        console.error(err);
        this.saving = false;
        Swal.fire('Error', err?.error?.message || 'Failed to update contract template.', 'error');
      }
    });
  }
}
