import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type TableActionDisplayMode = 'menu' | 'inline';

@Injectable({ providedIn: 'root' })
export class TableActionModeService {
  private readonly storageKey = 'sbms-table-action-mode';
  private readonly modeSubject = new BehaviorSubject<TableActionDisplayMode>(this.readInitialMode());

  readonly mode$ = this.modeSubject.asObservable();

  get mode(): TableActionDisplayMode {
    return this.modeSubject.value;
  }

  setMode(mode: TableActionDisplayMode): void {
    localStorage.setItem(this.storageKey, mode);
    this.modeSubject.next(mode);
  }

  toggle(): void {
    this.setMode(this.mode === 'menu' ? 'inline' : 'menu');
  }

  private readInitialMode(): TableActionDisplayMode {
    const stored = localStorage.getItem(this.storageKey);
    return stored === 'inline' ? 'inline' : 'menu';
  }
}
