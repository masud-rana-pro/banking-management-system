import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { environment } from 'src/environments/environment';

import { AccessControlService } from './access-control.service';
import { AuthService } from './auth.service';

export interface LiveUpdateItem {
  id: string;
  timestamp: string;
  category: string;
  title: string;
  message: string;
  severity: string;
  route?: string | null;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

interface NotificationLogFeedItem {
  id: number;
  eventCode?: string | null;
  eventName?: string | null;
  templateCode?: string | null;
  templateName?: string | null;
  recipientTo?: string | null;
  channelType?: string | null;
  deliveryStatus?: string | null;
  providerResponse?: string | null;
  retryCount?: number | null;
  sentAt?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class LiveUpdateService {

  private readonly notificationsSubject = new BehaviorSubject<LiveUpdateItem[]>([]);
  readonly notifications$ = this.notificationsSubject.asObservable();

  private readonly messagesSubject = new BehaviorSubject<LiveUpdateItem[]>([]);
  readonly messages$ = this.messagesSubject.asObservable();

  private readonly unreadCountSubject = new BehaviorSubject<number>(0);
  readonly unreadCount$ = this.unreadCountSubject.asObservable();

  private readonly unreadMessageCountSubject = new BehaviorSubject<number>(0);
  readonly unreadMessageCount$ = this.unreadMessageCountSubject.asObservable();

  private readonly onlineUsersSubject = new BehaviorSubject<string[]>([]);
  readonly onlineUsers$ = this.onlineUsersSubject.asObservable();

  private socket: WebSocket | null = null;
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  private notificationPollTimer: ReturnType<typeof setInterval> | null = null;
  private activeToken: string | null = null;
  private recentNotificationIds = new Set<string>();

  constructor(
    private http: HttpClient,
    private accessControl: AccessControlService,
    private authService: AuthService
  ) {
    this.accessControl.session$.subscribe(session => {
      const nextToken = session?.token || null;
      if (!nextToken) {
        this.activeToken = null;
        this.notificationsSubject.next([]);
        this.messagesSubject.next([]);
        this.unreadCountSubject.next(0);
        this.unreadMessageCountSubject.next(0);
        this.onlineUsersSubject.next([]);
        this.disconnect();
        this.stopNotificationPolling();
        return;
      }

      if (this.activeToken === nextToken && this.socket?.readyState === WebSocket.OPEN) {
        return;
      }

      this.activeToken = nextToken;
      this.loadOnlineUsers();
      this.loadRecentNotificationLogs(false);
      this.startNotificationPolling();
      this.connect(nextToken);
    });
  }

  isUserOnline(username?: string | null): boolean {
    if (!username) {
      return false;
    }
    const normalized = username.trim().toLowerCase();
    return this.onlineUsersSubject.value.some(item => item.trim().toLowerCase() === normalized);
  }

  markNotificationsSeen(): void {
    this.unreadCountSubject.next(0);
  }

  markMessagesSeen(): void {
    this.unreadMessageCountSubject.next(0);
  }

  private connect(token: string): void {
    this.disconnect();
    const socket = new WebSocket(this.buildWebSocketUrl(token));
    this.socket = socket;

    socket.onmessage = event => this.handleMessage(event.data);
    socket.onerror = () => socket.close();
    socket.onclose = () => {
      if (!this.activeToken) {
        return;
      }
      this.scheduleReconnect();
    };
  }

  private disconnect(): void {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    if (this.socket) {
      this.socket.onclose = null;
      this.socket.close();
      this.socket = null;
    }
  }

  private startNotificationPolling(): void {
    this.stopNotificationPolling();
    this.notificationPollTimer = setInterval(() => this.loadRecentNotificationLogs(true), 30000);
  }

  private stopNotificationPolling(): void {
    if (this.notificationPollTimer) {
      clearInterval(this.notificationPollTimer);
      this.notificationPollTimer = null;
    }
  }

  private scheduleReconnect(): void {
    if (this.reconnectTimer || !this.activeToken) {
      return;
    }
    this.reconnectTimer = setTimeout(() => {
      this.reconnectTimer = null;
      if (this.activeToken) {
        this.connect(this.activeToken);
      }
    }, 4000);
  }

  private handleMessage(payload: string): void {
    if (!payload || payload === 'PONG') {
      return;
    }

    try {
      const message = JSON.parse(payload) as LiveUpdateItem;
      if ((message.category || '').toUpperCase() === 'PRESENCE') {
        this.applyPresenceMessage(message);
        return;
      }
      if (this.isMessageCategory(message.category)) {
        const current = this.messagesSubject.value;
        const next = [message, ...current.filter(item => item.id !== message.id)].slice(0, 12);
        this.messagesSubject.next(next);
        this.unreadMessageCountSubject.next(this.unreadMessageCountSubject.value + 1);
        return;
      }

      const current = this.notificationsSubject.value;
      const next = [message, ...current.filter(item => item.id !== message.id)].slice(0, 12);
      this.notificationsSubject.next(next);
      this.unreadCountSubject.next(this.unreadCountSubject.value + 1);
    } catch {
      // Ignore malformed live payloads.
    }
  }

  private loadRecentNotificationLogs(countOnlyNewItems: boolean): void {
    this.http.get<ApiResponse<NotificationLogFeedItem[]>>(`${environment.apiBaseUrl}/notifications/logs/list`).subscribe({
      next: response => {
        const logs = (response.data || [])
          .slice()
          .sort((a, b) => this.timeValue(b) - this.timeValue(a))
          .slice(0, 8);

        const mapped = logs.map(log => this.notificationLogToLiveItem(log));
        const current = this.notificationsSubject.value;
        const merged = [
          ...mapped,
          ...current.filter(item => !mapped.some(next => next.id === item.id))
        ].slice(0, 12);

        const newItems = mapped.filter(item => !this.recentNotificationIds.has(item.id));
        this.notificationsSubject.next(merged);
        mapped.forEach(item => this.recentNotificationIds.add(item.id));

        if (countOnlyNewItems && newItems.length) {
          this.unreadCountSubject.next(this.unreadCountSubject.value + newItems.length);
        } else if (!countOnlyNewItems) {
          const attentionCount = mapped.filter(item => this.isAttentionSeverity(item.severity)).length;
          this.unreadCountSubject.next(attentionCount);
        }
      },
      error: () => {
        // Keep websocket-fed alerts if notification log lookup fails.
      }
    });
  }

  private notificationLogToLiveItem(log: NotificationLogFeedItem): LiveUpdateItem {
    const status = (log.deliveryStatus || 'PENDING').toUpperCase();
    const channel = (log.channelType || 'CHANNEL').toUpperCase();
    const recipient = log.recipientTo || 'recipient';
    const eventName = log.eventName || log.eventCode || 'Notification event';
    const templateName = log.templateName || log.templateCode || 'Template';

    return {
      id: `notification-log-${log.id}`,
      timestamp: log.sentAt || log.updatedAt || log.createdAt || new Date().toISOString(),
      category: 'NOTIFICATION',
      title: `${eventName} - ${this.formatStatus(status)}`,
      message: `${channel} alert for ${recipient} using ${templateName}`,
      severity: this.notificationSeverity(status),
      route: `/notifications/logs/${log.id}`
    };
  }

  private notificationSeverity(status: string): string {
    if (status === 'FAILED') return 'ERROR';
    if (status === 'RETRY_QUEUED') return 'WARNING';
    if (status === 'PENDING') return 'INFO';
    return 'SUCCESS';
  }

  private isAttentionSeverity(severity?: string | null): boolean {
    const normalized = (severity || '').toUpperCase();
    return normalized === 'ERROR' || normalized === 'WARNING' || normalized === 'INFO';
  }

  private formatStatus(status: string): string {
    return status.replace(/_/g, ' ');
  }

  private timeValue(log: NotificationLogFeedItem): number {
    return new Date(log.sentAt || log.updatedAt || log.createdAt || 0).getTime();
  }

  private buildWebSocketUrl(token: string): string {
    const apiUrl = new URL(environment.apiBaseUrl);
    apiUrl.protocol = apiUrl.protocol === 'https:' ? 'wss:' : 'ws:';
    apiUrl.pathname = '/ws/live';
    apiUrl.search = '';
    apiUrl.searchParams.set('token', token);
    return apiUrl.toString();
  }

  private loadOnlineUsers(): void {
    this.authService.getOnlineUsers().subscribe({
      next: usernames => this.onlineUsersSubject.next((usernames || []).filter(Boolean)),
      error: () => this.onlineUsersSubject.next([])
    });
  }

  private applyPresenceMessage(message: LiveUpdateItem): void {
    const username = (message.message || '').trim();
    if (!username) {
      return;
    }

    const current = new Set(this.onlineUsersSubject.value.map(item => item.trim().toLowerCase()));
    const originalMap = new Map(this.onlineUsersSubject.value.map(item => [item.trim().toLowerCase(), item]));
    const normalized = username.toLowerCase();
    const state = (message.title || '').trim().toUpperCase();

    if (state === 'ONLINE') {
      current.add(normalized);
      originalMap.set(normalized, username);
    } else if (state === 'OFFLINE') {
      current.delete(normalized);
      originalMap.delete(normalized);
    }

    this.onlineUsersSubject.next(Array.from(current).map(key => originalMap.get(key) || key));
  }

  private isMessageCategory(category?: string | null): boolean {
    const normalized = (category || '').trim().toUpperCase();
    return normalized === 'REPORT'
      || normalized === 'VERIFICATION'
      || normalized === 'MESSAGE'
      || normalized === 'CHAT';
  }
}
