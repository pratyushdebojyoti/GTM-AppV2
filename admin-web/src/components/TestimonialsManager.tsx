import React, { useState } from 'react';
import {
  Quote,
  Plus,
  Edit2,
  Trash2,
  Star,
  CheckCircle2,
  XCircle,
  X,
  Building,
  User
} from 'lucide-react';
import type { AgencyTestimonial } from '../types';

interface TestimonialsManagerProps {
  testimonials: AgencyTestimonial[];
  onSaveTestimonial: (t: Partial<AgencyTestimonial>) => Promise<void>;
  onDeleteTestimonial: (id: string) => Promise<void>;
}

export const TestimonialsManager: React.FC<TestimonialsManagerProps> = ({
  testimonials,
  onSaveTestimonial,
  onDeleteTestimonial
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<Partial<AgencyTestimonial> | null>(null);

  const handleOpenAdd = () => {
    setEditingItem({
      clientName: '',
      clientTitle: 'Chief Technology Officer',
      clientCompany: '',
      avatarUrl: '',
      quote: '',
      projectCategory: 'Enterprise Architecture',
      ratingStars: 5,
      verifiedEngagement: true,
      isEnabled: true
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (t: AgencyTestimonial) => {
    setEditingItem({ ...t });
    setIsModalOpen(true);
  };

  const handleToggleEnabled = async (t: AgencyTestimonial) => {
    await onSaveTestimonial({ id: t.id, isEnabled: !t.isEnabled });
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingItem?.clientName || !editingItem?.quote) return;
    await onSaveTestimonial(editingItem);
    setIsModalOpen(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">Client Testimonials & Endorsements</h1>
          <p className="text-xs text-slate-400 mt-1">
            Manage executive reviews, verified engagement badges, star ratings, and toggle live visibility.
          </p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 transition-all flex items-center gap-2 shadow-md shadow-cyan-500/20"
        >
          <Plus className="w-4 h-4" />
          Add Endorsement
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {testimonials.length === 0 ? (
          <div className="col-span-full glass-panel p-12 rounded-2xl text-center text-slate-500 text-xs">
            No testimonials found. Add your first executive client endorsement!
          </div>
        ) : (
          testimonials.map((item) => (
            <div
              key={item.id}
              className="glass-panel p-6 rounded-2xl flex flex-col justify-between hover:border-white/20 transition-all group"
            >
              <div>
                <div className="flex items-start justify-between gap-3 mb-4">
                  <div className="flex items-center gap-1 text-amber-400">
                    {[...Array(item.ratingStars || 5)].map((_, i) => (
                      <Star key={i} className="w-3.5 h-3.5 fill-amber-400" />
                    ))}
                  </div>

                  <button
                    onClick={() => handleToggleEnabled(item)}
                    className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border transition-all ${
                      item.isEnabled
                        ? 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30'
                        : 'bg-slate-800 text-slate-500 border-white/5'
                    }`}
                  >
                    {item.isEnabled ? 'Live on Site' : 'Hidden / Disabled'}
                  </button>
                </div>

                <p className="text-xs text-slate-300 italic leading-relaxed mb-4">
                  "{item.quote}"
                </p>

                <div className="flex items-center gap-3 pt-3 border-t border-white/5">
                  <div className="w-9 h-9 rounded-xl bg-slate-800 border border-white/10 flex items-center justify-center font-bold text-cyan-400 text-xs shrink-0">
                    {item.clientName[0]}
                  </div>
                  <div className="min-w-0">
                    <h4 className="text-xs font-bold text-white truncate">{item.clientName}</h4>
                    <p className="text-[11px] text-slate-400 truncate">
                      {item.clientTitle}, {item.clientCompany}
                    </p>
                  </div>
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-white/5 flex items-center justify-between">
                <span className="text-[10px] text-cyan-400 bg-cyan-500/10 px-2 py-0.5 rounded">
                  {item.projectCategory}
                </span>

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => handleOpenEdit(item)}
                    className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/5"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => {
                      if (confirm(`Delete testimonial from ${item.clientName}?`)) {
                        onDeleteTestimonial(item.id);
                      }
                    }}
                    className="p-1.5 rounded-lg text-slate-400 hover:text-red-400 hover:bg-red-500/10"
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
      {isModalOpen && editingItem && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm">
          <div className="glass-panel-elevated w-full max-w-md p-6 rounded-2xl shadow-2xl relative text-xs">
            <div className="flex items-center justify-between pb-3 border-b border-white/10 mb-4">
              <h2 className="text-sm font-bold text-white">
                {editingItem.id ? 'Edit Testimonial' : 'Add Testimonial'}
              </h2>
              <button onClick={() => setIsModalOpen(false)} className="p-1 rounded-lg text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSave} className="space-y-3.5">
              <div>
                <label className="block text-slate-300 mb-1">Executive Name</label>
                <input
                  type="text"
                  required
                  value={editingItem.clientName || ''}
                  onChange={(e) => setEditingItem({ ...editingItem, clientName: e.target.value })}
                  placeholder="e.g. Sarah Jenkins"
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 mb-1">Title</label>
                  <input
                    type="text"
                    value={editingItem.clientTitle || ''}
                    onChange={(e) => setEditingItem({ ...editingItem, clientTitle: e.target.value })}
                    placeholder="Chief Technology Officer"
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 mb-1">Company</label>
                  <input
                    type="text"
                    value={editingItem.clientCompany || ''}
                    onChange={(e) => setEditingItem({ ...editingItem, clientCompany: e.target.value })}
                    placeholder="Horizon FinTech"
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
              </div>

              <div>
                <label className="block text-slate-300 mb-1">Quote / Review</label>
                <textarea
                  rows={3}
                  required
                  value={editingItem.quote || ''}
                  onChange={(e) => setEditingItem({ ...editingItem, quote: e.target.value })}
                  placeholder="The team delivered exceptional high-throughput architecture..."
                  className="glass-input w-full p-2.5 rounded-xl text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 mb-1">Rating (1-5)</label>
                  <input
                    type="number"
                    min="1"
                    max="5"
                    value={editingItem.ratingStars || 5}
                    onChange={(e) => setEditingItem({ ...editingItem, ratingStars: parseInt(e.target.value) || 5 })}
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 mb-1">Category</label>
                  <input
                    type="text"
                    value={editingItem.projectCategory || ''}
                    onChange={(e) => setEditingItem({ ...editingItem, projectCategory: e.target.value })}
                    placeholder="Cloud & Mobile Engineering"
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
              </div>

              <div className="flex items-center gap-2 pt-2">
                <input
                  type="checkbox"
                  id="enableCheck"
                  checked={editingItem.isEnabled || false}
                  onChange={(e) => setEditingItem({ ...editingItem, isEnabled: e.target.checked })}
                  className="rounded accent-cyan-400"
                />
                <label htmlFor="enableCheck" className="text-slate-300">
                  Published and publicly visible on landing page
                </label>
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-white/10">
                <button type="button" onClick={() => setIsModalOpen(false)} className="px-3 py-1.5 text-xs text-slate-400">
                  Cancel
                </button>
                <button type="submit" className="px-4 py-1.5 text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 rounded-lg">
                  Save Endorsement
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
