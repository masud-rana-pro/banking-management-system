import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-form-field',
  templateUrl: './form-field.component.html',
  styleUrls: ['./form-field.component.scss']
})
export class FormFieldComponent {
  /** Bootstrap Icons name without 'bi-' prefix. e.g. 'envelope', 'person', 'building' */
  @Input() icon = '';
  @Input() required = false;
}
