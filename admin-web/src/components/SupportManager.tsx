import React, { useState } from 'react';
import {
  LifeBuoy,
  Search,
  Filter,
  AlertTriangle,
  Clock,
  CheckCircle2,
  XCircle,
  MessageSquare,
  User,
  ShieldAlert
} from 'lucide-react';
import type { AgencySupportTicket } from '../types';

interface SupportManagerProps {
  tickets: AgencySupportTicket[];
  onUpdateTicket: (ticketId: string, updates: Partial<AgencySupportTicket>) => Promise<void>;
}

export const SupportManager: React.FC<SupportManagerProps> = ({ tickets, onUpdateTicket }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');

  const statuses = ['Open', 'In Progress', 'Resolved', 'Closed'];
  const priorities = ['High', 'Medium', 'Low'];

  const filtered = tickets.filter((t) => {
    const matchesSearch =
      t.subject.toLowerCase().includes(searchTerm.toLowerCase()) ||
      t.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || t.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const handleStatusChange = async (ticketId: string, status: any) => {
    await onUpdateTicket(ticketId, { status });
  };

  const handlePriorityChange = async (ticketId: string, priority: any) => {
    await onUpdateTicket(ticketId, { priority });
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">Client Support Tickets & SLA</h1>
          <p className="text-xs text-slate-400 mt-1">
            Enterprise partner support queue, incident triage, priority escalation, and resolution tracking.
          </p>
        </div>
        <span className="text-xs text-slate-400 font-medium bg-slate-900 border border-white/5 px-3 py-1.5 rounded-xl">
          {tickets.filter((t) => t.status === 'Open' || t.status === 'In Progress').length} Active SLA Tickets
        </span>
      </div>

      {/* Filter Bar */}
      <div className="glass-panel p-4 rounded-2xl flex flex-col md:flex-row items-center gap-4">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search tickets by subject or description..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="glass-input w-full pl-10 pr-4 py-2 rounded-xl text-xs"
          />
        </div>

        <div className="flex items-center gap-2 w-full md:w-auto overflow-x-auto pb-1 md:pb-0">
          <button
            onClick={() => setStatusFilter('ALL')}
            className={`px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap ${
              statusFilter === 'ALL'
                ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/30'
                : 'text-slate-400 hover:text-white bg-slate-900/50'
            }`}
          >
            All ({tickets.length})
          </button>
          {statuses.map((st) => (
            <button
              key={st}
              onClick={() => setStatusFilter(st)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap ${
                statusFilter === st
                  ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/30'
                  : 'text-slate-400 hover:text-white bg-slate-900/50'
              }`}
            >
              {st} ({tickets.filter((t) => t.status === st).length})
            </button>
          ))}
        </div>
      </div>

      {/* Tickets List */}
      <div className="space-y-3">
        {filtered.length === 0 ? (
          <div className="glass-panel p-12 rounded-2xl text-center text-slate-500 text-xs">
            No support tickets match the selected filters.
          </div>
        ) : (
          filtered.map((ticket) => (
            <div
              key={ticket.id}
              className="glass-panel p-5 rounded-2xl hover:border-white/20 transition-all flex flex-col md:flex-row md:items-center md:justify-between gap-4"
            >
              <div className="min-w-0 flex-1">
                <div className="flex items-center gap-2.5 mb-1.5">
                  <span
                    className={`text-[10px] font-bold px-2 py-0.5 rounded-full border ${
                      ticket.priority === 'High'
                        ? 'bg-red-500/10 text-red-400 border-red-500/30'
                        : ticket.priority === 'Medium'
                        ? 'bg-amber-500/10 text-amber-400 border-amber-500/30'
                        : 'bg-cyan-500/10 text-cyan-400 border-cyan-500/30'
                    }`}
                  >
                    {ticket.priority} Priority
                  </span>
                  <h3 className="text-sm font-bold text-white truncate">{ticket.subject}</h3>
                </div>

                <p className="text-xs text-slate-400 line-clamp-2 leading-relaxed mb-2">
                  {ticket.description}
                </p>

                <div className="flex flex-wrap items-center gap-4 text-[10px] text-slate-500">
                  <span>Client UID: {ticket.userId}</span>
                  {ticket.projectId && <span>Project ID: {ticket.projectId}</span>}
                  <span>Updated: {new Date(ticket.updatedAtEpoch || ticket.createdAtEpoch).toLocaleDateString()}</span>
                </div>
              </div>

              {/* Status and Priority selectors */}
              <div className="flex items-center gap-3 shrink-0 pt-3 md:pt-0 border-t md:border-t-0 border-white/5">
                <div>
                  <label className="block text-[10px] text-slate-500 mb-1">Status</label>
                  <select
                    value={ticket.status}
                    onChange={(e) => handleStatusChange(ticket.id, e.target.value)}
                    className="glass-input px-3 py-1.5 rounded-lg text-xs bg-slate-900"
                  >
                    {statuses.map((st) => (
                      <option key={st} value={st}>
                        {st}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-[10px] text-slate-500 mb-1">Escalation Priority</label>
                  <select
                    value={ticket.priority}
                    onChange={(e) => handlePriorityChange(ticket.id, e.target.value)}
                    className="glass-input px-3 py-1.5 rounded-lg text-xs bg-slate-900"
                  >
                    {priorities.map((p) => (
                      <option key={p} value={p}>
                        {p}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
