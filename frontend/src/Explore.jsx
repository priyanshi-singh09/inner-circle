import { useEffect, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

async function get(path) {
  const response = await fetch(`${API_BASE_URL}${path}`, { headers: { Authorization: `Bearer ${localStorage.getItem('innerCircleToken')}` } });
  if (!response.ok) throw new Error('Unable to load Explore right now.');
  return response.json();
}

export default function Explore({ onBack }) {
  const [tab, setTab] = useState('posts');
  const [emotion, setEmotion] = useState('');
  const [query, setQuery] = useState('');
  const [posts, setPosts] = useState([]);
  const [people, setPeople] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const timer = setTimeout(async () => {
      try {
        setError('');
        if (tab === 'posts') {
          const value = emotion ? `?emotion=${encodeURIComponent(emotion)}&page=0&size=20` : '?page=0&size=20';
          const result = await get(`/explore/posts${value}`);
          setPosts(result.content || []);
        } else {
          const value = query.trim() ? `?q=${encodeURIComponent(query.trim())}&page=0&size=20` : '?page=0&size=20';
          const result = await get(`/explore/people${value}`);
          setPeople(result.content || []);
        }
      } catch (err) { setError(err.message); }
    }, 250);
    return () => clearTimeout(timer);
  }, [tab, emotion, query]);

  return (
    <main className="explore-page"><section className="explore-card">
      <button className="back-button" onClick={onBack}>← Back to feed</button>
      <p className="eyebrow">DISCOVER</p><h1>Find something that feels familiar.</h1>
      <p className="subtitle">Explore thoughts and people without turning connection into a popularity contest.</p>
      <div className="explore-tabs"><button className={tab === 'posts' ? 'active' : ''} onClick={() => setTab('posts')}>Posts</button><button className={tab === 'people' ? 'active' : ''} onClick={() => setTab('people')}>People</button></div>
      {tab === 'posts' ? <div className="filter-row"><button className={!emotion ? 'filter active' : 'filter'} onClick={() => setEmotion('')}>All</button>{['Low','Anxious','Angry','Happy','Emotional','Peaceful','Just want to talk'].map((item) => <button className={emotion === item ? 'filter active' : 'filter'} key={item} onClick={() => setEmotion(item)}>{item}</button>)}</div> : <input className="explore-search" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search by handle" />}
      {error && <p className="auth-message">{error}</p>}
      {tab === 'posts' && posts.map((post) => <article className="post-card" key={post.id}><div className="post-meta"><div className="avatar">{post.author === 'Anonymous' ? '?' : post.author?.[1]?.toUpperCase()}</div><div><strong>{post.author}</strong><span>{post.emotion} · {post.circle}</span></div></div><p className="post-content">{post.content}</p></article>)}
      {tab === 'people' && people.map((person) => <article className="person-card" key={person.id}><div className="avatar">{person.handle?.[1]?.toUpperCase()}</div><div><strong>{person.handle}</strong><span>{person.circle}</span></div></article>)}
      {!error && tab === 'posts' && posts.length === 0 && <p className="empty-state">No posts found.</p>}
      {!error && tab === 'people' && people.length === 0 && <p className="empty-state">No people found.</p>}
    </section></main>
  );
}
