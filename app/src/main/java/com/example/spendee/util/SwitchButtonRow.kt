package com.example.spendee.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun SwitchButtonRow(@StringRes text: Int, modifier: Modifier = Modifier) {
    Row (
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(text),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = true,
            onCheckedChange = {}
        )
    }
}