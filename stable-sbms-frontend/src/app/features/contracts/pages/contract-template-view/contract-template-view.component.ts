import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { ContractTemplateResponse, formatEnumLabel } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-contract-template-view',
  templateUrl: './contract-template-view.component.html',
  styleUrls: ['./contract-template-view.component.scss']
})
export class ContractTemplateViewComponent implements OnInit {

  id = 0;
  loading = false;
  item: ContractTemplateResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private contractService: ContractService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.contractService.getTemplateById(this.id).subscribe({
      next: data => {
        this.item = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load contract template view.', 'error');
      }
    });
  }

  print(): void {
    window.print();
  }

  edit(): void {
    this.router.navigate(['/contracts/templates', this.id, 'edit']);
  }

  generate(): void {
    this.router.navigate(['/contracts/generate'], { queryParams: { templateId: this.id } });
  }

  toggleArchive(): void {
    if (!this.item) return;
    const request$ = this.item.status === 'ARCHIVED'
      ? this.contractService.restoreTemplate(this.id)
      : this.contractService.archiveTemplate(this.id);

    request$.subscribe({
      next: data => {
        this.item = data;
        Swal.fire('Success', `Contract template ${data.status === 'ARCHIVED' ? 'archived' : 'restored'} successfully.`, 'success');
      },
      error: err => {
        console.error(err);
        Swal.fire('Error', err?.error?.message || 'Failed to change template status.', 'error');
      }
    });
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
