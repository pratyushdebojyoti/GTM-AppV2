import React, { useState } from 'react';
import {
  Briefcase,
  Plus,
  Search,
  Building,
  Mail,
  Phone,
  FolderKanban,
  Edit2,
  Trash2,
  CheckCircle2,
  XCircle,
  X,
  ShieldCheck
} from 'lucide-react';
import type { AgencyClient, AgencyProject } from '../types';

interface ClientsManagerProps {
  clients: AgencyClient[];
  projects: AgencyProject[];
  onSaveClient: (client: Partial<AgencyClient>) => Promise<void>;
  onDeleteClient: (clientId: string) => Promise<void>;
  onViewClientProjects: (clientId: string) => void;
}

export const ClientsManager: React.FC<ClientsManagerProps> = ({
  clients,
  projects,
  onSaveClient,
  onDeleteClient,
  onViewClientProjects
}) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingClient, setEditingClient] = useState<Partial<AgencyClient> | null>(null);
  const [saving, setSaving] = useState(false);

  const filteredClients = clients.filter(
    (c) =>
      c.companyName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.contactEmail.toLowerCase().includes(searchTerm.toLowerCase()) ||
      c.industry.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleOpenAdd = () => {
    setEditingClient({
      companyName: '',
      industry: 'Technology & Enterprise',
      tier: 'Enterprise Partner',
      status: 'ACTIVE',
      contactEmail: '',
      contactPhone: '',
      assignedAccountManager: 'Executive Partner Lead',
      userId: `client_user_${Date.now()}`
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (client: AgencyClient) => {
    setEditingClient({ ...client });
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingClient?.companyName || !editingClient?.contactEmail) {
      alert('Please provide at least Company Name and Primary Email.');
      return;
    }
    setSaving(true);
    try {
      await onSaveClient(editingClient);
      setIsModalOpen(false);
      setEditingClient(null);
    } catch (err: any) {
      alert(`Error saving client: ${err.message}`);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">Client Management</h1>
          <p className="text-xs text-slate-400 mt-1">
            Authorized enterprise organizations, contracted accounts, and associated engagements.
          </p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 transition-all flex items-center gap-2 shadow-md shadow-cyan-500/20"
        >
          <Plus className="w-4 h-4" />
          Add Client Organization
        </button>
      </div>

      {/* Search Bar */}
      <div className="glass-panel p-4 rounded-2xl flex items-center justify-between gap-4">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search enterprise clients by organization name, email, or industry..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="glass-input w-full pl-10 pr-4 py-2 rounded-xl text-xs"
          />
        </div>
        <span className="text-xs text-slate-400 whitespace-nowrap">
          {filteredClients.length} of {clients.length} Clients
        </span>
      </div>

      {/* Clients Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredClients.length === 0 ? (
          <div className="col-span-full glass-panel p-12 rounded-2xl text-center">
            <Briefcase className="w-8 h-8 text-slate-600 mx-auto mb-3" />
            <p className="text-sm font-medium text-slate-400">No client accounts found</p>
            <p className="text-xs text-slate-600 mt-1">Add a new organization or convert an incoming lead.</p>
          </div>
        ) : (
          filteredClients.map((client) => {
            const clientProjects = projects.filter(
              (p) => p.clientId === client.id || p.userId === client.userId
            );

            return (
              <div
                key={client.id}
                className="glass-panel p-6 rounded-2xl flex flex-col justify-between hover:border-white/20 transition-all group"
              >
                <div>
                  <div className="flex items-start justify-between gap-3 mb-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-xl bg-slate-900 border border-white/10 flex items-center justify-center font-bold text-cyan-400 text-sm">
                        {client.companyName[0]?.toUpperCase() || 'C'}
                      </div>
                      <div>
                        <h3 className="text-sm font-bold text-white group-hover:text-cyan-300 transition-colors">
                          {client.companyName}
                        </h3>
                        <span className="text-[11px] text-slate-400">{client.industry}</span>
                      </div>
                    </div>
                    <span
                      className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border ${
                        client.status === 'ACTIVE'
                          ? 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30'
                          : 'bg-slate-800 text-slate-400 border-white/10'
                      }`}
                    >
                      {client.status}
                    </span>
                  </div>

                  <div className="space-y-2 text-xs text-slate-400 py-3 border-y border-white/5">
                    <div className="flex items-center justify-between">
                      <span className="flex items-center gap-1.5 text-slate-500">
                        <Mail className="w-3.5 h-3.5" /> Contact
                      </span>
                      <span className="text-white truncate max-w-[170px]">{client.contactEmail}</span>
                    </div>
                    {client.contactPhone && (
                      <div className="flex items-center justify-between">
                        <span className="flex items-center gap-1.5 text-slate-500">
                          <Phone className="w-3.5 h-3.5" /> Phone
                        </span>
                        <span className="text-white">{client.contactPhone}</span>
                      </div>
                    )}
                    <div className="flex items-center justify-between">
                      <span className="flex items-center gap-1.5 text-slate-500">
                        <ShieldCheck className="w-3.5 h-3.5 text-cyan-400" /> Tier
                      </span>
                      <span className="text-cyan-300 font-medium">{client.tier}</span>
                    </div>
                  </div>

                  {/* Active Projects Summary */}
                  <div className="mt-4 pt-1">
                    <div className="flex items-center justify-between text-xs mb-2">
                      <span className="text-slate-400 font-medium flex items-center gap-1.5">
                        <FolderKanban className="w-3.5 h-3.5 text-indigo-400" /> Active Engagements
                      </span>
                      <span className="text-cyan-400 font-bold">{clientProjects.length} Projects</span>
                    </div>
                    {clientProjects.length > 0 ? (
                      <div className="space-y-1">
                        {clientProjects.slice(0, 2).map((cp) => (
                          <div
                            key={cp.id}
                            className="p-2 rounded-lg bg-slate-900/60 border border-white/5 text-[11px] text-slate-300 flex items-center justify-between"
                          >
                            <span className="truncate">{cp.title}</span>
                            <span className="text-[10px] text-cyan-400 font-medium">{cp.progressPercentage}%</span>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <p className="text-[11px] text-slate-600 italic">No associated projects yet.</p>
                    )}
                  </div>
                </div>

                {/* Card Actions */}
                <div className="mt-6 pt-4 border-t border-white/5 flex items-center justify-between">
                  <button
                    onClick={() => onViewClientProjects(client.id)}
                    className="text-xs text-cyan-400 hover:text-cyan-300 font-medium flex items-center gap-1"
                  >
                    View Sprints ({clientProjects.length})
                  </button>

                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => handleOpenEdit(client)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/5 transition-colors"
                      title="Edit organization"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => {
                        if (confirm(`Delete organization record for "${client.companyName}"?`)) {
                          onDeleteClient(client.id);
                        }
                      }}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition-colors"
                      title="Delete account"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Add / Edit Client Modal */}
      {isModalOpen && editingClient && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm">
          <div className="glass-panel-elevated w-full max-w-lg p-6 rounded-2xl shadow-2xl relative">
            <div className="flex items-center justify-between pb-4 border-b border-white/10 mb-5">
              <h2 className="text-base font-bold text-white">
                {editingClient.id ? 'Edit Client Organization' : 'Add New Client Organization'}
              </h2>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-1 rounded-lg text-slate-400 hover:text-white hover:bg-white/5"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSave} className="space-y-4 text-xs">
              <div>
                <label className="block text-slate-300 font-medium mb-1">Company / Organization Name</label>
                <input
                  type="text"
                  required
                  value={editingClient.companyName || ''}
                  onChange={(e) => setEditingClient({ ...editingClient, companyName: e.target.value })}
                  placeholder="e.g. Apex Financial Technologies"
                  className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Industry</label>
                  <input
                    type="text"
                    value={editingClient.industry || ''}
                    onChange={(e) => setEditingClient({ ...editingClient, industry: e.target.value })}
                    placeholder="Fintech, HealthTech, AI..."
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Account Tier</label>
                  <select
                    value={editingClient.tier || 'Enterprise'}
                    onChange={(e) => setEditingClient({ ...editingClient, tier: e.target.value })}
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs bg-slate-900"
                  >
                    <option value="Enterprise Partner">Enterprise Partner</option>
                    <option value="Growth Tier">Growth Tier</option>
                    <option value="Custom Retainer">Custom Retainer</option>
                    <option value="Pilot Sprint">Pilot Sprint</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Primary Email</label>
                  <input
                    type="email"
                    required
                    value={editingClient.contactEmail || ''}
                    onChange={(e) => setEditingClient({ ...editingClient, contactEmail: e.target.value })}
                    placeholder="partner@company.com"
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Phone Number</label>
                  <input
                    type="tel"
                    value={editingClient.contactPhone || ''}
                    onChange={(e) => setEditingClient({ ...editingClient, contactPhone: e.target.value })}
                    placeholder="+1 (555) 019-2834"
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Status</label>
                  <select
                    value={editingClient.status || 'ACTIVE'}
                    onChange={(e) => setEditingClient({ ...editingClient, status: e.target.value as any })}
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs bg-slate-900"
                  >
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="ONBOARDING">ONBOARDING</option>
                    <option value="INACTIVE">INACTIVE</option>
                    <option value="CHURNED">CHURNED</option>
                  </select>
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Assigned Account Lead</label>
                  <input
                    type="text"
                    value={editingClient.assignedAccountManager || ''}
                    onChange={(e) => setEditingClient({ ...editingClient, assignedAccountManager: e.target.value })}
                    placeholder="Managing Partner"
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
                  />
                </div>
              </div>

              <div className="pt-4 flex items-center justify-end gap-3 border-t border-white/10">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="px-5 py-2 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 disabled:opacity-50"
                >
                  {saving ? 'Saving...' : 'Save Organization'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
