import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-table-actions',
  templateUrl: './table-actions.component.html',
  styleUrls: ['./table-actions.component.scss']
})
export class TableActionsComponent {
  @Output() view   = new EventEmitter<void>();
  @Output() edit   = new EventEmitter<void>();
  @Output() delete = new EventEmitter<void>();
}
