import { ChangeDetectorRef, OnDestroy, Pipe, PipeTransform } from '@angular/core';
import { Subscription } from 'rxjs';
import { LanguageService } from 'src/app/core/services/language.service';

@Pipe({
  name: 'sbmsTranslate',
  pure: false
})
export class TranslatePipe implements PipeTransform, OnDestroy {
  private subscription: Subscription;

  constructor(
    private languageService: LanguageService,
    private changeDetectorRef: ChangeDetectorRef
  ) {
    this.subscription = this.languageService.language$.subscribe(() => {
      this.changeDetectorRef.markForCheck();
    });
  }

  transform(value?: string | null): string {
    return this.languageService.translate(value);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
