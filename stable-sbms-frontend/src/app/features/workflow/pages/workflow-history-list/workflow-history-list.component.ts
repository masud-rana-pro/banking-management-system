import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

import { TableExportService } from 'src/app/core/services/table-export.service';
import { WorkflowHistoryResponse, formatEnumLabel, resolveWorkflowSourceRoute } from '../../models/workflow.model';
import { WorkflowService } from '../../services/workflow.service';

@Component({
  selector: 'app-workflow-history-list',
  templateUrl: './workflow-history-list.component.html',
  styleUrls: ['./workflow-history-list.component.scss']
})
export class WorkflowHistoryListComponent implements OnInit {

  loading = false;
  allItems: WorkflowHistoryResponse[] = [];
  items: WorkflowHistoryResponse[] = [];
  page = 1;
  pageSize = 10;
  total = 0;
  filters = {
    keyword: '',
    moduleName: ''
  };

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
    this.workflowService.getHistory().subscribe({
      next: data => {
        this.allItems = data || [];
        this.page = 1;
        this.applyFilters();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load workflow history.', 'error');
      }
    });
  }

  applyFilters(): void {
    const keyword = this.filters.keyword.trim().toLowerCase();
    const filtered = this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.moduleName.toLowerCase().includes(keyword)
        || item.actionName.toLowerCase().includes(keyword)
        || item.actionBy.toLowerCase().includes(keyword)
        || (item.remarks || '').toLowerCase().includes(keyword);
      const matchesModule = !this.filters.moduleName || item.moduleName === this.filters.moduleName;
      return matchesKeyword && matchesModule;
    });
    this.total = filtered.length;
    const start = (this.page - 1) * this.pageSize;
    this.items = filtered.slice(start, start + this.pageSize);
  }

  onSearch(): void {
    this.page = 1;
    this.applyFilters();
  }

  onReset(): void {
    this.filters = { keyword: '', moduleName: '' };
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.applyFilters();
  }

  openView(item: WorkflowHistoryResponse): void {
    this.router.navigate(['/workflow/history', item.id]);
  }

  openSource(item: WorkflowHistoryResponse): void {
    this.router.navigateByUrl(this.resolveSourceRoute(item));
  }

  onPrint(): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No workflow history to print.', 'warning');
      return;
    }
    this.tableExport.printTableDocument(
      'Workflow History',
      'Al-Barakah Shariah Banking Management System',
      ['Module', 'Action', 'Actor', 'Status', 'Remarks'],
      data.map(item => [
        item.moduleName,
        item.actionName,
        item.actionBy,
        this.getLabel(item.status),
        item.remarks || ''
      ])
    );
  }

  onExport(type: string): void {
    const data = this.getExportableData();
    if (!data.length) {
      Swal.fire('No data', 'No workflow history to export.', 'warning');
      return;
    }
    this.tableExport.exportTable(
      'Workflow History',
      'workflow-history-list',
      ['Module', 'Action', 'Actor', 'Status', 'Remarks'],
      data.map(item => [
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

  resolveSourceRoute(item: WorkflowHistoryResponse): string {
    return resolveWorkflowSourceRoute(item);
  }

  private getExportableData(): WorkflowHistoryResponse[] {
    const keyword = this.filters.keyword.trim().toLowerCase();
    return this.allItems.filter(item => {
      const matchesKeyword = !keyword
        || item.moduleName.toLowerCase().includes(keyword)
        || item.actionName.toLowerCase().includes(keyword)
        || item.actionBy.toLowerCase().includes(keyword)
        || (item.remarks || '').toLowerCase().includes(keyword);
      const matchesModule = !this.filters.moduleName || item.moduleName === this.filters.moduleName;
      return matchesKeyword && matchesModule;
    });
  }
}
