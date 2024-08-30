package com.example.spendee.ui.expenses.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.spendee.R
import com.example.spendee.data.entities.Expense
import com.example.spendee.ui.expenses.components.ExpensesColumn
import com.example.spendee.ui.expenses.state.ExpensesEvent
import com.example.spendee.util.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Composable
fun ExpensesScreen(
    expenses: List<Expense>,
    onEvent: (ExpensesEvent) -> Unit,
    uiEvent: Flow<UiEvent>,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
            when(event) {
                is UiEvent.Navigate -> {
                    onNavigate(event.route)
                }
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = context.getString(event.message),
                        actionLabel = context.getString(event.action!!),
                        duration = SnackbarDuration.Long
                    )
                    if(result == SnackbarResult.ActionPerformed) {
                        onEvent(ExpensesEvent.OnUndoDelete)
                    }
                }
                else -> Unit
            }
        }
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(ExpensesEvent.OnAddExpenseClick)
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_expense),
                    tint = Color.White
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)}
    ) { _ ->
        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            ExpensesColumn(
                expenses = expenses,
                onEvent = onEvent,
                modifier = modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ExpensesScreenPreview() {
    ExpensesScreen(
        expenses = emptyList(),
        onEvent = {},
        uiEvent = flow {},
        onNavigate = {}
    )
}