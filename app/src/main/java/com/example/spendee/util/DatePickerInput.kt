package com.example.spendee.util

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource

@Composable
fun DatePickerInput(onClick: () -> Unit, value: String, @StringRes placeholder: Int, modifier: Modifier = Modifier) {
    Box {
        TextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = stringResource(placeholder)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null
                )
            }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0f)
                .clickable(onClick = {
                    onClick()
                }),
        )
    }
}