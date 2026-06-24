import { Pipe, PipeTransform } from '@angular/core';

type SupportedCurrency = 'BDT' | 'USD';

@Pipe({
  name: 'sbmsCurrency'
})
export class SbmsCurrencyPipe implements PipeTransform {

  transform(
    value?: number | string | null,
    currency: SupportedCurrency | string = 'BDT',
    digits = '1.2-2'
  ): string {
    const amount = Number(value ?? 0);
    const normalizedCurrency = `${currency || 'BDT'}`.toUpperCase();
    const symbol = normalizedCurrency === 'USD' ? '$' : 'Tk';

    return `${symbol} ${this.formatNumber(Number.isFinite(amount) ? amount : 0, digits)}`;
  }

  private formatNumber(value: number, digits: string): string {
    const match = /^1\.(\d+)-(\d+)$/.exec(digits);
    const minimumFractionDigits = match ? Number(match[1]) : 2;
    const maximumFractionDigits = match ? Number(match[2]) : 2;

    return new Intl.NumberFormat('en-US', {
      minimumFractionDigits,
      maximumFractionDigits
    }).format(value);
  }
}
