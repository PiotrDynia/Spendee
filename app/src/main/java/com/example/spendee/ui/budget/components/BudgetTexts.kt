package com.example.spendee.ui.budget.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.data.entities.Budget
import com.example.spendee.ui.budget.BudgetState
import com.example.spendee.util.dateToString
import com.example.spendee.util.differenceInDays

@Composable
fun BudgetTexts(budget: Budget, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.you_can_spend),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "${budget.currentAmount}$",
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF04AF70),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "From ${budget.totalAmount}$",
            fontStyle = FontStyle.Italic,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = differenceInDays(budget.startDate, budget.endDate).toString() + " days left",
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "${dateToString(budget.startDate)} - ${dateToString(budget.endDate)}",
            fontStyle = FontStyle.Italic,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodySmall
        )
    }
}