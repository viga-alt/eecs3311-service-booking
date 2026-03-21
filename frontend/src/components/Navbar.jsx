export default function Navbar({ user, onLogout }) {
  return (
    <nav className="navbar">
      <h1>Service Booking Platform</h1>
      <div className="navbar-right">
        <span>{user.name} ({user.role})</span>
        <button className="btn btn-logout btn-sm" onClick={onLogout}>
          Logout
        </button>
      </div>
    </nav>
  )
}
