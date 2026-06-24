import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { AccessControlService } from 'src/app/core/services/access-control.service';
import { AtmTerminalService } from '../../services/atm-terminal.service';
import {
  CashBinResponse,
  DeviceJournalResponse,
  ReconciliationResponse,
  ReplenishmentResponse,
  TerminalResponse
} from '../../models/terminal.model';

@Component({
  selector: 'app-terminal-view',
  templateUrl: './terminal-view.component.html',
  styleUrls: ['./terminal-view.component.scss']
})
export class TerminalViewComponent implements OnInit {

  id: number | null = null;
  terminal: TerminalResponse | null = null;
  cashBins: CashBinResponse[] = [];
  replenishments: ReplenishmentResponse[] = [];
  reconciliations: ReconciliationResponse[] = [];
  journal: DeviceJournalResponse[] = [];
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private atmApi: AtmTerminalService,
    public accessControl: AccessControlService
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;

    if (this.id) {
      this.loadTerminal(this.id);
    }
  }

  loadTerminal(id: number): void {
    this.loading = true;

    this.atmApi.getById(id).subscribe({
      next: data => {
        this.terminal = data;
        this.loadRelatedData(id);
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;

        Swal.fire({
          icon: 'error',
          title: 'Failed to load terminal',
          text: err?.error?.message || 'Please check backend API and try again.'
        });
      }
    });
  }

  loadRelatedData(terminalId: number): void {
    forkJoin({
      cashBins: this.atmApi.getCashBinsByTerminal(terminalId).pipe(catchError(() => of([]))),
      replenishments: this.atmApi.getReplenishmentsByTerminal(terminalId).pipe(catchError(() => of([]))),
      reconciliations: this.atmApi.getReconciliationsByTerminal(terminalId).pipe(catchError(() => of([]))),
      journal: this.atmApi.getDeviceJournal(terminalId).pipe(catchError(() => of([])))
    }).subscribe(result => {
      this.cashBins = result.cashBins;
      this.replenishments = result.replenishments;
      this.reconciliations = result.reconciliations;
      this.journal = result.journal.slice(0, 6);
    });
  }

  previewProfile(): void {
    if (!this.id) return;
    window.open(this.atmApi.getTerminalProfilePreviewUrl(this.id), '_blank', 'noopener');
  }

  downloadProfile(): void {
    if (!this.id) return;
    window.open(this.atmApi.getTerminalProfileDownloadUrl(this.id), '_blank', 'noopener');
  }

  printProfile(): void {
    this.previewProfile();
  }

  back(): void {
    this.router.navigate(['/atm/terminals']);
  }

  edit(): void {
    if (!this.terminal) return;
    this.router.navigate(['/atm/terminals', this.terminal.id, 'edit']);
  }

  addCashBin(): void {
    if (!this.terminal) return;
    this.router.navigate(['/atm/cash-bin/new'], {
      queryParams: { terminalId: this.terminal.id }
    });
  }

  replenish(): void {
    if (!this.terminal) return;
    this.router.navigate(['/atm/replenishment/new'], {
      queryParams: { terminalId: this.terminal.id }
    });
  }

  reconcile(): void {
    if (!this.terminal) return;
    this.router.navigate(['/atm/reconciliation/new'], {
      queryParams: { terminalId: this.terminal.id }
    });
  }

  archive(): void {
    if (!this.terminal) return;

    Swal.fire({
      icon: 'warning',
      title: 'Archive terminal?',
      text: `${this.terminal.terminalName} will be archived and removed from active operation.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, archive',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed || !this.terminal) return;

      this.atmApi.archive(this.terminal.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Archived',
            text: 'Terminal archived successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          if (this.id) {
            this.loadTerminal(this.id);
          }
        },
        error: err => {
          console.error(err);
          Swal.fire('Error', err?.error?.message || 'Failed to archive terminal.', 'error');
        }
      });
    });
  }

  restore(): void {
    if (!this.terminal) return;

    Swal.fire({
      icon: 'question',
      title: 'Restore terminal?',
      text: `${this.terminal.terminalName} will be restored as active terminal.`,
      showCancelButton: true,
      confirmButtonText: 'Yes, restore',
      cancelButtonText: 'Cancel'
    }).then(result => {
      if (!result.isConfirmed || !this.terminal) return;

      this.atmApi.restore(this.terminal.id).subscribe({
        next: () => {
          Swal.fire({
            icon: 'success',
            title: 'Restored',
            text: 'Terminal restored successfully.',
            timer: 1400,
            showConfirmButton: false
          });

          if (this.id) {
            this.loadTerminal(this.id);
          }
        },
        error: err => {
          console.error(err);
          Swal.fire('Error', err?.error?.message || 'Failed to restore terminal.', 'error');
        }
      });
    });
  }

  can(permissionCode: string): boolean {
    return this.accessControl.hasPermission(permissionCode);
  }
  
  onAddCashBin(): void {
    this.addCashBin();
  }

  onReplenish(): void {
    this.replenish();
  }

  onReconcile(): void {
    this.reconcile();
  }

  get totalCashAmount(): number {
    return this.cashBins.reduce((sum, item) => sum + Number(item.currentAmount || 0), 0);
  }

  get latestReplenishment(): ReplenishmentResponse | null {
    return this.replenishments.length ? this.replenishments[0] : null;
  }

  get latestReconciliation(): ReconciliationResponse | null {
    return this.reconciliations.length ? this.reconciliations[0] : null;
  }

  private safe(value: any): string {
    return String(value ?? '')
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

}
