import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

import { FileUploadService } from 'src/app/core/services/file-upload.service';
import { UserResponse } from 'src/app/features/admin/users/model/user.model';
import { UserApiService } from 'src/app/features/admin/users/service/user-api.service';
import { InvestigationCaseResponse, formatEnumLabel } from '../../models/security.model';
import { SecurityService } from '../../services/security.service';

@Component({
  selector: 'app-investigation-case-view',
  templateUrl: './investigation-case-view.component.html',
  styleUrls: ['./investigation-case-view.component.scss']
})
export class InvestigationCaseViewComponent implements OnInit {

  loading = false;
  item: InvestigationCaseResponse | null = null;
  userImageMap: Record<string, string> = {};

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private securityService: SecurityService,
    private fileUploadService: FileUploadService,
    private userApi: UserApiService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) this.load(id);
    });
  }

  load(id: number): void {
    this.loading = true;
    this.securityService.getInvestigationCaseById(id).subscribe({
      next: data => {
        this.item = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
        Swal.fire('Error', 'Failed to load investigation case detail.', 'error');
      }
    });
  }

  openList(): void {
    this.router.navigate(['/security/investigation-cases']);
  }

  openAction(): void {
    if (this.item) {
      this.router.navigate(['/security/investigation-cases', this.item.id, 'action']);
    }
  }

  previewEvidence(): void {
    if (!this.item?.evidenceFileName) return;
    window.open(this.fileUploadService.resolveDocumentUrl(this.item.evidenceFileName), '_blank');
  }

  hasEvidence(): boolean {
    return !!this.item?.evidenceFileName;
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
