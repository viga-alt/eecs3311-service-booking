import { useState, useEffect } from 'react'
import { api } from '../api'

export default function ConsultantDashboard({ user }) {
  const [tab, setTab] = useState('pending')
  const tabs = [
    { id: 'pending', label: 'Pending Requests' },
    { id: 'schedule', label: 'All Bookings' },
    { id: 'availability', label: 'Manage Availability' },
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
      {tab === 'pending' && <PendingRequests user={user} />}
      {tab === 'schedule' && <AllBookings user={user} />}
      {tab === 'availability' && <ManageAvailability user={user} />}
    </div>
  )
}

function PendingRequests({ user }) {
  const [bookings, setBookings] = useState([])
  const [msg, setMsg] = useState(null)

  const load = () => api.getPendingBookings(user.id).then(setBookings).catch(() => {})
  useEffect(() => { load() }, [])

  const accept = async (id) => {
    try {
      await api.acceptBooking(id, user.id)
      setMsg({ type: 'success', text: `Booking #${id} accepted` })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  const reject = async (id) => {
    try {
      await api.rejectBooking(id, user.id)
      setMsg({ type: 'success', text: `Booking #${id} rejected` })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  return (
    <div className="card">
      <h3>Pending Booking Requests</h3>
      {msg && <div className={msg.type === 'success' ? 'success-msg' : 'error-msg'}>{msg.text}</div>}
      {bookings.length === 0 ? <p className="empty">No pending requests</p> : (
        <table>
          <thead><tr><th>ID</th><th>Client</th><th>Service</th><th>Price</th><th>Time</th><th>Actions</th></tr></thead>
          <tbody>
            {bookings.map(b => (
              <tr key={b.id}>
                <td>#{b.id}</td>
                <td>{b.clientName}</td>
                <td>{b.serviceName}</td>
                <td>${b.totalPrice.toFixed(2)}</td>
                <td>{b.timeSlotStart ? new Date(b.timeSlotStart).toLocaleString() : '-'}</td>
                <td className="actions">
                  <button className="btn btn-success btn-sm" onClick={() => accept(b.id)}>Accept</button>
                  <button className="btn btn-danger btn-sm" onClick={() => reject(b.id)}>Reject</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

function AllBookings({ user }) {
  const [bookings, setBookings] = useState([])
  const [msg, setMsg] = useState(null)

  const load = () => api.getConsultantBookings(user.id).then(setBookings).catch(() => {})
  useEffect(() => { load() }, [])

  const complete = async (id) => {
    try {
      await api.completeBooking(id, user.id)
      setMsg({ type: 'success', text: `Booking #${id} marked as completed` })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  return (
    <div className="card">
      <h3>All My Bookings</h3>
      {msg && <div className={msg.type === 'success' ? 'success-msg' : 'error-msg'}>{msg.text}</div>}
      {bookings.length === 0 ? <p className="empty">No bookings</p> : (
        <table>
          <thead><tr><th>ID</th><th>Client</th><th>Service</th><th>Status</th><th>Price</th><th>Time</th><th></th></tr></thead>
          <tbody>
            {bookings.map(b => (
              <tr key={b.id}>
                <td>#{b.id}</td>
                <td>{b.clientName}</td>
                <td>{b.serviceName}</td>
                <td><span className={`badge badge-${b.state.toLowerCase()}`}>{b.state}</span></td>
                <td>${b.totalPrice.toFixed(2)}</td>
                <td>{b.timeSlotStart ? new Date(b.timeSlotStart).toLocaleString() : '-'}</td>
                <td>
                  {b.state === 'PAID' && (
                    <button className="btn btn-success btn-sm" onClick={() => complete(b.id)}>Complete</button>
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

function ManageAvailability({ user }) {
  const [slots, setSlots] = useState([])
  const [days, setDays] = useState('1')
  const [startHour, setStartHour] = useState('9')
  const [duration, setDuration] = useState('1')
  const [msg, setMsg] = useState(null)

  const load = () => api.getConsultantSlots(user.id).then(setSlots).catch(() => {})
  useEffect(() => { load() }, [])

  const addSlot = async () => {
    try {
      await api.addSlot(user.id, {
        daysFromToday: parseInt(days),
        startHour: parseInt(startHour),
        durationHours: parseInt(duration),
      })
      setMsg({ type: 'success', text: 'Time slot added' })
      load()
    } catch (err) { setMsg({ type: 'error', text: err.message }) }
  }

  return (
    <div className="card">
      <h3>Manage Availability</h3>
      {msg && <div className={msg.type === 'success' ? 'success-msg' : 'error-msg'}>{msg.text}</div>}

      <div style={{ marginBottom: 20 }}>
        <h4 style={{ fontSize: 14, marginBottom: 8 }}>Add New Time Slot</h4>
        <div className="form-row" style={{ gridTemplateColumns: '1fr 1fr 1fr auto' }}>
          <div className="form-group">
            <label>Days from Today</label>
            <input type="number" min="1" value={days} onChange={e => setDays(e.target.value)} />
          </div>
          <div className="form-group">
            <label>Start Hour (0-23)</label>
            <input type="number" min="0" max="23" value={startHour} onChange={e => setStartHour(e.target.value)} />
          </div>
          <div className="form-group">
            <label>Duration (hours)</label>
            <input type="number" min="1" max="8" value={duration} onChange={e => setDuration(e.target.value)} />
          </div>
          <div className="form-group" style={{ display: 'flex', alignItems: 'flex-end' }}>
            <button className="btn btn-primary btn-sm" onClick={addSlot}>Add</button>
          </div>
        </div>
      </div>

      <h4 style={{ fontSize: 14, marginBottom: 8 }}>Current Available Slots</h4>
      {slots.length === 0 ? <p className="empty">No available slots</p> : (
        <table>
          <thead><tr><th>ID</th><th>Start</th><th>End</th><th>Available</th></tr></thead>
          <tbody>
            {slots.map(s => (
              <tr key={s.id}>
                <td>#{s.id}</td>
                <td>{new Date(s.startTime).toLocaleString()}</td>
                <td>{new Date(s.endTime).toLocaleString()}</td>
                <td>{s.available ? 'Yes' : 'No'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
