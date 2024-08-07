package com.example.spendee.ui.budget.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.spendee.R
import com.example.spendee.ui.budget.components.BudgetCircle
import com.example.spendee.ui.budget.components.BudgetInfoCard
import com.example.spendee.ui.budget.components.BudgetMapKey
import com.example.spendee.ui.budget.components.UpdateBudgetButton
import kotlinx.coroutines.delay

@Composable
fun BudgetScreen(modifier: Modifier = Modifier) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "animated progress"
    )

    LaunchedEffect(Unit) {
        while (true) {
            animationProgress = 1f
            delay(2000)
        }
    }
    val totalBudget = 10000.0
    val amountSpent = 10000.0 - 2137.69
    val percentageSpent = amountSpent / totalBudget
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
    ) {
        BudgetCircle(
            percentageSpent = percentageSpent.toFloat(),
            animatedProgress = animatedProgress
        )
        BudgetMapKey()
        Spacer(modifier = Modifier.height(12.dp))
        UpdateBudgetButton()
        Column {
            BudgetInfoCard(text = R.string.spent, color = Color.Red)
            BudgetInfoCard(text = R.string.you_can_spend, color = Color(0xFF04AF70))
        }
    }
}

@Preview
@Composable
private fun BudgetScreenPreview() {
    BudgetScreen()
}