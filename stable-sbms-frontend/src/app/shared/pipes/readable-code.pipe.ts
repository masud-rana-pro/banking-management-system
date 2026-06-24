import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'readableCode'
})
export class ReadableCodePipe implements PipeTransform {

  transform(value?: string | null): string {
    if (!value) return '-';
    return value
      .replace(/[_-]+/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, char => char.toUpperCase());
  }
}
