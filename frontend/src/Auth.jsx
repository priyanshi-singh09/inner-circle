import { useState } from 'react';
import { login, register } from './api.js';

export default function Auth({ onAuthenticated }) {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ email: '', password: '', handle: '', dateOfBirth: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const update = (event) => setForm({ ...form, [event.target.name]: event.target.value });

  async function submit(event) {
    event.preventDefault();
    setError('');
    setLoading(true);
    try {
      const result = mode === 'login'
        ? await login(form.email, form.password)
        : await register({
            email: form.email,
            password: form.password,
            handle: form.handle,
            dateOfBirth: form.dateOfBirth,
          });

      if (mode === 'register') {
        setMode('login');
        setError('Account created. You can now sign in.');
      } else {
        localStorage.setItem('innerCircleToken', result.token);
        onAuthenticated();
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <div className="auth-brand"><span>◉</span> Inner Circle</div>
        <p className="eyebrow">A QUIET PLACE TO BE YOURSELF</p>
        <h1>{mode === 'login' ? 'Welcome back.' : 'Create your circle.'}</h1>
        <p className="subtitle">Share without fear. Listen without judgment.</p>

        <form onSubmit={submit}>
          <label>Email<input name="email" type="email" value={form.email} onChange={update} required /></label>
          {mode === 'register' && <>
            <label>Handle<input name="handle" minLength="3" maxLength="30" value={form.handle} onChange={update} required /></label>
            <label>Date of birth<input name="dateOfBirth" type="date" value={form.dateOfBirth} onChange={update} required /></label>
          </>}
          <label>Password<input name="password" type="password" minLength="8" maxLength="72" value={form.password} onChange={update} required /></label>
          {error && <p className="auth-message">{error}</p>}
          <button className="auth-submit" disabled={loading}>
            {loading ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}
          </button>
        </form>

        <button className="switch-auth" onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError(''); }}>
          {mode === 'login' ? 'New here? Create an account' : 'Already have an account? Sign in'}
        </button>
      </section>
    </main>
  );
}
