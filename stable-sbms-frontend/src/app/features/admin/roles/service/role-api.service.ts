import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import {
  ApiResponse,
  RoleCreateRequest,
  RoleDashboardSummary,
  RolePermissionAssignRequest,
  RolePermissionResponse,
  RoleResponse
} from '../model/role.model';

@Injectable({ providedIn: 'root' })
export class RoleApiService {

  private readonly baseUrl = `${environment.apiBaseUrl}/roles`;

  constructor(private http: HttpClient) {}

  getDashboardSummary(): Observable<RoleDashboardSummary> {
    return this.http.get<ApiResponse<RoleDashboardSummary>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getAll(): Observable<RoleResponse[]> {
    return this.http.get<ApiResponse<RoleResponse[]>>(`${this.baseUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getDropdown(): Observable<RoleResponse[]> {
    return this.http.get<ApiResponse<RoleResponse[]>>(`${this.baseUrl}/dropdown`)
      .pipe(map(res => res.data || []));
  }

  getById(id: number): Observable<RoleResponse> {
    return this.http.get<ApiResponse<RoleResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  create(payload: RoleCreateRequest): Observable<RoleResponse> {
    return this.http.post<ApiResponse<RoleResponse>>(`${this.baseUrl}/create`, payload)
      .pipe(map(res => res.data));
  }

  update(id: number, payload: RoleCreateRequest): Observable<RoleResponse> {
    return this.http.put<ApiResponse<RoleResponse>>(`${this.baseUrl}/${id}`, payload)
      .pipe(map(res => res.data));
  }

  deactivate(id: number): Observable<RoleResponse> {
    return this.http.delete<ApiResponse<RoleResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restore(id: number): Observable<RoleResponse> {
    return this.http.put<ApiResponse<RoleResponse>>(`${this.baseUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  getPermissions(roleId: number): Observable<RolePermissionResponse[]> {
    return this.http.get<ApiResponse<RolePermissionResponse[]>>(`${this.baseUrl}/permissions/${roleId}`)
      .pipe(map(res => res.data || []));
  }

  mapPermissions(roleId: number, payload: RolePermissionAssignRequest): Observable<RolePermissionResponse[]> {
    return this.http.post<ApiResponse<RolePermissionResponse[]>>(`${this.baseUrl}/${roleId}/permissions/map`, payload)
      .pipe(map(res => res.data || []));
  }

  mapPermissionsWithStepUp(roleId: number, payload: RolePermissionAssignRequest, stepUpToken: string): Observable<RolePermissionResponse[]> {
    return this.http.post<ApiResponse<RolePermissionResponse[]>>(`${this.baseUrl}/${roleId}/permissions/map`, payload, {
      headers: new HttpHeaders({ 'X-Step-Up-Token': stepUpToken })
    }).pipe(map(res => res.data || []));
  }
}
