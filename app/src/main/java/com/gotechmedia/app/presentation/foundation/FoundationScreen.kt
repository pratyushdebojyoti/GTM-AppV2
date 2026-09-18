package com.gotechmedia.app.presentation.foundation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gotechmedia.app.core.config.AppConfig
import com.gotechmedia.app.core.utils.Constants
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.TechBadge
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.EmeraldSuccess
import com.gotechmedia.app.ui.theme.GlassBorder
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.SoftIndigo
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextPrimary
import com.gotechmedia.app.ui.theme.TextSecondary

/**
 * Foundation Architecture Screen.
 * Demonstrates the production foundation, clean architecture layers, and Apple-inspired design language.
 */
@Composable
fun FoundationScreen(
    viewModel: FoundationViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag(Constants.TAG_ROOT_SCAFFOLD),
        containerColor = ObsidianCanvas,
        topBar = {
            AgencyTopBar(
                title = AppConfig.APP_NAME,
                subtitle = "Architecture & System Foundation"
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading && uiState.agencyProfile == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = ElectricCyan,
                    modifier = Modifier.semantics { contentDescription = "Loading foundation architecture" }
                )
            }
        } else {
            FoundationContent(
                uiState = uiState,
                paddingValues = innerPadding
            )
        }
    }
}

@Composable
private fun FoundationContent(
    uiState: FoundationUiState,
    paddingValues: PaddingValues
) {
    var expandedIndex by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .navigationBarsPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Hero / Agency Overview Card
        item {
            HeroAgencyCard(profile = uiState.agencyProfile)
        }

        // 2. Foundation Status & Metrics Grid
        item {
            ArchitectureStatusCard()
        }

        // 3. Section Header: Architecture Layers
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ARCHITECTURAL LAYERS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
                TechBadge(
                    text = "7 Modules Verified",
                    accentColor = EmeraldSuccess
                )
            }
        }

        // 4. Layer inspection cards
        itemsIndexed(uiState.architectureLayers) { index, layer ->
            val isExpanded = expandedIndex == index
            LayerItemCard(
                layer = layer,
                isExpanded = isExpanded,
                onClick = {
                    expandedIndex = if (isExpanded) -1 else index
                }
            )
        }

        // 5. Firebase & Next Step Readiness Card
        item {
            FirebaseRoadmapCard()
        }

        // Bottom breathing space
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HeroAgencyCard(profile: com.gotechmedia.app.domain.model.AgencyProfile?) {
    GlassmorphicCard(
        highlightGlow = true,
        borderColor = ElectricCyan.copy(alpha = 0.35f)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TechBadge(
                    text = "Digital Agency",
                    accentColor = ElectricCyan
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Foundation Ready",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = profile?.name ?: AppConfig.APP_NAME,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = profile?.headline ?: AppConfig.AGENCY_TAGLINE,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profile?.specializations?.take(3)?.forEach { tag ->
                    TechBadge(
                        text = tag,
                        accentColor = SoftIndigo,
                        backgroundColor = SoftIndigo.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchitectureStatusCard() {
    GlassmorphicCard {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ElectricCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security & Architecture",
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Production Baseline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Clean Architecture • MVVM • StateFlow • Compose",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ArchitectureMetricPill(label = "Package", value = "com.gotechmedia.app")
                ArchitectureMetricPill(label = "Theme", value = "Dark Mode Only")
                ArchitectureMetricPill(label = "DI Core", value = "AppContainer")
            }
        }
    }
}

@Composable
private fun ArchitectureMetricPill(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ObsidianSurface.copy(alpha = 0.7f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
private fun LayerItemCard(
    layer: ArchitectureLayerStatus,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "ArrowRotation"
    )

    GlassmorphicCard(
        elevation = if (isExpanded) 10.dp else 4.dp,
        borderColor = if (isExpanded) ElectricCyan.copy(alpha = 0.4f) else GlassBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Layer verified",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = layer.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse layer" else "Expand layer",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(rotation)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = layer.packageName,
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = layer.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun FirebaseRoadmapCard() {
    GlassmorphicCard(
        borderColor = SoftIndigo.copy(alpha = 0.3f)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SoftIndigo.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = "Roadmap",
                        tint = SoftIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Next Phase: Cloud & Firebase Integration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "The foundation cleanly abstracts all data operations via AgencyDataSource and AgencyRepository. Firebase Firestore and Auth can be attached with zero refactoring of the presentation or domain layers.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
