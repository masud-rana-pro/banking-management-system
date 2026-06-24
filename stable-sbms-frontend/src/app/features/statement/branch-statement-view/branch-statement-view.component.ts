import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { BranchStatementRequestResponse, formatEnumLabel } from '../models/statement.model';
import { StatementService } from '../services/statement.service';

@Component({
  selector: 'app-branch-statement-view',
  templateUrl: './branch-statement-view.component.html',
  styleUrls: ['./branch-statement-view.component.scss']
})
export class BranchStatementViewComponent implements OnInit {

  id: number | null = null;
  item: BranchStatementRequestResponse | null = null;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private statementApi: StatementService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) this.load(this.id);
  }

  load(id: number): void {
    this.loading = true;
    this.statementApi.getBranchStatementById(id).subscribe({
      next: item => {
        this.item = item;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load branch statement view.', 'error');
      }
    });
  }

  download(): void {
    if (!this.id || !this.item) return;
    this.statementApi.downloadBranchStatement(this.id).subscribe({
      next: response => {
        const blob = response.body;
        if (!blob) return;
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = this.resolveResponseFileName(response, this.item?.generatedFile?.originalFileName || `${this.item?.requestNo}.pdf`);
        link.click();
        URL.revokeObjectURL(url);
        if (this.id) this.load(this.id);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to download branch statement.', 'error')
    });
  }

  preview(): void {
    if (!this.id) return;
    this.statementApi.previewBranchStatement(this.id).subscribe({
      next: response => {
        const blob = response.body;
        if (!blob) return;
        const url = URL.createObjectURL(blob);
        window.open(url, '_blank');
        setTimeout(() => URL.revokeObjectURL(url), 60000);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to preview branch statement.', 'error')
    });
  }

  print(): void {
    if (!this.id) {
      window.print();
      return;
    }
    this.statementApi.previewBranchStatement(this.id).subscribe({
      next: response => {
        const blob = response.body;
        if (!blob) return;
        const url = URL.createObjectURL(blob);
        const printWindow = window.open(url, '_blank');
        if (printWindow) {
          setTimeout(() => {
            printWindow.focus();
            printWindow.print();
          }, 700);
        }
        setTimeout(() => URL.revokeObjectURL(url), 60000);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to open printable branch statement.', 'error')
    });
  }

  openBranch(): void {
    if (!this.item) return;
    this.router.navigate(['/branches', this.item.branchId]);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  private resolveResponseFileName(response: any, fallback: string): string {
    const disposition = response?.headers?.get?.('content-disposition') || '';
    const match = /filename="?([^";]+)"?/i.exec(disposition);
    return match?.[1] || fallback;
  }
}
