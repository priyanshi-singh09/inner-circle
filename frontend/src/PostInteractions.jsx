import { useEffect, useState } from 'react';
import { getReactions, reactToPost, removeReaction, getComments, createComment, createReport } from './api.js';

const reactions = [['I_HEAR_YOU','❤️ I hear you'],['RELATE','🤝 Relate'],['ROOTING_FOR_YOU','🌱 Rooting for you']];
const reportReasons = ['Harassment or bullying','Threatening or unsafe content','Spam or misleading content','Privacy concern','Other'];

export default function PostInteractions({ postId }) {
  const [reactionData, setReactionData] = useState({ counts: {}, myReaction: null });
  const [comments, setComments] = useState([]);
  const [open, setOpen] = useState(false);
  const [comment, setComment] = useState('');
  const [anonymous, setAnonymous] = useState(false);
  const [error, setError] = useState('');
  const [reporting, setReporting] = useState(false);
  const [reported, setReported] = useState(false);

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

  async function reportPost() {
    if (reporting || reported) return;
    const reason = window.prompt(`Why are you reporting this post?\n\n${reportReasons.map((item, index) => `${index + 1}. ${item}`).join('\n')}`);
    if (!reason) return;
    const selected = Number.parseInt(reason, 10);
    const reportReason = Number.isInteger(selected) && selected >= 1 && selected <= reportReasons.length ? reportReasons[selected - 1] : reason.trim();
    if (!reportReason) return;
    const description = window.prompt('Add a little more context (optional):') || '';
    try { setReporting(true); await createReport({ postId, reason: reportReason.slice(0, 40), description: description.slice(0, 1000) }); setReported(true); setError(''); }
    catch (err) { setError(err.message); }
    finally { setReporting(false); }
  }

  async function reportComment(commentId) {
    const reason = window.prompt(`Why are you reporting this comment?\n\n${reportReasons.map((item, index) => `${index + 1}. ${item}`).join('\n')}`);
    if (!reason) return;
    const selected = Number.parseInt(reason, 10);
    const reportReason = Number.isInteger(selected) && selected >= 1 && selected <= reportReasons.length ? reportReasons[selected - 1] : reason.trim();
    if (!reportReason) return;
    try { await createReport({ commentId, reason: reportReason.slice(0, 40) }); setError('Comment reported. Thank you for helping keep Inner Circle safe.'); }
    catch (err) { setError(err.message); }
  }

  async function toggleComments() { if (!open) await refreshComments(); setOpen(!open); }

  return <div className="post-interactions">
    <div className="reactions">{reactions.map(([type,label]) => <button className={reactionData.myReaction === type ? 'reaction active' : 'reaction'} key={type} onClick={() => chooseReaction(type)}>{label}{reactionData.counts?.[type] ? ` — ${reactionData.counts[type]}` : ''}</button>)}</div>
    <div className="interaction-secondary">
      <button className="comment-link" onClick={toggleComments}>💬 Supportive comments {open ? '↑' : '↓'}</button>
      <button className="report-link" onClick={reportPost} disabled={reporting || reported}>{reported ? '✓ Reported' : reporting ? 'Reporting…' : 'Report'}</button>
    </div>
    {open && <div className="comments-panel">
      {comments.map((item) => <div className="comment-item" key={item.id}><div><strong>{item.author}</strong><p>{item.content}</p></div><button className="report-link" onClick={() => reportComment(item.id)}>Report</button></div>)}
      {comments.length === 0 && <p className="empty-state">No comments yet. Be the first to offer support.</p>}
      <form className="comment-form" onSubmit={submitComment}><textarea value={comment} onChange={(event) => setComment(event.target.value)} maxLength={2000} rows={3} placeholder="Offer a supportive thought…" required /><label><input type="checkbox" checked={anonymous} onChange={(event) => setAnonymous(event.target.checked)} /> Comment anonymously</label><button className="auth-submit" disabled={!comment.trim()}>Add support</button></form>
    </div>}
    {error && <p className="auth-message">{error}</p>}
  </div>;
}
