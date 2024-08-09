package com.example.spendee.ui.budget.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spendee.R
import com.example.spendee.ui.budget.AddEditBudgetEvent
import com.example.spendee.ui.budget.AddEditBudgetState
import com.example.spendee.util.DatePickerInput
import com.example.spendee.util.SwitchButtonRow
import com.example.spendee.util.UiEvent
import com.example.spendee.util.isValidNumberInput
import com.example.spendee.util.millisToString
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBudgetScreen(
    onEvent: (AddEditBudgetEvent) -> Unit,
    state: AddEditBudgetState,
    uiEvent: Flow<UiEvent>,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val startDatePickerState = rememberDatePickerState()
    val endDatePickerState = rememberDatePickerState()
    val snackbarState = remember { SnackbarHostState() }
    val startDate = startDatePickerState.selectedDateMillis?.let {
        millisToString(it)
    } ?: ""
    val endDate = endDatePickerState.selectedDateMillis?.let {
        millisToString(it)
    } ?: ""
    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.Navigate -> onNavigate(event.route)
                is UiEvent.ShowSnackbar -> snackbarState.showSnackbar(event.message)
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        snackbarHost = { SnackbarHost(snackbarState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(AddEditBudgetEvent.OnSaveBudgetClick)
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.save)
                )
            }
        }
    ) { _ ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.your_budget),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            TextField(
                value = state.amount,
                onValueChange = { amount ->
                    if(isValidNumberInput(amount)) {
                        onEvent(AddEditBudgetEvent.OnAmountChange(amount))
                    }
                },
                placeholder = { Text(stringResource(R.string.enter_budget_amount)) },
                modifier = Modifier.fillMaxWidth()
            )
            DatePickerInput(
                placeholder = R.string.select_a_start_date,
                value = state.startDate,
                onClick = { onEvent(AddEditBudgetEvent.OnOpenStartDatePicker) }
            )
            DatePickerInput(
                placeholder = R.string.select_an_end_date,
                value = state.endDate,
                onClick = { onEvent(AddEditBudgetEvent.OnOpenEndDatePicker) }
            )
            if (state.isStartDatePickerOpened) {
                DatePickerDialog(
                    onDismissRequest = { onEvent(AddEditBudgetEvent.OnCloseStartDatePicker) },
                    confirmButton = {
                        Button(onClick = {
                            onEvent(AddEditBudgetEvent.OnStartDateChange(startDate))
                        }) {
                            Text(text = stringResource(R.string.ok))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { onEvent(AddEditBudgetEvent.OnCloseStartDatePicker) }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    },
                ) {
                    DatePicker(state = startDatePickerState)
                }
            }
            if (state.isEndDatePickerOpened) {
                DatePickerDialog(
                    onDismissRequest = { onEvent(AddEditBudgetEvent.OnCloseEndDatePicker) },
                    confirmButton = {
                        Button(onClick = { onEvent(AddEditBudgetEvent.OnEndDateChange(endDate)) }) {
                            Text(text = stringResource(R.string.ok))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { onEvent(AddEditBudgetEvent.OnCloseEndDatePicker) }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    },
                ) {
                    DatePicker(state = endDatePickerState)
                }
            }
            SwitchButtonRow(
                text = R.string.notify_me_when_i_exceed_my_budget,
                switchState = state.isExceedButtonPressed,
                onCheckedChange = { pressed -> onEvent(AddEditBudgetEvent.OnExceedButtonPress(pressed)) }
            )
            SwitchButtonRow(
                text = R.string.notify_me_when_i_reach_80_of_my_budget,
                switchState = state.isReach80PercentButtonPressed,
                onCheckedChange = { pressed ->
                    onEvent(AddEditBudgetEvent.OnReach80PercentButtonPress(pressed))
                }
            )
        }
    }
}