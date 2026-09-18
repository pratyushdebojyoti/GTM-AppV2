import React, { useState } from 'react';
import {
  FolderKanban,
  Plus,
  Search,
  Calendar,
  DollarSign,
  Users,
  CheckCircle,
  Clock,
  Edit2,
  Trash2,
  X,
  FileText,
  MessageSquare,
  LifeBuoy,
  Layers,
  ChevronRight,
  ExternalLink,
  Send
} from 'lucide-react';
import type {
  AgencyProject,
  AgencyClient,
  AgencyProjectMilestone,
  AgencyDocument,
  AgencyMessage,
  AgencySupportTicket,
  ProjectStatus,
  AgencyUser
} from '../types';
import { AdminApiService } from '../services/adminApi';

interface ProjectsManagerProps {
  projects: AgencyProject[];
  clients: AgencyClient[];
  currentUser: AgencyUser | null;
  onSaveProject: (project: Partial<AgencyProject>) => Promise<void>;
  onDeleteProject: (projectId: string) => Promise<void>;
  selectedClientId?: string | null;
}

export const ProjectsManager: React.FC<ProjectsManagerProps> = ({
  projects,
  clients,
  currentUser,
  onSaveProject,
  onDeleteProject,
  selectedClientId
}) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [selectedProject, setSelectedProject] = useState<AgencyProject | null>(projects[0] || null);

  // Inspector Sub-tabs
  const [activeTab, setActiveTab] = useState<'overview' | 'milestones' | 'documents' | 'messages'>('overview');

  // Subcollection state
  const [milestones, setMilestones] = useState<AgencyProjectMilestone[]>([]);
  const [documents, setDocuments] = useState<AgencyDocument[]>([]);
  const [messages, setMessages] = useState<AgencyMessage[]>([]);
  const [loadingSub, setLoadingSub] = useState(false);

  // Modals
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingProject, setEditingProject] = useState<Partial<AgencyProject> | null>(null);

  const [isAddMilestoneOpen, setIsAddMilestoneOpen] = useState(false);
  const [newMilestone, setNewMilestone] = useState<Partial<AgencyProjectMilestone>>({
    stepNumber: 1,
    title: '',
    description: '',
    status: 'Pending',
    dueDate: 'Week 4',
    deliverables: []
  });

  const [newMessageText, setNewMessageText] = useState('');

  const statuses: ProjectStatus[] = [
    'Inquiry',
    'Planning',
    'Design',
    'Development',
    'Testing',
    'Review',
    'Launch',
    'Completed',
    'On Hold'
  ];

  // Load subcollections whenever selected project changes
  React.useEffect(() => {
    if (selectedProject) {
      loadProjectSubcollections(selectedProject.id);
    }
  }, [selectedProject?.id]);

  const loadProjectSubcollections = async (projId: string) => {
    setLoadingSub(true);
    try {
      const [m, d, msg] = await Promise.all([
        AdminApiService.getProjectMilestones(projId),
        AdminApiService.getProjectDocuments(projId),
        AdminApiService.getProjectMessages(projId)
      ]);
      setMilestones(m);
      setDocuments(d);
      setMessages(msg);
    } catch (err) {
      console.error('Failed to load project details:', err);
    } finally {
      setLoadingSub(false);
    }
  };

  const filteredProjects = projects.filter((p) => {
    const matchesSearch =
      p.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || p.status === statusFilter;
    const matchesClient = !selectedClientId || p.clientId === selectedClientId;
    return matchesSearch && matchesStatus && matchesClient;
  });

  const handleOpenAdd = () => {
    setEditingProject({
      title: '',
      description: '',
      status: 'Development',
      progressPercentage: 10,
      budget: '$75,000',
      startDate: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
      expectedCompletion: '12 Weeks',
      clientId: clients[0]?.id || '',
      userId: clients[0]?.userId || '',
      techStack: ['Jetpack Compose', 'Kotlin', 'Firebase', 'Clean Architecture'],
      assignedTeam: [
        { id: 'tm1', name: 'Marcus Vance', role: 'Lead Architect', email: 'm.vance@gotechmedia.com' }
      ]
    });
    setIsEditModalOpen(true);
  };

  const handleSaveModal = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingProject?.title) return;
    await onSaveProject(editingProject);
    setIsEditModalOpen(false);
    if (selectedProject && editingProject.id === selectedProject.id) {
      setSelectedProject({ ...selectedProject, ...editingProject } as AgencyProject);
    }
  };

  const handleQuickStatusChange = async (proj: AgencyProject, newStatus: ProjectStatus) => {
    await onSaveProject({ id: proj.id, status: newStatus });
    if (selectedProject?.id === proj.id) {
      setSelectedProject({ ...selectedProject, status: newStatus });
    }
  };

  const handleQuickProgressChange = async (proj: AgencyProject, newProgress: number) => {
    await onSaveProject({ id: proj.id, progressPercentage: newProgress });
    if (selectedProject?.id === proj.id) {
      setSelectedProject({ ...selectedProject, progressPercentage: newProgress });
    }
  };

  const handleCreateMilestone = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProject || !newMilestone.title) return;
    await AdminApiService.saveProjectMilestone(selectedProject.id, newMilestone);
    setIsAddMilestoneOpen(false);
    setNewMilestone({ stepNumber: milestones.length + 1, title: '', description: '', status: 'Pending', dueDate: 'Week 4', deliverables: [] });
    loadProjectSubcollections(selectedProject.id);
  };

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedProject || !newMessageText.trim() || !currentUser) return;
    await AdminApiService.sendAdminMessage(selectedProject.id, currentUser, newMessageText.trim());
    setNewMessageText('');
    const updated = await AdminApiService.getProjectMessages(selectedProject.id);
    setMessages(updated);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">Project & Sprint Delivery</h1>
          <p className="text-xs text-slate-400 mt-1">
            Control delivery status, progress velocity, team assignments, milestones, and client communications.
          </p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 transition-all flex items-center gap-2 shadow-md shadow-cyan-500/20"
        >
          <Plus className="w-4 h-4" />
          Create New Sprint Project
        </button>
      </div>

      {/* Filter & Search Bar */}
      <div className="glass-panel p-4 rounded-2xl flex flex-col md:flex-row items-center gap-4">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search projects by title, scope, or technology..."
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
            All ({projects.length})
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
              {st}
            </button>
          ))}
        </div>
      </div>

      {/* Two Pane Layout: Master Projects + Deep Sprint Inspector */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Column: Project Cards List */}
        <div className="lg:col-span-5 space-y-3">
          {filteredProjects.length === 0 ? (
            <div className="glass-panel p-12 rounded-2xl text-center">
              <FolderKanban className="w-8 h-8 text-slate-600 mx-auto mb-3" />
              <p className="text-sm font-medium text-slate-400">No projects found</p>
              <p className="text-xs text-slate-600 mt-1">Create a new project or adjust filters.</p>
            </div>
          ) : (
            filteredProjects.map((proj) => {
              const isSelected = selectedProject?.id === proj.id;
              return (
                <div
                  key={proj.id}
                  onClick={() => setSelectedProject(proj)}
                  className={`glass-panel p-4 rounded-xl cursor-pointer transition-all ${
                    isSelected
                      ? 'border-cyan-500/50 bg-cyan-500/[0.05] shadow-lg shadow-cyan-500/5'
                      : 'hover:border-white/20'
                  }`}
                >
                  <div className="flex items-start justify-between gap-3 mb-2">
                    <h3 className="text-sm font-bold text-white truncate">{proj.title}</h3>
                    <span className="text-[10px] font-semibold text-cyan-400 bg-cyan-500/10 px-2 py-0.5 rounded border border-cyan-500/20 shrink-0">
                      {proj.status}
                    </span>
                  </div>

                  <p className="text-xs text-slate-400 line-clamp-2 mb-3">{proj.description}</p>

                  {/* Progress Bar & percentage */}
                  <div className="space-y-1 mb-3">
                    <div className="flex justify-between text-[10px] text-slate-400">
                      <span>Completion Velocity</span>
                      <span className="font-bold text-white">{proj.progressPercentage}%</span>
                    </div>
                    <div className="w-full bg-slate-800 rounded-full h-1.5 overflow-hidden">
                      <div
                        className="bg-gradient-to-r from-cyan-400 to-indigo-400 h-1.5 rounded-full"
                        style={{ width: `${proj.progressPercentage}%` }}
                      />
                    </div>
                  </div>

                  <div className="flex items-center justify-between text-[11px] text-slate-400 pt-2 border-t border-white/5">
                    <span>{proj.startDate || 'Active'}</span>
                    <span className="text-slate-300 font-medium">{proj.budget}</span>
                  </div>
                </div>
              );
            })
          )}
        </div>

        {/* Right Column: Deep Sprint Inspector & Subcollections */}
        <div className="lg:col-span-7">
          {selectedProject ? (
            <div className="glass-panel p-6 rounded-2xl space-y-6">
              {/* Project Header and Edit trigger */}
              <div className="flex items-start justify-between gap-4 pb-4 border-b border-white/5">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <h2 className="text-lg font-bold text-white">{selectedProject.title}</h2>
                  </div>
                  <p className="text-xs text-slate-400 leading-relaxed">{selectedProject.description}</p>
                </div>

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => {
                      setEditingProject(selectedProject);
                      setIsEditModalOpen(true);
                    }}
                    className="p-2 rounded-xl text-slate-300 hover:text-white bg-slate-800/80 hover:bg-slate-800 border border-white/10"
                    title="Edit project properties"
                  >
                    <Edit2 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => {
                      if (confirm(`Permanently delete project "${selectedProject.title}"?`)) {
                        onDeleteProject(selectedProject.id);
                        setSelectedProject(null);
                      }
                    }}
                    className="p-2 rounded-xl text-red-400 hover:bg-red-500/10 border border-red-500/20"
                    title="Delete project"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>

              {/* Status & Velocity Fast Controls */}
              <div className="p-4 rounded-xl bg-slate-900/60 border border-white/5 grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-[11px] text-slate-400 font-medium mb-1.5">
                    Project Delivery Status (Server Enforced)
                  </label>
                  <select
                    value={selectedProject.status}
                    onChange={(e) => handleQuickStatusChange(selectedProject, e.target.value as ProjectStatus)}
                    className="glass-input w-full px-3 py-1.5 rounded-lg text-xs bg-slate-900"
                  >
                    {statuses.map((st) => (
                      <option key={st} value={st}>
                        {st}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <div className="flex justify-between text-[11px] text-slate-400 font-medium mb-1.5">
                    <span>Progress: {selectedProject.progressPercentage}%</span>
                    <span className="text-cyan-400 font-semibold">Update Velocity</span>
                  </div>
                  <input
                    type="range"
                    min="0"
                    max="100"
                    value={selectedProject.progressPercentage}
                    onChange={(e) => handleQuickProgressChange(selectedProject, parseInt(e.target.value))}
                    className="w-full accent-cyan-400 cursor-pointer"
                  />
                </div>
              </div>

              {/* Inspector Subtabs: Overview, Milestones, Documents, Messages */}
              <div className="flex items-center gap-2 border-b border-white/5 pb-2">
                {[
                  { id: 'overview', label: 'Overview & Team', icon: Layers },
                  { id: 'milestones', label: `Milestones (${milestones.length})`, icon: CheckCircle },
                  { id: 'documents', label: `Documents (${documents.length})`, icon: FileText },
                  { id: 'messages', label: `Messages (${messages.length})`, icon: MessageSquare }
                ].map((tab) => {
                  const Icon = tab.icon;
                  return (
                    <button
                      key={tab.id}
                      onClick={() => setActiveTab(tab.id as any)}
                      className={`px-3 py-1.5 rounded-lg text-xs font-medium flex items-center gap-1.5 transition-all ${
                        activeTab === tab.id
                          ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/30'
                          : 'text-slate-400 hover:text-white'
                      }`}
                    >
                      <Icon className="w-3.5 h-3.5" />
                      {tab.label}
                    </button>
                  );
                })}
              </div>

              {/* Inspector Content */}
              {activeTab === 'overview' && (
                <div className="space-y-4 text-xs">
                  <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                    <div className="p-3 rounded-xl bg-slate-900/40 border border-white/5">
                      <span className="text-slate-500 block">Start Date</span>
                      <span className="text-white font-medium">{selectedProject.startDate}</span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-900/40 border border-white/5">
                      <span className="text-slate-500 block">Delivery Date</span>
                      <span className="text-white font-medium">{selectedProject.expectedCompletion}</span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-900/40 border border-white/5">
                      <span className="text-slate-500 block">Contract Budget</span>
                      <span className="text-cyan-400 font-medium">{selectedProject.budget}</span>
                    </div>
                    <div className="p-3 rounded-xl bg-slate-900/40 border border-white/5">
                      <span className="text-slate-500 block">Team Roster</span>
                      <span className="text-white font-medium">{selectedProject.assignedTeam?.length || 0} Engineers</span>
                    </div>
                  </div>

                  {/* Assigned Team Members */}
                  <div>
                    <span className="font-semibold text-slate-300 block mb-2">Assigned Solution Architects</span>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                      {selectedProject.assignedTeam?.map((tm) => (
                        <div key={tm.id} className="p-2.5 rounded-xl bg-slate-900/60 border border-white/5 flex items-center gap-3">
                          <div className="w-8 h-8 rounded-lg bg-cyan-500/10 text-cyan-400 flex items-center justify-center font-bold text-xs">
                            {tm.name[0]}
                          </div>
                          <div className="min-w-0">
                            <p className="text-xs font-semibold text-white truncate">{tm.name}</p>
                            <p className="text-[10px] text-slate-400 truncate">{tm.role}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Tech Stack Chips */}
                  <div>
                    <span className="font-semibold text-slate-300 block mb-2">Architecture & Tech Stack</span>
                    <div className="flex flex-wrap gap-1.5">
                      {selectedProject.techStack?.map((tech, i) => (
                        <span key={i} className="px-2 py-0.5 rounded-md bg-slate-800 text-slate-300 text-[11px] border border-white/5">
                          {tech}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>
              )}

              {activeTab === 'milestones' && (
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-semibold text-slate-300">Deliverable Milestones</span>
                    <button
                      onClick={() => setIsAddMilestoneOpen(true)}
                      className="text-xs text-cyan-400 hover:text-cyan-300 flex items-center gap-1 font-medium"
                    >
                      <Plus className="w-3.5 h-3.5" /> Add Milestone
                    </button>
                  </div>

                  <div className="space-y-2.5">
                    {milestones.length === 0 ? (
                      <p className="text-xs text-slate-500 italic py-4 text-center">No milestones provisioned yet.</p>
                    ) : (
                      milestones.map((ms) => (
                        <div key={ms.id} className="p-3 rounded-xl bg-slate-900/40 border border-white/5 flex items-start justify-between gap-4">
                          <div className="min-w-0">
                            <div className="flex items-center gap-2">
                              <span className="text-[11px] font-bold text-cyan-400 bg-cyan-500/10 px-1.5 py-0.5 rounded">
                                Stage {ms.stepNumber}
                              </span>
                              <span className="text-xs font-bold text-white truncate">{ms.title}</span>
                            </div>
                            <p className="text-[11px] text-slate-400 mt-1">{ms.description}</p>
                            <div className="flex items-center gap-3 mt-2 text-[10px] text-slate-500">
                              <span>Due: {ms.dueDate}</span>
                              {ms.deliverables?.length > 0 && (
                                <span>{ms.deliverables.length} Deliverables</span>
                              )}
                            </div>
                          </div>

                          <span
                            className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border shrink-0 ${
                              ms.status === 'Completed'
                                ? 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30'
                                : ms.status === 'In Progress'
                                ? 'bg-cyan-500/10 text-cyan-300 border-cyan-500/30'
                                : 'bg-slate-800 text-slate-400 border-white/10'
                            }`}
                          >
                            {ms.status}
                          </span>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}

              {activeTab === 'documents' && (
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-semibold text-slate-300">Contractual Deliverables & Specs</span>
                    <button
                      onClick={async () => {
                        const title = prompt('Document Title (e.g. Architecture Blueprint, SOW, Security Audit):');
                        if (!title) return;
                        await AdminApiService.saveProjectDocument(selectedProject.id, {
                          title,
                          type: 'DELIVERABLE',
                          fileUrl: 'https://gotechmedia.com/docs/spec.pdf',
                          sizeBytes: 1024 * 512,
                          uploadedBy: currentUser?.displayName || 'Executive Partner'
                        });
                        loadProjectSubcollections(selectedProject.id);
                      }}
                      className="text-xs text-cyan-400 hover:text-cyan-300 flex items-center gap-1 font-medium"
                    >
                      <Plus className="w-3.5 h-3.5" /> Attach Document
                    </button>
                  </div>

                  <div className="space-y-2">
                    {documents.length === 0 ? (
                      <p className="text-xs text-slate-500 italic py-4 text-center">No documents registered for this project.</p>
                    ) : (
                      documents.map((doc) => (
                        <div key={doc.id} className="p-3 rounded-xl bg-slate-900/40 border border-white/5 flex items-center justify-between">
                          <div className="flex items-center gap-3">
                            <FileText className="w-4 h-4 text-cyan-400" />
                            <div>
                              <p className="text-xs font-semibold text-white">{doc.title}</p>
                              <span className="text-[10px] text-slate-500">
                                {doc.type} • {(doc.sizeBytes / 1024).toFixed(1)} KB
                              </span>
                            </div>
                          </div>
                          <span className="text-[10px] text-cyan-400 bg-cyan-500/10 px-2 py-0.5 rounded">
                            Verified Deliverable
                          </span>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}

              {activeTab === 'messages' && (
                <div className="space-y-4">
                  <div className="h-60 overflow-y-auto space-y-3 p-3 rounded-xl bg-slate-900/50 border border-white/5">
                    {messages.length === 0 ? (
                      <p className="text-xs text-slate-500 italic text-center py-10">No message thread history.</p>
                    ) : (
                      messages.map((m) => {
                        const isAdmin = m.senderRole === 'ADMIN';
                        return (
                          <div key={m.id} className={`flex flex-col ${isAdmin ? 'items-end' : 'items-start'}`}>
                            <div className="flex items-center gap-2 mb-0.5">
                              <span className={`text-[10px] font-bold ${isAdmin ? 'text-cyan-400' : 'text-indigo-400'}`}>
                                {m.senderName}
                              </span>
                              <span className="text-[9px] text-slate-500">
                                {new Date(m.timestampEpoch).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                              </span>
                            </div>
                            <div
                              className={`p-2.5 rounded-xl text-xs max-w-sm ${
                                isAdmin
                                  ? 'bg-cyan-500/20 text-cyan-100 border border-cyan-500/30'
                                  : 'bg-slate-800 text-slate-200 border border-white/10'
                              }`}
                            >
                              {m.messageText}
                            </div>
                          </div>
                        );
                      })
                    )}
                  </div>

                  {/* Dispatch admin reply */}
                  <form onSubmit={handleSendMessage} className="flex gap-2">
                    <input
                      type="text"
                      value={newMessageText}
                      onChange={(e) => setNewMessageText(e.target.value)}
                      placeholder="Dispatch executive response to client..."
                      className="glass-input flex-1 px-3.5 py-2 rounded-xl text-xs"
                    />
                    <button
                      type="submit"
                      disabled={!newMessageText.trim()}
                      className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 disabled:opacity-40 flex items-center gap-1.5 shadow-sm"
                    >
                      <Send className="w-3.5 h-3.5" />
                      Send
                    </button>
                  </form>
                </div>
              )}
            </div>
          ) : (
            <div className="glass-panel p-12 rounded-2xl text-center text-slate-500 text-xs">
              Select a project from the left to inspect milestones, documents, and communications.
            </div>
          )}
        </div>
      </div>

      {/* Edit / Create Project Modal */}
      {isEditModalOpen && editingProject && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm">
          <div className="glass-panel-elevated w-full max-w-xl p-6 rounded-2xl shadow-2xl relative max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-4 border-b border-white/10 mb-5">
              <h2 className="text-base font-bold text-white">
                {editingProject.id ? 'Edit Sprint Project' : 'Create New Sprint Project'}
              </h2>
              <button onClick={() => setIsEditModalOpen(false)} className="p-1 rounded-lg text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveModal} className="space-y-4 text-xs">
              <div>
                <label className="block text-slate-300 font-medium mb-1">Project Title</label>
                <input
                  type="text"
                  required
                  value={editingProject.title || ''}
                  onChange={(e) => setEditingProject({ ...editingProject, title: e.target.value })}
                  placeholder="e.g. Enterprise Core Cloud Architecture"
                  className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Scope & Description</label>
                <textarea
                  rows={3}
                  value={editingProject.description || ''}
                  onChange={(e) => setEditingProject({ ...editingProject, description: e.target.value })}
                  placeholder="Detailed project summary, deliverable boundaries, and objectives..."
                  className="glass-input w-full p-3 rounded-xl text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Client Organization</label>
                  <select
                    value={editingProject.clientId || ''}
                    onChange={(e) => {
                      const c = clients.find((cl) => cl.id === e.target.value);
                      setEditingProject({
                        ...editingProject,
                        clientId: e.target.value,
                        userId: c?.userId || e.target.value
                      });
                    }}
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs bg-slate-900"
                  >
                    {clients.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.companyName}
                      </option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Status</label>
                  <select
                    value={editingProject.status || 'Development'}
                    onChange={(e) => setEditingProject({ ...editingProject, status: e.target.value as any })}
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs bg-slate-900"
                  >
                    {statuses.map((st) => (
                      <option key={st} value={st}>
                        {st}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Progress %</label>
                  <input
                    type="number"
                    min="0"
                    max="100"
                    value={editingProject.progressPercentage || 0}
                    onChange={(e) => setEditingProject({ ...editingProject, progressPercentage: parseInt(e.target.value) || 0 })}
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Budget</label>
                  <input
                    type="text"
                    value={editingProject.budget || ''}
                    onChange={(e) => setEditingProject({ ...editingProject, budget: e.target.value })}
                    placeholder="$85,000"
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Expected Completion</label>
                  <input
                    type="text"
                    value={editingProject.expectedCompletion || ''}
                    onChange={(e) => setEditingProject({ ...editingProject, expectedCompletion: e.target.value })}
                    placeholder="Dec 18, 2026"
                    className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
                  />
                </div>
              </div>

              <div className="pt-4 flex items-center justify-end gap-3 border-t border-white/10">
                <button
                  type="button"
                  onClick={() => setIsEditModalOpen(false)}
                  className="px-4 py-2 rounded-xl text-xs text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300"
                >
                  Save Project
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Add Milestone Modal */}
      {isAddMilestoneOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm">
          <div className="glass-panel-elevated w-full max-w-md p-6 rounded-2xl shadow-2xl relative">
            <h2 className="text-sm font-bold text-white mb-4">Add Deliverable Milestone</h2>
            <form onSubmit={handleCreateMilestone} className="space-y-4 text-xs">
              <div>
                <label className="block text-slate-300 mb-1">Milestone Title</label>
                <input
                  type="text"
                  required
                  value={newMilestone.title || ''}
                  onChange={(e) => setNewMilestone({ ...newMilestone, title: e.target.value })}
                  placeholder="e.g. Security Audit & Zero Trust Signoff"
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                />
              </div>

              <div>
                <label className="block text-slate-300 mb-1">Description</label>
                <textarea
                  rows={2}
                  value={newMilestone.description || ''}
                  onChange={(e) => setNewMilestone({ ...newMilestone, description: e.target.value })}
                  placeholder="Specific deliverable specifications..."
                  className="glass-input w-full p-2.5 rounded-xl text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-slate-300 mb-1">Status</label>
                  <select
                    value={newMilestone.status || 'Pending'}
                    onChange={(e) => setNewMilestone({ ...newMilestone, status: e.target.value as any })}
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs bg-slate-900"
                  >
                    <option value="Pending">Pending</option>
                    <option value="In Progress">In Progress</option>
                    <option value="Completed">Completed</option>
                  </select>
                </div>
                <div>
                  <label className="block text-slate-300 mb-1">Due Date</label>
                  <input
                    type="text"
                    value={newMilestone.dueDate || ''}
                    onChange={(e) => setNewMilestone({ ...newMilestone, dueDate: e.target.value })}
                    placeholder="Week 8"
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
              </div>

              <div className="pt-3 flex justify-end gap-2">
                <button type="button" onClick={() => setIsAddMilestoneOpen(false)} className="px-3 py-1.5 text-xs text-slate-400">
                  Cancel
                </button>
                <button type="submit" className="px-4 py-1.5 text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 rounded-lg">
                  Add Milestone
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
