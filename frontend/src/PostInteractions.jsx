import { useEffect, useState } from 'react';
import { getReactions, reactToPost, removeReaction, getComments, createComment } from './api.js';

const reactions = [['I_HEAR_YOU','❤️ I hear you'],['RELATE','🤝 Relate'],['ROOTING_FOR_YOU','🌱 Rooting for you']];

export default function PostInteractions({ postId }) {
  const [reactionData, setReactionData] = useState({ counts: {}, myReaction: null });
  const [comments, setComments] = useState([]);
  const [open, setOpen] = useState(false);
  const [comment, setComment] = useState('');
  const [anonymous, setAnonymous] = useState(false);
  const [error, setError] = useState('');

  async function refreshReactions() { try { setReactionData(await getReactions(postId)); } catch (err) { setError(err.message); } }
  async function refreshComments() { try { const page = await getComments(postId); setComments(page.content || []); } catch (err) { setError(err.message); } }
  useEffect(() => { refreshReactions(); }, [postId]);

  async function chooseReaction(type) {
    try { if (reactionData.myReaction === type) await removeReaction(postId, type); else await reactToPost(postId, type); await refreshReactions(); }
    catch (err) { setError(err.message); }
  }

  async function submitComment(event) {
    event.preventDefault(); if (!comment.trim()) return;
    try { const created = await createComment(postId, comment.trim(), anonymous); setComments((current) => [...current, created]); setComment(''); setOpen(true); }
    catch (err) { setError(err.message); }
  }

  async function toggleComments() { if (!open) await refreshComments(); setOpen(!open); }

  return <div className="post-interactions">
    <div className="reactions">{reactions.map(([type,label]) => <button className={reactionData.myReaction === type ? 'reaction active' : 'reaction'} key={type} onClick={() => chooseReaction(type)}>{label}{reactionData.counts?.[type] ? ` — ${reactionData.counts[type]}` : ''}</button>)}</div>
    <button className="comment-link" onClick={toggleComments}>💬 Supportive comments {open ? '↑' : '↓'}</button>
    {open && <div className="comments-panel">
      {comments.map((item) => <div className="comment-item" key={item.id}><strong>{item.author}</strong><p>{item.content}</p></div>)}
      {comments.length === 0 && <p className="empty-state">No comments yet. Be the first to offer support.</p>}
      <form className="comment-form" onSubmit={submitComment}><textarea value={comment} onChange={(event) => setComment(event.target.value)} maxLength={2000} rows={3} placeholder="Offer a supportive thought…" required /><label><input type="checkbox" checked={anonymous} onChange={(event) => setAnonymous(event.target.checked)} /> Comment anonymously</label><button className="auth-submit" disabled={!comment.trim()}>Add support</button></form>
    </div>}
    {error && <p className="auth-message">{error}</p>}
  </div>;
}
