import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface UserDto {
  id: number;
  username: string;
  roleName: string;
  status: string;
}

export interface UserListResponse {
  items: UserDto[];
  total: number;
}

@Injectable({ providedIn: 'root' })
export class UserService {

  private readonly baseUrl = `${environment.apiBaseUrl}/users`;

  constructor(private http: HttpClient) {}

  getUsers(
    page: number,
    size: number,
    filters: any
  ): Observable<UserListResponse> {

    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (filters.search) params = params.set('search', filters.search);
    if (filters.role) params = params.set('role', filters.role);
    if (filters.status) params = params.set('status', filters.status);

    return this.http.get<UserListResponse>(this.baseUrl, { params });
  }
}