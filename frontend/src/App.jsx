import { useEffect, useState } from 'react';
import Auth from './Auth.jsx';
import Explore from './Explore.jsx';
import ShareThought from './ShareThought.jsx';
import { getFeed, getMyProfile } from './api.js';

const emotions = [['😔','Low'],['😟','Anxious'],['😡','Angry'],['😊','Happy'],['🥹','Emotional'],['😌','Peaceful'],['🤍','Talk']];

function App() {
  const [authenticated, setAuthenticated] = useState(Boolean(localStorage.getItem('innerCircleToken')));
  const [screen, setScreen] = useState('Feed');
  const [profile, setProfile] = useState(null);
  const [posts, setPosts] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!authenticated) return;
    Promise.all([getMyProfile(), getFeed()]).then(([user, feed]) => { setProfile(user); setPosts(feed.content || []); }).catch((err) => { setError(err.message); localStorage.removeItem('innerCircleToken'); setAuthenticated(false); });
  }, [authenticated]);

  if (!authenticated) return <Auth onAuthenticated={() => setAuthenticated(true)} />;
  if (screen === 'Share') return <ShareThought onCancel={() => setScreen('Feed')} onPosted={(post) => { setPosts((current) => [post, ...current]); setScreen('Feed'); }} />;
  if (screen === 'Explore') return <Explore onBack={() => setScreen('Feed')} />;

  return <div className="app-shell">
    <header className="topbar"><button className="icon-button">☰</button><div className="brand"><span className="brand-mark">◉</span><span>Inner Circle</span></div><button className="icon-button">🔔</button></header>
    <main className="main-content">
      <section className="welcome"><p className="eyebrow">{profile ? `@${profile.handle}` : 'YOUR SPACE'}</p><h1>What’s on your mind?</h1><p className="subtitle">Share without fear. Listen without judgment.</p><button className="share-button" onClick={() => setScreen('Share')}>+ Share a thought</button></section>
      <section className="emotion-section"><div className="section-heading"><h2>How are you feeling?</h2><button onClick={() => setScreen('Explore')}>Explore all</button></div><div className="emotion-row">{emotions.map(([emoji,label]) => <button className="emotion-chip" key={label} onClick={() => setScreen('Explore')}><span>{emoji}</span><small>{label}</small></button>)}</div></section>
      <section className="feed-section"><div className="feed-tabs"><button className="active">For You</button><button>My Circle</button><button>Following</button></div>{error && <p className="auth-message">{error}</p>}{posts.length === 0 && !error && <p className="empty-state">No thoughts yet. Your Circle is waiting for its first voice.</p>}{posts.map((post) => <article className="post-card" key={post.id}><div className="post-meta"><div className="avatar">{post.author === 'Anonymous' ? '?' : post.author?.[1]?.toUpperCase()}</div><div><strong>{post.author}</strong><span>{post.emotion} · {new Date(post.createdAt).toLocaleString()}</span></div></div><p className="post-content">{post.content}</p><div className="reactions"><button>❤️ I hear you</button><button>🤝 Relate</button><button>🌱 Rooting for you</button></div><button className="comment-link">💬 Supportive comments</button></article>)}</section>
    </main>
    <nav className="bottom-nav">{[['🏠','Feed'],['🔎','Explore'],['＋','Share'],['🤝','Connections'],['👤','Profile']].map(([icon,label]) => <button className={screen === label ? 'nav-active' : ''} key={label} onClick={() => setScreen(label)}><span>{icon}</span><small>{label}</small></button>)}</nav>
  </div>;
}
export default App;
