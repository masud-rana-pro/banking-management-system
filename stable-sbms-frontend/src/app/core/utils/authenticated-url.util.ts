const SESSION_STORAGE_KEY = 'sbms_access_session_runtime';
const LOCAL_STORAGE_KEY = 'sbms_access_session';

interface StoredAccessSession {
  token?: string;
}

export function withAccessToken(url: string): string {
  const token = readAccessToken();
  if (!token) {
    return url;
  }

  const separator = url.includes('?') ? '&' : '?';
  return `${url}${separator}access_token=${encodeURIComponent(token)}`;
}

function readAccessToken(): string {
  try {
    const raw = sessionStorage.getItem(SESSION_STORAGE_KEY) || localStorage.getItem(LOCAL_STORAGE_KEY);
    if (!raw) {
      return '';
    }
    const session = JSON.parse(raw) as StoredAccessSession;
    return session?.token || '';
  } catch {
    return '';
  }
}