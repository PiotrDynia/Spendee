package com.example.spendee.ui.budget.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.spendee.R
import com.example.spendee.data.entities.Budget
import com.example.spendee.ui.budget.BudgetEvent
import com.example.spendee.ui.budget.components.BudgetCircle
import com.example.spendee.ui.budget.components.BudgetInfoCard
import com.example.spendee.ui.budget.components.BudgetInfoCardType
import com.example.spendee.ui.budget.components.BudgetMapKey
import com.example.spendee.ui.budget.components.UpdateBudgetButton
import com.example.spendee.util.UiEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

@Composable
fun BudgetScreen(
    budget: Budget,
    onEvent: (BudgetEvent) -> Unit,
    uiEvent: Flow<UiEvent>,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
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
        uiEvent.collect { event ->
            when(event) {
                is UiEvent.Navigate -> onNavigate(event.route)
                else -> Unit
            }
        }
    }
    var expanded by rememberSaveable { mutableStateOf(false) }
    val totalBudget = budget.totalAmount
    val amountSpent = budget.totalSpent
    val percentageSpent = if (totalBudget != 0.0) amountSpent / totalBudget else 0.0
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(2f))
            if (budget.isExceeded) {
                Text(
                    text = stringResource(R.string.budget_exceeded) + " by ${(budget.totalSpent - budget.totalAmount)}$!",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { expanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options))
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(x = 220.dp, y = 0.dp),
                properties = PopupProperties(
                    focusable = true
                ),
                modifier = Modifier
                    .fillMaxWidth(0.4f)
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.edit),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onEvent(BudgetEvent.OnSetBudgetClick)
                    }
                )
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.delete),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        onEvent(BudgetEvent.OnDeleteBudget)
                    }
                )
            }
        }
        BudgetCircle(
            percentageSpent = percentageSpent.toFloat(),
            animatedProgress = animatedProgress,
            budget = budget
        )
        BudgetMapKey()
        Spacer(modifier = Modifier.height(12.dp))
        UpdateBudgetButton(
            onClick = { onEvent(BudgetEvent.OnSetBudgetClick) }
        )
        Column {
            BudgetInfoCard(
                text = R.string.spent,
                color = Color.Red,
                budget = budget,
                cardType = BudgetInfoCardType.SPENT
            )
            BudgetInfoCard(
                text = R.string.you_can_spend,
                color = Color(0xFF04AF70),
                budget = budget,
                cardType = BudgetInfoCardType.YOU_CAN_SPEND
            )
        }
    }
}