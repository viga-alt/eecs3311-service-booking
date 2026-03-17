import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import ClientDashboard from './pages/ClientDashboard'
import ConsultantDashboard from './pages/ConsultantDashboard'
import AdminDashboard from './pages/AdminDashboard'
import Navbar from './components/Navbar'

export default function App() {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user')
    return saved ? JSON.parse(saved) : null
  })

  useEffect(() => {
    if (user) localStorage.setItem('user', JSON.stringify(user))
    else localStorage.removeItem('user')
  }, [user])

  const handleLogout = () => setUser(null)

  if (!user) return <LoginPage onLogin={setUser} />

  return (
    <BrowserRouter>
      <Navbar user={user} onLogout={handleLogout} />
      <main className="main-content">
        <Routes>
          {user.role === 'CLIENT' && (
            <Route path="/*" element={<ClientDashboard user={user} />} />
          )}
          {user.role === 'CONSULTANT' && (
            <Route path="/*" element={<ConsultantDashboard user={user} />} />
          )}
          {user.role === 'ADMIN' && (
            <Route path="/*" element={<AdminDashboard user={user} />} />
          )}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}
