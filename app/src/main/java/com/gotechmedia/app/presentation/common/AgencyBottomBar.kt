package com.gotechmedia.app.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotechmedia.app.navigation.Screen
import com.gotechmedia.app.ui.theme.ElectricCyan
import com.gotechmedia.app.ui.theme.GlassBorderSubtle
import com.gotechmedia.app.ui.theme.ObsidianCanvas
import com.gotechmedia.app.ui.theme.ObsidianSurface
import com.gotechmedia.app.ui.theme.TextMuted
import com.gotechmedia.app.ui.theme.TextSecondary

data class BottomNavItemData(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun AgencyBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItemData(Screen.Home, "Home", Icons.Outlined.Home, "bottom_nav_home"),
        BottomNavItemData(Screen.Services, "Services", Icons.Outlined.Widgets, "bottom_nav_services"),
        BottomNavItemData(Screen.Portfolio, "Portfolio", Icons.Outlined.WorkOutline, "bottom_nav_portfolio"),
        BottomNavItemData(Screen.Contact, "Contact", Icons.Outlined.MailOutline, "bottom_nav_contact"),
        BottomNavItemData(Screen.More, "More", Icons.Outlined.MoreHoriz, "bottom_nav_more")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianCanvas.copy(alpha = 0.95f))
            .border(
                1.dp,
                GlassBorderSubtle,
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("agency_bottom_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) ElectricCyan else TextMuted,
                    animationSpec = tween(200),
                    label = "bottom_nav_tint"
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(item.screen) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag(item.testTag),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ElectricCyan.copy(alpha = 0.12f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconTint,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) ElectricCyan else TextSecondary
                    )
                }
            }
        }
    }
}
