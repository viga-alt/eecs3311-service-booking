import { useState, useEffect } from 'react'
import { api } from '../api'

export default function AdminDashboard({ user }) {
  const [tab, setTab] = useState('status')
  const tabs = [
    { id: 'status', label: 'System Status' },
    { id: 'consultants', label: 'Pending Consultants' },
    { id: 'policies', label: 'System Policies' },
  ]

  return (
    <div>
      <div className="tabs">
        {tabs.map(t => (
          <button key={t.id} className={`tab ${tab === t.id ? 'active' : ''}`} onClick={() => setTab(t.id)}>
            {t.label}
          </button>
        ))}
      </div>
      {tab === 'status' && <SystemStatus />}
      {tab === 'consultants' && <PendingConsultants />}
      {tab === 'policies' && <SystemPolicies />}
    </div>
  )
}

function SystemStatus() {
  const [status, setStatus] = useState(null)
  useEffect(() => { api.getSystemStatus().then(setStatus).catch(() => {}) }, [])

  if (!status) return <p className="empty">Loading...</p>

  const stats = [
    { label: 'Total Users', value: status.totalUsers },
    { label: 'Total Bookings', value: status.totalBookings },
    { label: 'Total Services', value: status.totalServices },
    { label: 'Total Payments', value: status.totalPayments },
    { label: 'Pending Consultants', value: status.pendingConsultants },
  ]

  return (
    <div>
      <div className="stat-grid">
        {stats.map(s => (
          <div className="stat-card" key={s.label}>
            <div className="stat-value">{s.value}</div>
            <div className="stat-label">{s.label}</div>
          </div>
        ))}
      </div>
    </div>
  )
}

function PendingConsultants() {
  const [consultants, setConsultants] = useState([])
  const [msg, setMsg] = useState(null)

  const load = () => api.getPendingConsultants().then(setConsultants).catch(() => {})
  useEffect(() => { load() }, [])

  const approve = async (id) => {
    try {
      await api.approveConsultant(id)
      setMsg({ type: 'success', text: `Consultant #${id} approved` })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  const reject = async (id) => {
    try {
      await api.rejectConsultant(id)
      setMsg({ type: 'success', text: `Consultant #${id} rejected` })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  return (
    <div className="card">
      <h3>Pending Consultant Registrations</h3>
      {msg && <div className={msg.type === 'success' ? 'success-msg' : 'error-msg'}>{msg.text}</div>}
      {consultants.length === 0 ? <p className="empty">No pending registrations</p> : (
        <table>
          <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Specialization</th><th>Actions</th></tr></thead>
          <tbody>
            {consultants.map(c => (
              <tr key={c.id}>
                <td>#{c.id}</td>
                <td>{c.name}</td>
                <td>{c.email}</td>
                <td>{c.specialization}</td>
                <td className="actions">
                  <button className="btn btn-success btn-sm" onClick={() => approve(c.id)}>Approve</button>
                  <button className="btn btn-danger btn-sm" onClick={() => reject(c.id)}>Reject</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

function SystemPolicies() {
  const [policy, setPolicy] = useState(null)
  const [msg, setMsg] = useState(null)

  const load = () => api.getPolicy().then(setPolicy).catch(() => {})
  useEffect(() => { load() }, [])

  const setCancellation = async (type) => {
    try {
      await api.setCancellationPolicy(type)
      setMsg({ type: 'success', text: `Cancellation policy set to ${type}` })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  const setPayment = async (method) => {
    try {
      await api.setDefaultPaymentMethodPolicy(method)
      setMsg({ type: 'success', text: `Default payment method set to ${method}` })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  const setNotification = async (channel) => {
    try {
      await api.setNotificationChannel(channel)
      setMsg({ type: 'success', text: `Notification channel set to ${channel}` })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  if (!policy) return <p className="empty">Loading...</p>

  return (
    <div>
      {msg && <div className={msg.type === 'success' ? 'success-msg' : 'error-msg'}>{msg.text}</div>}
      <div className="policy-grid">
        <div className="card">
          <h3>Cancellation Policy</h3>
          <p style={{ marginBottom: 12, fontSize: 14, color: 'var(--gray-500)' }}>
            Current: <strong>{policy.cancellationPolicy}</strong>
          </p>
          <div className="actions">
            <button className={`btn btn-sm ${policy.cancellationPolicy === 'FLEXIBLE' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setCancellation('FLEXIBLE')}>Flexible</button>
            <button className={`btn btn-sm ${policy.cancellationPolicy === 'STRICT' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setCancellation('STRICT')}>Strict</button>
            <button className={`btn btn-sm ${policy.cancellationPolicy === 'NO_CANCELLATION' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setCancellation('NO_CANCELLATION')}>No Cancellation</button>
          </div>
        </div>
        <div className="card">
          <h3>Default Payment Method</h3>
          <p style={{ marginBottom: 12, fontSize: 14, color: 'var(--gray-500)' }}>
            Current: <strong>{policy.defaultPaymentMethod}</strong>
          </p>
          <div className="actions">
            <button className={`btn btn-sm ${policy.defaultPaymentMethod === 'CREDIT_CARD' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setPayment('CREDIT_CARD')}>Credit Card</button>
            <button className={`btn btn-sm ${policy.defaultPaymentMethod === 'DEBIT_CARD' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setPayment('DEBIT_CARD')}>Debit Card</button>
            <button className={`btn btn-sm ${policy.defaultPaymentMethod === 'PAYPAL' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setPayment('PAYPAL')}>PayPal</button>
            <button className={`btn btn-sm ${policy.defaultPaymentMethod === 'BANK_TRANSFER' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setPayment('BANK_TRANSFER')}>Bank Transfer</button>
          </div>
        </div>
        <div className="card">
          <h3>Notification Channel</h3>
          <p style={{ marginBottom: 12, fontSize: 14, color: 'var(--gray-500)' }}>
            Current: <strong>{policy.notificationSettings?.channel || 'BOTH'}</strong>
          </p>
          <div className="actions">
            <button className={`btn btn-sm ${(policy.notificationSettings?.channel || '') === 'EMAIL' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setNotification('EMAIL')}>Email</button>
            <button className={`btn btn-sm ${(policy.notificationSettings?.channel || '') === 'IN_APP' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setNotification('IN_APP')}>In-App</button>
            <button className={`btn btn-sm ${(policy.notificationSettings?.channel || '') === 'BOTH' ? 'btn-primary' : 'btn-outline'}`} onClick={() => setNotification('BOTH')}>Both</button>
          </div>
        </div>
      </div>
    </div>
  )
}
