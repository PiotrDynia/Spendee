package com.example.spendee.ui.current_balance.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.spendee.R
import com.example.spendee.data.entities.Expense
import com.example.spendee.ui.current_balance.CurrentBalanceEvent
import com.example.spendee.ui.current_balance.CurrentBalanceState
import com.example.spendee.ui.current_balance.components.CurrentBalanceTexts
import com.example.spendee.ui.current_balance.components.LatestExpensesColumn
import com.example.spendee.util.UiEvent
import com.example.spendee.util.isValidNumberInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

@Composable
fun CurrentBalanceScreen(
    state: CurrentBalanceState,
    onEvent: (CurrentBalanceEvent) -> Unit,
    onShowMoreClick: () -> Unit,
    onNavigate: (String) -> Unit,
    uiEvent: Flow<UiEvent>,
    modifier: Modifier = Modifier
    ) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val animatedCircleColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF60DDAD),
        targetValue = Color(0xFF4285F4),
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "color"
    )
    var animationProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = animationProgress,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "animated progress"
    )

    var boxSize by remember { mutableStateOf(0.dp) }
    val animatedSize by animateDpAsState(
        targetValue = boxSize,
        animationSpec = tween(durationMillis = 2000),
        label = "animated size"
    )

    LaunchedEffect(Unit) {
        while (true) {
            animationProgress = 1f
            boxSize = 320.dp
            delay(2000)
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
            when(event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
                    .aspectRatio(1f)
                    .drawBehind {
                        val strokeWidth = 8.dp.toPx()
                        val halfStrokeWidth = strokeWidth / 2
                        val rect = Rect(
                            halfStrokeWidth,
                            halfStrokeWidth,
                            size.width - halfStrokeWidth,
                            size.height - halfStrokeWidth
                        )
                        drawArc(
                            color = animatedCircleColor,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter = false,
                            topLeft = rect.topLeft,
                            size = Size(rect.width, rect.height),
                            style = Stroke(strokeWidth, cap = StrokeCap.Round)
                        )
                    }
            ) {
                CurrentBalanceTexts(currentBalance = state.currentAmount)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onEvent(CurrentBalanceEvent.OnSetBalanceClick)
                }
            ) {
                Text(text = stringResource(R.string.set_balance))
            }
            if (state.isDialogOpen) {
                AlertDialog(
                    onDismissRequest = { onEvent(CurrentBalanceEvent.OnCancelSetBalanceClick) },
                    confirmButton = {
                        Button(onClick = {
                            onEvent(CurrentBalanceEvent.OnConfirmSetBalanceClick)
                        }) {
                            Text(text = stringResource(R.string.ok))
                        } },
                    dismissButton = {
                        Button(onClick = {
                            onEvent(CurrentBalanceEvent.OnCancelSetBalanceClick)
                        }) {
                            Text(text = stringResource(R.string.cancel))
                        } },
                    title = {
                        Text(text = stringResource(R.string.set_balance))
                    },
                    text = {
                        TextField(
                            value = state.currentAmount,
                            onValueChange = { newValue ->
                                if (isValidNumberInput(newValue)) {
                                    onEvent(CurrentBalanceEvent.OnAmountChange(newValue))
                                }},
                            label = { Text(text = stringResource(R.string.balance)) }
                        )
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(animatedSize)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                LatestExpensesColumn(
                    latestExpenses = state.latestExpenses,
                    // TODO somehow make it less confusing
                    onShowMoreClick = {
                        onShowMoreClick()
                        onEvent(CurrentBalanceEvent.OnShowMoreClick) },
                    onExpenseClick = {expense -> onEvent(CurrentBalanceEvent.OnExpenseClick(expense))},
                    modifier = modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrentBalanceScreenPreview() {
    CurrentBalanceScreen(
        state = CurrentBalanceState(),
        onEvent = {},
        onNavigate = {},
        onShowMoreClick = {},
        uiEvent = flow{}
    )
}

fun getExampleExpenses(): List<Expense> {
    return listOf(
        Expense(
            id = 0,
            amount = 30.0,
            description = "Movie",
            date = Date(),
            categoryId = 1
        ),
        Expense(
            id = 1,
            amount = 50.0,
            description = "Electricity Bill",
            date = Date(),
            categoryId = 2
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
        Expense(
            id = 2,
            amount = 15.0,
            description = "Bus Ticket",
            date = Date(),
            categoryId = 3
        ),
    )
}