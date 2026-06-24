import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnChanges {

  @Input() page     = 1;
  @Input() pageSize = 10;
  @Input() total    = 0;

  @Output() pageChange = new EventEmitter<number>();

  totalPages = 0;
  pages: number[] = [];

  ngOnChanges(_changes: SimpleChanges): void {
    this.totalPages = Math.ceil(this.total / this.pageSize) || 0;
    this.buildPages();
  }

  buildPages(): void {
    const windowSize = 5;
    const start = Math.max(1, this.page - Math.floor(windowSize / 2));
    const end   = Math.min(this.totalPages, start + windowSize - 1);
    this.pages  = [];
    for (let i = start; i <= end; i++) {
      this.pages.push(i);
    }
  }

  goTo(p: number): void {
    if (p < 1 || p > this.totalPages || p === this.page) return;
    this.pageChange.emit(p);
  }
}
