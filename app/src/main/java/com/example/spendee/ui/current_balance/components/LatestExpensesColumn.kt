package com.example.spendee.ui.current_balance.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.ui.current_balance.screens.getExampleExpenses

@Composable
fun LatestExpensesColumn(modifier: Modifier = Modifier) {
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        item {
            Text(
                text = stringResource(R.string.latest_expenses),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        items(getExampleExpenses().take(3)) { item ->
            LatestExpensesCard(expense = item)
        }
        item {
            Text(
                text = stringResource(R.string.show_more),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { }
            )
        }
    }
}