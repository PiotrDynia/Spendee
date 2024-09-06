package com.example.spendee.feature_budget.presentation.budget.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.core.domain.util.calculateDailySpending
import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.util.BudgetInfoCardType

@Composable
fun BudgetInfoCard(
    @StringRes text: Int,
    color: Color,
    budget: Budget,
    cardType: BudgetInfoCardType,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(text),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp, 12.dp)
                        .background(color, RectangleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when(cardType) {
                        BudgetInfoCardType.SpentCard -> "${budget.totalSpent}$"
                        BudgetInfoCardType.YouCanSpendCard -> "${budget.leftToSpend}$"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = when(cardType) {
                        BudgetInfoCardType.SpentCard -> stringResource(R.string.from, budget.totalAmount)
                        BudgetInfoCardType.YouCanSpendCard -> stringResource(
                            R.string.day,
                            calculateDailySpending(budget.endDate, budget.leftToSpend)
                        )
                    },
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}