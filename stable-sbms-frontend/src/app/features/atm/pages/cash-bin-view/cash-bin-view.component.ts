import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import { CashBinResponse } from '../../models/terminal.model';

@Component({
  selector: 'app-cash-bin-view',
  templateUrl: './cash-bin-view.component.html',
  styleUrls: ['./cash-bin-view.component.scss']
})
export class CashBinViewComponent implements OnInit {

  id: number | null = null;
  cashBin: CashBinResponse | null = null;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private atmApi: AtmTerminalService,
    public accessControl: AccessControlService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;

    if (this.id) {
      this.loadCashBin(this.id);
    }
  }

  loadCashBin(id: number): void {
    this.loading = true;

    this.atmApi.getCashBinById(id).subscribe({
      next: data => {
        this.cashBin = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load cash bin profile.', 'error');
      }
    });
  }

  previewProfile(): void {
    if (!this.id) return;
    window.open(this.atmApi.getCashBinProfilePreviewUrl(this.id), '_blank', 'noopener');
  }

  downloadProfile(): void {
    if (!this.id) return;
    window.open(this.atmApi.getCashBinProfileDownloadUrl(this.id), '_blank', 'noopener');
  }

  printProfile(): void {
    this.previewProfile();
  }

  back(): void {
    this.router.navigate(['/atm/cash-bins']);
  }

  edit(): void {
    if (!this.cashBin) return;
    this.router.navigate(['/atm/cash-bin', this.cashBin.id, 'edit']);
  }

  viewTerminal(): void {
    if (!this.cashBin) return;
    this.router.navigate(['/atm/terminals', this.cashBin.terminalId]);
  }

  archive(): void {
    if (!this.cashBin) return;

    Swal.fire({
      icon: 'warning',
      title: 'Archive cash bin?',
      text: `${this.cashBin.binNo} will be archived.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, archive',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed || !this.cashBin) return;

      this.atmApi.archiveCashBin(this.cashBin.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Archived',
            text: 'Cash bin archived successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          if (this.id) {
            this.loadCashBin(this.id);
          }
        },
        error: err => {
          console.error(err);
          Swal.fire('Error', err?.error?.message || 'Failed to archive cash bin.', 'error');
        }
      });
    });
  }

  restore(): void {
    if (!this.cashBin) return;

    Swal.fire({
      icon: 'question',
      title: 'Restore cash bin?',
      text: `${this.cashBin.binNo} will be restored.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, restore',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed || !this.cashBin) return;

      this.atmApi.restoreCashBin(this.cashBin.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Restored',
            text: 'Cash bin restored successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          if (this.id) {
            this.loadCashBin(this.id);
          }
        },
        error: err => {
          console.error(err);
          Swal.fire('Error', err?.error?.message || 'Failed to restore cash bin.', 'error');
        }
      });
    });
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
}
