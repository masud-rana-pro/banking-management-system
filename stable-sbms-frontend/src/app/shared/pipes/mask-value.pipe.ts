import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'maskValue'
})
export class MaskValuePipe implements PipeTransform {

  transform(value?: string | null, visibleStart = 2, visibleEnd = 2): string {
    if (!value) return '-';
    const normalized = `${value}`;
    if (normalized.length <= visibleStart + visibleEnd) return normalized;
    return `${normalized.slice(0, visibleStart)}${'*'.repeat(Math.max(2, normalized.length - (visibleStart + visibleEnd)))}${normalized.slice(-visibleEnd)}`;
  }
}
