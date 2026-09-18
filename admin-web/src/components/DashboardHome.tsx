import React from 'react';
import {
  Users,
  Briefcase,
  FolderKanban,
  FileCheck2,
  ArrowUpRight,
  Clock,
  DollarSign,
  AlertTriangle,
  Send,
  ExternalLink
} from 'lucide-react';
import type { AgencyLead, AgencyClient, AgencyProject, AgencySupportTicket } from '../types';

interface DashboardHomeProps {
  leads: AgencyLead[];
  clients: AgencyClient[];
  projects: AgencyProject[];
  tickets: AgencySupportTicket[];
  onNavigateTab: (tab: any) => void;
  onSelectLead: (lead: AgencyLead) => void;
  onSelectProject: (project: AgencyProject) => void;
}

export const DashboardHome: React.FC<DashboardHomeProps> = ({
  leads,
  clients,
  projects,
  tickets,
  onNavigateTab,
  onSelectLead,
  onSelectProject
}) => {
  const newLeads = leads.filter((l) => l.status === 'New' || l.status === 'Contacted');
  const activeClients = clients.filter((c) => c.status === 'ACTIVE');
  const activeProjects = projects.filter((p) => p.status !== 'Completed' && p.status !== 'On Hold');
  const openTickets = tickets.filter((t) => t.status === 'Open' || t.status === 'In Progress');

  const recentEnquiries = leads.slice(0, 5);

  const stats = [
    {
      label: 'Total Leads',
      value: leads.length,
      change: `${newLeads.length} new/pending triage`,
      icon: Users,
      color: 'text-cyan-400',
      bg: 'bg-cyan-500/10',
      border: 'border-cyan-500/20',
      actionTab: 'leads'
    },
    {
      label: 'Active Clients',
      value: activeClients.length,
      change: 'Verified enterprise contracts',
      icon: Briefcase,
      color: 'text-sky-400',
      bg: 'bg-sky-500/10',
      border: 'border-sky-500/20',
      actionTab: 'clients'
    },
    {
      label: 'Active Projects',
      value: activeProjects.length,
      change: 'In active sprint delivery',
      icon: FolderKanban,
      color: 'text-indigo-400',
      bg: 'bg-indigo-500/10',
      border: 'border-indigo-500/20',
      actionTab: 'projects'
    },
    {
      label: 'Open Support Tickets',
      value: openTickets.length,
      change: `${tickets.filter(t => t.priority === 'High').length} high priority SLA`,
      icon: AlertTriangle,
      color: 'text-amber-400',
      bg: 'bg-amber-500/10',
      border: 'border-amber-500/20',
      actionTab: 'support'
    }
  ];

  return (
    <div className="space-y-8">
      {/* Top Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">Agency Mission Control</h1>
          <p className="text-xs text-slate-400 mt-1">
            Real-time pipeline metrics, active client delivery, and contract health.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => onNavigateTab('leads')}
            className="px-3.5 py-2 rounded-xl text-xs font-medium text-slate-300 bg-slate-800/80 hover:bg-slate-800 border border-white/10 transition-colors flex items-center gap-2"
          >
            <Users className="w-3.5 h-3.5 text-cyan-400" />
            Triage Inquiries
          </button>
          <button
            onClick={() => onNavigateTab('projects')}
            className="px-3.5 py-2 rounded-xl text-xs font-medium text-slate-950 bg-cyan-400 hover:bg-cyan-300 transition-colors flex items-center gap-2 font-semibold shadow-md shadow-cyan-500/20"
          >
            <FolderKanban className="w-3.5 h-3.5" />
            Active Sprints
          </button>
        </div>
      </div>

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat, i) => {
          const Icon = stat.icon;
          return (
            <div
              key={i}
              onClick={() => onNavigateTab(stat.actionTab)}
              className="glass-panel p-5 rounded-2xl hover:border-white/20 transition-all cursor-pointer group"
            >
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-medium text-slate-400">{stat.label}</span>
                <div className={`p-2 rounded-xl ${stat.bg} ${stat.color} border ${stat.border}`}>
                  <Icon className="w-4 h-4" />
                </div>
              </div>
              <div className="text-2xl font-bold text-white tracking-tight group-hover:text-cyan-300 transition-colors">
                {stat.value}
              </div>
              <p className="text-[11px] text-slate-500 mt-1 flex items-center justify-between">
                <span>{stat.change}</span>
                <ArrowUpRight className="w-3.5 h-3.5 opacity-0 group-hover:opacity-100 transition-opacity text-cyan-400" />
              </p>
            </div>
          );
        })}
      </div>

      {/* Two Column Layout: Recent Enquiries + Active Sprints */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Left Column: Recent Enquiries */}
        <div className="glass-panel p-6 rounded-2xl">
          <div className="flex items-center justify-between mb-5">
            <div>
              <h2 className="text-sm font-bold text-white flex items-center gap-2">
                <Users className="w-4 h-4 text-cyan-400" />
                Recent Enquiries & Quotes
              </h2>
              <p className="text-xs text-slate-400 mt-0.5">Prospective enterprise partners</p>
            </div>
            <button
              onClick={() => onNavigateTab('leads')}
              className="text-xs text-cyan-400 hover:text-cyan-300 flex items-center gap-1 font-medium"
            >
              View All ({leads.length})
              <ExternalLink className="w-3 h-3" />
            </button>
          </div>

          <div className="space-y-3">
            {recentEnquiries.length === 0 ? (
              <div className="text-center py-10 text-xs text-slate-500">
                No recent lead enquiries recorded.
              </div>
            ) : (
              recentEnquiries.map((lead) => (
                <div
                  key={lead.id}
                  onClick={() => onSelectLead(lead)}
                  className="p-3.5 rounded-xl bg-slate-900/40 hover:bg-slate-900/80 border border-white/5 hover:border-white/10 transition-all cursor-pointer flex items-center justify-between gap-4 group"
                >
                  <div className="min-w-0">
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-semibold text-white group-hover:text-cyan-300 transition-colors truncate">
                        {lead.clientName}
                      </span>
                      {lead.company && (
                        <span className="text-[11px] text-slate-400 truncate">
                          • {lead.company}
                        </span>
                      )}
                    </div>
                    <div className="flex items-center gap-2 mt-1">
                      <span className="text-[11px] text-slate-400">
                        {lead.serviceRequested || 'Custom Scope'}
                      </span>
                      <span className="text-[10px] text-cyan-400 bg-cyan-500/10 px-1.5 py-0.5 rounded border border-cyan-500/20">
                        {lead.budgetRange || 'Enterprise'}
                      </span>
                    </div>
                  </div>

                  <div className="text-right shrink-0">
                    <span
                      className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border ${
                        lead.status === 'New'
                          ? 'bg-cyan-500/10 text-cyan-300 border-cyan-500/30'
                          : lead.status === 'Qualified'
                          ? 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30'
                          : 'bg-slate-800 text-slate-300 border-white/10'
                      }`}
                    >
                      {lead.status}
                    </span>
                    <p className="text-[10px] text-slate-500 mt-1">
                      {new Date(lead.createdAtEpoch).toLocaleDateString()}
                    </p>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Right Column: Active Sprint Deliverables */}
        <div className="glass-panel p-6 rounded-2xl">
          <div className="flex items-center justify-between mb-5">
            <div>
              <h2 className="text-sm font-bold text-white flex items-center gap-2">
                <FolderKanban className="w-4 h-4 text-indigo-400" />
                Active Project Sprints
              </h2>
              <p className="text-xs text-slate-400 mt-0.5">Engineering roadmap velocity</p>
            </div>
            <button
              onClick={() => onNavigateTab('projects')}
              className="text-xs text-indigo-400 hover:text-indigo-300 flex items-center gap-1 font-medium"
            >
              All Projects ({projects.length})
              <ExternalLink className="w-3 h-3" />
            </button>
          </div>

          <div className="space-y-3">
            {activeProjects.length === 0 ? (
              <div className="text-center py-10 text-xs text-slate-500">
                No active projects in flight.
              </div>
            ) : (
              activeProjects.slice(0, 5).map((project) => (
                <div
                  key={project.id}
                  onClick={() => onSelectProject(project)}
                  className="p-3.5 rounded-xl bg-slate-900/40 hover:bg-slate-900/80 border border-white/5 hover:border-white/10 transition-all cursor-pointer group"
                >
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-semibold text-white group-hover:text-cyan-300 transition-colors truncate">
                      {project.title}
                    </span>
                    <span className="text-[10px] font-medium text-cyan-400 bg-cyan-500/10 px-2 py-0.5 rounded border border-cyan-500/20">
                      {project.status}
                    </span>
                  </div>

                  {/* Progress bar */}
                  <div className="w-full bg-slate-800 rounded-full h-1.5 mb-2 overflow-hidden">
                    <div
                      className="bg-gradient-to-r from-cyan-400 to-indigo-400 h-1.5 rounded-full transition-all duration-500"
                      style={{ width: `${project.progressPercentage}%` }}
                    />
                  </div>

                  <div className="flex items-center justify-between text-[11px] text-slate-400">
                    <span>{project.progressPercentage}% completed</span>
                    <span>Target: {project.expectedCompletion || 'Q4 2026'}</span>
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
