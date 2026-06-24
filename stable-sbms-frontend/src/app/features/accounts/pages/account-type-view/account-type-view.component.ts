import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccountTypeResponse, formatEnumLabel } from '../../models/account.model';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-account-type-view',
  templateUrl: './account-type-view.component.html',
  styleUrls: ['./account-type-view.component.scss']
})
export class AccountTypeViewComponent implements OnInit {

  loading = false;
  id: number | null = null;
  item: AccountTypeResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountApi: AccountService
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
    this.accountApi.getAccountTypeById(id).subscribe({
      next: item => {
        this.item = item;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load account type.', 'error');
      }
    });
  }

  edit(): void {
    if (!this.id) return;
    this.router.navigate(['/accounts/account-types', this.id, 'edit']);
  }

  openRequests(): void {
    this.router.navigate(['/accounts/opening-requests']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
