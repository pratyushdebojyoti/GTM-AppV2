import React, { useState } from 'react';
import {
  Bell,
  Send,
  Users,
  CheckCircle2,
  AlertCircle,
  Radio,
  Clock,
  Sparkles
} from 'lucide-react';
import type { AgencyNotification, AgencyClient } from '../types';

interface NotificationsManagerProps {
  notifications: AgencyNotification[];
  clients: AgencyClient[];
  onPublishNotification: (notification: Omit<AgencyNotification, 'id' | 'createdAtEpoch' | 'isRead'>) => Promise<void>;
}

export const NotificationsManager: React.FC<NotificationsManagerProps> = ({
  notifications,
  clients,
  onPublishNotification
}) => {
  const [targetType, setTargetType] = useState<'ALL' | 'SPECIFIC'>('ALL');
  const [selectedRecipientId, setSelectedRecipientId] = useState<string>('ALL');
  const [title, setTitle] = useState('');
  const [body, setBody] = useState('');
  const [notifType, setNotifType] = useState('ANNOUNCEMENT');
  const [publishing, setPublishing] = useState(false);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  const handlePublish = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title || !body) return;
    setPublishing(true);
    setSuccessMsg(null);
    try {
      await onPublishNotification({
        recipientId: targetType === 'ALL' ? 'ALL' : selectedRecipientId,
        title,
        body,
        type: notifType
      });
      setTitle('');
      setBody('');
      setSuccessMsg('Broadcast alert published successfully to client notification feed!');
      setTimeout(() => setSuccessMsg(null), 4000);
    } catch (err: any) {
      alert(`Failed to broadcast: ${err.message}`);
    } finally {
      setPublishing(false);
    }
  };

  return (
    <div className="space-y-6 max-w-5xl">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-white">Broadcast Notifications</h1>
        <p className="text-xs text-slate-400 mt-1">
          Publish push notifications and in-app announcements to all enterprise clients or targeted partner accounts.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left: Broadcast Composer */}
        <div className="lg:col-span-6 glass-panel p-6 rounded-2xl space-y-5">
          <div className="flex items-center gap-2 pb-3 border-b border-white/5">
            <Radio className="w-4 h-4 text-cyan-400 animate-pulse" />
            <h2 className="text-sm font-bold text-white">Compose Broadcast Alert</h2>
          </div>

          {successMsg && (
            <div className="p-3.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center gap-2 text-xs text-emerald-300">
              <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
              <span>{successMsg}</span>
            </div>
          )}

          <form onSubmit={handlePublish} className="space-y-4 text-xs">
            {/* Target Audience */}
            <div>
              <label className="block text-slate-300 font-medium mb-1.5">Target Audience</label>
              <div className="grid grid-cols-2 gap-3 mb-2">
                <button
                  type="button"
                  onClick={() => {
                    setTargetType('ALL');
                    setSelectedRecipientId('ALL');
                  }}
                  className={`py-2 px-3 rounded-xl border text-xs font-medium transition-all ${
                    targetType === 'ALL'
                      ? 'bg-cyan-500/20 text-cyan-300 border-cyan-500/40'
                      : 'bg-slate-900/60 text-slate-400 border-white/5'
                  }`}
                >
                  All Active Clients ({clients.length})
                </button>
                <button
                  type="button"
                  onClick={() => setTargetType('SPECIFIC')}
                  className={`py-2 px-3 rounded-xl border text-xs font-medium transition-all ${
                    targetType === 'SPECIFIC'
                      ? 'bg-cyan-500/20 text-cyan-300 border-cyan-500/40'
                      : 'bg-slate-900/60 text-slate-400 border-white/5'
                  }`}
                >
                  Target Specific Partner
                </button>
              </div>

              {targetType === 'SPECIFIC' && (
                <select
                  value={selectedRecipientId}
                  onChange={(e) => setSelectedRecipientId(e.target.value)}
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs bg-slate-900 mt-2"
                >
                  {clients.map((c) => (
                    <option key={c.id} value={c.userId}>
                      {c.companyName} ({c.contactEmail})
                    </option>
                  ))}
                </select>
              )}
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-slate-300 font-medium mb-1">Notification Category</label>
                <select
                  value={notifType}
                  onChange={(e) => setNotifType(e.target.value)}
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs bg-slate-900"
                >
                  <option value="ANNOUNCEMENT">System Announcement</option>
                  <option value="SPRINT_UPDATE">Sprint Roadmap Update</option>
                  <option value="DEPLOYMENT">Production Deployment</option>
                  <option value="MAINTENANCE">Scheduled Maintenance</option>
                </select>
              </div>
              <div>
                <label className="block text-slate-300 font-medium mb-1">Subject / Headline</label>
                <input
                  type="text"
                  required
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="e.g. Q4 Sprint Architecture Delivered"
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                />
              </div>
            </div>

            <div>
              <label className="block text-slate-300 font-medium mb-1">Message Body</label>
              <textarea
                rows={4}
                required
                value={body}
                onChange={(e) => setBody(e.target.value)}
                placeholder="Detailed announcement or sprint update information for client review..."
                className="glass-input w-full p-3 rounded-xl text-xs leading-relaxed"
              />
            </div>

            <button
              type="submit"
              disabled={publishing}
              className="w-full py-2.5 px-4 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 disabled:opacity-50 transition-all flex items-center justify-center gap-2 shadow-md shadow-cyan-500/20"
            >
              <Send className="w-3.5 h-3.5" />
              {publishing ? 'Publishing Broadcast...' : 'Publish Notification Broadcast'}
            </button>
          </form>
        </div>

        {/* Right: Broadcast History */}
        <div className="lg:col-span-6 glass-panel p-6 rounded-2xl space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-white/5">
            <h2 className="text-sm font-bold text-white flex items-center gap-2">
              <Bell className="w-4 h-4 text-cyan-400" /> Broadcast Dispatch History
            </h2>
            <span className="text-[11px] text-slate-500">{notifications.length} Alerts</span>
          </div>

          <div className="space-y-3 max-h-[500px] overflow-y-auto">
            {notifications.length === 0 ? (
              <p className="text-xs text-slate-500 italic text-center py-10">No broadcast history recorded.</p>
            ) : (
              notifications.map((n) => (
                <div key={n.id} className="p-3.5 rounded-xl bg-slate-900/40 border border-white/5 space-y-1.5">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-bold text-white">{n.title}</span>
                    <span className="text-[10px] text-cyan-400 bg-cyan-500/10 px-2 py-0.5 rounded">
                      {n.type}
                    </span>
                  </div>
                  <p className="text-xs text-slate-300 leading-relaxed">{n.body}</p>
                  <div className="flex items-center justify-between text-[10px] text-slate-500 pt-1">
                    <span>Target: {n.recipientId === 'ALL' ? 'All Enterprise Clients' : `User (${n.recipientId})`}</span>
                    <span>{new Date(n.createdAtEpoch).toLocaleDateString()}</span>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
