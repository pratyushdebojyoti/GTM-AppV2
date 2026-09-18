import React, { useEffect, useState } from 'react';
import { useAuth } from './services/AuthContext';
import { LoginScreen } from './components/LoginScreen';
import { Sidebar, AdminTab } from './components/Sidebar';
import { DashboardHome } from './components/DashboardHome';
import { LeadsManager } from './components/LeadsManager';
import { ClientsManager } from './components/ClientsManager';
import { ProjectsManager } from './components/ProjectsManager';
import { ServicesManager } from './components/ServicesManager';
import { PortfolioManager } from './components/PortfolioManager';
import { TestimonialsManager } from './components/TestimonialsManager';
import { ContentManager } from './components/ContentManager';
import { NotificationsManager } from './components/NotificationsManager';
import { SupportManager } from './components/SupportManager';
import { AdminApiService } from './services/adminApi';
import {
  AgencyLead,
  AgencyClient,
  AgencyProject,
  AgencySupportTicket,
  AgencyService,
  PortfolioItem,
  AgencyTestimonial,
  AgencySettings,
  AgencyNotification
} from './types';
import { Loader2, RefreshCw, AlertCircle } from 'lucide-react';

export const App: React.FC = () => {
  const { currentUser, loading: authLoading, isAdmin } = useAuth();
  const [currentTab, setCurrentTab] = useState<AdminTab>('dashboard');

  // Core Collections State
  const [leads, setLeads] = useState<AgencyLead[]>([]);
  const [clients, setClients] = useState<AgencyClient[]>([]);
  const [projects, setProjects] = useState<AgencyProject[]>([]);
  const [tickets, setTickets] = useState<AgencySupportTicket[]>([]);
  const [services, setServices] = useState<AgencyService[]>([]);
  const [portfolio, setPortfolio] = useState<PortfolioItem[]>([]);
  const [testimonials, setTestimonials] = useState<AgencyTestimonial[]>([]);
  const [settings, setSettings] = useState<AgencySettings | null>(null);
  const [notifications, setNotifications] = useState<AgencyNotification[]>([]);

  const [loadingData, setLoadingData] = useState<boolean>(true);
  const [refreshing, setRefreshing] = useState<boolean>(false);
  const [selectedClientIdForProjects, setSelectedClientIdForProjects] = useState<string | null>(null);

  // Load all admin data once authenticated
  useEffect(() => {
    if (isAdmin) {
      loadAllData();
    }
  }, [isAdmin]);

  const loadAllData = async () => {
    setLoadingData(true);
    try {
      const [
        leadsData,
        clientsData,
        projectsData,
        ticketsData,
        servicesData,
        portfolioData,
        testimonialsData,
        settingsData,
        notifsData
      ] = await Promise.all([
        AdminApiService.getLeads(),
        AdminApiService.getClients(),
        AdminApiService.getProjects(),
        AdminApiService.getSupportTickets(),
        AdminApiService.getServices(),
        AdminApiService.getPortfolio(),
        AdminApiService.getTestimonials(),
        AdminApiService.getSettings(),
        AdminApiService.getNotifications()
      ]);

      setLeads(leadsData);
      setClients(clientsData);
      setProjects(projectsData);
      setTickets(ticketsData);
      setServices(servicesData);
      setPortfolio(portfolioData);
      setTestimonials(testimonialsData);
      setSettings(settingsData);
      setNotifications(notifsData);
    } catch (err) {
      console.error('Failed to load administrative collections:', err);
    } finally {
      setLoadingData(false);
    }
  };

  const handleManualRefresh = async () => {
    setRefreshing(true);
    await loadAllData();
    setRefreshing(false);
  };

  if (authLoading) {
    return (
      <div className="min-h-screen bg-[#050508] flex flex-col items-center justify-center text-slate-400 gap-3">
        <Loader2 className="w-8 h-8 text-cyan-400 animate-spin" />
        <span className="text-xs font-medium tracking-wider uppercase">Authenticating GoTech Console...</span>
      </div>
    );
  }

  if (!isAdmin) {
    return <LoginScreen />;
  }

  const counts = {
    newLeads: leads.filter((l) => l.status === 'New' || l.status === 'Contacted').length,
    activeProjects: projects.filter((p) => p.status !== 'Completed' && p.status !== 'On Hold').length,
    openTickets: tickets.filter((t) => t.status === 'Open' || t.status === 'In Progress').length
  };

  return (
    <div className="min-h-screen bg-[#050508] text-slate-100 flex">
      {/* Fixed Apple-inspired Dark Sidebar */}
      <Sidebar
        currentTab={currentTab}
        onSelectTab={(tab) => {
          setSelectedClientIdForProjects(null);
          setCurrentTab(tab);
        }}
        counts={counts}
      />

      {/* Main Administrative Content Canvas */}
      <div className="flex-1 ml-64 min-h-screen flex flex-col">
        {/* Top Floating App Bar */}
        <header className="sticky top-0 z-10 h-14 bg-[#050508]/80 backdrop-blur-xl border-b border-white/5 px-8 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="text-xs text-slate-500">GoTech Media Console</span>
            <span className="text-slate-600">/</span>
            <span className="text-xs font-semibold text-cyan-400 uppercase tracking-wider">
              {currentTab}
            </span>
          </div>

          <div className="flex items-center gap-4">
            <button
              onClick={handleManualRefresh}
              disabled={refreshing || loadingData}
              className="px-3 py-1.5 rounded-xl text-xs font-medium text-slate-400 hover:text-white bg-white/[0.03] hover:bg-white/[0.08] border border-white/5 transition-all flex items-center gap-1.5"
            >
              <RefreshCw className={`w-3.5 h-3.5 ${refreshing ? 'animate-spin text-cyan-400' : ''}`} />
              <span>{refreshing ? 'Syncing...' : 'Sync Firestore'}</span>
            </button>
            <div className="h-4 w-px bg-white/10" />
            <div className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              <span className="text-[11px] text-slate-400 font-medium">Enterprise Online</span>
            </div>
          </div>
        </header>

        {/* Dynamic Tab Body */}
        <main className="flex-1 p-8 overflow-y-auto">
          {loadingData ? (
            <div className="h-96 flex flex-col items-center justify-center text-slate-500 gap-3">
              <Loader2 className="w-8 h-8 text-cyan-400 animate-spin" />
              <p className="text-xs">Loading operational databases from Cloud Firestore...</p>
            </div>
          ) : (
            <>
              {currentTab === 'dashboard' && (
                <DashboardHome
                  leads={leads}
                  clients={clients}
                  projects={projects}
                  tickets={tickets}
                  onNavigateTab={(tab) => setCurrentTab(tab)}
                  onSelectLead={(lead) => {
                    setCurrentTab('leads');
                  }}
                  onSelectProject={(project) => {
                    setCurrentTab('projects');
                  }}
                />
              )}

              {currentTab === 'leads' && (
                <LeadsManager
                  leads={leads}
                  onUpdateLead={async (leadId, updates) => {
                    await AdminApiService.updateLead(leadId, updates);
                    setLeads(leads.map((l) => (l.id === leadId ? { ...l, ...updates } : l)));
                  }}
                  onDeleteLead={async (leadId) => {
                    await AdminApiService.deleteLead(leadId);
                    setLeads(leads.filter((l) => l.id !== leadId));
                  }}
                  onConvertToClient={async (lead) => {
                    await AdminApiService.convertLeadToClient(lead);
                    await loadAllData();
                  }}
                />
              )}

              {currentTab === 'clients' && (
                <ClientsManager
                  clients={clients}
                  projects={projects}
                  onSaveClient={async (client) => {
                    await AdminApiService.saveClient(client);
                    const updated = await AdminApiService.getClients();
                    setClients(updated);
                  }}
                  onDeleteClient={async (clientId) => {
                    await AdminApiService.deleteClient(clientId);
                    setClients(clients.filter((c) => c.id !== clientId));
                  }}
                  onViewClientProjects={(clientId) => {
                    setSelectedClientIdForProjects(clientId);
                    setCurrentTab('projects');
                  }}
                />
              )}

              {currentTab === 'projects' && (
                <ProjectsManager
                  projects={projects}
                  clients={clients}
                  currentUser={currentUser}
                  selectedClientId={selectedClientIdForProjects}
                  onSaveProject={async (proj) => {
                    await AdminApiService.saveProject(proj);
                    const updated = await AdminApiService.getProjects();
                    setProjects(updated);
                  }}
                  onDeleteProject={async (projectId) => {
                    await AdminApiService.deleteProject(projectId);
                    setProjects(projects.filter((p) => p.id !== projectId));
                  }}
                />
              )}

              {currentTab === 'services' && (
                <ServicesManager
                  services={services}
                  onSaveService={async (svc) => {
                    await AdminApiService.saveService(svc);
                    const updated = await AdminApiService.getServices();
                    setServices(updated);
                  }}
                  onDeleteService={async (serviceId) => {
                    await AdminApiService.deleteService(serviceId);
                    setServices(services.filter((s) => s.id !== serviceId));
                  }}
                />
              )}

              {currentTab === 'portfolio' && (
                <PortfolioManager
                  portfolio={portfolio}
                  onSavePortfolio={async (item) => {
                    await AdminApiService.savePortfolio(item);
                    const updated = await AdminApiService.getPortfolio();
                    setPortfolio(updated);
                  }}
                  onDeletePortfolio={async (id) => {
                    await AdminApiService.deletePortfolio(id);
                    setPortfolio(portfolio.filter((p) => p.id !== id));
                  }}
                />
              )}

              {currentTab === 'testimonials' && (
                <TestimonialsManager
                  testimonials={testimonials}
                  onSaveTestimonial={async (t) => {
                    await AdminApiService.saveTestimonial(t);
                    const updated = await AdminApiService.getTestimonials();
                    setTestimonials(updated);
                  }}
                  onDeleteTestimonial={async (id) => {
                    await AdminApiService.deleteTestimonial(id);
                    setTestimonials(testimonials.filter((t) => t.id !== id));
                  }}
                />
              )}

              {currentTab === 'content' && (
                <ContentManager
                  settings={settings}
                  onSaveSettings={async (newSettings) => {
                    await AdminApiService.saveSettings(newSettings);
                    setSettings({ ...settings, ...newSettings } as AgencySettings);
                  }}
                />
              )}

              {currentTab === 'notifications' && (
                <NotificationsManager
                  notifications={notifications}
                  clients={clients}
                  onPublishNotification={async (notif) => {
                    await AdminApiService.publishNotification(notif);
                    const updated = await AdminApiService.getNotifications();
                    setNotifications(updated);
                  }}
                />
              )}

              {currentTab === 'support' && (
                <SupportManager
                  tickets={tickets}
                  onUpdateTicket={async (ticketId, updates) => {
                    await AdminApiService.updateSupportTicket(ticketId, updates);
                    setTickets(tickets.map((t) => (t.id === ticketId ? { ...t, ...updates } : t)));
                  }}
                />
              )}
            </>
          )}
        </main>
      </div>
    </div>
  );
};
