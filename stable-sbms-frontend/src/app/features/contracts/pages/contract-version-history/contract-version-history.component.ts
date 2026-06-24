import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

import { ContractVersionResponse, formatEnumLabel } from '../../models/contract.model';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-contract-version-history',
  templateUrl: './contract-version-history.component.html',
  styleUrls: ['./contract-version-history.component.scss']
})
export class ContractVersionHistoryComponent implements OnInit {

  id = 0;
  loading = false;
  items: ContractVersionResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private contractService: ContractService
  ) {}

  ngOnInit(): void {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load(): void {
    this.loading = true;
    this.contractService.getVersions(this.id).subscribe({
      next: data => {
        this.items = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load contract version history.', 'error');
      }
    });
  }

  print(): void {
    window.print();
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
