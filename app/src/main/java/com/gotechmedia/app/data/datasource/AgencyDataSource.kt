package com.gotechmedia.app.data.datasource

import com.gotechmedia.app.data.model.AgencyMetricEntity
import com.gotechmedia.app.data.model.AgencyProcessStepEntity
import com.gotechmedia.app.data.model.AgencyProfileEntity
import com.gotechmedia.app.data.model.AgencyServiceEntity
import com.gotechmedia.app.data.model.PortfolioItemEntity
import com.gotechmedia.app.data.model.UserProfileEntity
import com.gotechmedia.app.domain.model.ContactInquiry
import com.gotechmedia.app.domain.model.QuoteRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * Data source contract for GoTech Media.
 * Decouples concrete remote or local storage implementations from the repository layer.
 */
interface AgencyDataSource {
    fun streamProfile(): Flow<AgencyProfileEntity>
    fun streamServices(): Flow<List<AgencyServiceEntity>>
    fun streamPortfolio(): Flow<List<PortfolioItemEntity>>
    fun streamProcessSteps(): Flow<List<AgencyProcessStepEntity>>
    fun streamUserProfile(): Flow<UserProfileEntity?>
    suspend fun submitQuote(request: QuoteRequest): Result<Unit>
    suspend fun submitContact(inquiry: ContactInquiry): Result<Unit>
    suspend fun refresh(): Result<Unit>
}

/**
 * Production-ready In-Memory data source providing baseline repository data
 * featuring all 14 requested practice services, realistic architectural case studies,
 * and delivery process stages.
 */
class InMemoryAgencyDataSource : AgencyDataSource {

    private val profileState = MutableStateFlow(
        AgencyProfileEntity(
            name = "GoTech Media",
            headline = "Engineering Digital Sovereignty & Next-Gen Software",
            mission = "Empowering ambitious ventures and market leaders with bespoke mobile systems, intelligent web platforms, and data-driven growth architectures.",
            foundedYear = 2021,
            location = "San Francisco • London • Singapore",
            specializations = listOf(
                "Mobile & Android Engineering",
                "Full-Stack Web Systems",
                "AI & Intelligent Automation",
                "Brand Identity & UI/UX",
                "Enterprise Growth Marketing"
            ),
            metrics = listOf(
                AgencyMetricEntity("14 Practice Areas", "Full Stack", "End-to-end digital lifecycle coverage"),
                AgencyMetricEntity("Architecture", "100% Bespoke", "Zero pre-made templates or technical shortcuts"),
                AgencyMetricEntity("Code Standards", "Clean Arch", "Strict separation of concerns & unit testability"),
                AgencyMetricEntity("Consultation", "< 24h Response", "Direct senior engineering & strategy review")
            )
        )
    )

    private val servicesState = MutableStateFlow(
        listOf(
            AgencyServiceEntity(
                id = "service_website_dev",
                title = "Website Development",
                category = "Engineering & Web",
                summary = "Bespoke, high-performance web platforms engineered with modern frameworks, sub-second load times, and fluid responsive layouts.",
                capabilities = listOf(
                    "Jamstack & SSR Architectures",
                    "Headless CMS Integration",
                    "Core Web Vitals Optimization",
                    "Responsive Accessibility (WCAG 2.1)",
                    "Security Hardening & HTTPS Setup"
                ),
                deliverables = listOf(
                    "Production-ready codebase",
                    "Responsive viewport adaptations",
                    "Content management backend",
                    "Automated CI/CD deployment pipelines"
                ),
                methodology = "Component-driven design systems, server-rendered static generation, and automated performance audits on every pull request.",
                techStack = listOf("Next.js", "TypeScript", "Tailwind CSS", "Vercel / Cloudflare", "Node.js"),
                featured = true
            ),
            AgencyServiceEntity(
                id = "service_web_apps",
                title = "Web Applications",
                category = "Engineering & Web",
                summary = "Complex web portals, real-time collaboration platforms, and enterprise dashboards with resilient state architectures.",
                capabilities = listOf(
                    "Single-Page & Micro-Frontend Systems",
                    "Real-Time WebSocket Sync",
                    "Role-Based Access Control (RBAC)",
                    "Complex Data Visualizations",
                    "Offline Caching & State Hydration"
                ),
                deliverables = listOf(
                    "Full-stack web application",
                    "Interactive data dashboards",
                    "REST / GraphQL API integration",
                    "End-to-end integration test suites"
                ),
                methodology = "Domain-Driven Design (DDD), strict state machines, modular component trees, and enterprise security standards.",
                techStack = listOf("React", "Next.js", "Ktor", "PostgreSQL", "Docker", "Redis"),
                featured = true
            ),
            AgencyServiceEntity(
                id = "service_android_apps",
                title = "Android Apps",
                category = "Mobile Engineering",
                summary = "Cutting-edge native Android applications built with Jetpack Compose, Kotlin Coroutines, and modern Material Design 3.",
                capabilities = listOf(
                    "Jetpack Compose Declarative UI",
                    "Clean Architecture & MVI/MVVM",
                    "Offline-First Room Persistence",
                    "Hardware Sensors & Bluetooth Integration",
                    "Google Play Publishing & Security"
                ),
                deliverables = listOf(
                    "Production APK / AAB artifacts",
                    "Modular Gradle multi-module project",
                    "Automated Robolectric JVM test suite",
                    "Google Play Console deployment setup"
                ),
                methodology = "Unidirectional Data Flow (UDF), rigorous memory profiling, dark-mode first design tokens, and smooth 60fps animations.",
                techStack = listOf("Kotlin", "Jetpack Compose", "Coroutines & Flow", "Room", "Retrofit", "Hilt"),
                featured = true
            ),
            AgencyServiceEntity(
                id = "service_ios_apps",
                title = "iOS Apps",
                category = "Mobile Engineering",
                summary = "Fluid native iOS and iPadOS experiences architected using SwiftUI, Swift Concurrency, and Apple Human Interface Guidelines.",
                capabilities = listOf(
                    "SwiftUI & Combine Frameworks",
                    "Swift Concurrency (async/await)",
                    "CoreData & SwiftData Persistence",
                    "Apple Vision / Dynamic Island Support",
                    "App Store Review & Release Management"
                ),
                deliverables = listOf(
                    "Xcode workspace & source repository",
                    "TestFlight beta deployment build",
                    "Adaptive layouts for iPhone and iPad",
                    "Full App Store submission package"
                ),
                methodology = "Human Interface Guideline compliance, native haptic feedback, fluid spring animations, and Apple-grade craft.",
                techStack = listOf("Swift", "SwiftUI", "SwiftData", "StoreKit", "TestFlight"),
                featured = false
            ),
            AgencyServiceEntity(
                id = "service_ui_ux",
                title = "UI/UX Design",
                category = "Design & Brand",
                summary = "Apple-inspired minimalist product design, interaction design, and comprehensive design systems built for scale.",
                capabilities = listOf(
                    "Interactive High-Fidelity Prototyping",
                    "Multi-Platform Design Systems (Tokens)",
                    "User Journey Mapping & Information Architecture",
                    "Micro-Interactions & Motion Choreography",
                    "Accessibility Audits (Contrast & Touch Targets)"
                ),
                deliverables = listOf(
                    "Figma component library with design tokens",
                    "Clickable interactive prototypes",
                    "Developer handoff documentation",
                    "Visual design asset library"
                ),
                methodology = "User-centric iterative sprints, strict 8dp spatial grids, typography hierarchy calibration, and tactile micro-states.",
                techStack = listOf("Figma", "Design Tokens", "Protopie", "Material 3", "Apple HIG"),
                featured = true
            ),
            AgencyServiceEntity(
                id = "service_seo",
                title = "SEO",
                category = "Growth & Marketing",
                summary = "Technical search engine optimization, structured schema markup, and crawlability engineering for organic market visibility.",
                capabilities = listOf(
                    "Technical Site Audits & Crawl Optimization",
                    "JSON-LD Schema & Semantic Markup",
                    "Core Web Vitals Performance Tuning",
                    "Information Architecture & Internal Linking",
                    "Search Console & Analytics Integration"
                ),
                deliverables = listOf(
                    "Technical SEO audit & remediation plan",
                    "Automated XML sitemap & robots.txt",
                    "Schema markup implementation",
                    "Keyword mapping & performance telemetry"
                ),
                methodology = "Data-backed technical indexing, speed enhancements, clean canonical structures, and continuous search console monitoring.",
                techStack = listOf("Schema.org", "Google Search Console", "Screaming Frog", "Lighthouse", "Semrush"),
                featured = false
            ),
            AgencyServiceEntity(
                id = "service_branding",
                title = "Branding",
                category = "Design & Brand",
                summary = "Cohesive brand identities, geometric marks, typography palettes, and comprehensive guidelines for forward-thinking enterprises.",
                capabilities = listOf(
                    "Logo Mark & Logotype Development",
                    "Visual Identity Systems & Color Science",
                    "Brand Guidelines & Usage Standards",
                    "Digital Collateral & Social Kits",
                    "Brand Voice & Editorial Direction"
                ),
                deliverables = listOf(
                    "Vector brand identity package (SVG, EPS, PNG)",
                    "Comprehensive brand style guide (PDF)",
                    "Typography pairing matrix & web fonts",
                    "Iconography and decorative vector toolkit"
                ),
                methodology = "Geometry-driven identity construction, timeless minimalism, cross-medium legibility, and high-contrast digital precision.",
                techStack = listOf("Vector Geometry", "Typography Licensing", "Figma", "Illustrator"),
                featured = false
            ),
            AgencyServiceEntity(
                id = "service_digital_marketing",
                title = "Digital Marketing",
                category = "Growth & Marketing",
                summary = "Full-funnel digital acquisition, multi-channel performance campaigns, and data-driven customer lifetime value optimization.",
                capabilities = listOf(
                    "Multi-Channel Campaign Architecture",
                    "Conversion Rate Optimization (CRO)",
                    "Customer Journey Attribution Modeling",
                    "Landing Page Testing & Experimentation",
                    "Marketing Automation & Lifecycle Triggers"
                ),
                deliverables = listOf(
                    "Growth strategy roadmap & campaign architecture",
                    "Configured analytics & conversion pixels",
                    "Dedicated landing pages with A/B variants",
                    "Monthly performance telemetry reports"
                ),
                methodology = "Rigorous attribution tracking, hypothesis-driven experimentation, conversion funnel instrumentation, and rapid iteration.",
                techStack = listOf("Google Tag Manager", "Mixpanel", "Segment", "Looker Studio"),
                featured = false
            ),
            AgencyServiceEntity(
                id = "service_meta_ads",
                title = "Meta Ads",
                category = "Growth & Marketing",
                summary = "Targeted acquisition and retargeting campaigns across Instagram and Facebook engineered for measurable return on ad spend.",
                capabilities = listOf(
                    "Conversions API (CAPI) Server-Side Tracking",
                    "Audience Segmentation & Lookalikes",
                    "Dynamic Creative Optimization (DCO)",
                    "High-Converting Video & Carousel Assets",
                    "ROAS-Driven Budget Allocation"
                ),
                deliverables = listOf(
                    "Meta Ads Manager campaign infrastructure",
                    "Server-side Conversions API integration",
                    "Creative testing matrix & asset library",
                    "Real-time ROI dashboard"
                ),
                methodology = "First-party server tracking resilience, disciplined creative sprints, and structured campaign budgeting frameworks.",
                techStack = listOf("Meta Ads Manager", "Meta CAPI", "Pixel Tracking", "Figma Ad Tooling"),
                featured = false
            ),
            AgencyServiceEntity(
                id = "service_google_ads",
                title = "Google Ads",
                category = "Growth & Marketing",
                summary = "High-intent search, Performance Max, and YouTube ad architectures designed to capture qualified commercial demand.",
                capabilities = listOf(
                    "Intent-Based Search Campaigns",
                    "Performance Max (PMax) Machine Bidding",
                    "Negative Keyword Negative Scrubbing",
                    "Enhanced Conversions Implementation",
                    "Competitor Share-of-Voice Capture"
                ),
                deliverables = listOf(
                    "Google Ads account architecture",
                    "Targeted keyword & negative keyword matrices",
                    "Ad copy variations & responsive search ads",
                    "Enhanced conversion tracking"
                ),
                methodology = "Single-theme ad groups, exact-match intent prioritization, quality score optimization, and disciplined bid automation.",
                techStack = listOf("Google Ads", "Google Analytics 4", "Search Console", "Tag Manager"),
                featured = false
            ),
            AgencyServiceEntity(
                id = "service_content_strategy",
                title = "Content Strategy",
                category = "Growth & Marketing",
                summary = "Editorial frameworks, thought leadership narratives, and technical documentation systems that position your team as an industry authority.",
                capabilities = listOf(
                    "Content Architecture & Topic Clusters",
                    "Technical Whitepapers & Case Studies",
                    "Developer Documentation Portals",
                    "Editorial Calendar & Production Workflow",
                    "Brand Tone of Voice Playbooks"
                ),
                deliverables = listOf(
                    "Quarterly content strategy blueprint",
                    "Topic cluster and keyword architecture",
                    "Style guide and terminology glossary",
                    "Publishing workflow automation"
                ),
                methodology = "Subject-matter depth, domain accuracy, authoritative tone, and structured modular content distribution.",
                techStack = listOf("Markdown / MDX", "Notion CMS", "Git-based Workflows", "Ahrefs"),
                featured = false
            ),
            AgencyServiceEntity(
                id = "service_automation",
                title = "Automation",
                category = "Intelligent Systems",
                summary = "End-to-end business workflow automation, API orchestrations, and data pipelines that eliminate repetitive operational friction.",
                capabilities = listOf(
                    "Custom Webhook & API Integrations",
                    "CRM & ERP Pipeline Synchronization",
                    "Automated Client Onboarding Flows",
                    "Error Alerting & Incident Telemetry",
                    "Scheduled ETL & Data Sync Tasks"
                ),
                deliverables = listOf(
                    "Production workflow automation pipeline",
                    "API connectors & webhook endpoints",
                    "Monitoring & retry mechanisms",
                    "Operational runbook documentation"
                ),
                methodology = "Idempotent event processing, resilient error retry queues, structured logging, and zero data loss architecture.",
                techStack = listOf("Python", "Node.js", "Zapier / Make", "Airflow", "Cloud Functions"),
                featured = false
            ),
            AgencyServiceEntity(
                id = "service_ai_integration",
                title = "AI Integration",
                category = "Intelligent Systems",
                summary = "Custom generative AI workflows, intelligent knowledge assistants, and machine learning pipelines tailored for your business data.",
                capabilities = listOf(
                    "LLM Integration & Prompt Pipelines",
                    "Retrieval-Augmented Generation (RAG)",
                    "Multimodal Audio & Vision Processing",
                    "On-Device Machine Learning Models",
                    "AI Safety, Guardrails & Token Optimization"
                ),
                deliverables = listOf(
                    "Production AI integration backend",
                    "Vector database & document index",
                    "Custom client-facing AI interfaces",
                    "Latency & token cost optimization profile"
                ),
                methodology = "Domain-grounded RAG, structured JSON generation, semantic chunking, and deterministic guardrails.",
                techStack = listOf("Gemini AI API", "LangChain", "Pinecone / pgvector", "Python", "TensorFlow Lite"),
                featured = true
            ),
            AgencyServiceEntity(
                id = "service_growth_consulting",
                title = "Business Growth Consulting",
                category = "Strategy & Advisory",
                summary = "Strategic technical advisory, product roadmap validation, and digital monetization frameworks for leadership teams.",
                capabilities = listOf(
                    "Technical Feasibility & Architecture Review",
                    "Product-Market Fit & Roadmap Alignment",
                    "Digital Monetization & Pricing Modeling",
                    "Team Capability & Vendor Assessment",
                    "Executive Advisory & Board Reporting"
                ),
                deliverables = listOf(
                    "Comprehensive strategic evaluation report",
                    "Prioritized engineering and growth roadmap",
                    "Technology stack recommendation dossier",
                    "Executive briefing and quarterly checkpoints"
                ),
                methodology = "First-principles analysis, metric-driven product decisions, bottleneck identification, and clear milestone roadmaps.",
                techStack = listOf("Strategic Modeling", "Unit Economics", "Roadmap Frameworks", "Executive Briefs"),
                featured = false
            )
        )
    )

    private val portfolioState = MutableStateFlow(
        listOf(
            PortfolioItemEntity(
                id = "portfolio_fintech_core",
                title = "Apex Wealth & Treasury Portal",
                category = "Web Applications",
                industry = "Financial Technology",
                summary = "Engineered a low-latency digital treasury console enabling real-time capital allocation, institutional reporting, and audit logs.",
                challenge = "Legacy portal suffered from 4-second render delays and unmanageable state across high-frequency market tickers.",
                solution = "Designed an event-driven architecture using WebSocket channels, custom Canvas charts, and optimistic local state cache.",
                architectureHighlights = listOf(
                    "Sub-100ms real-time quote streaming via WebSockets",
                    "Client-side zero-knowledge security encryption",
                    "Modular widget dashboard with drag-and-drop layout engine"
                ),
                techStack = listOf("Next.js", "TypeScript", "Tailwind CSS", "Redis", "WebSockets"),
                deliverables = listOf(
                    "Enterprise web portal",
                    "Design system & component library",
                    "Financial chart visualization engine",
                    "SOC2 compliance documentation"
                ),
                isFeatured = true
            ),
            PortfolioItemEntity(
                id = "portfolio_android_health",
                title = "Vitalis Biometric Companion",
                category = "Android Apps",
                industry = "Digital Health & Wearables",
                summary = "High-performance native Android application integrating Bluetooth Low Energy biometric wearables with offline analytics.",
                challenge = "Client needed continuous real-time sensor processing without degrading battery life or dropping BLE connection packets.",
                solution = "Architected a dedicated foreground BLE sync service with Jetpack Compose UI, Room time-series storage, and coroutines.",
                architectureHighlights = listOf(
                    "Optimized BLE packet parsing with zero UI frame drops",
                    "Offline-first Room database with automated rolling partition cleanup",
                    "Full Material 3 dynamic theming with accessible high-contrast modes"
                ),
                techStack = listOf("Kotlin", "Jetpack Compose", "BLE APIs", "Room", "Coroutines"),
                deliverables = listOf(
                    "Native Android application (Play Store ready)",
                    "Background sync engine",
                    "Device onboarding and pairing flow",
                    "Automated Robolectric CUJ test suite"
                ),
                isFeatured = true
            ),
            PortfolioItemEntity(
                id = "portfolio_logistics_intel",
                title = "LogiRoute Fleet Automation",
                category = "AI Integration",
                industry = "Logistics & Supply Chain",
                summary = "AI-assisted dispatch optimization system calculating route contingencies, vehicle load balancing, and automated driver dispatch.",
                challenge = "Dispatchers manually coordinated hundreds of daily delivery exceptions via phone and disconnected spreadsheets.",
                solution = "Deployed automated event triggers integrated with Gemini multimodal models to transcribe dispatch voice memos and synthesize route plans.",
                architectureHighlights = listOf(
                    "Automated voice-to-schedule ingestion pipeline",
                    "Multi-variable constraint optimization algorithm",
                    "Real-time driver notification and acknowledgment channels"
                ),
                techStack = listOf("Gemini AI API", "Python", "FastAPI", "PostgreSQL", "Docker"),
                deliverables = listOf(
                    "AI scheduling backend service",
                    "Dispatcher web workspace",
                    "Driver notification gateway",
                    "Exception resolution analytics"
                ),
                isFeatured = true
            ),
            PortfolioItemEntity(
                id = "portfolio_brand_ident",
                title = "Kinetix Robotics Identity & Platform",
                category = "UI/UX Design",
                industry = "Industrial Automation",
                summary = "Comprehensive brand identity system, 3D interaction guidelines, and responsive corporate web platform for advanced robotics.",
                challenge = "The client had groundbreaking hardware but an outdated visual presence that failed to convey enterprise reliability.",
                solution = "Created a stark, high-precision visual identity, interactive web experience, and tokenized multi-platform design system.",
                architectureHighlights = listOf(
                    "Modular typography and color system built on modern design tokens",
                    "Interactive 3D model viewer with smooth fallback for low-power devices",
                    "100/100 Google Lighthouse Core Web Vitals score"
                ),
                techStack = listOf("Figma", "Design Tokens", "Next.js", "Three.js", "Tailwind CSS"),
                deliverables = listOf(
                    "Brand identity guidelines & vector marks",
                    "Interactive corporate web platform",
                    "Figma UI component kit",
                    "Marketing collateral templates"
                ),
                isFeatured = false
            )
        )
    )

    private val processStepsState = MutableStateFlow(
        listOf(
            AgencyProcessStepEntity(
                stepNumber = 1,
                title = "Discovery & Technical Architecture",
                subtitle = "Aligning business objectives with scalable engineering",
                description = "We conduct deep architectural discovery, system audits, and requirements validation. Rather than guessing, we define explicit technical specifications, data models, and measurable milestones.",
                deliverables = listOf(
                    "Technical Specification Document",
                    "System Architecture Diagram",
                    "Scope & Milestone Roadmap",
                    "Security & Infrastructure Baseline"
                ),
                duration = "Weeks 1 - 2"
            ),
            AgencyProcessStepEntity(
                stepNumber = 2,
                title = "Design Systems & High-Fidelity UI/UX",
                subtitle = "Apple-inspired craft, micro-interactions, and prototyping",
                description = "Our design team translates requirements into tactile, minimalist interfaces adhering to strict spatial grids and ergonomic touch standards. We test interactive flows before a single line of production code is written.",
                deliverables = listOf(
                    "Interactive Clickable Prototype",
                    "Figma Design Token Library",
                    "Responsive Screen Variations",
                    "Accessibility & Contrast Review"
                ),
                duration = "Weeks 2 - 4"
            ),
            AgencyProcessStepEntity(
                stepNumber = 3,
                title = "Agile Production & Clean Engineering",
                subtitle = "Kotlin, Compose, modern web, and resilient infrastructure",
                description = "We build production-grade software using Clean Architecture, Unidirectional Data Flow, and strict automated testing. Every sprint produces tangible, demonstrable software with continuous integration.",
                deliverables = listOf(
                    "Modular Codebase in Git Repository",
                    "Sprint Demos & Bi-Weekly Releases",
                    "Automated Unit & Integration Tests",
                    "Real-Time Telemetry & Error Logging"
                ),
                duration = "Weeks 4 - 10"
            ),
            AgencyProcessStepEntity(
                stepNumber = 4,
                title = "Quality Assurance & Performance Auditing",
                subtitle = "Stress testing, memory profiling, and security hardening",
                description = "We subject every interface and API endpoint to thorough profiling. On mobile, we benchmark frame rates and memory footprint; on web, we ensure sub-second First Contentful Paint and 100% test coverage for critical flows.",
                deliverables = listOf(
                    "Quality Assurance Verification Report",
                    "Performance Profiling Matrix",
                    "Security & Vulnerability Scan",
                    "Cross-Device Compatibility Matrix"
                ),
                duration = "Weeks 10 - 11"
            ),
            AgencyProcessStepEntity(
                stepNumber = 5,
                title = "Deployment & Production Launch",
                subtitle = "Zero-downtime release and store submission",
                description = "We manage the entire release cycle, including Google Play Store or Apple App Store review compliance, cloud infrastructure provisioning, DNS routing, and automated rollback configurations.",
                deliverables = listOf(
                    "Store Artifact Submission & Approvals",
                    "Production Cloud Infrastructure",
                    "Monitoring & Sentry / Firebase Crashlytics Setup",
                    "Launch Day Support & Monitoring"
                ),
                duration = "Week 12"
            ),
            AgencyProcessStepEntity(
                stepNumber = 6,
                title = "Continuous Evolution & Growth",
                subtitle = "SLA support, feature iteration, and telemetry optimization",
                description = "Post-launch, we provide ongoing architectural maintenance, technical support SLAs, and data-driven feature iterations to continuously compound client ROI.",
                deliverables = listOf(
                    "Monthly Performance Reviews",
                    "Proactive Dependency & Security Updates",
                    "Conversion & Funnel Telemetry Reports",
                    "Priority Engineering Escalation Channel"
                ),
                duration = "Ongoing"
            )
        )
    )

    private val userProfileState = MutableStateFlow<UserProfileEntity?>(
        UserProfileEntity(
            id = "usr_client_preview",
            name = "Agency Partner",
            email = "partner@enterprise.com",
            company = "Enterprise Ventures",
            role = "Strategic Client",
            memberSince = "2025",
            activeInquiriesCount = 1
        )
    )

    override fun streamProfile(): Flow<AgencyProfileEntity> = profileState.asStateFlow()

    override fun streamServices(): Flow<List<AgencyServiceEntity>> = servicesState.asStateFlow()

    override fun streamPortfolio(): Flow<List<PortfolioItemEntity>> = portfolioState.asStateFlow()

    override fun streamProcessSteps(): Flow<List<AgencyProcessStepEntity>> = processStepsState.asStateFlow()

    override fun streamUserProfile(): Flow<UserProfileEntity?> = userProfileState.asStateFlow()

    override suspend fun submitQuote(request: QuoteRequest): Result<Unit> {
        // Simulates saving quote request locally and prepares state for future Firebase sync
        userProfileState.update { current ->
            current?.copy(activeInquiriesCount = current.activeInquiriesCount + 1)
        }
        return Result.success(Unit)
    }

    override suspend fun submitContact(inquiry: ContactInquiry): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun refresh(): Result<Unit> = Result.success(Unit)
}
