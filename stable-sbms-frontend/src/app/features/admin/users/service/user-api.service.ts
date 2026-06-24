import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import {
  ApiResponse,
  BranchOption,
  UserCreateRequest,
  UserDashboardSummary,
  UserHistoryEntry,
  UserLockActionRequest,
  UserPasswordResetRequest,
  UserResponse,
  UserRoleAssignRequest,
  UserUpdateRequest
} from '../model/user.model';

@Injectable({ providedIn: 'root' })
export class UserApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/users`;
  private readonly branchUrl = `${environment.apiBaseUrl}/branches`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<UserDashboardSummary> {
    return this.http.get<ApiResponse<UserDashboardSummary>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getAll(filters?: { search?: string; status?: string; roleId?: number | null; branchId?: number | null }): Observable<UserResponse[]> {
    const params: Record<string, string | number> = {};
    if (filters?.search) params['search'] = filters.search;
    if (filters?.status) params['status'] = filters.status;
    if (filters?.roleId) params['roleId'] = filters.roleId;
    if (filters?.branchId) params['branchId'] = filters.branchId;
    return this.http.get<ApiResponse<UserResponse[]>>(`${this.baseUrl}/list`, { params })
      .pipe(map(res => res.data || []));
  }

  getDropdown(): Observable<UserResponse[]> {
    return this.http.get<ApiResponse<UserResponse[]>>(`${this.baseUrl}/dropdown`)
      .pipe(map(res => res.data || []));
  }

  search(keyword: string): Observable<UserResponse[]> {
    return this.http.get<ApiResponse<UserResponse[]>>(`${this.baseUrl}/search`, { params: { keyword } })
      .pipe(map(res => res.data || []));
  }

  getById(id: number): Observable<UserResponse> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  create(payload: UserCreateRequest): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.baseUrl}/create`, payload)
      .pipe(map(res => res.data));
  }

  update(id: number, payload: UserUpdateRequest): Observable<UserResponse> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}`, payload)
      .pipe(map(res => res.data));
  }

  deactivate(id: number): Observable<UserResponse> {
    return this.http.delete<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restore(id: number): Observable<UserResponse> {
    return this.http.put<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  lock(id: number, payload: UserLockActionRequest): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/lock`, payload)
      .pipe(map(res => res.data));
  }

  lockWithStepUp(id: number, payload: UserLockActionRequest, stepUpToken: string): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/lock`, payload, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  unlock(id: number, payload: UserLockActionRequest): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/unlock`, payload)
      .pipe(map(res => res.data));
  }

  unlockWithStepUp(id: number, payload: UserLockActionRequest, stepUpToken: string): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/unlock`, payload, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  resetPassword(id: number, payload: UserPasswordResetRequest): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/reset-password`, payload)
      .pipe(map(res => res.data));
  }

  resetPasswordWithStepUp(id: number, payload: UserPasswordResetRequest, stepUpToken: string): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/reset-password`, payload, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  assignRole(id: number, payload: UserRoleAssignRequest): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/assign-role`, payload)
      .pipe(map(res => res.data));
  }

  assignRoleWithStepUp(id: number, payload: UserRoleAssignRequest, stepUpToken: string): Observable<UserResponse> {
    return this.http.post<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/assign-role`, payload, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data));
  }

  getHistory(id: number): Observable<UserHistoryEntry[]> {
    return this.http.get<ApiResponse<UserHistoryEntry[]>>(`${this.baseUrl}/${id}/history`)
      .pipe(map(res => res.data || []));
  }

  getBranchDropdown(): Observable<BranchOption[]> {
    return this.http.get<ApiResponse<BranchOption[]>>(`${this.branchUrl}/dropdown`)
      .pipe(map(res => res.data || []));
  }
}
