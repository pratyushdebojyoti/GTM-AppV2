export interface AgencyUser {
  id: string;
  email: string;
  displayName: string;
  photoUrl?: string | null;
  role: 'PUBLIC' | 'CLIENT' | 'ADMIN';
  company?: string;
  phone?: string;
  fcmToken?: string | null;
  createdAtEpoch: number;
  updatedAtEpoch: number;
}

export interface AgencyClient {
  id: string;
  userId: string;
  companyName: string;
  industry: string;
  tier: string;
  status: 'ACTIVE' | 'INACTIVE' | 'ONBOARDING' | 'CHURNED';
  contactEmail: string;
  contactPhone: string;
  assignedAccountManager: string;
  createdAtEpoch: number;
}

export type LeadStatus = 'New' | 'Contacted' | 'Qualified' | 'Proposal' | 'Won' | 'Lost' | 'Archived';

export interface AgencyLead {
  id: string;
  leadReferenceId?: string;
  userId?: string | null;
  clientName: string;
  email: string;
  company: string;
  phone: string;
  serviceRequested: string;
  selectedServices?: string[];
  projectDescription: string;
  budgetRange: string;
  expectedTimeline: string;
  referenceWebsite?: string;
  attachmentUrl?: string | null;
  attachmentName?: string | null;
  source?: string;
  status: LeadStatus;
  notes?: string;
  assignedTo?: string;
  createdAtEpoch: number;
}

export type ProjectStatus =
  | 'Inquiry'
  | 'Planning'
  | 'Design'
  | 'Development'
  | 'Testing'
  | 'Review'
  | 'Launch'
  | 'Completed'
  | 'On Hold';

export type MilestoneStatus = 'Pending' | 'In Progress' | 'Completed';

export interface AgencyTeamMember {
  id: string;
  name: string;
  role: string;
  avatarUrl?: string | null;
  email: string;
}

export interface AgencyProjectMilestone {
  id: string;
  projectId: string;
  stepNumber: number;
  title: string;
  description: string;
  status: MilestoneStatus;
  dueDate: string;
  completionDate?: string | null;
  deliverables: string[];
}

export interface AgencyDocument {
  id: string;
  projectId: string;
  clientId: string;
  title: string;
  type: string;
  fileUrl: string;
  storagePath?: string;
  sizeBytes: number;
  uploadedBy: string;
  createdAtEpoch: number;
}

export interface AgencyMessage {
  id: string;
  projectId: string;
  senderId: string;
  senderName: string;
  senderRole: string;
  messageText: string;
  timestampEpoch: number;
  readBy: string[];
}

export interface AgencyProject {
  id: string;
  clientId: string;
  userId: string;
  title: string;
  description: string;
  status: ProjectStatus;
  progressPercentage: number;
  budget: string;
  startDate: string;
  expectedCompletion: string;
  targetDeliveryDate?: string;
  assignedTeam: AgencyTeamMember[];
  techStack: string[];
  githubRepo?: string | null;
  stagingUrl?: string | null;
  createdAtEpoch: number;
  updatedAtEpoch: number;
}

export interface AgencySupportTicket {
  id: string;
  userId: string;
  projectId?: string | null;
  subject: string;
  description: string;
  priority: 'Low' | 'Medium' | 'High';
  status: 'Open' | 'In Progress' | 'Resolved' | 'Closed';
  assignedTo?: string | null;
  createdAtEpoch: number;
  updatedAtEpoch: number;
}

export interface AgencyService {
  id: string;
  title: string;
  tagline: string;
  description: string;
  category: string;
  displayOrder: number;
  isActive: boolean;
  features: string[];
  deliverables: string[];
  startingPrice: string;
  estimatedWeeks: number;
  iconName?: string;
}

export interface PortfolioItem {
  id: string;
  title: string;
  category: string;
  summary: string;
  detailedCaseStudy: string;
  featuredImageUrl: string;
  screenshotUrls: string[];
  technologies: string[];
  metrics: { label: string; value: string }[];
  clientName: string;
  completionDate: string;
  isFeatured: boolean;
  liveUrl?: string;
}

export interface AgencyTestimonial {
  id: string;
  clientName: string;
  clientTitle: string;
  clientCompany: string;
  avatarUrl?: string;
  quote: string;
  projectCategory: string;
  ratingStars: number;
  verifiedEngagement: boolean;
  isEnabled: boolean;
}

export interface AgencySettings {
  id: string;
  agencyName: string;
  tagline: string;
  contactEmail: string;
  contactPhone: string;
  officeLocations: string[];
  operatingHours: string;
  primaryCalendarBookingUrl: string;
  statusNoticeBanner?: string;
  privacyPolicyUrl?: string;
  termsOfServiceUrl?: string;
}

export interface AgencyNotification {
  id: string;
  recipientId: string; // userId or 'ALL'
  title: string;
  body: string;
  type: string;
  targetUrl?: string;
  isRead: boolean;
  createdAtEpoch: number;
}
