import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import Swal from 'sweetalert2';

import { BranchApiService } from '../services/branch-api.service';
import { VaultBalanceApiService } from '../services/vault-balance-api.service';
import { BranchCashLedgerApiService } from '../services/branch-cash-ledger-api.service';
import { BranchResponse } from '../models/branch.model';
import { VaultBalanceResponse } from '../models/vault-balance.model';
import { BranchCashLedgerResponse } from '../models/branch-cash-ledger.model';

interface EodRow {
  branchId: number;
  branchCode: string;
  branchName: string;
  openingBalance: number;
  totalCashIn: number;
  totalCashOut: number;
  closingBalance: number;
  vaultClosed: boolean;
  ledgerEntries: number;
}

@Component({
  selector: 'app-eod-summary',
  templateUrl: './eod-summary.component.html',
  styleUrls: ['./eod-summary.component.scss']
})
export class EodSummaryComponent implements OnInit {

  loading = false;
  branches: BranchResponse[] = [];
  vaults: VaultBalanceResponse[] = [];
  ledgers: BranchCashLedgerResponse[] = [];

  filters = {
    branchId: '',
    date: new Date().toISOString().slice(0, 10)
  };

  rows: EodRow[] = [];

  constructor(
    private branchApi: BranchApiService,
    private vaultApi: VaultBalanceApiService,
    private ledgerApi: BranchCashLedgerApiService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    forkJoin({
      branches: this.branchApi.getAll('', ''),
      vaults: this.vaultApi.getAll(null, '', null),
      ledgers: this.ledgerApi.getAll(null, '', '')
    }).subscribe({
      next: ({ branches, vaults, ledgers }) => {
        this.branches = branches || [];
        this.vaults = vaults || [];
        this.ledgers = ledgers || [];
        this.buildRows();
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load EOD summary.', 'error');
      }
    });
  }

  onSearch(): void {
    this.buildRows();
  }

  onReset(): void {
    this.filters = {
      branchId: '',
      date: new Date().toISOString().slice(0, 10)
    };
    this.buildRows();
  }

  private buildRows(): void {
    const selectedDate = this.filters.date;
    const selectedBranchId = this.filters.branchId ? Number(this.filters.branchId) : null;

    this.rows = this.branches
      .filter(branch => !selectedBranchId || branch.id === selectedBranchId)
      .map(branch => {
        const vault = this.vaults.find(item => item.branchId === branch.id && item.balanceDate === selectedDate);
        const ledgerItems = this.ledgers.filter(item => item.branchId === branch.id && item.ledgerDate === selectedDate);
        return {
          branchId: branch.id,
          branchCode: branch.branchCode,
          branchName: branch.branchName,
          openingBalance: Number(vault?.openingBalance || 0),
          totalCashIn: Number(vault?.totalCashIn || 0),
          totalCashOut: Number(vault?.totalCashOut || 0),
          closingBalance: Number(vault?.closingBalance || 0),
          vaultClosed: !!vault?.isClosed,
          ledgerEntries: ledgerItems.length
        };
      });
  }

  get totalOpeningBalance(): number {
    return this.rows.reduce((sum, item) => sum + item.openingBalance, 0);
  }

  get totalClosingBalance(): number {
    return this.rows.reduce((sum, item) => sum + item.closingBalance, 0);
  }

  get totalCashIn(): number {
    return this.rows.reduce((sum, item) => sum + item.totalCashIn, 0);
  }

  get totalCashOut(): number {
    return this.rows.reduce((sum, item) => sum + item.totalCashOut, 0);
  }
}
