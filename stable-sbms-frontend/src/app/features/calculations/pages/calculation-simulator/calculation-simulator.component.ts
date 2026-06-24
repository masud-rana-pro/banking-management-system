import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import {
  CALCULATION_FREQUENCY_OPTIONS,
  CALCULATION_PRODUCT_OPTIONS,
  CALCULATION_SOURCE_OPTIONS,
  CalculationSimulateRequest,
  CalculationSimulationResponse,
  formatEnumLabel
} from '../../models/calculation.model';
import { CalculationService } from '../../services/calculation.service';

@Component({
  selector: 'app-calculation-simulator',
  templateUrl: './calculation-simulator.component.html',
  styleUrls: ['./calculation-simulator.component.scss']
})
export class CalculationSimulatorComponent implements OnInit {

  calculating = false;
  result: CalculationSimulationResponse | null = null;

  form: CalculationSimulateRequest = {
    sourceModule: 'GENERAL',
    productType: 'MURABAHA',
    principalAmount: null,
    ratePercent: null,
    tenureMonths: 12,
    frequency: 'MONTHLY',
    startDate: new Date().toISOString().slice(0, 10),
    remarks: ''
  };

  context = {
    sourceName: '',
    returnRoute: '',
    productId: null as number | null,
    schemeId: null as number | null,
    scheduleId: null as number | null,
    accountId: null as number | null,
    accountTypeId: null as number | null,
    customerId: null as number | null,
    branchId: null as number | null
  };

  readonly sourceOptions = CALCULATION_SOURCE_OPTIONS;
  readonly productOptions = CALCULATION_PRODUCT_OPTIONS;
  readonly frequencyOptions = CALCULATION_FREQUENCY_OPTIONS;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private calculationService: CalculationService
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.form = {
        sourceModule: (params.get('sourceModule') || 'GENERAL') as CalculationSimulateRequest['sourceModule'],
        productType: params.get('productType') || 'MURABAHA',
        principalAmount: this.toNumber(params.get('principalAmount')),
        ratePercent: this.toNumber(params.get('ratePercent')),
        tenureMonths: this.toNumber(params.get('tenureMonths')) || 12,
        frequency: (params.get('frequency') || 'MONTHLY') as CalculationSimulateRequest['frequency'],
        startDate: params.get('startDate') || new Date().toISOString().slice(0, 10),
        remarks: params.get('remarks') || ''
      };
      this.context = {
        sourceName: params.get('sourceName') || '',
        returnRoute: params.get('returnRoute') || '',
        productId: this.toNumber(params.get('productId')),
        schemeId: this.toNumber(params.get('schemeId')),
        scheduleId: this.toNumber(params.get('scheduleId')),
        accountId: this.toNumber(params.get('accountId')),
        accountTypeId: this.toNumber(params.get('accountTypeId')),
        customerId: this.toNumber(params.get('customerId')),
        branchId: this.toNumber(params.get('branchId'))
      };
      this.result = null;
      if (this.canAutoCalculate()) {
        this.runCalculation(false);
      }
    });
  }

  back(): void {
    if (this.context.returnRoute) {
      this.router.navigateByUrl(this.context.returnRoute);
      return;
    }
    this.router.navigate(['/calculations/dashboard']);
  }

  print(): void {
    window.print();
  }

  runCalculation(showSuccess = true): void {
    if (!this.validateBeforeSubmit()) {
      return;
    }
    this.calculating = true;
    this.result = null;
    this.calculationService.simulate(this.form).subscribe({
      next: data => {
        this.result = data;
        this.calculating = false;
        if (showSuccess) {
          Swal.fire('Calculated', 'Simulation completed successfully.', 'success');
        }
      },
      error: err => {
        console.error(err);
        this.calculating = false;
        Swal.fire('Error', err?.error?.message || 'Failed to run calculation simulation.', 'error');
      }
    });
  }

  previewSchedule(): void {
    if (!this.result) {
      this.runCalculation(false);
      setTimeout(() => this.scrollToSchedule(), 300);
      return;
    }
    this.scrollToSchedule();
  }

  applyResult(): void {
    if (!this.result) {
      Swal.fire('Simulation required', 'Run the calculation first, then apply the result to a source module.', 'warning');
      return;
    }

    const remarks = `${this.result.formulaName} | Total profit ${this.result.totalProfit.toFixed(2)} | Total payable ${this.result.totalPayable.toFixed(2)}`;
    const sourceModule = String(this.form.sourceModule || 'GENERAL').toUpperCase();

    if (sourceModule === 'FINANCING') {
      this.router.navigate(['/financing/applications/new'], {
        queryParams: {
          productId: this.context.productId || undefined,
          customerId: this.context.customerId || undefined,
          branchId: this.context.branchId || undefined,
          requestedAmount: this.result.totalPrincipal,
          remarks
        }
      });
      return;
    }

    if (sourceModule === 'DEPOSIT_SCHEME') {
      this.router.navigate(['/deposit-schemes/enrollments/new'], {
        queryParams: {
          schemeId: this.context.schemeId || undefined,
          customerId: this.context.customerId || undefined,
          accountId: this.context.accountId || undefined,
          installmentAmount: this.result.principalAmount,
          remarks
        }
      });
      return;
    }

    if (sourceModule === 'PROFIT') {
      this.router.navigate(['/profit/postings/run'], {
        queryParams: {
          scheduleId: this.context.scheduleId || undefined,
          accountId: this.context.accountId || undefined,
          accountTypeId: this.context.accountTypeId || undefined
        }
      });
      return;
    }

    Swal.fire('Applied', 'This simulation is not bound to a source module record. Use the preview values manually.', 'info');
  }

  openDashboard(): void {
    this.router.navigate(['/calculations/dashboard']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  private validateBeforeSubmit(): boolean {
    if (!this.form.productType?.trim()) {
      Swal.fire('Missing product type', 'Select a product type before calculation.', 'warning');
      return false;
    }
    if (!this.form.principalAmount || this.form.principalAmount <= 0) {
      Swal.fire('Invalid amount', 'Principal, cost, installment or balance must be greater than zero.', 'warning');
      return false;
    }
    if (this.form.ratePercent === null || this.form.ratePercent < 0) {
      Swal.fire('Invalid rate', 'Rate percent must be zero or greater.', 'warning');
      return false;
    }
    if (!this.form.tenureMonths || this.form.tenureMonths <= 0) {
      Swal.fire('Invalid tenure', 'Tenure months must be greater than zero.', 'warning');
      return false;
    }
    if (!this.form.frequency) {
      Swal.fire('Missing frequency', 'Select a posting or repayment frequency.', 'warning');
      return false;
    }
    return true;
  }

  private canAutoCalculate(): boolean {
    return !!(
      this.form.productType
      && this.form.principalAmount
      && this.form.principalAmount > 0
      && this.form.tenureMonths
      && this.form.tenureMonths > 0
      && this.form.frequency
      && this.form.ratePercent !== null
    );
  }

  private scrollToSchedule(): void {
    document.getElementById('calculation-schedule')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  private toNumber(value: string | null): number | null {
    if (value === null || value.trim() === '') {
      return null;
    }
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
  }
}
