import React, { useState } from 'react';
import {
  Sparkles,
  Plus,
  Edit2,
  Trash2,
  ExternalLink,
  Star,
  X,
  Image as ImageIcon
} from 'lucide-react';
import type { PortfolioItem } from '../types';

interface PortfolioManagerProps {
  portfolio: PortfolioItem[];
  onSavePortfolio: (item: Partial<PortfolioItem>) => Promise<void>;
  onDeletePortfolio: (id: string) => Promise<void>;
}

export const PortfolioManager: React.FC<PortfolioManagerProps> = ({
  portfolio,
  onSavePortfolio,
  onDeletePortfolio
}) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingItem, setEditingItem] = useState<Partial<PortfolioItem> | null>(null);

  const handleOpenAdd = () => {
    setEditingItem({
      title: '',
      category: 'Enterprise Engineering',
      summary: '',
      detailedCaseStudy: '',
      featuredImageUrl: 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?auto=format&fit=crop&w=1200&q=80',
      screenshotUrls: [],
      technologies: ['React', 'Kotlin', 'Firebase', 'Google Cloud'],
      metrics: [
        { label: 'Latency Reduction', value: '42%' },
        { label: 'DAU Growth', value: '3.4x' }
      ],
      clientName: 'Apex Enterprise',
      completionDate: 'Q3 2026',
      isFeatured: true,
      liveUrl: 'https://gotechmedia.com'
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (item: PortfolioItem) => {
    setEditingItem({ ...item });
    setIsModalOpen(true);
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingItem?.title) return;
    await onSavePortfolio(editingItem);
    setIsModalOpen(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-white">Portfolio & Case Studies</h1>
          <p className="text-xs text-slate-400 mt-1">
            Showcase enterprise case studies, production metrics, live URLs, and hero credentials.
          </p>
        </div>
        <button
          onClick={handleOpenAdd}
          className="px-4 py-2 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 transition-all flex items-center gap-2 shadow-md shadow-cyan-500/20"
        >
          <Plus className="w-4 h-4" />
          Add Case Study
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {portfolio.length === 0 ? (
          <div className="col-span-full glass-panel p-12 rounded-2xl text-center text-slate-500 text-xs">
            No portfolio case studies registered yet.
          </div>
        ) : (
          portfolio.map((item) => (
            <div
              key={item.id}
              className="glass-panel rounded-2xl overflow-hidden flex flex-col justify-between hover:border-white/20 transition-all group"
            >
              <div>
                {/* Hero preview */}
                <div className="h-44 w-full bg-slate-900 relative overflow-hidden">
                  <img
                    src={item.featuredImageUrl}
                    alt={item.title}
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500 opacity-80"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-[#0D111A] via-transparent to-transparent" />
                  {item.isFeatured && (
                    <span className="absolute top-3 left-3 bg-cyan-400 text-slate-950 font-bold text-[10px] px-2 py-0.5 rounded-full flex items-center gap-1 shadow-md">
                      <Star className="w-3 h-3 fill-slate-950" /> Featured
                    </span>
                  )}
                  <span className="absolute bottom-3 right-3 text-[10px] bg-black/60 backdrop-blur-md px-2 py-0.5 rounded text-slate-300">
                    {item.clientName}
                  </span>
                </div>

                <div className="p-5">
                  <span className="text-[10px] font-semibold text-cyan-400 uppercase tracking-wider">
                    {item.category}
                  </span>
                  <h3 className="text-base font-bold text-white group-hover:text-cyan-300 transition-colors mt-0.5">
                    {item.title}
                  </h3>
                  <p className="text-xs text-slate-400 mt-2 line-clamp-2">{item.summary}</p>

                  {/* Technologies */}
                  <div className="mt-3 flex flex-wrap gap-1">
                    {item.technologies?.map((tech, i) => (
                      <span key={i} className="text-[10px] bg-slate-800 text-slate-300 px-1.5 py-0.5 rounded">
                        {tech}
                      </span>
                    ))}
                  </div>

                  {/* Metrics preview */}
                  {item.metrics && item.metrics.length > 0 && (
                    <div className="grid grid-cols-2 gap-2 mt-4 pt-3 border-t border-white/5">
                      {item.metrics.slice(0, 2).map((m, i) => (
                        <div key={i}>
                          <span className="text-[10px] text-slate-500 block">{m.label}</span>
                          <span className="text-xs font-bold text-cyan-300">{m.value}</span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>

              <div className="p-5 pt-0 flex items-center justify-between border-t border-white/5 mt-3 pt-3">
                {item.liveUrl ? (
                  <a
                    href={item.liveUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="text-xs text-cyan-400 hover:underline flex items-center gap-1 font-medium"
                  >
                    Live Deployment <ExternalLink className="w-3 h-3" />
                  </a>
                ) : (
                  <span className="text-xs text-slate-500">NDA Protected</span>
                )}

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => handleOpenEdit(item)}
                    className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-white/5"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => {
                      if (confirm(`Delete case study "${item.title}"?`)) {
                        onDeletePortfolio(item.id);
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
          <div className="glass-panel-elevated w-full max-w-lg p-6 rounded-2xl shadow-2xl relative max-h-[90vh] overflow-y-auto text-xs">
            <div className="flex items-center justify-between pb-4 border-b border-white/10 mb-5">
              <h2 className="text-base font-bold text-white">
                {editingItem.id ? 'Edit Case Study' : 'Add Case Study'}
              </h2>
              <button onClick={() => setIsModalOpen(false)} className="p-1 rounded-lg text-slate-400 hover:text-white">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-slate-300 font-medium mb-1">Title</label>
                <input
                  type="text"
                  required
                  value={editingItem.title || ''}
                  onChange={(e) => setEditingItem({ ...editingItem, title: e.target.value })}
                  placeholder="e.g. Next-Gen Financial Brokerage Engine"
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Client / Brand</label>
                  <input
                    type="text"
                    value={editingItem.clientName || ''}
                    onChange={(e) => setEditingItem({ ...editingItem, clientName: e.target.value })}
                    placeholder="Apex Global"
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Category</label>
                  <input
                    type="text"
                    value={editingItem.category || ''}
                    onChange={(e) => setEditingItem({ ...editingItem, category: e.target.value })}
                    placeholder="Mobile / Cloud Infrastructure"
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Executive Summary</label>
                <textarea
                  rows={2}
                  value={editingItem.summary || ''}
                  onChange={(e) => setEditingItem({ ...editingItem, summary: e.target.value })}
                  placeholder="High level overview..."
                  className="glass-input w-full p-2.5 rounded-xl text-xs"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Detailed Case Study</label>
                <textarea
                  rows={3}
                  value={editingItem.detailedCaseStudy || ''}
                  onChange={(e) => setEditingItem({ ...editingItem, detailedCaseStudy: e.target.value })}
                  placeholder="Challenge, architecture solution, and results..."
                  className="glass-input w-full p-2.5 rounded-xl text-xs"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Hero Image URL</label>
                  <input
                    type="url"
                    value={editingItem.featuredImageUrl || ''}
                    onChange={(e) => setEditingItem({ ...editingItem, featuredImageUrl: e.target.value })}
                    placeholder="https://..."
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Live URL</label>
                  <input
                    type="url"
                    value={editingItem.liveUrl || ''}
                    onChange={(e) => setEditingItem({ ...editingItem, liveUrl: e.target.value })}
                    placeholder="https://clientapp.com"
                    className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                  />
                </div>
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Technologies (comma separated)</label>
                <input
                  type="text"
                  value={editingItem.technologies?.join(', ') || ''}
                  onChange={(e) =>
                    setEditingItem({
                      ...editingItem,
                      technologies: e.target.value.split(',').map((t) => t.trim()).filter(Boolean)
                    })
                  }
                  className="glass-input w-full px-3 py-2 rounded-xl text-xs"
                />
              </div>

              <div className="flex items-center gap-2 pt-2">
                <input
                  type="checkbox"
                  id="featCheck"
                  checked={editingItem.isFeatured || false}
                  onChange={(e) => setEditingItem({ ...editingItem, isFeatured: e.target.checked })}
                  className="rounded accent-cyan-400"
                />
                <label htmlFor="featCheck" className="text-slate-300">
                  Feature prominently on agency flagship showcase
                </label>
              </div>

              <div className="pt-3 flex justify-end gap-2 border-t border-white/10">
                <button type="button" onClick={() => setIsModalOpen(false)} className="px-3 py-1.5 text-xs text-slate-400">
                  Cancel
                </button>
                <button type="submit" className="px-4 py-1.5 text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 rounded-lg">
                  Save Case Study
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
