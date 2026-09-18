import React from 'react';
import {
  LayoutDashboard,
  Users,
  Briefcase,
  FolderKanban,
  Wrench,
  Sparkles,
  Quote,
  Bell,
  LifeBuoy,
  FileText,
  LogOut,
  ChevronRight
} from 'lucide-react';
import { useAuth } from '../services/AuthContext';

export type AdminTab =
  | 'dashboard'
  | 'leads'
  | 'clients'
  | 'projects'
  | 'services'
  | 'portfolio'
  | 'testimonials'
  | 'content'
  | 'notifications'
  | 'support';

interface SidebarProps {
  currentTab: AdminTab;
  onSelectTab: (tab: AdminTab) => void;
  counts: {
    newLeads: number;
    activeProjects: number;
    openTickets: number;
  };
}

export const Sidebar: React.FC<SidebarProps> = ({ currentTab, onSelectTab, counts }) => {
  const { currentUser, signOut } = useAuth();

  const navigationItems = [
    { id: 'dashboard' as AdminTab, label: 'Mission Control', icon: LayoutDashboard },
    { id: 'leads' as AdminTab, label: 'Lead Management', icon: Users, badge: counts.newLeads },
    { id: 'clients' as AdminTab, label: 'Client Accounts', icon: Briefcase },
    { id: 'projects' as AdminTab, label: 'Projects & Sprints', icon: FolderKanban, badge: counts.activeProjects },
    { id: 'support' as AdminTab, label: 'Support Tickets', icon: LifeBuoy, badge: counts.openTickets },
    { id: 'services' as AdminTab, label: 'Service Catalog', icon: Wrench },
    { id: 'portfolio' as AdminTab, label: 'Portfolio Work', icon: Sparkles },
    { id: 'testimonials' as AdminTab, label: 'Testimonials', icon: Quote },
    { id: 'content' as AdminTab, label: 'Agency Brand & Info', icon: FileText },
    { id: 'notifications' as AdminTab, label: 'Broadcast Alerts', icon: Bell }
  ];

  return (
    <aside className="w-64 bg-[#0A0D14]/90 border-r border-white/5 flex flex-col h-screen fixed left-0 top-0 z-20 backdrop-blur-xl">
      {/* Brand Header */}
      <div className="p-5 border-b border-white/5 flex items-center gap-3">
        <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-cyan-500 to-sky-400 flex items-center justify-center font-bold text-slate-950 text-sm shadow-md shadow-cyan-500/20">
          GT
        </div>
        <div>
          <h2 className="text-sm font-bold tracking-tight text-white flex items-center gap-1.5">
            GoTech Media
          </h2>
          <span className="text-[10px] uppercase font-semibold tracking-wider text-cyan-400 bg-cyan-500/10 px-1.5 py-0.5 rounded border border-cyan-500/20">
            ADMIN CONSOLE
          </span>
        </div>
      </div>

      {/* Navigation Links */}
      <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
        <div className="px-3 py-2 text-[10px] font-bold uppercase tracking-wider text-slate-500">
          Operations
        </div>
        {navigationItems.map((item) => {
          const Icon = item.icon;
          const isActive = currentTab === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onSelectTab(item.id)}
              className={`w-full flex items-center justify-between px-3 py-2.5 rounded-xl text-xs font-medium transition-all group ${
                isActive
                  ? 'bg-cyan-500/15 text-cyan-300 border border-cyan-500/30 shadow-sm'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-white/[0.04]'
              }`}
            >
              <div className="flex items-center gap-3">
                <Icon className={`w-4 h-4 transition-colors ${isActive ? 'text-cyan-400' : 'text-slate-500 group-hover:text-slate-300'}`} />
                <span>{item.label}</span>
              </div>
              {item.badge !== undefined && item.badge > 0 ? (
                <span className={`px-1.5 py-0.5 rounded-full text-[10px] font-bold ${
                  isActive ? 'bg-cyan-400 text-slate-950' : 'bg-slate-800 text-cyan-300'
                }`}>
                  {item.badge}
                </span>
              ) : (
                isActive && <ChevronRight className="w-3.5 h-3.5 opacity-60" />
              )}
            </button>
          );
        })}
      </nav>

      {/* User Profile & Sign Out Footer */}
      <div className="p-3 border-t border-white/5 bg-black/20">
        <div className="p-2.5 rounded-xl bg-slate-900/60 border border-white/5 flex items-center justify-between">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-8 h-8 rounded-lg bg-cyan-500/10 border border-cyan-500/20 text-cyan-400 flex items-center justify-center font-bold text-xs shrink-0">
              {currentUser?.displayName ? currentUser.displayName[0].toUpperCase() : 'A'}
            </div>
            <div className="min-w-0">
              <p className="text-xs font-medium text-white truncate">
                {currentUser?.displayName || 'Administrator'}
              </p>
              <p className="text-[10px] text-slate-500 truncate">
                {currentUser?.email || 'admin@gotechmedia.com'}
              </p>
            </div>
          </div>
          <button
            onClick={signOut}
            title="Sign out"
            className="p-1.5 rounded-lg text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition-colors"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </aside>
  );
};
