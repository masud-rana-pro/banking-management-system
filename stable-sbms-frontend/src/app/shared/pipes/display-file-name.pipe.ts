import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'displayFileName'
})
export class DisplayFileNamePipe implements PipeTransform {

  transform(value?: string | null): string {
    if (!value) {
      return '-';
    }

    const fileName = decodeURIComponent(value).split(/[\\/]/).pop() || value;
    return fileName.replace(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}_/i, '');
  }
}
