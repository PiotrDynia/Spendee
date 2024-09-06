package com.example.spendee.feature_budget.presentation.budget

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.spendee.R
import com.example.spendee.core.presentation.util.LoadingScreen
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.feature_budget.domain.util.BudgetInfoCardType
import com.example.spendee.feature_budget.presentation.budget.components.BudgetCircle
import com.example.spendee.feature_budget.presentation.budget.components.BudgetInfoCard
import com.example.spendee.feature_budget.presentation.budget.components.BudgetMapKey
import com.example.spendee.feature_budget.presentation.budget.components.TopBudgetRow
import com.example.spendee.feature_budget.presentation.budget.components.UpdateBudgetButton
import kotlinx.coroutines.delay

@Composable
fun BudgetScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "animated progress"
    )

    LaunchedEffect(Unit) {
        animationProgress = 1f
        delay(2000)
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when(event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                else -> Unit
            }
        }
    }
    val budget = viewModel.budget.collectAsStateWithLifecycle().value
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value

    when {
        isLoading -> LoadingScreen()
        budget == null -> NoBudgetScreen(
            onEvent = viewModel::onEvent,
            onNavigate = { onNavigate(it) },
            uiEvent = viewModel.uiEvent
        )

        else -> {
            val totalBudget = budget.totalAmount
            val amountSpent = budget.totalSpent
            val percentageSpent = if (totalBudget != 0.0) amountSpent / totalBudget else 0.0
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                TopBudgetRow(budget = budget, onEvent = viewModel::onEvent)
                BudgetCircle(
                    percentageSpent = percentageSpent.toFloat(),
                    animatedProgress = animatedProgress,
                    budget = budget
                )
                BudgetMapKey()
                Spacer(modifier = Modifier.height(4.dp))
                UpdateBudgetButton(
                    onClick = { viewModel.onEvent(BudgetEvent.OnSetBudgetClick) }
                )
                Column {
                    BudgetInfoCard(
                        text = R.string.spent,
                        color = Color.Red,
                        budget = budget,
                        cardType = BudgetInfoCardType.SpentCard
                    )
                    BudgetInfoCard(
                        text = R.string.you_can_spend,
                        color = Color(0xFF04AF70),
                        budget = budget,
                        cardType = BudgetInfoCardType.YouCanSpendCard
                    )
                }
            }
        }
    }
}