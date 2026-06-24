import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface FileUploadResult {
  fileName: string;
  fileUrl: string;
  size: number;
}

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {

  private readonly baseUrl = `${environment.apiBaseUrl}/files`;
  private readonly maxUploadSizeBytes = 20 * 1024 * 1024;

  constructor(private http: HttpClient) {}

  uploadImage(file: File): Observable<FileUploadResult> {
    const validationError = this.validateUpload(file);
    if (validationError) {
      return throwError(() => ({ error: { message: validationError } }));
    }

    const formData = new FormData();
    formData.append('file', file);

    return this.http
      .post<ApiResponse<FileUploadResult>>(`${this.baseUrl}/upload-image`, formData)
      .pipe(map(res => res.data));
  }

  uploadDocument(file: File): Observable<FileUploadResult> {
    const validationError = this.validateUpload(file);
    if (validationError) {
      return throwError(() => ({ error: { message: validationError } }));
    }

    const formData = new FormData();
    formData.append('file', file);

    return this.http
      .post<ApiResponse<FileUploadResult>>(`${this.baseUrl}/upload-document`, formData)
      .pipe(map(res => res.data));
  }

  resolveImageUrl(fileName?: string | null): string {
    if (!fileName) {
      return '';
    }
    return `${this.baseUrl}/images/${encodeURIComponent(fileName)}`;
  }

  resolveDocumentUrl(fileName?: string | null): string {
    if (!fileName) {
      return '';
    }
    return `${this.baseUrl}/documents/${encodeURIComponent(fileName)}`;
  }

  isImageFile(fileName?: string | null): boolean {
    return /\.(jpg|jpeg|png|webp|gif)$/i.test(fileName || '');
  }

  isPdfFile(fileName?: string | null): boolean {
    return /\.pdf$/i.test(fileName || '');
  }

  private validateUpload(file: File): string | null {
    if (!file) {
      return 'Please select a file first.';
    }
    if (file.size > this.maxUploadSizeBytes) {
      return 'File size cannot exceed 20 MB.';
    }
    return null;
  }
}
