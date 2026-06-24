import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { CARD_PIN_EVENT_OPTIONS, CardPinEventRequest, CardPinEventResponse, CardResponse, formatEnumLabel } from '../../models/card.model';
import { CardService } from '../../services/card.service';

@Component({
  selector: 'app-pin-event-list',
  templateUrl: './pin-event-list.component.html',
  styleUrls: ['./pin-event-list.component.scss']
})
export class PinEventListComponent implements OnInit {

  id: number | null = null;
  loading = false;
  item: CardResponse | null = null;
  pinEvents: CardPinEventResponse[] = [];
  readonly eventOptions = CARD_PIN_EVENT_OPTIONS;
  userImageMap: Record<string, string> = {};
  model: CardPinEventRequest = {
    eventType: 'PIN_GENERATED',
    performedBy: 'SYSTEM'
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private cardApi: CardService,
    private userApi: UserApiService,
    private fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    const idParam = this.route.snapshot.paramMap.get('id');
    this.id = idParam ? Number(idParam) : null;
    if (this.id) {
      this.load(this.id);
    }
  }

  load(id: number): void {
    this.loading = true;
    forkJoin({
      item: this.cardApi.getById(id),
      pinEvents: this.cardApi.getPinEvents(id).pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ item, pinEvents }) => {
        this.item = item;
        this.pinEvents = pinEvents;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load PIN event list.', 'error');
      }
    });
  }

  save(): void {
    if (!this.id || !this.model.eventType) return;
    this.cardApi.addPinEvent(this.id, this.model).subscribe({
      next: () => {
        Swal.fire('Success', 'PIN event recorded successfully.', 'success');
        if (this.id) this.load(this.id);
      },
      error: err => Swal.fire('Error', err?.error?.message || 'Failed to record PIN event.', 'error')
    });
  }

  openView(): void {
    if (!this.id) return;
    this.router.navigate(['/cards', this.id]);
  }

  openActivation(): void {
    if (!this.id) return;
    this.router.navigate(['/cards', this.id, 'activate']);
  }

  getLabel(value?: string | null): string {
    return formatEnumLabel(value);
  }

  getUserImageUrl(username?: string | null): string {
    const key = (username || '').trim().toLowerCase();
    return key ? this.userImageMap[key] || '' : '';
  }

  private loadUsers(): void {
    this.userApi.getAll().subscribe({
      next: users => {
        this.userImageMap = this.buildUserImageMap(users || []);
      },
      error: () => {
        this.userImageMap = {};
      }
    });
  }

  private buildUserImageMap(users: UserResponse[]): Record<string, string> {
    return users.reduce<Record<string, string>>((acc, user) => {
      if (user.username && user.profileImageName) {
        acc[user.username.trim().toLowerCase()] = this.fileUploadService.resolveImageUrl(user.profileImageName);
      }
      return acc;
    }, {});
  }
}
