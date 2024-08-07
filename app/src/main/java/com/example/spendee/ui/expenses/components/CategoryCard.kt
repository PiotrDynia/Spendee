package com.example.spendee.ui.expenses.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun CategoryCard(
    @DrawableRes drawable: Int,
    @StringRes text: Int,
    categoryId: Int,
    modifier: Modifier = Modifier
) {
    // TODO pass state from viewModel
    val backgroundColor = if (true) MaterialTheme.colorScheme.primary else Color.Transparent

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.clickable {  }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(255.dp)
        ) {
            Icon(
                painter = painterResource(drawable),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = stringResource(text)
            )
        }
    }
}