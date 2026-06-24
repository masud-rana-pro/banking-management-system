import { Component, HostListener, OnDestroy } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { RouteFormModalService } from 'src/app/core/services/route-form-modal.service';

@Component({
  selector: 'app-route-form-modal',
  templateUrl: './route-form-modal.component.html',
  styleUrls: ['./route-form-modal.component.scss']
})
export class RouteFormModalComponent implements OnDestroy {
  state$ = this.modalService.state$;
  frameLoaded = false;
  frameUrl?: SafeResourceUrl;
  private open = false;
  private stateSub: Subscription;

  constructor(
    private readonly modalService: RouteFormModalService,
    private readonly sanitizer: DomSanitizer
  ) {
    this.stateSub = this.modalService.state$.subscribe(state => {
      this.open = state.open;
      document.body.classList.toggle('route-form-open', state.open);
      if (state.open) {
        this.frameLoaded = false;
        this.frameUrl = this.sanitizer.bypassSecurityTrustResourceUrl(state.url);
      } else {
        this.frameUrl = undefined;
      }
    });
  }

  ngOnDestroy(): void {
    this.stateSub.unsubscribe();
    document.body.classList.remove('route-form-open');
  }

  close(): void {
    this.modalService.close(false);
  }

  @HostListener('window:message', ['$event'])
  onHostMessage(event: MessageEvent): void {
    if (!this.open || event.origin !== window.location.origin || event.data?.type !== 'sbms-route-form-done') {
      return;
    }

    this.modalService.close(true);
  }

  @HostListener('window:keydown.escape')
  onEscape(): void {
    if (this.open) {
      this.close();
    }
  }

  onFrameLoad(): void {
    this.frameLoaded = true;
  }
}
