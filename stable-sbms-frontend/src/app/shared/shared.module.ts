import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';


import { PageTitleComponent } from './components/page-title/page-title.component';
import { StatusBadgeComponent } from './components/status-badge/status-badge.component';
import { DataTableComponent } from './components/data-table/data-table.component';
import { FilterBarComponent } from './components/filter-bar/filter-bar.component';
import { EmptyStateComponent } from './components/empty-state/empty-state.component';
import { LoadingSpinnerComponent } from './components/loading-spinner/loading-spinner.component';
import { TableActionsComponent } from './components/table-actions/table-actions.component';
import { PageHeaderComponent } from './components/page-header/page-header.component';
import { RouterModule } from '@angular/router';
import { FormFieldComponent } from './components/form-field/form-field.component';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { PaginationComponent } from './components/pagination/pagination.component';
import { RouteFormModalComponent } from './components/route-form-modal/route-form-modal.component';
import { TableActionMenuDirective } from './directives/table-action-menu.directive';
import { ReadableCodePipe } from './pipes/readable-code.pipe';
import { MaskValuePipe } from './pipes/mask-value.pipe';
import { SbmsCurrencyPipe } from './pipes/sbms-currency.pipe';
import { DisplayFileNamePipe } from './pipes/display-file-name.pipe';
import { TranslatePipe } from './pipes/translate.pipe';

@NgModule({
  declarations: [
    PageTitleComponent,
    StatusBadgeComponent,
    DataTableComponent,
    FilterBarComponent,
    EmptyStateComponent,
    LoadingSpinnerComponent,
    TableActionsComponent,
    PageHeaderComponent,
    FormFieldComponent,
    ConfirmDialogComponent,
    PaginationComponent,
    RouteFormModalComponent,
    TableActionMenuDirective,
    ReadableCodePipe,
    MaskValuePipe,
    SbmsCurrencyPipe,
    DisplayFileNamePipe,
    TranslatePipe
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,

    PageTitleComponent,
    StatusBadgeComponent,
    DataTableComponent,
    FilterBarComponent,
    EmptyStateComponent,
    LoadingSpinnerComponent,
    PageHeaderComponent,
    FormFieldComponent,
    ConfirmDialogComponent,
    TableActionsComponent,
    PaginationComponent,
    RouteFormModalComponent,
    TableActionMenuDirective,
    ReadableCodePipe,
    MaskValuePipe,
    SbmsCurrencyPipe,
    DisplayFileNamePipe,
    TranslatePipe
  ]
})
export class SharedModule {}
