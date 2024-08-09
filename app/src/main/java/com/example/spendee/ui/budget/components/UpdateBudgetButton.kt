package com.example.spendee.ui.budget.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.spendee.R

@Composable
fun UpdateBudgetButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = { onClick() }
    ) {
        Text(text = stringResource(R.string.update_budget))
    }
}