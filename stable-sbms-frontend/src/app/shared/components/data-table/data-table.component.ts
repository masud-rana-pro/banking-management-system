import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-data-table',
  templateUrl: './data-table.component.html',
  styleUrls: ['./data-table.component.scss']
})
export class DataTableComponent {
  @Input() loading = false;
  @Input() empty = false;
  @Input() emptyTitle = 'No data found';
  @Input() emptyMessage = 'There is nothing to display here yet.';
}
