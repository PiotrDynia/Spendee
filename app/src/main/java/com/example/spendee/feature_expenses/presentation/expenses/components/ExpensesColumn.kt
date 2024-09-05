package com.example.spendee.feature_expenses.presentation.expenses.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.R
import com.example.spendee.core.presentation.util.DismissBackground
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.presentation.expenses.ExpensesEvent
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExpensesColumn(
    expenses: List<Expense>,
    onEvent: (ExpensesEvent) -> Unit,
    modifier: Modifier = Modifier
) {
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
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
        if (expenses.isNotEmpty()) {
            items(items = expenses, key = { it.id }) { item ->
                val dismissState = rememberSwipeToDismissBoxState(
                    positionalThreshold = { it * .25f }
                )
                LaunchedEffect(dismissState.currentValue) {
                    when (dismissState.currentValue) {
                        SwipeToDismissBoxValue.StartToEnd -> {
                            onEvent(ExpensesEvent.OnDeleteExpense(item))
                        }
                        SwipeToDismissBoxValue.EndToStart -> {
                            onEvent(ExpensesEvent.OnExpenseClick(item))
                            // Necessary for correct animation
                            delay(1000)
                            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
                        }
                        SwipeToDismissBoxValue.Settled -> {}
                    }
                }
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        DismissBackground(
                            dismissState = dismissState
                        )
                    },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .animateItemPlacement()
                ) {
                    ExpenseCard(
                        expense = item,
                        onEvent = onEvent
                    )
                }
            }
        } else {
            item {
                Text(
                    text = stringResource(R.string.no_expenses_yet),
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }
    }
}