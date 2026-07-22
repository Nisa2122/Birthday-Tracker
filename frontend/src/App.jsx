import React, { useEffect, useState } from 'react'

const demoUser = { username: 'admin', password: 'password' }

function parseDate(dateString) {
  return new Date(dateString + 'T00:00:00')
}

function nextBirthday(birthDate) {
  const today = new Date()
  const birth = parseDate(birthDate)
  const thisYear = new Date(today.getFullYear(), birth.getMonth(), birth.getDate())
  if (thisYear >= today) return thisYear
  return new Date(today.getFullYear() + 1, birth.getMonth(), birth.getDate())
}

function isWeekendBirthday(birthDate) {
  const next = nextBirthday(birthDate)
  const day = next.getDay()
  return day === 0 || day === 6
}

function App() {
  const [loggedIn, setLoggedIn] = useState(() => localStorage.getItem('bday-logged-in') === 'true')
  const [loginError, setLoginError] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  const [members, setMembers] = useState([])
  const [todayMembers, setTodayMembers] = useState([])
  const [weekMembers, setWeekMembers] = useState([])
  const [monthMembers, setMonthMembers] = useState([])
  const [selectedId, setSelectedId] = useState(null)
  const [banner, setBanner] = useState(null)
  const [saving, setSaving] = useState(false)

  const [form, setForm] = useState({ id: null, name: '', birthDate: '', team: '' })

  async function fetchMembers() {
    const [all, today, week, month] = await Promise.all([
      fetch('/api/members').then(res => res.json()),
      fetch('/api/members/today').then(res => res.json()),
      fetch('/api/members/week').then(res => res.json()),
      fetch('/api/members/month').then(res => res.json())
    ])
    setMembers(all)
    setTodayMembers(today)
    setWeekMembers(week)
    setMonthMembers(month)
  }

  useEffect(() => {
    if (!loggedIn) return
    fetchMembers()
  }, [loggedIn])

  function handleLogin(event) {
    event.preventDefault()
    if (username === demoUser.username && password === demoUser.password) {
      setLoggedIn(true)
      localStorage.setItem('bday-logged-in', 'true')
      setLoginError('')
      return
    }
    setLoginError('Use demo credentials: admin / password')
  }

  function handleSelectMember(member) {
    setSelectedId(member.id)
    setForm({ id: member.id, name: member.name, birthDate: member.birthDate, team: member.team })
    setBanner(null)
  }

  function clearForm() {
    setForm({ id: null, name: '', birthDate: '', team: '' })
    setSelectedId(null)
    setBanner(null)
  }

  async function saveMember(event) {
    event.preventDefault()
    if (!form.name || !form.birthDate || !form.team) {
      alert('Fill name, birthdate and team.')
      return
    }

    setSaving(true)
    try {
      const url = form.id ? `/api/members/${form.id}` : '/api/members'
      const method = form.id ? 'PUT' : 'POST'
      const response = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: form.name, birthDate: form.birthDate, team: form.team })
      })
      if (!response.ok) throw new Error('Failed to save member')
      await fetchMembers()
      clearForm()
    } catch (error) {
      alert(error.message)
    } finally {
      setSaving(false)
    }
  }

  async function createBanner() {
    if (!selectedId) return alert('Select a member to generate a banner.')
    const response = await fetch(`/api/banner/${selectedId}`)
    if (!response.ok) return alert('Unable to create banner right now.')
    const bannerData = await response.json()
    setBanner(bannerData)
  }

  if (!loggedIn) {
    return (
      <div className="login-page">
        <div className="login-card">
          <h1>Birthday Tracker Login</h1>
          <p>Demo credentials: <strong>admin</strong> / <strong>password</strong></p>
          <form onSubmit={handleLogin}>
            <label>
              Username
              <input value={username} onChange={e => setUsername(e.target.value)} />
            </label>
            <label>
              Password
              <input type="password" value={password} onChange={e => setPassword(e.target.value)} />
            </label>
            {loginError && <div className="error">{loginError}</div>}
            <button type="submit">Login</button>
          </form>
        </div>
      </div>
    )
  }

  return (
    <div className="app">
      <header>
        <div>
          <h1>Birthday Tracker Dashboard</h1>
          <p>Auto-loaded from SQLite on each refresh.</p>
        </div>
        <button className="logout" onClick={() => { setLoggedIn(false); localStorage.removeItem('bday-logged-in') }}>Logout</button>
      </header>

      <section className="stats">
        <article>
          <h2>Today</h2>
          <p>{todayMembers.length} member(s)</p>
        </article>
        <article>
          <h2>This Week</h2>
          <p>{weekMembers.length} member(s)</p>
        </article>
        <article>
          <h2>This Month</h2>
          <p>{monthMembers.length} member(s)</p>
        </article>
        <article>
          <h2>All Members</h2>
          <p>{members.length} total</p>
        </article>
      </section>

      <main>
        <div className="panel">
          <div className="panel-header">
            <h2>Member List</h2>
            <button onClick={fetchMembers}>Refresh</button>
          </div>
          <div className="member-list">
            {members.map(member => {
              const weekend = isWeekendBirthday(member.birthDate)
              return (
                <div key={member.id} className={`member-card ${weekend ? 'weekend' : ''}`}>
                  <label>
                    <input
                      type="radio"
                      checked={selectedId === member.id}
                      onChange={() => handleSelectMember(member)}
                    />
                    <strong>{member.name}</strong>
                  </label>
                  <div className="member-info">
                    <span>{member.team}</span>
                    <span>{member.birthDate}</span>
                    {weekend && <span className="badge">Weekend</span>}
                  </div>
                </div>
              )
            })}
          </div>
          <div className="actions-row">
            <button onClick={createBanner}>Create Birthday Banner</button>
            <button onClick={clearForm} className="secondary">Clear</button>
          </div>
        </div>

        <div className="panel form-panel">
          <h2>{form.id ? 'Edit Member' : 'Add Member'}</h2>
          <form onSubmit={saveMember} className="member-form">
            <label>
              Name
              <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} />
            </label>
            <label>
              Birthdate
              <input type="date" value={form.birthDate} onChange={e => setForm({ ...form, birthDate: e.target.value })} />
            </label>
            <label>
              Team
              <input value={form.team} onChange={e => setForm({ ...form, team: e.target.value })} />
            </label>
            <button type="submit" disabled={saving}>{saving ? 'Saving...' : (form.id ? 'Update Member' : 'Add Member')}</button>
          </form>
        </div>

        <div className="panel banner-panel">
          <h2>Banner Preview</h2>
          {banner ? (
            <div className="banner-card">
              <h3>{banner.memberName}</h3>
              <p><strong>Team:</strong> {banner.team}</p>
              <p><strong>Next Birthday:</strong> {banner.nextBirthday} {banner.weekend ? '(Weekend)' : ''}</p>
              <blockquote>{banner.quote}</blockquote>
              <p>{banner.message}</p>
            </div>
          ) : (
            <p>Select a member and click "Create Birthday Banner" to generate a funny quote.</p>
          )}
        </div>
      </main>
    </div>
  )
}

export default App
