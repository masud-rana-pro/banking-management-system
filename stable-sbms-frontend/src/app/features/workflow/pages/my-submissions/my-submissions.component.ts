import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { TableExportService } from 'src/app/core/services/table-export.service';
import { WorkflowHistoryResponse, formatEnumLabel, resolveWorkflowSourceRoute } from '../../models/workflow.model';
import { WorkflowService } from '../../services/workflow.service';

@Component({
  selector: 'app-my-submissions',
  templateUrl: './my-submissions.component.html',
  styleUrls: ['./my-submissions.component.scss']
})
export class MySubmissionsComponent implements OnInit {

  loading = false;
  actor = 'SYSTEM';
  items: WorkflowHistoryResponse[] = [];

  constructor(
    private workflowService: WorkflowService,
    private route: ActivatedRoute,
    private router: Router,
    private tableExport: TableExportService
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.actor = params.get('actor') || 'SYSTEM';
      this.load();
    });
  }

  load(): void {
    this.loading = true;
    this.workflowService.getMySubmissions(this.actor).subscribe({
      next: data => {
        this.items = data || [];
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load my submissions.', 'error');
      }
    });
  }

  refresh(): void {
    this.router.navigate([], { queryParams: { actor: this.actor } });
  }

  openView(item: WorkflowHistoryResponse): void {
    this.router.navigate(['/workflow/history', item.id]);
  }

  openSource(item: WorkflowHistoryResponse): void {
    this.router.navigateByUrl(resolveWorkflowSourceRoute(item));
  }

  print(): void {
    if (!this.items.length) {
      Swal.fire('No data', 'No submission data to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'My Workflow Submissions',
      `Actor: ${this.actor}`,
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
      Swal.fire('No data', 'No submission data to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'My Submissions',
      'workflow-my-submissions',
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
