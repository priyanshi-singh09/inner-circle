import { useState } from 'react';
import { createPost } from './api.js';

const emotions = ['Low', 'Anxious', 'Angry', 'Happy', 'Emotional', 'Peaceful', 'Just want to talk'];

export default function ShareThought({ onPosted, onCancel }) {
  const [content, setContent] = useState('');
  const [emotion, setEmotion] = useState('');
  const [anonymous, setAnonymous] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function submit(event) {
    event.preventDefault();
    if (!content.trim() || !emotion) return;
    setLoading(true);
    setError('');
    try {
      const post = await createPost(content.trim(), emotion, anonymous);
      onPosted(post);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="share-page">
      <section className="share-card">
        <button className="back-button" onClick={onCancel}>← Back</button>
        <p className="eyebrow">SHARE WITHOUT FEAR</p>
        <h1>Say what you mean.</h1>
        <p className="subtitle">There is no need to make it perfect. Just make it yours.</p>

        <form onSubmit={submit} className="share-form">
          <textarea
            value={content}
            onChange={(event) => setContent(event.target.value)}
            maxLength={5000}
            placeholder="What’s on your mind?"
            rows={7}
            required
          />
          <div className="character-count">{content.length}/5000</div>

          <div>
            <p className="field-title">How does this feel?</p>
            <div className="emotion-select-row">
              {emotions.map((item) => (
                <button type="button" key={item} className={emotion === item ? 'emotion-option selected' : 'emotion-option'} onClick={() => setEmotion(item)}>
                  {item}
                </button>
              ))}
            </div>
          </div>

          <label className="anonymous-toggle">
            <input type="checkbox" checked={anonymous} onChange={(event) => setAnonymous(event.target.checked)} />
            <span>Post anonymously</span>
          </label>

          {error && <p className="auth-message">{error}</p>}
          <button className="auth-submit" disabled={loading || !content.trim() || !emotion}>
            {loading ? 'Sharing…' : 'Share thought'}
          </button>
        </form>
      </section>
    </main>
  );
}
