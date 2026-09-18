import {
  collection,
  doc,
  getDocs,
  getDoc,
  setDoc,
  updateDoc,
  deleteDoc,
  query,
  where,
  orderBy,
  limit,
  serverTimestamp,
  addDoc
} from 'firebase/firestore';
import { db } from './firebase';
import type {
  AgencyLead,
  AgencyClient,
  AgencyProject,
  AgencyProjectMilestone,
  AgencyDocument,
  AgencyMessage,
  AgencySupportTicket,
  AgencyService,
  PortfolioItem,
  AgencyTestimonial,
  AgencySettings,
  AgencyNotification,
  AgencyUser
} from '../types';

export const AdminApiService = {
  // 1. Leads
  async getLeads(): Promise<AgencyLead[]> {
    try {
      const q = query(collection(db, 'leads'), orderBy('createdAtEpoch', 'desc'));
      const snap = await getDocs(q);
      return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyLead));
    } catch {
      // Fallback if index is building or empty
      const snap = await getDocs(collection(db, 'leads'));
      return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyLead))
        .sort((a, b) => (b.createdAtEpoch || 0) - (a.createdAtEpoch || 0));
    }
  },

  async updateLead(leadId: string, updates: Partial<AgencyLead>): Promise<void> {
    const ref = doc(db, 'leads', leadId);
    await updateDoc(ref, updates);
  },

  async deleteLead(leadId: string): Promise<void> {
    await deleteDoc(doc(db, 'leads', leadId));
  },

  async convertLeadToClient(lead: AgencyLead): Promise<{ clientId: string; projectId: string }> {
    // 1. Create client organization record
    const clientRef = doc(collection(db, 'clients'));
    const clientId = clientRef.id;
    const clientData: AgencyClient = {
      id: clientId,
      userId: lead.userId || clientId,
      companyName: lead.company || lead.clientName,
      industry: 'Technology & Enterprise',
      tier: 'Enterprise Partner',
      status: 'ACTIVE',
      contactEmail: lead.email,
      contactPhone: lead.phone,
      assignedAccountManager: 'Executive Partner Lead',
      createdAtEpoch: Date.now()
    };
    await setDoc(clientRef, clientData);

    // 2. Create initial associated project
    const projectRef = doc(collection(db, 'projects'));
    const projectId = projectRef.id;
    const projectData: AgencyProject = {
      id: projectId,
      clientId: clientId,
      userId: lead.userId || clientId,
      title: `${lead.company || lead.clientName} — ${lead.serviceRequested || 'Digital Platform'}`,
      description: lead.projectDescription || 'Enterprise project initialized from qualified lead discovery.',
      status: 'Planning',
      progressPercentage: 5,
      budget: lead.budgetRange || '$50,000+',
      startDate: new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }),
      expectedCompletion: lead.expectedTimeline || '12 Weeks',
      targetDeliveryDate: lead.expectedTimeline || '12 Weeks',
      assignedTeam: [
        { id: 'tm1', name: 'Marcus Vance', role: 'Lead Solution Architect', email: 'm.vance@gotechmedia.com' },
        { id: 'tm2', name: 'Elena Rostova', role: 'Senior Android & Web Engineer', email: 'e.rostova@gotechmedia.com' }
      ],
      techStack: lead.selectedServices && lead.selectedServices.length > 0
        ? lead.selectedServices
        : ['React', 'TypeScript', 'Kotlin', 'Firebase', 'Google Cloud'],
      createdAtEpoch: Date.now(),
      updatedAtEpoch: Date.now()
    };
    await setDoc(projectRef, projectData);

    // 3. Mark lead as Won
    await updateDoc(doc(db, 'leads', lead.id), {
      status: 'Won',
      notes: `Converted to enterprise client (${clientId}) and project (${projectId}) on ${new Date().toLocaleDateString()}`
    });

    return { clientId, projectId };
  },

  // 2. Clients
  async getClients(): Promise<AgencyClient[]> {
    const snap = await getDocs(collection(db, 'clients'));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyClient));
  },

  async saveClient(client: Partial<AgencyClient>): Promise<string> {
    if (client.id) {
      await updateDoc(doc(db, 'clients', client.id), client);
      return client.id;
    } else {
      const ref = doc(collection(db, 'clients'));
      const newClient = {
        ...client,
        id: ref.id,
        createdAtEpoch: Date.now()
      };
      await setDoc(ref, newClient);
      return ref.id;
    }
  },

  async deleteClient(clientId: string): Promise<void> {
    await deleteDoc(doc(db, 'clients', clientId));
  },

  // 3. Projects
  async getProjects(): Promise<AgencyProject[]> {
    const snap = await getDocs(collection(db, 'projects'));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyProject));
  },

  async saveProject(project: Partial<AgencyProject>): Promise<string> {
    if (project.id) {
      await updateDoc(doc(db, 'projects', project.id), {
        ...project,
        updatedAtEpoch: Date.now()
      });
      return project.id;
    } else {
      const ref = doc(collection(db, 'projects'));
      const newProj = {
        ...project,
        id: ref.id,
        createdAtEpoch: Date.now(),
        updatedAtEpoch: Date.now()
      };
      await setDoc(ref, newProj);
      return ref.id;
    }
  },

  async deleteProject(projectId: string): Promise<void> {
    await deleteDoc(doc(db, 'projects', projectId));
  },

  // Project Subcollections (Milestones, Documents, Messages)
  async getProjectMilestones(projectId: string): Promise<AgencyProjectMilestone[]> {
    const snap = await getDocs(collection(db, 'projects', projectId, 'milestones'));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyProjectMilestone))
      .sort((a, b) => a.stepNumber - b.stepNumber);
  },

  async saveProjectMilestone(projectId: string, milestone: Partial<AgencyProjectMilestone>): Promise<void> {
    if (milestone.id) {
      await updateDoc(doc(db, 'projects', projectId, 'milestones', milestone.id), milestone);
    } else {
      const ref = doc(collection(db, 'projects', projectId, 'milestones'));
      await setDoc(ref, { ...milestone, id: ref.id, projectId });
    }
  },

  async deleteProjectMilestone(projectId: string, milestoneId: string): Promise<void> {
    await deleteDoc(doc(db, 'projects', projectId, 'milestones', milestoneId));
  },

  async getProjectDocuments(projectId: string): Promise<AgencyDocument[]> {
    const snap = await getDocs(collection(db, 'projects', projectId, 'documents'));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyDocument));
  },

  async saveProjectDocument(projectId: string, docData: Partial<AgencyDocument>): Promise<void> {
    if (docData.id) {
      await updateDoc(doc(db, 'projects', projectId, 'documents', docData.id), docData);
    } else {
      const ref = doc(collection(db, 'projects', projectId, 'documents'));
      await setDoc(ref, {
        ...docData,
        id: ref.id,
        projectId,
        createdAtEpoch: Date.now()
      });
    }
  },

  async deleteProjectDocument(projectId: string, documentId: string): Promise<void> {
    await deleteDoc(doc(db, 'projects', projectId, 'documents', documentId));
  },

  async getProjectMessages(projectId: string): Promise<AgencyMessage[]> {
    try {
      const q = query(
        collection(db, 'messages'),
        where('projectId', '==', projectId),
        orderBy('timestampEpoch', 'asc')
      );
      const snap = await getDocs(q);
      return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyMessage));
    } catch {
      const q = query(collection(db, 'messages'), where('projectId', '==', projectId));
      const snap = await getDocs(q);
      return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyMessage))
        .sort((a, b) => a.timestampEpoch - b.timestampEpoch);
    }
  },

  async sendAdminMessage(projectId: string, adminUser: AgencyUser, text: string): Promise<void> {
    const ref = doc(collection(db, 'messages'));
    const message: AgencyMessage = {
      id: ref.id,
      projectId,
      senderId: adminUser.id,
      senderName: `${adminUser.displayName} (GoTech Admin)`,
      senderRole: 'ADMIN',
      messageText: text,
      timestampEpoch: Date.now(),
      readBy: [adminUser.id]
    };
    await setDoc(ref, message);
  },

  // 4. Support Tickets
  async getSupportTickets(): Promise<AgencySupportTicket[]> {
    const snap = await getDocs(collection(db, 'supportTickets'));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencySupportTicket))
      .sort((a, b) => (b.updatedAtEpoch || 0) - (a.updatedAtEpoch || 0));
  },

  async updateSupportTicket(ticketId: string, updates: Partial<AgencySupportTicket>): Promise<void> {
    await updateDoc(doc(db, 'supportTickets', ticketId), {
      ...updates,
      updatedAtEpoch: Date.now()
    });
  },

  // 5. Services
  async getServices(): Promise<AgencyService[]> {
    const snap = await getDocs(collection(db, 'services'));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyService))
      .sort((a, b) => a.displayOrder - b.displayOrder);
  },

  async saveService(service: Partial<AgencyService>): Promise<void> {
    if (service.id) {
      await updateDoc(doc(db, 'services', service.id), service);
    } else {
      const ref = doc(collection(db, 'services'));
      await setDoc(ref, { ...service, id: ref.id });
    }
  },

  async deleteService(serviceId: string): Promise<void> {
    await deleteDoc(doc(db, 'services', serviceId));
  },

  // 6. Portfolio
  async getPortfolio(): Promise<PortfolioItem[]> {
    const snap = await getDocs(collection(db, 'portfolio'));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as PortfolioItem));
  },

  async savePortfolio(item: Partial<PortfolioItem>): Promise<void> {
    if (item.id) {
      await updateDoc(doc(db, 'portfolio', item.id), item);
    } else {
      const ref = doc(collection(db, 'portfolio'));
      await setDoc(ref, { ...item, id: ref.id });
    }
  },

  async deletePortfolio(itemId: string): Promise<void> {
    await deleteDoc(doc(db, 'portfolio', itemId));
  },

  // 7. Testimonials
  async getTestimonials(): Promise<AgencyTestimonial[]> {
    const snap = await getDocs(collection(db, 'testimonials'));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyTestimonial));
  },

  async saveTestimonial(testimonial: Partial<AgencyTestimonial>): Promise<void> {
    if (testimonial.id) {
      await updateDoc(doc(db, 'testimonials', testimonial.id), testimonial);
    } else {
      const ref = doc(collection(db, 'testimonials'));
      await setDoc(ref, { ...testimonial, id: ref.id });
    }
  },

  async deleteTestimonial(id: string): Promise<void> {
    await deleteDoc(doc(db, 'testimonials', id));
  },

  // 8. Content / Agency Settings
  async getSettings(): Promise<AgencySettings | null> {
    const snap = await getDoc(doc(db, 'agencySettings', 'main_config'));
    if (snap.exists()) {
      return { id: snap.id, ...snap.data() } as AgencySettings;
    }
    return null;
  },

  async saveSettings(settings: Partial<AgencySettings>): Promise<void> {
    await setDoc(doc(db, 'agencySettings', 'main_config'), settings, { merge: true });
  },

  // 9. Notifications Broadcast
  async publishNotification(notification: Omit<AgencyNotification, 'id' | 'createdAtEpoch' | 'isRead'>): Promise<void> {
    const ref = doc(collection(db, 'notifications'));
    const item: AgencyNotification = {
      ...notification,
      id: ref.id,
      isRead: false,
      createdAtEpoch: Date.now()
    };
    await setDoc(ref, item);
  },

  async getNotifications(): Promise<AgencyNotification[]> {
    const snap = await getDocs(collection(db, 'notifications'));
    return snap.docs.map(d => ({ id: d.id, ...d.data() } as AgencyNotification))
      .sort((a, b) => b.createdAtEpoch - a.createdAtEpoch);
  }
};
