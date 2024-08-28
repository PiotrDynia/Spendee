package com.example.spendee.ui.budget.components

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
import androidx.compose.material3.ElevatedCard
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
import com.example.spendee.data.entities.Budget
import com.example.spendee.util.calculateDailySpending

enum class BudgetInfoCardType {
    SPENT, YOU_CAN_SPEND
}

@Composable
fun BudgetInfoCard(
    @StringRes text: Int,
    color: Color,
    budget: Budget,
    cardType: BudgetInfoCardType,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = { },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(text),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp, 16.dp)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (cardType == BudgetInfoCardType.SPENT) {
                        ((budget.totalAmount) - (budget.totalSpent)).toString() + "$"
                    } else budget.leftToSpend.toString() + "$",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.weight(2f))
                Text(
                    text = if (cardType == BudgetInfoCardType.SPENT) {
                        "From ${budget.totalAmount}$"
                    } else {
                        calculateDailySpending(
                            budget.endDate,
                            budget.leftToSpend
                        ).toString() + "$/day"
                    },
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}