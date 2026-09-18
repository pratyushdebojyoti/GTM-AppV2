package com.gotechmedia.app.presentation.screens.profile

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotechmedia.app.core.auth.AuthState
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.di.LocalAppContainer
import com.gotechmedia.app.di.ViewModelFactoryProvider
import com.gotechmedia.app.domain.model.AgencyProject
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.presentation.common.TechBadge
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToQuote: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val viewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactoryProvider.provideProfileViewModelFactory(container)
    )

    val authState by viewModel.authState.collectAsState()
    val projectsState by viewModel.projectsState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ProfileEvent.SignedOut -> {
                    onSignOut()
                }
                is ProfileEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Client Profile",
                subtitle = "Authorized Enterprise Workspace",
                onBackClick = onNavigateBack,
                actions = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ObsidianSurface)
                            .clickable { onNavigateToSettings() }
                            .testTag("profile_settings_icon"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("profile_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (val state = authState) {
                is AuthState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ElectricCyan)
                        }
                    }
                }

                is AuthState.Unauthenticated -> {
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(ObsidianSurface)
                                        .border(1.dp, GlassBorderSubtle, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Login,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Text(
                                    text = "Guest Access",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Text(
                                    text = "You are currently browsing GoTech Media as a public visitor. Sign in with your client account to unlock your dedicated project repository and communication channels.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                PrimaryCtaButton(
                                    text = "Sign In to Client Portal",
                                    onClick = onSignOut, // Navigates to Login
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                is AuthState.Authenticated -> {
                    val user = state.user
                    val initials = if (user.displayName.isNotBlank()) {
                        user.displayName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
                    } else "GT"

                    // Profile Card
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(ElectricCyan.copy(alpha = 0.15f))
                                        .border(1.5.dp, ElectricCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = user.displayName.ifBlank { "Enterprise Partner" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = user.company.ifBlank { user.email },
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    TechBadge(text = "${user.role.name} ACCESS")
                                }
                            }
                        }
                    }

                    // Account Metadata Details
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Account Credentials",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Authorized Email", color = TextSecondary, fontSize = 13.sp)
                                    Text(user.email, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Assigned Role", color = TextSecondary, fontSize = 13.sp)
                                    Text(user.role.name, color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Workspace ID", color = TextSecondary, fontSize = 13.sp)
                                    Text(user.id.take(10) + "...", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Active Projects in Firestore
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Active Project Engagements",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )

                            when (val projects = projectsState) {
                                is Resource.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = ElectricCyan)
                                }
                                is Resource.Error -> {
                                    Text("No active projects found: ${projects.message}", color = TextSecondary, fontSize = 13.sp)
                                }
                                is Resource.Success -> {
                                    if (projects.data.isEmpty()) {
                                        Text("No active projects currently provisioned.", color = TextSecondary, fontSize = 13.sp)
                                    } else {
                                        projects.data.forEach { project ->
                                            ProjectEngagementCard(project = project)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Sign Out Action
                    item {
                        OutlinedButton(
                            onClick = {
                                viewModel.signOut()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("profile_sign_out_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.dp,
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = ObsidianSurface,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign Out of Client Portal", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                is AuthState.Error -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                .padding(16.dp)
                        ) {
                            Text(text = "Authentication Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectEngagementCard(project: AgencyProject) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TechBadge(text = project.status)
            }

            Text(
                text = project.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            // Progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Sprint Progress", fontSize = 12.sp, color = TextMuted)
                    Text("${project.progressPercentage}%", fontSize = 12.sp, color = ElectricCyan, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ObsidianSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(project.progressPercentage / 100f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ElectricCyan)
                    )
                }
            }
        }
    }
}
