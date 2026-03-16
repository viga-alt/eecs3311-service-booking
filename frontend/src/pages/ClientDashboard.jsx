import { useState, useEffect } from 'react'
import { api } from '../api'
import ChatBot from '../components/ChatBot'

export default function ClientDashboard({ user }) {
  const [tab, setTab] = useState('services')
  const tabs = [
    { id: 'services', label: 'Browse Services' },
    { id: 'book', label: 'New Booking' },
    { id: 'bookings', label: 'My Bookings' },
    { id: 'payment', label: 'Process Payment' },
    { id: 'methods', label: 'Payment Methods' },
    { id: 'history', label: 'Payment History' },
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
      {tab === 'services' && <BrowseServices />}
      {tab === 'book' && <NewBooking user={user} />}
      {tab === 'bookings' && <MyBookings user={user} />}
      {tab === 'payment' && <ProcessPayment user={user} />}
      {tab === 'methods' && <PaymentMethods user={user} />}
      {tab === 'history' && <PaymentHistory user={user} />}
      <ChatBot />
    </div>
  )
}

function BrowseServices() {
  const [services, setServices] = useState([])
  useEffect(() => { api.getServices().then(setServices).catch(() => {}) }, [])

  return (
    <div className="card">
      <h3>Available Consulting Services</h3>
      {services.length === 0 ? <p className="empty">No services available</p> : (
        <table>
          <thead><tr><th>Name</th><th>Duration</th><th>Price</th><th>Description</th></tr></thead>
          <tbody>
            {services.map(s => (
              <tr key={s.id}>
                <td><strong>{s.name}</strong></td>
                <td>{s.durationMinutes} min</td>
                <td>${s.basePrice.toFixed(2)}</td>
                <td>{s.description}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

function NewBooking({ user }) {
  const [consultants, setConsultants] = useState([])
  const [selectedConsultant, setSelectedConsultant] = useState(null)
  const [slots, setSlots] = useState([])
  const [services, setServices] = useState([])
  const [selectedSlot, setSelectedSlot] = useState('')
  const [selectedService, setSelectedService] = useState('')
  const [msg, setMsg] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => { api.getApprovedConsultants().then(setConsultants).catch(() => {}) }, [])

  const selectConsultant = async (c) => {
    setSelectedConsultant(c)
    setSelectedSlot('')
    setSelectedService('')
    setMsg(null)
    try {
      const [s, sv] = await Promise.all([api.getConsultantSlots(c.id), api.getConsultantServices(c.id)])
      setSlots(s)
      setServices(sv)
    } catch { setSlots([]); setServices([]) }
  }

  const submit = async () => {
    if (!selectedSlot || !selectedService) return
    setLoading(true)
    setMsg(null)
    try {
      await api.createBooking({
        clientId: user.id,
        consultantId: selectedConsultant.id,
        slotId: parseInt(selectedSlot),
        serviceId: parseInt(selectedService),
      })
      setMsg({ type: 'success', text: 'Booking request submitted successfully!' })
      setSelectedConsultant(null)
    } catch (err) {
      setMsg({ type: 'error', text: err.message })
    } finally { setLoading(false) }
  }

  return (
    <div className="card">
      <h3>Request a New Booking</h3>
      {msg && <div className={msg.type === 'success' ? 'success-msg' : 'error-msg'}>{msg.text}</div>}
      {!selectedConsultant ? (
        <>
          <p style={{ marginBottom: 12, color: 'var(--gray-500)' }}>Select a consultant:</p>
          {consultants.length === 0 ? <p className="empty">No approved consultants available</p> : (
            <table>
              <thead><tr><th>Name</th><th>Specialization</th><th></th></tr></thead>
              <tbody>
                {consultants.map(c => (
                  <tr key={c.id}>
                    <td>{c.name}</td>
                    <td>{c.specialization}</td>
                    <td><button className="btn btn-primary btn-sm" onClick={() => selectConsultant(c)}>Select</button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </>
      ) : (
        <>
          <p style={{ marginBottom: 16 }}>Booking with <strong>{selectedConsultant.name}</strong> ({selectedConsultant.specialization})
            <button className="btn btn-outline btn-sm" style={{ marginLeft: 12 }} onClick={() => setSelectedConsultant(null)}>Change</button>
          </p>
          <div className="form-row">
            <div className="form-group">
              <label>Available Time Slot</label>
              <select value={selectedSlot} onChange={e => setSelectedSlot(e.target.value)}>
                <option value="">Select a slot...</option>
                {slots.map(s => (
                  <option key={s.id} value={s.id}>
                    {new Date(s.startTime).toLocaleString()} - {new Date(s.endTime).toLocaleTimeString()}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Service</label>
              <select value={selectedService} onChange={e => setSelectedService(e.target.value)}>
                <option value="">Select a service...</option>
                {services.map(s => (
                  <option key={s.id} value={s.id}>{s.name} — ${s.basePrice.toFixed(2)}</option>
                ))}
              </select>
            </div>
          </div>
          <button className="btn btn-primary" onClick={submit} disabled={loading || !selectedSlot || !selectedService}>
            {loading ? 'Submitting...' : 'Submit Booking Request'}
          </button>
        </>
      )}
    </div>
  )
}

function MyBookings({ user }) {
  const [bookings, setBookings] = useState([])
  const [msg, setMsg] = useState(null)
  const load = () => api.getClientBookings(user.id).then(setBookings).catch(() => {})
  useEffect(() => { load() }, [])

  const cancel = async (id) => {
    try {
      const res = await api.cancelBooking(id, user.id)
      setMsg({ type: 'success', text: res.message })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  const canCancel = (state) => !['COMPLETED', 'CANCELLED', 'REJECTED'].includes(state)

  return (
    <div className="card">
      <h3>Booking History</h3>
      {msg && <div className={msg.type === 'success' ? 'success-msg' : 'error-msg'}>{msg.text}</div>}
      {bookings.length === 0 ? <p className="empty">No bookings yet</p> : (
        <table>
          <thead><tr><th>ID</th><th>Service</th><th>Consultant</th><th>Status</th><th>Price</th><th>Time</th><th></th></tr></thead>
          <tbody>
            {bookings.map(b => (
              <tr key={b.id}>
                <td>#{b.id}</td>
                <td>{b.serviceName}</td>
                <td>{b.consultantName}</td>
                <td><span className={`badge badge-${b.state.toLowerCase()}`}>{b.state}</span></td>
                <td>${b.totalPrice.toFixed(2)}</td>
                <td>{b.timeSlotStart ? new Date(b.timeSlotStart).toLocaleDateString() : '-'}</td>
                <td>
                  {canCancel(b.state) && (
                    <button className="btn btn-danger btn-sm" onClick={() => cancel(b.id)}>Cancel</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

function ProcessPayment({ user }) {
  const [bookings, setBookings] = useState([])
  const [methods, setMethods] = useState([])
  const [selectedBooking, setSelectedBooking] = useState('')
  const [selectedMethod, setSelectedMethod] = useState('')
  const [msg, setMsg] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    api.getPendingPaymentBookings(user.id).then(setBookings).catch(() => {})
    api.getPaymentMethods(user.id).then(setMethods).catch(() => {})
  }, [])

  const submit = async () => {
    setLoading(true)
    setMsg(null)
    try {
      const res = await api.processPayment({
        bookingId: parseInt(selectedBooking),
        clientId: user.id,
        paymentMethodId: parseInt(selectedMethod),
      })
      setMsg({ type: 'success', text: `Payment successful! Transaction ID: ${res.transactionId}` })
      api.getPendingPaymentBookings(user.id).then(setBookings)
    } catch (err) {
      setMsg({ type: 'error', text: err.message })
    } finally { setLoading(false) }
  }

  return (
    <div className="card">
      <h3>Process Payment</h3>
      {msg && <div className={msg.type === 'success' ? 'success-msg' : 'error-msg'}>{msg.text}</div>}
      {bookings.length === 0 ? <p className="empty">No bookings awaiting payment</p> : (
        <>
          <div className="form-row">
            <div className="form-group">
              <label>Booking</label>
              <select value={selectedBooking} onChange={e => setSelectedBooking(e.target.value)}>
                <option value="">Select booking...</option>
                {bookings.map(b => (
                  <option key={b.id} value={b.id}>#{b.id} — {b.serviceName} (${b.totalPrice.toFixed(2)})</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Payment Method</label>
              <select value={selectedMethod} onChange={e => setSelectedMethod(e.target.value)}>
                <option value="">Select method...</option>
                {methods.map(m => (
                  <option key={m.id} value={m.id}>{m.type}{m.isDefault ? ' (default)' : ''}</option>
                ))}
              </select>
            </div>
          </div>
          <button className="btn btn-success" onClick={submit} disabled={loading || !selectedBooking || !selectedMethod}>
            {loading ? 'Processing...' : 'Pay Now'}
          </button>
        </>
      )}
    </div>
  )
}

function PaymentMethods({ user }) {
  const [methods, setMethods] = useState([])
  const [showAdd, setShowAdd] = useState(false)
  const [type, setType] = useState('CREDIT_CARD')
  const [fields, setFields] = useState({})
  const [msg, setMsg] = useState(null)

  const load = () => api.getPaymentMethods(user.id).then(setMethods).catch(() => {})
  useEffect(() => { load() }, [])

  const addMethod = async () => {
    try {
      await api.addPaymentMethod(user.id, { type, ...fields })
      setMsg({ type: 'success', text: 'Payment method added' })
      setShowAdd(false)
      setFields({})
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  const remove = async (id) => {
    try {
      await api.removePaymentMethod(id, user.id)
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  const setDefault = async (id) => {
    try {
      await api.setDefaultPaymentMethod(id, user.id)
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  return (
    <div className="card">
      <h3>Payment Methods</h3>
      {msg && <div className={msg.type === 'success' ? 'success-msg' : 'error-msg'}>{msg.text}</div>}
      {methods.length === 0 ? <p className="empty">No saved payment methods</p> : (
        <table>
          <thead><tr><th>Type</th><th>Default</th><th>Actions</th></tr></thead>
          <tbody>
            {methods.map(m => (
              <tr key={m.id}>
                <td>{m.type}</td>
                <td>{m.isDefault ? 'Yes' : 'No'}</td>
                <td className="actions">
                  {!m.isDefault && <button className="btn btn-outline btn-sm" onClick={() => setDefault(m.id)}>Set Default</button>}
                  <button className="btn btn-danger btn-sm" onClick={() => remove(m.id)}>Remove</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <div style={{ marginTop: 16 }}>
        <button className="btn btn-primary btn-sm" onClick={() => setShowAdd(!showAdd)}>
          {showAdd ? 'Cancel' : 'Add Payment Method'}
        </button>
      </div>
      {showAdd && (
        <div style={{ marginTop: 16 }}>
          <div className="form-group">
            <label>Type</label>
            <select value={type} onChange={e => { setType(e.target.value); setFields({}) }}>
              <option value="CREDIT_CARD">Credit Card</option>
              <option value="DEBIT_CARD">Debit Card</option>
              <option value="PAYPAL">PayPal</option>
              <option value="BANK_TRANSFER">Bank Transfer</option>
            </select>
          </div>
          {(type === 'CREDIT_CARD' || type === 'DEBIT_CARD') && (
            <>
              <div className="form-group"><label>Card Number (16 digits)</label><input value={fields.cardNumber || ''} onChange={e => setFields({ ...fields, cardNumber: e.target.value })} placeholder="4111111111111111" /></div>
              <div className="form-row">
                <div className="form-group"><label>Expiry (MM/YY)</label><input value={fields.expiry || ''} onChange={e => setFields({ ...fields, expiry: e.target.value })} placeholder="12/27" /></div>
                <div className="form-group"><label>CVV (3-4 digits)</label><input value={fields.cvv || ''} onChange={e => setFields({ ...fields, cvv: e.target.value })} placeholder="123" /></div>
              </div>
            </>
          )}
          {type === 'PAYPAL' && (
            <div className="form-group"><label>PayPal Email</label><input value={fields.email || ''} onChange={e => setFields({ ...fields, email: e.target.value })} placeholder="user@paypal.com" /></div>
          )}
          {type === 'BANK_TRANSFER' && (
            <div className="form-row">
              <div className="form-group"><label>Account Number (8-17 digits)</label><input value={fields.accountNumber || ''} onChange={e => setFields({ ...fields, accountNumber: e.target.value })} placeholder="12345678" /></div>
              <div className="form-group"><label>Routing Number (9 digits)</label><input value={fields.routingNumber || ''} onChange={e => setFields({ ...fields, routingNumber: e.target.value })} placeholder="123456789" /></div>
            </div>
          )}
          <button className="btn btn-success btn-sm" style={{ marginTop: 8 }} onClick={addMethod}>Save</button>
        </div>
      )}
    </div>
  )
}

function PaymentHistory({ user }) {
  const [payments, setPayments] = useState([])
  useEffect(() => { api.getPaymentHistory(user.id).then(setPayments).catch(() => {}) }, [])

  return (
    <div className="card">
      <h3>Payment History</h3>
      {payments.length === 0 ? <p className="empty">No payments yet</p> : (
        <table>
          <thead><tr><th>Transaction ID</th><th>Amount</th><th>Method</th><th>Status</th><th>Date</th></tr></thead>
          <tbody>
            {payments.map(p => (
              <tr key={p.id}>
                <td style={{ fontFamily: 'monospace', fontSize: 13 }}>{p.transactionId}</td>
                <td>${p.amount.toFixed(2)}</td>
                <td>{p.paymentMethodType}</td>
                <td><span className={`badge badge-${p.status.toLowerCase()}`}>{p.status}</span></td>
                <td>{new Date(p.timestamp).toLocaleDateString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
