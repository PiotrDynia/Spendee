package com.example.spendee.ui.expenses.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendee.data.entities.Expense
import com.example.spendee.data.entities.ExpenseCategory
import com.example.spendee.ui.expenses.ExpensesEvent
import com.example.spendee.util.EditDeleteDropdownMenu
import com.example.spendee.util.dateToString

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseCard(expense: Expense, onEvent: (ExpensesEvent) -> Unit, modifier: Modifier = Modifier) {
    val haptics = LocalHapticFeedback.current
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier) {
        Card(
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onEvent(ExpensesEvent.OnExpenseClick(expense)) },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        expanded = true
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(ExpenseCategory.fromId(expense.categoryId)!!.iconResource),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .weight(100f)
                ) {
                    Text(
                        text = stringResource(ExpenseCategory.fromId(expense.categoryId)!!.name),
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic
                    )
                    Text(
                        text = "${expense.description} at ${dateToString(expense.date)}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${expense.amount}$", fontWeight = FontWeight.SemiBold)
            }
        }

        EditDeleteDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            onEditClick = {
                onEvent(ExpensesEvent.OnExpenseClick(expense))
                expanded = false
            },
            onDeleteClick = {
                onEvent(ExpensesEvent.OnDeleteExpense(expense))
                expanded = false
            })
    }
}