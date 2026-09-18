package com.gotechmedia.app.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gotechmedia.app.ui.theme.GlassBorder
import com.gotechmedia.app.ui.theme.GlassHighlight
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.ObsidianSurfaceVariant

/**
 * Apple-inspired Minimal Glassmorphic Surface with subtle 3D depth.
 * Employs fine 1dp translucent border, subtle dual gradient, and soft shadow.
 */
@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = 8.dp,
    borderColor: Color = GlassBorder,
    highlightGlow: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            ObsidianSurfaceVariant.copy(alpha = 0.85f),
            ObsidianSurface.copy(alpha = 0.95f)
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.6f),
                spotColor = if (highlightGlow) GlassHighlight.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.4f)
            )
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = shape
            )
            .clip(shape),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(backgroundBrush)
                .padding(20.dp),
            content = content
        )
    }
}
