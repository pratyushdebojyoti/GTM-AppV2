package com.gotechmedia.app.presentation.screens.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gotechmedia.app.core.auth.GoogleSignInHelper
import com.gotechmedia.app.di.LocalAppContainer
import com.gotechmedia.app.di.ViewModelFactoryProvider
import com.gotechmedia.app.presentation.common.AgencyTopBar
import com.gotechmedia.app.presentation.common.GlassmorphicCard
import com.gotechmedia.app.presentation.common.GoTechLogo
import com.gotechmedia.app.presentation.common.PrimaryCtaButton
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = LocalAppContainer.current
    val viewModel: LoginViewModel = viewModel(
        factory = ViewModelFactoryProvider.provideLoginViewModelFactory(container)
    )

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val googleSignInHelper = remember { GoogleSignInHelper(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> {
                    container.analyticsHelper.logLogin(method = "email_password")
                    onLoginSuccess()
                }
                is LoginEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AgencyTopBar(
                title = "Client Portal",
                subtitle = "Authorized Enterprise Access",
                onBackClick = onNavigateBack
            )
        },
        containerColor = ObsidianCanvas,
        modifier = modifier.testTag("login_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    GoTechLogo(
                        symbolSize = 44.dp,
                        titleSize = 28.dp,
                        subtitleSize = 18.dp,
                        showGlow = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Sign in with your enterprise credentials to access project roadmaps, code artifacts, and milestone approvals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            if (uiState.errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                            .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                viewModel.clearError()
                            },
                            label = { Text("Work Email") },
                            placeholder = { Text("partner@enterprise.com", color = TextMuted) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorderSubtle,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input")
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                viewModel.clearError()
                            },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ObsidianSurface,
                                unfocusedContainerColor = ObsidianSurface,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = GlassBorderSubtle,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = ElectricCyan,
                                        checkmarkColor = ObsidianCanvas,
                                        uncheckedColor = TextMuted
                                    )
                                )
                                Text(
                                    text = "Remember me",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }

                            Text(
                                text = "Forgot password?",
                                fontSize = 12.sp,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.sendPasswordReset(email)
                                    }
                                    .padding(4.dp)
                                    .testTag("login_forgot_password")
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        PrimaryCtaButton(
                            text = "Sign In to Client Portal",
                            onClick = {
                                viewModel.signInWithEmail(email, password)
                            },
                            isLoading = uiState.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "login_submit_button"
                        )

                        // Google Sign-In Button
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    val result = googleSignInHelper.launchGoogleSignIn()
                                    result.onSuccess { idToken ->
                                        viewModel.signInWithGoogle(idToken)
                                    }.onFailure { ex ->
                                        Toast.makeText(context, ex.message ?: "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_google_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(GlassBorderSubtle)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = ObsidianSurface,
                                contentColor = Color.White
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = ElectricCyan, strokeWidth = 2.dp)
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("G  ", fontWeight = FontWeight.Bold, color = ElectricCyan, fontSize = 16.sp)
                                    Text("Continue with Google", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New enterprise partner?",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Create an Account",
                        fontSize = 13.sp,
                        color = ElectricCyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { onNavigateToRegister() }
                            .padding(4.dp)
                            .testTag("login_goto_register")
                    )
                }
            }
        }
    }
}
