package com.example.spendee.ui.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.ui.current_balance.screens.getExampleExpenses

@Composable
fun ExpensesColumn(modifier: Modifier = Modifier) {
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        item {
            Text(
                text = stringResource(R.string.your_expenses),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        items(getExampleExpenses()) { item ->
            ExpensesCard(expense = item)
        }
    }
}