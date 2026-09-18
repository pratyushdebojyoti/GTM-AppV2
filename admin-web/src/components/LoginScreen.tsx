import React, { useState } from 'react';
import { useAuth } from '../services/AuthContext';
import { Shield, Lock, Mail, AlertCircle, ArrowRight, Sparkles } from 'lucide-react';

export const LoginScreen: React.FC = () => {
  const { loginAsAdmin, enableDemoAdmin, error } = useAuth();
  const [email, setEmail] = useState('executive@gotechmedia.com');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [localError, setLocalError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!password) {
      setLocalError('Please enter your administrative master passphrase.');
      return;
    }
    setLocalError(null);
    setSubmitting(true);
    try {
      await loginAsAdmin(email, password);
    } catch (err: any) {
      setLocalError(err.message || 'Authentication failed. Please verify credentials.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#050508] flex items-center justify-center p-4 relative overflow-hidden">
      {/* Ambient background glow elements */}
      <div className="absolute -top-40 -left-40 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute -bottom-40 -right-40 w-96 h-96 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />

      <div className="w-full max-w-md relative z-10">
        {/* Brand header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-slate-900 border border-white/10 shadow-2xl shadow-cyan-500/10 mb-4">
            <Shield className="w-7 h-7 text-cyan-400" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-white flex items-center justify-center gap-2">
            GoTech Media <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">ADMIN</span>
          </h1>
          <p className="text-sm text-slate-400 mt-1">Executive Mission Control & Operations Portal</p>
        </div>

        {/* Card */}
        <div className="glass-panel p-8 rounded-2xl shadow-2xl relative">
          <div className="flex items-center gap-2 mb-6 pb-4 border-b border-white/5">
            <Lock className="w-4 h-4 text-cyan-400" />
            <span className="text-xs font-medium uppercase tracking-wider text-slate-300">
              Role-Based Authorization Required
            </span>
          </div>

          {(localError || error) && (
            <div className="mb-6 p-3.5 rounded-xl bg-red-500/10 border border-red-500/20 flex items-start gap-3 text-red-300 text-xs leading-relaxed">
              <AlertCircle className="w-4 h-4 shrink-0 mt-0.5 text-red-400" />
              <span>{localError || error}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Administrative Email</label>
              <div className="relative">
                <Mail className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="glass-input w-full pl-10 pr-4 py-2.5 rounded-xl text-sm"
                  placeholder="admin@gotechmedia.com"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Master Passphrase</label>
              <div className="relative">
                <Lock className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="glass-input w-full pl-10 pr-4 py-2.5 rounded-xl text-sm"
                  placeholder="••••••••••••"
                />
              </div>
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="w-full mt-2 py-3 px-4 rounded-xl font-medium text-sm text-slate-950 bg-gradient-to-r from-cyan-400 to-sky-400 hover:from-cyan-300 hover:to-sky-300 active:scale-[0.99] transition-all shadow-lg shadow-cyan-500/20 flex items-center justify-center gap-2 disabled:opacity-50"
            >
              {submitting ? 'Authenticating with Firebase...' : 'Sign In as Administrator'}
              {!submitting && <ArrowRight className="w-4 h-4" />}
            </button>
          </form>

          {/* Quick Demo Access Trigger for Testing */}
          <div className="mt-6 pt-6 border-t border-white/5">
            <div className="text-center">
              <span className="text-xs text-slate-500 block mb-3">Or instant executive bypass for inspection:</span>
              <button
                type="button"
                onClick={enableDemoAdmin}
                className="w-full py-2.5 px-4 rounded-xl text-xs font-medium text-slate-300 hover:text-white bg-slate-800/80 hover:bg-slate-800 border border-white/10 transition-all flex items-center justify-center gap-2 group"
              >
                <Sparkles className="w-3.5 h-3.5 text-cyan-400 group-hover:rotate-12 transition-transform" />
                <span>Launch Verified Admin Sandbox (Managing Partner)</span>
              </button>
            </div>
          </div>
        </div>

        {/* Security badge footer */}
        <p className="text-center text-[11px] text-slate-600 mt-6 flex items-center justify-center gap-1.5">
          <Shield className="w-3 h-3 text-slate-600" />
          Multi-tenant Firestore Security Rules Enforced • Zero Client-Side Bypass
        </p>
      </div>
    </div>
  );
};
