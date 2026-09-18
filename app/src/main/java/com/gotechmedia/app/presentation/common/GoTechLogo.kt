package com.gotechmedia.app.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.TextSecondary

/**
 * Renders the official GoTech Media brand symbol using the vector asset
 * strictly matching the uploaded identity.
 */
@Composable
fun GoTechSymbol(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showGlow: Boolean = false
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (showGlow) {
            Box(
                modifier = Modifier
                    .size(size)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ElectricCyan.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_gotech_symbol),
            contentDescription = "GoTech Media Brand Symbol",
            modifier = Modifier.size(size)
        )
    }
}

/**
 * Renders the full GoTech Media official logo (Symbol + "GoTech" in Cyan + "Media" in Titanium Gray),
 * exactly as defined in the uploaded brand reference.
 */
@Composable
fun GoTechLogo(
    modifier: Modifier = Modifier,
    symbolSize: Dp = 38.dp,
    titleSize: Dp = 22.dp,
    subtitleSize: Dp = 15.dp,
    showGlow: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GoTechSymbol(
            size = symbolSize,
            showGlow = showGlow
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = "GoTech",
                color = ElectricCyan,
                fontSize = titleSize.value.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-0.5).sp,
                lineHeight = (titleSize.value * 1.05).sp
            )
            Text(
                text = "Media",
                color = TextSecondary,
                fontSize = subtitleSize.value.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp,
                lineHeight = (subtitleSize.value * 1.1).sp
            )
        }
    }
}
