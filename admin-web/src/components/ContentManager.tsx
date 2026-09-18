import React, { useState } from 'react';
import {
  FileText,
  Save,
  Building,
  Mail,
  Phone,
  MapPin,
  Calendar,
  AlertCircle,
  CheckCircle2,
  Globe
} from 'lucide-react';
import type { AgencySettings } from '../types';

interface ContentManagerProps {
  settings: AgencySettings | null;
  onSaveSettings: (settings: Partial<AgencySettings>) => Promise<void>;
}

export const ContentManager: React.FC<ContentManagerProps> = ({ settings, onSaveSettings }) => {
  const [formData, setFormData] = useState<Partial<AgencySettings>>(
    settings || {
      agencyName: 'GoTech Media',
      tagline: 'Enterprise Digital Product Studio & Cloud Engineering',
      contactEmail: 'hello@gotechmedia.com',
      contactPhone: '+1 (415) 890-3400',
      officeLocations: ['San Francisco, CA (HQ)', 'New York, NY', 'London, UK'],
      operatingHours: 'Monday – Friday, 08:00 – 19:00 PST',
      primaryCalendarBookingUrl: 'https://cal.com/gotechmedia/discovery',
      statusNoticeBanner: 'Q4 Enterprise Production Capacity Open — Scheduling Discovery Sprints'
    }
  );

  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setSuccess(false);
    try {
      await onSaveSettings(formData);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 4000);
    } catch (err: any) {
      alert(`Failed to save settings: ${err.message}`);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6 max-w-4xl">
      <div>
        <h1 className="text-2xl font-bold tracking-tight text-white">Agency Brand & Content Operations</h1>
        <p className="text-xs text-slate-400 mt-1">
          Update global agency identities, contact touchpoints, office locations, and live announcement banners.
        </p>
      </div>

      {success && (
        <div className="p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center gap-2 text-xs text-emerald-300">
          <CheckCircle2 className="w-4 h-4 text-emerald-400" />
          Global agency configuration updated successfully across web and Android client ecosystems!
        </div>
      )}

      <form onSubmit={handleSubmit} className="glass-panel p-6 rounded-2xl space-y-6 text-xs">
        <div className="border-b border-white/5 pb-4">
          <h2 className="text-sm font-bold text-white mb-1">General Brand Identity</h2>
          <p className="text-slate-400">Core studio name and positioning statement.</p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-slate-300 font-medium mb-1">Agency Name</label>
            <input
              type="text"
              required
              value={formData.agencyName || ''}
              onChange={(e) => setFormData({ ...formData, agencyName: e.target.value })}
              className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
            />
          </div>
          <div>
            <label className="block text-slate-300 font-medium mb-1">Brand Tagline</label>
            <input
              type="text"
              value={formData.tagline || ''}
              onChange={(e) => setFormData({ ...formData, tagline: e.target.value })}
              className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
            />
          </div>
        </div>

        <div className="border-b border-white/5 pb-4 pt-2">
          <h2 className="text-sm font-bold text-white mb-1">Direct Contact Touchpoints</h2>
          <p className="text-slate-400">Public support channels displayed on Android client and web portal.</p>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-slate-300 font-medium mb-1">Inquiry / Contact Email</label>
            <input
              type="email"
              required
              value={formData.contactEmail || ''}
              onChange={(e) => setFormData({ ...formData, contactEmail: e.target.value })}
              className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
            />
          </div>
          <div>
            <label className="block text-slate-300 font-medium mb-1">Executive Phone</label>
            <input
              type="tel"
              value={formData.contactPhone || ''}
              onChange={(e) => setFormData({ ...formData, contactPhone: e.target.value })}
              className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-slate-300 font-medium mb-1">Operating Hours</label>
            <input
              type="text"
              value={formData.operatingHours || ''}
              onChange={(e) => setFormData({ ...formData, operatingHours: e.target.value })}
              placeholder="Monday - Friday, 8am - 6pm PST"
              className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
            />
          </div>
          <div>
            <label className="block text-slate-300 font-medium mb-1">VIP Calendar Booking URL</label>
            <input
              type="url"
              value={formData.primaryCalendarBookingUrl || ''}
              onChange={(e) => setFormData({ ...formData, primaryCalendarBookingUrl: e.target.value })}
              placeholder="https://cal.com/..."
              className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
            />
          </div>
        </div>

        <div>
          <label className="block text-slate-300 font-medium mb-1">Global Announcement Banner (Optional)</label>
          <input
            type="text"
            value={formData.statusNoticeBanner || ''}
            onChange={(e) => setFormData({ ...formData, statusNoticeBanner: e.target.value })}
            placeholder="Broadcast banner visible across top of client dashboards..."
            className="glass-input w-full px-3.5 py-2.5 rounded-xl text-xs"
          />
        </div>

        <div>
          <label className="block text-slate-300 font-medium mb-1">
            Global Office Hubs (One per line)
          </label>
          <textarea
            rows={3}
            value={formData.officeLocations?.join('\n') || ''}
            onChange={(e) =>
              setFormData({
                ...formData,
                officeLocations: e.target.value.split('\n').map((s) => s.trim()).filter(Boolean)
              })
            }
            placeholder="San Francisco, CA&#10;New York, NY&#10;London, UK"
            className="glass-input w-full p-3 rounded-xl text-xs leading-relaxed"
          />
        </div>

        <div className="pt-4 flex justify-end">
          <button
            type="submit"
            disabled={saving}
            className="px-6 py-2.5 rounded-xl text-xs font-semibold text-slate-950 bg-cyan-400 hover:bg-cyan-300 disabled:opacity-50 flex items-center gap-2 shadow-md shadow-cyan-500/20"
          >
            <Save className="w-4 h-4" />
            {saving ? 'Publishing Changes...' : 'Save & Publish Global Config'}
          </button>
        </div>
      </form>
    </div>
  );
};
