import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { TableExportService } from 'src/app/core/services/table-export.service';
import { WorkflowHistoryResponse, formatEnumLabel, resolveWorkflowSourceRoute } from '../../models/workflow.model';
import { WorkflowService } from '../../services/workflow.service';

@Component({
  selector: 'app-pending-approval-queue',
  templateUrl: './pending-approval-queue.component.html',
  styleUrls: ['./pending-approval-queue.component.scss']
})
export class PendingApprovalQueueComponent implements OnInit {

  loading = false;
  items: WorkflowHistoryResponse[] = [];
  keyword = '';

  constructor(
    private workflowService: WorkflowService,
    private router: Router,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.workflowService.getPending(this.keyword).subscribe({
      next: data => {
        this.items = data || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load pending approval queue.', 'error');
      }
    });
  }

  onSearch(): void {
    this.load();
  }

  onReset(): void {
    this.keyword = '';
    this.load();
  }

  openView(item: WorkflowHistoryResponse): void {
    this.router.navigate(['/workflow/history', item.id]);
  }

  openSource(item: WorkflowHistoryResponse): void {
    this.router.navigateByUrl(resolveWorkflowSourceRoute(item));
  }

  print(): void {
    if (!this.items.length) {
      Swal.fire('No data', 'No pending approval data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Pending Approval Queue',
      'Al-Barakah Shariah Banking Management System',
      ['Module', 'Action', 'Submitted By', 'Status', 'Remarks'],
      this.items.map(item => [
        item.moduleName,
        item.actionName,
        item.actionBy,
        this.getLabel(item.status),
        item.remarks || ''
      ])
    );
  }

  onExport(type: string): void {
    if (!this.items.length) {
      Swal.fire('No data', 'No pending approval data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Pending Approvals',
      'workflow-pending-approval-queue',
      ['Module', 'Action', 'Submitted By', 'Status', 'Remarks'],
      this.items.map(item => [
        item.moduleName,
        item.actionName,
        item.actionBy,
        this.getLabel(item.status),
        item.remarks || ''
      ]),
      type as 'csv' | 'excel' | 'pdf'
    );
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
