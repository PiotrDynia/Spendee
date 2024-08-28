package com.example.spendee.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    selectedItem: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    item = item,
                    isSelected = selectedItem == item.route,
                    onNavigate = {
                        onNavigate(it)
                    }
                )
            }
        }
    }
}

data class BottomNavItem(val label: String, val icon: Painter, val route: String)

@Composable
fun BottomNavigationItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    val iconTint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
    Column(
        modifier = Modifier
            .clickable { onNavigate(item.route) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painter = item.icon, contentDescription = item.label, tint = iconTint)
        Text(text = item.label, color = textColor, style = MaterialTheme.typography.labelSmall)
    }
}