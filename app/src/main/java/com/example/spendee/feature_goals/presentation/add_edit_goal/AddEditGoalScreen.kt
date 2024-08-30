package com.example.spendee.feature_goals.presentation.add_edit_goal

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spendee.R
import com.example.spendee.core.presentation.util.DatePickerInput
import com.example.spendee.core.presentation.util.SwitchButtonRow
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.core.domain.util.isValidNumberInput
import com.example.spendee.core.domain.util.millisToString
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalScreen(
    onEvent: (AddEditGoalEvent) -> Unit,
    state: AddEditGoalState,
    uiEvent: Flow<UiEvent>,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deadlineDateState = rememberDatePickerState()
    val deadlineDate = deadlineDateState.selectedDateMillis?.let {
        millisToString(it)
    } ?: ""
    val snackbarState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.Navigate -> onNavigate(event.route)
                is UiEvent.ShowSnackbar -> snackbarState.showSnackbar(context.getString(event.message))
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        snackbarHost = { SnackbarHost(snackbarState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(AddEditGoalEvent.OnSaveGoalClick)
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.save),
                    tint = MaterialTheme.colorScheme.onSecondary
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
                text = stringResource(R.string.your_goal),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            TextField(
                value = state.description,
                onValueChange = {
                    onEvent(AddEditGoalEvent.OnDescriptionChange(it))
                },
                placeholder = {
                    Text(text = stringResource(R.string.description))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 2,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
            TextField(
                value = state.targetAmount,
                onValueChange = { amount ->
                    if (isValidNumberInput(amount)) {
                        onEvent(AddEditGoalEvent.OnAmountChange(amount))
                    }
                },
                placeholder = { Text(text = stringResource(R.string.target_amount)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
            DatePickerInput(
                placeholder = R.string.set_a_deadline,
                value = state.deadline,
                onClick = { onEvent(AddEditGoalEvent.OnOpenDeadlineDatePicker) }
            )
            if (state.isDeadlineDatePickerOpened) {
                DatePickerDialog(
                    onDismissRequest = { onEvent(AddEditGoalEvent.OnCloseDeadlineDatePicker) },
                    confirmButton = {
                        Button(onClick = {
                            onEvent(AddEditGoalEvent.OnDeadlineChange(deadlineDate))
                        }) {
                            Text(text = stringResource(R.string.ok))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { onEvent(AddEditGoalEvent.OnCloseDeadlineDatePicker) }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    },
                ) {
                    DatePicker(state = deadlineDateState)
                }
            }
            SwitchButtonRow(
                text = R.string.notify_me_when_i_reach_my_goal,
                onCheckedChange = { pressed ->
                    onEvent(AddEditGoalEvent.OnReachedButtonPress(pressed))
                },
                switchState = state.isReachedButtonPressed
            )
        }
    }
}