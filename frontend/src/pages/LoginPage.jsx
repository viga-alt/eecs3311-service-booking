import { useState } from 'react'
import { api } from '../api'

export default function LoginPage({ onLogin }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await api.login(email, password)
      onLogin(user)
    } catch (err) {
      setError(err.message || 'Invalid credentials')
    } finally {
      setLoading(false)
    }
  }

  const quickLogin = (email, password) => {
    setEmail(email)
    setPassword(password)
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <h1>Welcome Back</h1>
        <p>Sign in to the Service Booking Platform</p>
        {error && <div className="error-msg">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} required placeholder="Enter your email" />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} required placeholder="Enter your password" />
          </div>
          <button className="btn btn-primary btn-full" type="submit" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
        <div className="demo-creds">
          <h4>Demo Accounts (click to fill)</h4>
          <table>
            <tbody>
              <tr onClick={() => quickLogin('bob@email.com', 'pass123')} style={{ cursor: 'pointer' }}>
                <td>bob@email.com</td><td>pass123</td><td>Client</td>
              </tr>
              <tr onClick={() => quickLogin('carol@email.com', 'pass456')} style={{ cursor: 'pointer' }}>
                <td>carol@email.com</td><td>pass456</td><td>Client</td>
              </tr>
              <tr onClick={() => quickLogin('dave@consult.com', 'pass789')} style={{ cursor: 'pointer' }}>
                <td>dave@consult.com</td><td>pass789</td><td>Consultant</td>
              </tr>
              <tr onClick={() => quickLogin('eve@consult.com', 'passabc')} style={{ cursor: 'pointer' }}>
                <td>eve@consult.com</td><td>passabc</td><td>Consultant</td>
              </tr>
              <tr onClick={() => quickLogin('admin@platform.com', 'admin123')} style={{ cursor: 'pointer' }}>
                <td>admin@platform.com</td><td>admin123</td><td>Admin</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
