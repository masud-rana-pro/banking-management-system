import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  CustomerAddressRequest,
  CustomerAddressResponse,
  CustomerDashboardSummaryResponse,
  CustomerDropdownResponse,
  CustomerIdentityRequest,
  CustomerIdentityResponse,
  CustomerRequest,
  CustomerResponse,
  CustomerTimelineResponse
} from '../models/customer.model';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private readonly baseUrl = `${environment.apiBaseUrl}/customers`;

  constructor(private http: HttpClient) {}

  create(request: CustomerRequest): Observable<CustomerResponse> {
    return this.http
      .post<ApiResponse<CustomerResponse>>(`${this.baseUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getAll(): Observable<CustomerResponse[]> {
    return this.http
      .get<ApiResponse<CustomerResponse[]>>(`${this.baseUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getById(id: number): Observable<CustomerResponse> {
    return this.http
      .get<ApiResponse<CustomerResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  update(id: number, request: CustomerRequest): Observable<CustomerResponse> {
    return this.http
      .put<ApiResponse<CustomerResponse>>(`${this.baseUrl}/${id}`, request)
      .pipe(map(res => res.data));
  }

  archive(id: number): Observable<CustomerResponse> {
    return this.http
      .delete<ApiResponse<CustomerResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restore(id: number): Observable<CustomerResponse> {
    return this.http
      .put<ApiResponse<CustomerResponse>>(`${this.baseUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  activate(id: number): Observable<CustomerResponse> {
    return this.http
      .post<ApiResponse<CustomerResponse>>(`${this.baseUrl}/${id}/activate`, {})
      .pipe(map(res => res.data));
  }

  block(id: number): Observable<CustomerResponse> {
    return this.http
      .post<ApiResponse<CustomerResponse>>(`${this.baseUrl}/${id}/block`, {})
      .pipe(map(res => res.data));
  }

  dropdown(keyword = ''): Observable<CustomerDropdownResponse[]> {
    let params = new HttpParams();
    if (keyword.trim()) {
      params = params.set('keyword', keyword.trim());
    }

    return this.http
      .get<ApiResponse<CustomerDropdownResponse[]>>(`${this.baseUrl}/dropdown`, { params })
      .pipe(map(res => res.data || []));
  }

  search(keyword = ''): Observable<CustomerResponse[]> {
    let params = new HttpParams();
    if (keyword.trim()) {
      params = params.set('keyword', keyword.trim());
    }

    return this.http
      .get<ApiResponse<CustomerResponse[]>>(`${this.baseUrl}/search`, { params })
      .pipe(map(res => res.data || []));
  }

  getDashboardSummary(): Observable<CustomerDashboardSummaryResponse> {
    return this.http
      .get<ApiResponse<CustomerDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }

  getTimeline(id: number): Observable<CustomerTimelineResponse[]> {
    return this.http
      .get<ApiResponse<CustomerTimelineResponse[]>>(`${this.baseUrl}/${id}/timeline`)
      .pipe(map(res => res.data || []));
  }

  createAddress(request: CustomerAddressRequest): Observable<CustomerAddressResponse> {
    return this.http
      .post<ApiResponse<CustomerAddressResponse>>(`${this.baseUrl}/address/create`, request)
      .pipe(map(res => res.data));
  }

  updateAddress(id: number, request: CustomerAddressRequest): Observable<CustomerAddressResponse> {
    return this.http
      .put<ApiResponse<CustomerAddressResponse>>(`${this.baseUrl}/address/${id}`, request)
      .pipe(map(res => res.data));
  }

  getAddressById(id: number): Observable<CustomerAddressResponse> {
    return this.http
      .get<ApiResponse<CustomerAddressResponse>>(`${this.baseUrl}/address/${id}`)
      .pipe(map(res => res.data));
  }

  getAddressesByCustomer(customerId: number): Observable<CustomerAddressResponse[]> {
    return this.http
      .get<ApiResponse<CustomerAddressResponse[]>>(`${this.baseUrl}/${customerId}/addresses`)
      .pipe(map(res => res.data || []));
  }

  createIdentity(request: CustomerIdentityRequest): Observable<CustomerIdentityResponse> {
    return this.http
      .post<ApiResponse<CustomerIdentityResponse>>(`${this.baseUrl}/identity/create`, request)
      .pipe(map(res => res.data));
  }

  updateIdentity(id: number, request: CustomerIdentityRequest): Observable<CustomerIdentityResponse> {
    return this.http
      .put<ApiResponse<CustomerIdentityResponse>>(`${this.baseUrl}/identity/${id}`, request)
      .pipe(map(res => res.data));
  }

  getIdentityById(id: number): Observable<CustomerIdentityResponse> {
    return this.http
      .get<ApiResponse<CustomerIdentityResponse>>(`${this.baseUrl}/identity/${id}`)
      .pipe(map(res => res.data));
  }

  getIdentitiesByCustomer(customerId: number): Observable<CustomerIdentityResponse[]> {
    return this.http
      .get<ApiResponse<CustomerIdentityResponse[]>>(`${this.baseUrl}/${customerId}/identities`)
      .pipe(map(res => res.data || []));
  }
}
