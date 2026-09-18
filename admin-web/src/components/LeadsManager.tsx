import React, { useState } from 'react';
import {
  Search,
  Filter,
  Users,
  Mail,
  Phone,
  Building,
  DollarSign,
  Calendar,
  Clock,
  CheckCircle2,
  XCircle,
  ArrowRight,
  UserCheck,
  Trash2,
  FileText
} from 'lucide-react';
import type { AgencyLead, LeadStatus } from '../types';

interface LeadsManagerProps {
  leads: AgencyLead[];
  onUpdateLead: (leadId: string, updates: Partial<AgencyLead>) => Promise<void>;
  onDeleteLead: (leadId: string) => Promise<void>;
  onConvertToClient: (lead: AgencyLead) => Promise<void>;
}

export const LeadsManager: React.FC<LeadsManagerProps> = ({
  leads,
  onUpdateLead,
  onDeleteLead,
  onConvertToClient
}) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [selectedLead, setSelectedLead] = useState<AgencyLead | null>(leads[0] || null);
  const [converting, setConverting] = useState(false);
  const [notes, setNotes] = useState('');

  const statuses: LeadStatus[] = [
    'New',
    'Contacted',
    'Qualified',
    'Proposal',
    'Won',
    'Lost',
    'Archived'
  ];

  const filteredLeads = leads.filter((lead) => {
    const matchesSearch =
      lead.clientName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      lead.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (lead.company && lead.company.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (lead.serviceRequested && lead.serviceRequested.toLowerCase().includes(searchTerm.toLowerCase()));

    const matchesStatus = statusFilter === 'ALL' || lead.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const handleStatusChange = async (lead: AgencyLead, newStatus: LeadStatus) => {
    await onUpdateLead(lead.id, { status: newStatus });
    if (selectedLead?.id === lead.id) {
      setSelectedLead({ ...selectedLead, status: newStatus });
    }
  };

  const handleSaveNotes = async () => {
    if (!selectedLead) return;
    await onUpdateLead(selectedLead.id, { notes });
    setSelectedLead({ ...selectedLead, notes });
  };

  const handleConvert = async (lead: AgencyLead) => {
    if (!confirm(`Convert lead "${lead.clientName} (${lead.company || 'Enterprise'})" to a verified Client Account and provision an initial Project?`)) {
      return;
    }
    setConverting(true);
    try {
      await onConvertToClient(lead);
      alert('Lead successfully converted to an active Client Account and initial Sprint Project provisioned!');
    } catch (err: any) {
      alert(`Error converting lead: ${err.message}`);
    } finally {
      setConverting(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">Lead Management</h1>
          <p className="text-xs text-slate-400 mt-1">
            Review incoming project enquiries, calculate quotes, and convert prospects to enterprise accounts.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-xs text-slate-400 font-medium bg-slate-900 border border-white/5 px-3 py-1.5 rounded-xl">
            {leads.length} Total Leads
          </span>
        </div>
      </div>

      {/* Search & Filter Bar */}
      <div className="glass-panel p-4 rounded-2xl flex flex-col md:flex-row items-center gap-4">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search leads by name, email, company, or service..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="glass-input w-full pl-10 pr-4 py-2 rounded-xl text-xs"
          />
        </div>

        <div className="flex items-center gap-2 w-full md:w-auto overflow-x-auto pb-1 md:pb-0">
          <Filter className="w-3.5 h-3.5 text-slate-500 shrink-0" />
          <button
            onClick={() => setStatusFilter('ALL')}
            className={`px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap transition-all ${
              statusFilter === 'ALL'
                ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/30'
                : 'text-slate-400 hover:text-white bg-slate-900/50'
            }`}
          >
            All ({leads.length})
          </button>
          {statuses.map((st) => {
            const count = leads.filter((l) => l.status === st).length;
            return (
              <button
                key={st}
                onClick={() => setStatusFilter(st)}
                className={`px-3 py-1.5 rounded-lg text-xs font-medium whitespace-nowrap transition-all ${
                  statusFilter === st
                    ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/30'
                    : 'text-slate-400 hover:text-white bg-slate-900/50'
                }`}
              >
                {st} ({count})
              </button>
            );
          })}
        </div>
      </div>

      {/* Two Pane Layout: Master List + Lead Detail View */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left: Leads Table / List */}
        <div className="lg:col-span-7 space-y-3">
          {filteredLeads.length === 0 ? (
            <div className="glass-panel p-12 rounded-2xl text-center">
              <Users className="w-8 h-8 text-slate-600 mx-auto mb-3" />
              <p className="text-sm font-medium text-slate-400">No matching leads found</p>
              <p className="text-xs text-slate-600 mt-1">Try refining your search terms or filter criteria.</p>
            </div>
          ) : (
            filteredLeads.map((lead) => {
              const isSelected = selectedLead?.id === lead.id;
              return (
                <div
                  key={lead.id}
                  onClick={() => {
                    setSelectedLead(lead);
                    setNotes(lead.notes || '');
                  }}
                  className={`glass-panel p-4 rounded-xl cursor-pointer transition-all ${
                    isSelected
                      ? 'border-cyan-500/50 bg-cyan-500/[0.05] shadow-lg shadow-cyan-500/5'
                      : 'hover:border-white/20'
                  }`}
                >
                  <div className="flex items-start justify-between gap-4">
                    <div className="min-w-0">
                      <div className="flex items-center gap-2">
                        <span className="text-sm font-bold text-white truncate">
                          {lead.clientName}
                        </span>
                        {lead.company && (
                          <span className="text-xs text-slate-400 truncate">
                            • {lead.company}
                          </span>
                        )}
                      </div>

                      <div className="text-xs text-slate-400 mt-1 flex flex-wrap items-center gap-3">
                        <span className="flex items-center gap-1">
                          <Mail className="w-3 h-3 text-slate-500" />
                          {lead.email}
                        </span>
                        {lead.phone && (
                          <span className="flex items-center gap-1">
                            <Phone className="w-3 h-3 text-slate-500" />
                            {lead.phone}
                          </span>
                        )}
                      </div>

                      <div className="mt-2.5 flex items-center gap-2">
                        <span className="text-[11px] font-medium text-cyan-400 bg-cyan-500/10 px-2 py-0.5 rounded border border-cyan-500/20">
                          {lead.serviceRequested || 'Full Suite'}
                        </span>
                        <span className="text-[11px] text-slate-400 bg-slate-800 px-2 py-0.5 rounded">
                          {lead.budgetRange || 'Enterprise Tier'}
                        </span>
                      </div>
                    </div>

                    <div className="flex flex-col items-end gap-2 shrink-0">
                      <select
                        value={lead.status}
                        onClick={(e) => e.stopPropagation()}
                        onChange={(e) => handleStatusChange(lead, e.target.value as LeadStatus)}
                        className="bg-slate-900 border border-white/10 text-[11px] text-slate-300 rounded-lg px-2 py-1 focus:outline-none focus:border-cyan-500"
                      >
                        {statuses.map((st) => (
                          <option key={st} value={st}>
                            {st}
                          </option>
                        ))}
                      </select>
                      <span className="text-[10px] text-slate-500">
                        {new Date(lead.createdAtEpoch).toLocaleDateString()}
                      </span>
                    </div>
                  </div>
                </div>
              );
            })
          )}
        </div>

        {/* Right: Lead Detail Inspector & Convert to Client */}
        <div className="lg:col-span-5">
          {selectedLead ? (
            <div className="glass-panel p-6 rounded-2xl sticky top-6 space-y-6">
              {/* Header & Conversion CTA */}
              <div className="flex items-start justify-between gap-4 pb-4 border-b border-white/5">
                <div>
                  <h3 className="text-base font-bold text-white">{selectedLead.clientName}</h3>
                  <p className="text-xs text-slate-400">{selectedLead.company || 'Direct Client'}</p>
                </div>
                <button
                  onClick={() => handleConvert(selectedLead)}
                  disabled={converting || selectedLead.status === 'Won'}
                  className="px-3 py-1.5 rounded-xl text-xs font-semibold text-slate-950 bg-gradient-to-r from-emerald-400 to-teal-400 hover:from-emerald-300 hover:to-teal-300 disabled:opacity-40 transition-all flex items-center gap-1.5 shadow-sm"
                >
                  <UserCheck className="w-3.5 h-3.5" />
                  {selectedLead.status === 'Won' ? 'Converted Client' : converting ? 'Converting...' : 'Convert to Client'}
                </button>
              </div>

              {/* Contact Information */}
              <div className="space-y-2.5 text-xs">
                <div className="flex justify-between py-1.5 border-b border-white/5">
                  <span className="text-slate-400">Email Address</span>
                  <span className="text-white font-medium">{selectedLead.email}</span>
                </div>
                <div className="flex justify-between py-1.5 border-b border-white/5">
                  <span className="text-slate-400">Phone Number</span>
                  <span className="text-white font-medium">{selectedLead.phone || 'N/A'}</span>
                </div>
                <div className="flex justify-between py-1.5 border-b border-white/5">
                  <span className="text-slate-400">Budget Range</span>
                  <span className="text-cyan-400 font-medium">{selectedLead.budgetRange || 'Unspecified'}</span>
                </div>
                <div className="flex justify-between py-1.5 border-b border-white/5">
                  <span className="text-slate-400">Expected Timeline</span>
                  <span className="text-white font-medium">{selectedLead.expectedTimeline || 'Immediate'}</span>
                </div>
                {selectedLead.referenceWebsite && (
                  <div className="flex justify-between py-1.5 border-b border-white/5">
                    <span className="text-slate-400">Reference URL</span>
                    <a
                      href={selectedLead.referenceWebsite}
                      target="_blank"
                      rel="noreferrer"
                      className="text-cyan-400 hover:underline truncate max-w-[200px]"
                    >
                      {selectedLead.referenceWebsite}
                    </a>
                  </div>
                )}
              </div>

              {/* Project Scope Description */}
              <div>
                <span className="text-xs font-semibold text-slate-300 block mb-2">Scope & Requirements</span>
                <div className="p-3.5 rounded-xl bg-slate-900/60 border border-white/5 text-xs text-slate-300 leading-relaxed max-h-40 overflow-y-auto">
                  {selectedLead.projectDescription || 'No detailed scope description provided.'}
                </div>
              </div>

              {/* Internal Notes */}
              <div>
                <div className="flex items-center justify-between mb-2">
                  <span className="text-xs font-semibold text-slate-300">Executive Notes</span>
                  <button
                    onClick={handleSaveNotes}
                    className="text-[11px] text-cyan-400 hover:text-cyan-300 font-medium"
                  >
                    Save Notes
                  </button>
                </div>
                <textarea
                  rows={3}
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                  placeholder="Add internal qualification notes, call logs, or partner requirements..."
                  className="glass-input w-full p-3 rounded-xl text-xs resize-none"
                />
              </div>

              {/* Danger Zone: Delete */}
              <div className="pt-4 border-t border-white/5 flex justify-end">
                <button
                  onClick={() => {
                    if (confirm('Permanently delete this prospective lead?')) {
                      onDeleteLead(selectedLead.id);
                      setSelectedLead(null);
                    }
                  }}
                  className="text-xs text-red-400 hover:text-red-300 flex items-center gap-1.5 px-3 py-1.5 rounded-lg hover:bg-red-500/10 transition-colors"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                  Delete Lead
                </button>
              </div>
            </div>
          ) : (
            <div className="glass-panel p-12 rounded-2xl text-center text-slate-500 text-xs">
              Select a lead from the list to inspect details and convert to client.
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
