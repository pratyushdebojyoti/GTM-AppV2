package com.gotechmedia.app.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.ArrowOutward
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.domain.model.AgencyService
import com.gotechmedia.app.domain.model.PortfolioItem
import com.gotechmedia.app.presentation.base.UiState
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.EmptyStateView
import com.gotechmedia.app.presentation.common.ErrorStateView
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.GoTechLogo
import com.gotechmedia.app.presentation.common.LoadingStateView
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.presentation.common.TechBadge
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToServices: () -> Unit,
    onNavigateToServiceDetails: (String) -> Unit,
    onNavigateToPortfolio: () -> Unit,
    onNavigateToPortfolioDetails: (String) -> Unit,
    onNavigateToQuote: (String) -> Unit,
    onNavigateToProcess: () -> Unit,
    onNavigateToContact: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "GoTech Media",
                subtitle = "Digital Engineering Agency",
                actions = {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.12f))
                            .clickable { onNavigateToQuote("") }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                            .testTag("home_top_quote_cta")
                    ) {
                        Text(
                            text = "Get a Quote",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("home_screen")
    ) { innerPadding ->
        when (val uiState = state) {
            is UiState.Loading -> {
                LoadingStateView(
                    message = "Loading agency data...",
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is UiState.Empty -> {
                EmptyStateView(
                    title = "No Agency Data Available",
                    description = "Agency information is currently synchronizing.",
                    modifier = Modifier.padding(innerPadding),
                    actionText = "Get a Custom Quote",
                    onActionClick = { onNavigateToQuote("") }
                )
            }
            is UiState.Error -> {
                ErrorStateView(
                    message = uiState.message,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is UiState.Success -> {
                val data = uiState.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Hero Section
                    item {
                        HomeHeroSection(
                            onGetQuote = { onNavigateToQuote("") },
                            onExploreServices = onNavigateToServices
                        )
                    }

                    // Agency Pillars & Metrics
                    item {
                        HomeMetricsSection(metrics = data.profile.metrics)
                    }

                    // Featured Practice Services
                    item {
                        HomeFeaturedServicesSection(
                            services = data.featuredServices,
                            onViewAll = onNavigateToServices,
                            onServiceClick = onNavigateToServiceDetails,
                            onQuoteClick = onNavigateToQuote
                        )
                    }

                    // Engineering Process Teaser
                    item {
                        HomeProcessTeaserSection(
                            onViewProcess = onNavigateToProcess
                        )
                    }

                    // Featured Case Studies
                    item {
                        HomePortfolioSection(
                            portfolioItems = data.featuredPortfolio,
                            onViewAll = onNavigateToPortfolio,
                            onItemClick = onNavigateToPortfolioDetails
                        )
                    }

                    // High-Impact CTA Consultation Banner
                    item {
                        HomeConsultationBanner(
                            onGetQuote = { onNavigateToQuote("") },
                            onContact = onNavigateToContact
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeroSection(
    onGetQuote: () -> Unit,
    onExploreServices: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // High-Tech Pill Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.3f), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "14 FULL-STACK PRACTICE DISCIPLINES",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Official Logo representation
            GoTechLogo(
                symbolSize = 44.dp,
                titleSize = 28.dp,
                subtitleSize = 18.dp,
                showGlow = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Engineering Digital Sovereignty & Next-Gen Software",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "We architect bespoke mobile systems, intelligent web applications, and high-velocity digital marketing engines with Apple-grade craft and zero technical debt.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Primary CTA: Get a Custom Quote
            PrimaryCtaButton(
                text = "Get a Custom Quote",
                onClick = onGetQuote,
                modifier = Modifier.fillMaxWidth(),
                testTag = "home_hero_get_quote_cta"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .clickable { onExploreServices() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explore 14 Practice Areas",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeMetricsSection(
    metrics: List<com.gotechmedia.app.domain.model.AgencyMetric>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "AGENCY CAPABILITIES",
            style = MaterialTheme.typography.labelSmall,
            color = ElectricCyan,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(metrics) { metric ->
                GlassmorphicCard(
                    modifier = Modifier.width(170.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = metric.value,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = metric.label,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = metric.description,
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 15.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeFeaturedServicesSection(
    services: List<AgencyService>,
    onViewAll: () -> Unit,
    onServiceClick: (String) -> Unit,
    onQuoteClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PRACTICE DISCIPLINES",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Core Specializations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "View All (14)",
                color = ElectricCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onViewAll() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        services.forEach { service ->
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onServiceClick(service.id) }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = service.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Outlined.ArrowOutward,
                            contentDescription = "View service details",
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = service.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TechBadge(text = service.category)

                        Text(
                            text = "Get a Custom Quote",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onQuoteClick(service.id) }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeProcessTeaserSection(
    onViewProcess: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        GlassmorphicCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewProcess() }
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "METHODOLOGY",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "The GoTech Delivery Lifecycle",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "6-stage agile framework engineered for precision: Discovery, UI/UX Systems, Production Engineering, Rigorous QA, Production Launch, and Scaled Optimization.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Inspect Full Process",
                        color = ElectricCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomePortfolioSection(
    portfolioItems: List<PortfolioItem>,
    onViewAll: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CASE STUDIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Selected Engineering Works",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "All Projects",
                color = ElectricCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onViewAll() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        portfolioItems.forEach { item ->
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onItemClick(item.id) }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TechBadge(text = item.industry)
                        Text(
                            text = item.category,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View Architecture Case Study",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.ArrowOutward,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeConsultationBanner(
    onGetQuote: () -> Unit,
    onContact: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ObsidianSurface,
                            ElectricCyan.copy(alpha = 0.08f)
                        )
                    )
                )
                .border(1.dp, GlassBorderSubtle, RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Ready to Architect Your Next System?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Consult directly with senior engineers and product strategists. Transparent scoping, custom pricing, and zero obligations.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                PrimaryCtaButton(
                    text = "Get a Custom Quote",
                    onClick = onGetQuote,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Direct Agency Hotlines & Chat:",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                com.gotechmedia.app.presentation.common.AgencyCommunicationChannels(
                    sourceScreen = "home"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Or contact our team directly",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clickable { onContact() }
                        .padding(4.dp)
                )
            }
        }
    }
}
