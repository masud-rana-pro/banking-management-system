import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ApiResponse } from '../models/branch.model';
import { BranchAssignmentRequest, BranchAssignmentResponse } from '../models/branch-assignment.model';

@Injectable({ providedIn: 'root' })
export class BranchAssignmentApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/branches/assignments`;

  constructor(private http: HttpClient) { }

  getAll(branchId: number | null = null, status: string = ''): Observable<BranchAssignmentResponse[]> {
    let params = new HttpParams();

    if (branchId !== null && branchId > 0) {
      params = params.set('branchId', String(branchId));
    }

    if (status && status.trim() !== '') {
      params = params.set('status', status);
    }

    return this.http
      .get<ApiResponse<BranchAssignmentResponse[]>>(`${this.baseUrl}/list`, { params })
      .pipe(map(res => res.data || []));
  }

  create(payload: BranchAssignmentRequest): Observable<BranchAssignmentResponse> {
    return this.http.post<ApiResponse<BranchAssignmentResponse>>(`${this.baseUrl}/create`, payload)
      .pipe(map(res => res.data));
  }

  update(id: number, payload: BranchAssignmentRequest): Observable<BranchAssignmentResponse> {
    return this.http.put<ApiResponse<BranchAssignmentResponse>>(`${this.baseUrl}/${id}`, payload)
      .pipe(map(res => res.data));
  }

  deactivate(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/${id}`);
  }
}
