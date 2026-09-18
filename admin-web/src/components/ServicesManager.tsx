import React, { useState } from 'react';
import {
  Wrench,
  Plus,
  Edit2,
  Trash2,
  CheckCircle2,
  XCircle,
  X,
  Layers,
  Sparkles,
  ArrowUpDown
} from 'lucide-react';
import type { AgencyService } from '../types';

interface ServicesManagerProps {
  services: AgencyService[];
  onSaveService: (service: Partial<AgencyService>) => Promise<void>;
  onDeleteService: (serviceId: string) => Promise<void>;
}

export const ServicesManager: React.FC<ServicesManagerProps> = ({
  services,
  onSaveService,
  onDeleteService
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingService, setEditingService] = useState<Partial<AgencyService> | null>(null);

  const handleOpenAdd = () => {
    setEditingService({
      title: '',
      tagline: '',
      description: '',
      category: 'Enterprise Engineering',
      displayOrder: services.length + 1,
      isActive: true,
      features: ['24/7 SLA Engineering Support', 'Enterprise Cloud Architecture', 'CI/CD Pipeline Setup'],
      deliverables: ['Full Source Repository Access', 'Architecture Blueprint', 'Technical Documentation'],
      startingPrice: '$15,000',
      estimatedWeeks: 6
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (svc: AgencyService) => {
    setEditingService({ ...svc });
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingService?.title) return;
    await onSaveService(editingService);
    setIsModalOpen(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">Service Catalog Management</h1>
          <p className="text-xs text-slate-400 mt-1">
            Configure public and client service offerings, feature breakdowns, SLAs, and display ordering.
          </p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 transition-all flex items-center gap-2 shadow-md shadow-cyan-500/20"
        >
          <Plus className="w-4 h-4" />
          Add Service Offering
        </button>
      </div>

      {/* Services Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {services.length === 0 ? (
          <div className="col-span-full glass-panel p-12 rounded-2xl text-center text-slate-500 text-xs">
            No service catalog offerings defined yet.
          </div>
        ) : (
          services.map((svc) => (
            <div
              key={svc.id}
              className="glass-panel p-6 rounded-2xl flex flex-col justify-between hover:border-white/20 transition-all group"
            >
              <div>
                <div className="flex items-start justify-between gap-3 mb-3">
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] font-bold text-cyan-400 bg-cyan-500/10 px-2 py-0.5 rounded border border-cyan-500/20">
                      Order #{svc.displayOrder}
                    </span>
                    <span className="text-[10px] text-slate-400">{svc.category}</span>
                  </div>
                  <span
                    className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border ${
                      svc.isActive
                        ? 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30'
                        : 'bg-slate-800 text-slate-500 border-white/5'
                    }`}
                  >
                    {svc.isActive ? 'Active' : 'Inactive'}
                  </span>
                </div>

                <h3 className="text-base font-bold text-white group-hover:text-cyan-300 transition-colors">
                  {svc.title}
                </h3>
                <p className="text-xs text-cyan-400/90 font-medium mt-0.5">{svc.tagline}</p>
                <p className="text-xs text-slate-400 mt-2 line-clamp-3 leading-relaxed">
                  {svc.description}
                </p>

                {/* Features & Deliverables preview */}
                <div className="mt-4 pt-3 border-t border-white/5 space-y-2">
                  <span className="text-[11px] font-semibold text-slate-300 block">Core Deliverables</span>
                  <div className="space-y-1">
                    {svc.deliverables?.slice(0, 3).map((d, i) => (
                      <div key={i} className="text-[11px] text-slate-400 flex items-center gap-1.5">
                        <span className="w-1 h-1 rounded-full bg-cyan-400" />
                        <span className="truncate">{d}</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div className="mt-6 pt-4 border-t border-white/5 flex items-center justify-between">
                <div>
                  <span className="text-[10px] text-slate-500 block">Starting from</span>
                  <span className="text-xs font-bold text-white">{svc.startingPrice}</span>
                </div>

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => handleOpenEdit(svc)}
                    className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/5 transition-colors"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => {
                      if (confirm(`Delete service "${svc.title}"?`)) {
                        onDeleteService(svc.id);
                      }
                    }}
                    className="p-1.5 rounded-lg text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition-colors"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Edit / Create Modal */}
      {isModalOpen && editingService && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm">
          <div className="glass-panel-elevated w-full max-w-lg p-6 rounded-2xl shadow-2xl relative max-h-[90vh] overflow-y-auto text-xs">
            <div className="flex items-center justify-between pb-4 border-b border-white/10 mb-5">
              <h2 className="text-base font-bold text-white">
                {editingService.id ? 'Edit Service' : 'Add Service Offering'}
              </h2>
              <button onClick={() => setIsModalOpen(false)} className="p-1 rounded-lg text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-slate-300 font-medium mb-1">Service Title</label>
                <input
                  type="text"
                  required
                  value={editingService.title || ''}
                  onChange={(e) => setEditingService({ ...editingService, title: e.target.value })}
                  placeholder="e.g. Enterprise Mobile App Development"
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Tagline</label>
                <input
                  type="text"
                  value={editingService.tagline || ''}
                  onChange={(e) => setEditingService({ ...editingService, tagline: e.target.value })}
                  placeholder="Native Jetpack Compose & Swift with Reactive Architecture"
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Description</label>
                <textarea
                  rows={3}
                  value={editingService.description || ''}
                  onChange={(e) => setEditingService({ ...editingService, description: e.target.value })}
                  placeholder="Full scope explanation..."
                  className="glass-input w-full p-2.5 rounded-xl text-xs"
                />
              </div>

              <div className="grid grid-cols-3 gap-3">
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Display Order</label>
                  <input
                    type="number"
                    value={editingService.displayOrder || 1}
                    onChange={(e) => setEditingService({ ...editingService, displayOrder: parseInt(e.target.value) || 1 })}
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Starting Price</label>
                  <input
                    type="text"
                    value={editingService.startingPrice || ''}
                    onChange={(e) => setEditingService({ ...editingService, startingPrice: e.target.value })}
                    placeholder="$25,000"
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Active Status</label>
                  <select
                    value={editingService.isActive ? 'true' : 'false'}
                    onChange={(e) => setEditingService({ ...editingService, isActive: e.target.value === 'true' })}
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs bg-slate-900"
                  >
                    <option value="true">Active</option>
                    <option value="false">Inactive</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Features (comma separated)</label>
                <input
                  type="text"
                  value={editingService.features?.join(', ') || ''}
                  onChange={(e) =>
                    setEditingService({
                      ...editingService,
                      features: e.target.value.split(',').map((s) => s.trim()).filter(Boolean)
                    })
                  }
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Deliverables (comma separated)</label>
                <input
                  type="text"
                  value={editingService.deliverables?.join(', ') || ''}
                  onChange={(e) =>
                    setEditingService({
                      ...editingService,
                      deliverables: e.target.value.split(',').map((s) => s.trim()).filter(Boolean)
                    })
                  }
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                />
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-white/10">
                <button type="button" onClick={() => setIsModalOpen(false)} className="px-3 py-1.5 text-xs text-slate-400">
                  Cancel
                </button>
                <button type="submit" className="px-4 py-1.5 text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 rounded-lg">
                  Save Offering
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
