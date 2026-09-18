package com.gotechmedia.app.presentation.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.ElectricCyanDark
import com.gotechmedia.app.ui.theme.ObsidianCanvas

/**
 * Apple-inspired primary CTA button with tactile micro-interaction,
 * subtle cyan gradient, 3D border highlight, and accessibility minimum touch sizing.
 */
@Composable
fun PrimaryCtaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = Icons.AutoMirrored.Filled.ArrowForward,
    testTag: String = "primary_cta_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        label = "button_scale"
    )

    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = shape,
                spotColor = ElectricCyan.copy(alpha = 0.35f),
                ambientColor = ElectricCyan.copy(alpha = 0.15f)
            )
            .clip(shape)
            .background(
                brush = if (enabled) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            ElectricCyan,
                            ElectricCyanDark
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                }
            )
            .border(
                width = 1.dp,
                color = if (enabled) Color.White.copy(alpha = 0.35f) else Color.Transparent,
                shape = shape
            )
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .defaultMinSize(minHeight = 52.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = ObsidianCanvas,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    color = if (enabled) ObsidianCanvas else Color.White.copy(alpha = 0.4f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp
                )

                if (icon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) ObsidianCanvas else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
