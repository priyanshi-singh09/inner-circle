const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

async function request(path, options = {}) {
  const token = localStorage.getItem('innerCircleToken');
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers || {}),
    },
  });

  if (!response.ok) {
    let message = 'Something went wrong.';
    try {
      const body = await response.json();
      message = body.message || body.error || message;
    } catch {
      // Keep the generic message when the response has no JSON body.
    }
    throw new Error(message);
  }

  if (response.status === 204) return null;
  return response.json();
}

export function login(email, password) {
  return request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
}

export function register(payload) {
  return request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export function getMyProfile() {
  return request('/users/me');
}

export function getFeed(page = 0, size = 20) {
  return request(`/posts/feed?page=${page}&size=${size}`);
}

export function createPost(content, emotion, anonymous = false) {
  return request('/posts', {
    method: 'POST',
    body: JSON.stringify({ content, emotion, anonymous }),
  });
}
