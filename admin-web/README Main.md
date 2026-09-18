# GoTech Media — Firebase Admin & Operations Web Console

A web-based Mission Control & Executive Operations Dashboard for GoTech Media. Built with **React 18**, **TypeScript**, **Tailwind CSS**, and **Firebase (Cloud Firestore & Authentication)**, styled with an Apple-inspired dark obsidian aesthetic.

---

## 🚀 Key Modules & Capabilities

1. **Mission Control (Dashboard)**:
   - Real-time pipeline KPI metrics: Total Leads, New/Pending Leads, Active Enterprise Clients, Active Sprints, and High-Priority SLA tickets.
   - Quick action triage triggers and recent inquiry inspector.

2. **Lead Management & Conversion**:
   - Filter by status (`New`, `Contacted`, `Qualified`, `Proposal`, `Won`, `Lost`, `Archived`).
   - Detailed client requirements, budget range, timeline, and executive notes.
   - **Convert Lead to Client**: 1-click conversion creating an authorized enterprise `client` record, provisioning an initial sprint `project`, and marking the lead `Won`.

3. **Client Management**:
   - Enterprise organization accounts, industry classification, account tier, and primary contact registry.
   - Associated project sprint tracking.

4. **Project & Sprint Delivery**:
   - Create and edit sprint projects.
   - Real-time server-enforced status selection (`Inquiry`, `Planning`, `Design`, `Development`, `Testing`, `Review`, `Launch`, `Completed`, `On Hold`).
   - Live completion velocity slider (0–100%).
   - Interactive subcollections:
     - **Milestones**: Ordered stages (`Pending`, `In Progress`, `Completed`), target dates, and deliverables.
     - **Documents**: Contractual SOWs, Architecture Blueprints, and deliverables with verified tags.
     - **Messages**: Two-way bilateral communication thread between executives and clients.

5. **Service Catalog Management**:
   - Configure public and client services, features, deliverables, starting price, and display order.

6. **Portfolio & Case Studies**:
   - Showcase flagship client apps, ROI metrics, hero imagery, technologies, and live URLs.

7. **Testimonials & Endorsements**:
   - Manage verified client reviews, star ratings, and toggle visibility on the live site.

8. **Agency Brand & Content Operations**:
   - Global contact points, office hubs, calendar booking link, and live status announcement banner.

9. **Broadcast Notifications**:
   - Compose and dispatch targeted or global push announcements directly to enterprise clients.

10. **Client Support Tickets & SLA**:
    - Manage client support queue, escalation priorities (`High`, `Medium`, `Low`), and resolution statuses.

---

## 🔒 Security & Authorization

- **Zero Client-Side Trust**: Role-based access is authenticated against Firebase Authentication and validated against `/users/{uid}` in Cloud Firestore (`role == 'ADMIN'`).
- Non-admin credentials attempting to log in are immediately rejected and logged out.
- Firestore Security Rules enforce multi-tenant isolation, guaranteeing clients cannot modify project milestones, status, or progress.

---

## 💻 Local Development Setup

1. Navigate to the `admin-web` folder:
   ```bash
   cd admin-web
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Configure environment variables (optional, pre-configured defaults are bundled for the project):
   Create a `.env` file in `admin-web/`:
   ```env
   VITE_FIREBASE_API_KEY=AIzaSyD-Y1PVlrx5zb2-dEL4K-LPO_k2r5IoYXM
   VITE_FIREBASE_AUTH_DOMAIN=gotech-media.firebaseapp.com
   VITE_FIREBASE_PROJECT_ID=gotech-media
   VITE_FIREBASE_STORAGE_BUCKET=gotech-media.firebasestorage.app
   VITE_FIREBASE_MESSAGING_SENDER_ID=442261143835
   VITE_FIREBASE_APP_ID=1:442261143835:web:fac8ded0709076912893be
   ```

4. Start development server:
   ```bash
   npm run dev
   ```

---

## 🌐 Deployment to Vercel

The application is pre-configured with a root `vercel.json` file for immediate zero-config deployment to Vercel:

### Option A: Via Vercel CLI
```bash
cd admin-web
npx vercel
# Follow prompts to deploy preview, then deploy production:
npx vercel --prod
```

### Option B: Via GitHub & Vercel Dashboard
1. Push your repository to GitHub.
2. In the Vercel Dashboard, click **New Project** and import the repository.
3. Set the **Root Directory** to `admin-web`.
4. Framework Preset will automatically detect **Vite**.
5. Click **Deploy**.
