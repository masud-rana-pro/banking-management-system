import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-filter-bar',
  templateUrl: './filter-bar.component.html',
  styleUrls: ['./filter-bar.component.scss']
})
export class FilterBarComponent {
  @Input() title = 'Filter';
  @Input() subtitle = 'Refine records before viewing or exporting data.';
  @Input() searchLabel = 'View';
  @Input() resetLabel = 'Clear Filters';

  @Output() search = new EventEmitter<void>();
  @Output() reset = new EventEmitter<void>();

  onSearch(): void { this.search.emit(); }
  onReset(): void { this.reset.emit(); }
}