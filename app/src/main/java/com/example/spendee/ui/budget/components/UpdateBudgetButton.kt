package com.example.spendee.ui.budget.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.spendee.R

@Composable
fun UpdateBudgetButton(modifier: Modifier = Modifier) {
    Button(
        onClick = { }
    ) {
        Text(text = stringResource(R.string.set_budget))
    }
}