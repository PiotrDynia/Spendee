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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.spendee.R

@Composable
fun BottomNavigationBar() {
    BottomAppBar {
        val items = listOf(
            BottomNavItem(stringResource(R.string.home), painterResource(R.drawable.ic_home)),
            BottomNavItem(stringResource(R.string.expenses), painterResource(R.drawable.ic_money)),
            BottomNavItem(stringResource(R.string.budget), painterResource(R.drawable.ic_budget)),
            BottomNavItem(stringResource(R.string.goals), painterResource(R.drawable.ic_goals))
        )
        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            items.forEach { item ->
                BottomNavigationItem(item = item)
            }
        }
    }
}

data class BottomNavItem(val label: String, val icon: Painter)

@Composable
fun BottomNavigationItem(item: BottomNavItem) {
    Column(
        modifier = Modifier
            .clickable {  },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painter = item.icon, contentDescription = item.label)
        Text(text = item.label, style = MaterialTheme.typography.labelSmall)
    }
}