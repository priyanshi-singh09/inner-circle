import { useState } from 'react';

const emotions = [
  ['😔', 'Low'],
  ['😟', 'Anxious'],
  ['😡', 'Angry'],
  ['😊', 'Happy'],
  ['🥹', 'Emotional'],
  ['😌', 'Peaceful'],
  ['🤍', 'Talk'],
];

const samplePosts = [
  {
    id: 1,
    author: '@quiet_mind',
    emotion: 'Peaceful',
    time: 'Just now',
    content: 'Some days do not need to be productive. Sometimes a quiet day is enough.',
  },
  {
    id: 2,
    author: 'Anonymous',
    emotion: 'Anxious',
    time: '12 min ago',
    content: 'Trying to take things one step at a time instead of worrying about everything at once.',
  },
];

function App() {
  const [activeNav, setActiveNav] = useState('Feed');
  const [activeEmotion, setActiveEmotion] = useState(null);

  return (
    <div className="app-shell">
      <header className="topbar">
        <button className="icon-button" aria-label="Open menu">☰</button>
        <div className="brand">
          <span className="brand-mark">◉</span>
          <span>Inner Circle</span>
        </div>
        <button className="icon-button" aria-label="Notifications">🔔</button>
      </header>

      <main className="main-content">
        <section className="welcome">
          <p className="eyebrow">YOUR SPACE</p>
          <h1>What’s on your mind?</h1>
          <p className="subtitle">Share without fear. Listen without judgment.</p>
          <button className="share-button" onClick={() => setActiveNav('Share')}>
            + Share a thought
          </button>
        </section>

        <section className="emotion-section">
          <div className="section-heading">
            <h2>How are you feeling?</h2>
            <button onClick={() => setActiveNav('Explore')}>Explore all</button>
          </div>
          <div className="emotion-row">
            {emotions.map(([emoji, label]) => (
              <button
                className={`emotion-chip ${activeEmotion === label ? 'selected' : ''}`}
                key={label}
                onClick={() => setActiveEmotion(activeEmotion === label ? null : label)}
              >
                <span>{emoji}</span>
                <small>{label}</small>
              </button>
            ))}
          </div>
        </section>

        <section className="feed-section">
          <div className="feed-tabs">
            {['For You', 'My Circle', 'Following'].map((tab, index) => (
              <button className={index === 0 ? 'active' : ''} key={tab}>{tab}</button>
            ))}
          </div>

          {samplePosts.map((post) => (
            <article className="post-card" key={post.id}>
              <div className="post-meta">
                <div className="avatar">{post.author === 'Anonymous' ? '?' : post.author[1].toUpperCase()}</div>
                <div>
                  <strong>{post.author}</strong>
                  <span>{post.emotion} · {post.time}</span>
                </div>
              </div>
              <p className="post-content">{post.content}</p>
              <div className="reactions">
                <button>❤️ I hear you</button>
                <button>🤝 Relate</button>
                <button>🌱 Rooting for you</button>
              </div>
              <button className="comment-link">💬 Supportive comments</button>
            </article>
          ))}
        </section>
      </main>

      <nav className="bottom-nav" aria-label="Main navigation">
        {[
          ['🏠', 'Feed'],
          ['🔎', 'Explore'],
          ['＋', 'Share'],
          ['🤝', 'Connections'],
          ['👤', 'Profile'],
        ].map(([icon, label]) => (
          <button
            className={activeNav === label ? 'nav-active' : ''}
            key={label}
            onClick={() => setActiveNav(label)}
          >
            <span>{icon}</span>
            <small>{label}</small>
          </button>
        ))}
      </nav>
    </div>
  );
}

export default App;
