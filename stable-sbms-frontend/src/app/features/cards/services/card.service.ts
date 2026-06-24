import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

import {
  ApiResponse,
  CardDashboardSummaryResponse,
  CardEventLogResponse,
  CardPinEventRequest,
  CardPinEventResponse,
  CardRequest,
  CardResponse,
  CardTransactionResponse,
  CardWorkflowActionRequest
} from '../models/card.model';

@Injectable({
  providedIn: 'root'
})
export class CardService {

  private readonly baseUrl = `${environment.apiBaseUrl}/cards`;

  constructor(private http: HttpClient) {}

  create(request: CardRequest): Observable<CardResponse> {
    return this.http.post<ApiResponse<CardResponse>>(`${this.baseUrl}/create`, request)
      .pipe(map(res => res.data));
  }

  getCards(): Observable<CardResponse[]> {
    return this.http.get<ApiResponse<CardResponse[]>>(`${this.baseUrl}/list`)
      .pipe(map(res => res.data || []));
  }

  getById(id: number): Observable<CardResponse> {
    return this.http.get<ApiResponse<CardResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  update(id: number, request: CardRequest): Observable<CardResponse> {
    return this.http.put<ApiResponse<CardResponse>>(`${this.baseUrl}/${id}`, request)
      .pipe(map(res => res.data));
  }

  archive(id: number): Observable<CardResponse> {
    return this.http.delete<ApiResponse<CardResponse>>(`${this.baseUrl}/${id}`)
      .pipe(map(res => res.data));
  }

  restore(id: number): Observable<CardResponse> {
    return this.http.put<ApiResponse<CardResponse>>(`${this.baseUrl}/${id}/restore`, {})
      .pipe(map(res => res.data));
  }

  activate(id: number, request: CardWorkflowActionRequest): Observable<CardResponse> {
    return this.http.post<ApiResponse<CardResponse>>(`${this.baseUrl}/${id}/activate`, request)
      .pipe(map(res => res.data));
  }

  block(id: number, request: CardWorkflowActionRequest): Observable<CardResponse> {
    return this.http.post<ApiResponse<CardResponse>>(`${this.baseUrl}/${id}/block`, request)
      .pipe(map(res => res.data));
  }

  unblock(id: number, request: CardWorkflowActionRequest): Observable<CardResponse> {
    return this.http.post<ApiResponse<CardResponse>>(`${this.baseUrl}/${id}/unblock`, request)
      .pipe(map(res => res.data));
  }

  replace(id: number, request: CardWorkflowActionRequest): Observable<CardResponse> {
    return this.http.post<ApiResponse<CardResponse>>(`${this.baseUrl}/${id}/replace`, request)
      .pipe(map(res => res.data));
  }

  renew(id: number, request: CardWorkflowActionRequest): Observable<CardResponse> {
    return this.http.post<ApiResponse<CardResponse>>(`${this.baseUrl}/${id}/renew`, request)
      .pipe(map(res => res.data));
  }

  getEvents(id: number): Observable<CardEventLogResponse[]> {
    return this.http.get<ApiResponse<CardEventLogResponse[]>>(`${this.baseUrl}/${id}/events`)
      .pipe(map(res => res.data || []));
  }

  getPinEvents(id: number): Observable<CardPinEventResponse[]> {
    return this.http.get<ApiResponse<CardPinEventResponse[]>>(`${this.baseUrl}/${id}/pin-events`)
      .pipe(map(res => res.data || []));
  }

  addPinEvent(id: number, request: CardPinEventRequest): Observable<CardPinEventResponse> {
    return this.http.post<ApiResponse<CardPinEventResponse>>(`${this.baseUrl}/${id}/pin-events`, request)
      .pipe(map(res => res.data));
  }

  getAtmCdmTransactions(): Observable<CardTransactionResponse[]> {
    return this.http.get<ApiResponse<CardTransactionResponse[]>>(`${this.baseUrl}/atm-cdm-transactions`)
      .pipe(map(res => res.data || []));
  }

  getDashboardSummary(): Observable<CardDashboardSummaryResponse> {
    return this.http.get<ApiResponse<CardDashboardSummaryResponse>>(`${this.baseUrl}/dashboard-summary`)
      .pipe(map(res => res.data));
  }
}
