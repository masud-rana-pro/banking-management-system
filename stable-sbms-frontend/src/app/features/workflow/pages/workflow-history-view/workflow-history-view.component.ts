import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { WorkflowHistoryResponse, formatEnumLabel, resolveWorkflowSourceRoute } from '../../models/workflow.model';
import { WorkflowService } from '../../services/workflow.service';

@Component({
  selector: 'app-workflow-history-view',
  templateUrl: './workflow-history-view.component.html',
  styleUrls: ['./workflow-history-view.component.scss']
})
export class WorkflowHistoryViewComponent implements OnInit {

  loading = false;
  item: WorkflowHistoryResponse | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private workflowService: WorkflowService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) this.load(id);
    });
  }

  load(id: number): void {
    this.loading = true;
    this.workflowService.getHistoryById(id).subscribe({
      next: data => {
        this.item = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load workflow history detail.', 'error');
      }
    });
  }

  openList(): void {
    this.router.navigate(['/workflow/history']);
  }

  openSource(): void {
    if (!this.item) return;
    this.router.navigateByUrl(resolveWorkflowSourceRoute(this.item));
  }

  print(): void {
    window.print();
  }

  exportJson(): void {
    if (!this.item) return;
    const blob = new Blob([JSON.stringify(this.item, null, 2)], { type: 'application/json;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = `workflow-history-${this.item.id}.json`;
    anchor.click();
    window.URL.revokeObjectURL(url);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }
}
