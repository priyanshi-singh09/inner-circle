import { useEffect, useState } from 'react';
import { getNotifications, markAllNotificationsRead, markNotificationRead } from './api.js';
export default function Notifications({ onBack }) {
 const [items,setItems]=useState([]),[error,setError]=useState('');
 async function load(){try{const page=await getNotifications();setItems(page.content||[]);}catch(err){setError(err.message);}}
 useEffect(()=>{load();},[]);
 async function read(id){try{await markNotificationRead(id);setItems(c=>c.map(x=>x.id===id?{...x,read:true}:x));}catch(err){setError(err.message);}}
 async function readAll(){try{await markAllNotificationsRead();setItems(c=>c.map(x=>({...x,read:true})));}catch(err){setError(err.message);}}
 return <main className="notifications-page"><section className="notifications-card"><button className="back-button" onClick={onBack}>← Back to feed</button><div className="notification-heading"><div><p className="eyebrow">YOUR CIRCLE</p><h1>Notifications</h1></div><button onClick={readAll}>Mark all read</button></div>{error&&<p className="auth-message">{error}</p>}{items.length===0&&!error&&<p className="empty-state">You’re all caught up.</p>}{items.map(item=><button className={item.read?'notification read':'notification unread'} key={item.id} onClick={()=>!item.read&&read(item.id)}><span className="notification-dot">{item.read?'○':'●'}</span><span><strong>{item.type.replaceAll('_',' ')}</strong><p>{item.message}</p><small>{new Date(item.createdAt).toLocaleString()}</small></span></button>)}</section></main>;
}
