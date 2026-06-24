import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  templateUrl: './empty-state.component.html',
  styleUrls: ['./empty-state.component.scss']
})
export class EmptyStateComponent {
  @Input() title = 'No data found';
  @Input() message = 'There is nothing to display here yet.';
  @Input() icon = 'inbox'; // Bootstrap Icons name without 'bi-'
}
