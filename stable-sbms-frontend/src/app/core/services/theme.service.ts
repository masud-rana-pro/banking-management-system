import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly storageKey = 'ibms-theme';
  private readonly themeSubject = new BehaviorSubject<'light' | 'dark'>('light');
  readonly currentTheme$ = this.themeSubject.asObservable();

  constructor() {
    this.initializeTheme();
  }

  initializeTheme(): void {
    const saved = (localStorage.getItem(this.storageKey) || 'light') as 'light' | 'dark';
    this.applyTheme(saved);
  }

  toggleTheme(): void {
    const current = this.themeSubject.value;
    const next = current === 'light' ? 'dark' : 'light';
    this.applyTheme(next);
    localStorage.setItem(this.storageKey, next);
  }

  applyTheme(theme: 'light' | 'dark' | string): void {
    const root = document.documentElement;
    if (theme === 'dark') {
      root.setAttribute('data-theme', 'dark');
      this.themeSubject.next('dark');
    } else {
      root.removeAttribute('data-theme');
      this.themeSubject.next('light');
    }
  }

  getCurrentTheme(): string {
    return this.themeSubject.value;
  }
}
