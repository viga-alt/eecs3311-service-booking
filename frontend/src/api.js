const BASE = '/api';

async function request(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  });
  const data = await res.json();
  if (!res.ok) throw new Error(data.error || 'Request failed');
  return data;
}

export const api = {
  login: (email, password) =>
    request('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),

  getServices: () => request('/services'),

  getApprovedConsultants: () => request('/consultants/approved'),
  getConsultantSlots: (id) => request(`/consultants/${id}/slots`),
  getConsultantServices: (id) => request(`/consultants/${id}/services`),
  addSlot: (id, data) =>
    request(`/consultants/${id}/slots`, { method: 'POST', body: JSON.stringify(data) }),

  createBooking: (data) =>
    request('/bookings', { method: 'POST', body: JSON.stringify(data) }),
  getClientBookings: (clientId) => request(`/bookings/client/${clientId}`),
  getPendingPaymentBookings: (clientId) => request(`/bookings/client/${clientId}/pending-payment`),
  cancelBooking: (bookingId, clientId) =>
    request(`/bookings/${bookingId}/client/${clientId}`, { method: 'DELETE' }),
  getConsultantBookings: (consultantId) => request(`/bookings/consultant/${consultantId}`),
  getPendingBookings: (consultantId) => request(`/bookings/consultant/${consultantId}/pending`),
  acceptBooking: (bookingId, consultantId) =>
    request(`/bookings/${bookingId}/accept`, { method: 'POST', body: JSON.stringify({ consultantId }) }),
  rejectBooking: (bookingId, consultantId) =>
    request(`/bookings/${bookingId}/reject`, { method: 'POST', body: JSON.stringify({ consultantId }) }),
  completeBooking: (bookingId, consultantId) =>
    request(`/bookings/${bookingId}/complete`, { method: 'POST', body: JSON.stringify({ consultantId }) }),

  processPayment: (data) =>
    request('/payments', { method: 'POST', body: JSON.stringify(data) }),
  getPaymentHistory: (clientId) => request(`/payments/client/${clientId}`),
  getPaymentMethods: (clientId) => request(`/payments/methods/client/${clientId}`),
  addPaymentMethod: (clientId, data) =>
    request(`/payments/methods/client/${clientId}`, { method: 'POST', body: JSON.stringify(data) }),
  removePaymentMethod: (methodId, clientId) =>
    request(`/payments/methods/${methodId}/client/${clientId}`, { method: 'DELETE' }),
  setDefaultPaymentMethod: (methodId, clientId) =>
    request(`/payments/methods/${methodId}/client/${clientId}/default`, { method: 'PUT' }),

  getPendingConsultants: () => request('/admin/consultants/pending'),
  approveConsultant: (id) =>
    request(`/admin/consultants/${id}/approve`, { method: 'POST' }),
  rejectConsultant: (id) =>
    request(`/admin/consultants/${id}/reject`, { method: 'POST' }),
  getPolicy: () => request('/admin/policy'),
  setCancellationPolicy: (policyType) =>
    request('/admin/policy/cancellation', { method: 'PUT', body: JSON.stringify({ policyType }) }),
  setDefaultPaymentMethodPolicy: (method) =>
    request('/admin/policy/payment-method', { method: 'PUT', body: JSON.stringify({ method }) }),
  setNotificationChannel: (channel) =>
    request('/admin/policy/notification', { method: 'PUT', body: JSON.stringify({ channel }) }),
  getSystemStatus: () => request('/admin/status'),

  chat: (message) =>
    request('/chat', { method: 'POST', body: JSON.stringify({ message }) }),
};
