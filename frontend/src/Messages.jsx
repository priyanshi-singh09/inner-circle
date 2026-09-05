import { useEffect, useState } from 'react';
import { getConversation, sendMessage, markMessageRead } from './api.js';

export default function Messages({ person, onBack }) {
  const [messages, setMessages] = useState([]);
  const [content, setContent] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);

  async function load() {
    try {
      setLoading(true);
      setError('');
      const page = await getConversation(person.userId);
      const next = page.content || [];
      setMessages(next);
      await Promise.all(next.filter((item) => !item.read).map((item) => markMessageRead(item.id).catch(() => null)));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { load(); }, [person.userId]);

  async function submit(event) {
    event.preventDefault();
    if (!content.trim() || sending) return;
    try {
      setSending(true);
      setError('');
      const created = await sendMessage(person.userId, content.trim());
      setMessages((current) => [...current, created]);
      setContent('');
    } catch (err) {
      setError(err.message);
    } finally {
      setSending(false);
    }
  }

  return (
    <main className="messages-page">
      <section className="messages-card">
        <button className="back-button" onClick={onBack}>← Back to connections</button>
        <header className="conversation-header">
          <div className="avatar">{person.handle?.[1]?.toUpperCase()}</div>
          <div><strong>{person.handle}</strong><span>Mutual connection · {person.circle}</span></div>
        </header>

        {error && <p className="auth-message">{error}</p>}
        <div className="message-list">
          {loading && <p className="empty-state">Loading conversation…</p>}
          {!loading && messages.length === 0 && !error && <p className="empty-state">No messages yet. Start a thoughtful conversation.</p>}
          {!loading && messages.map((item) => (
            <div className={item.senderId === person.userId ? 'message-bubble received' : 'message-bubble sent'} key={item.id}>
              <p>{item.content}</p>
              <time>{new Date(item.createdAt).toLocaleString()}</time>
            </div>
          ))}
        </div>

        <form className="message-form" onSubmit={submit}>
          <textarea value={content} onChange={(event) => setContent(event.target.value)} maxLength={2000} rows={3} placeholder="Write a thoughtful message…" required />
          <div className="message-form-footer"><small>{content.length}/2000</small><button className="auth-submit" disabled={!content.trim() || sending}>{sending ? 'Sending…' : 'Send message'}</button></div>
        </form>
      </section>
    </main>
  );
}
