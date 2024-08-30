package com.example.spendee.ui.budget.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.chargemap.compose.numberpicker.NumberPicker
import com.example.spendee.R
import com.example.spendee.ui.budget.state.AddEditBudgetEvent
import com.example.spendee.ui.budget.state.AddEditBudgetState
import com.example.spendee.util.DatePickerInput
import com.example.spendee.util.SwitchButtonRow
import com.example.spendee.util.UiEvent
import com.example.spendee.util.isValidNumberInput
import kotlinx.coroutines.flow.Flow

@Composable
fun AddEditBudgetScreen(
    onEvent: (AddEditBudgetEvent) -> Unit,
    state: AddEditBudgetState,
    uiEvent: Flow<UiEvent>,
    onNavigate: (String) -> Unit,
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPickerOpened by remember {
        mutableStateOf(false)
    }
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
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        snackbarHost = { SnackbarHost(snackbarState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(AddEditBudgetEvent.OnSaveBudgetClick)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.save)
                )
            }
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.your_budget),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = state.amount,
                onValueChange = { amount ->
                    if (isValidNumberInput(amount)) {
                        onEvent(AddEditBudgetEvent.OnAmountChange(amount))
                    }
                },
                placeholder = { Text(stringResource(R.string.enter_budget_amount)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    focusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            DatePickerInput(
                placeholder = R.string.select_a_starting_day_of_the_month,
                value = state.startingDay?.toString() ?: "",
                onClick = { isPickerOpened = true },
                modifier = Modifier.fillMaxWidth()
            )
            if (isPickerOpened) {
                Dialog(onDismissRequest = {
                    isPickerOpened = false
                    onEvent(AddEditBudgetEvent.OnCancelStartingDay)
                }) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.select_a_starting_day_of_the_month),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            NumberPicker(
                                value = state.startingDay ?: 1,
                                onValueChange = {
                                    onEvent(AddEditBudgetEvent.OnChangeStartingDay(it))
                                },
                                range = 1..31,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.size(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        isPickerOpened = false
                                        onEvent(AddEditBudgetEvent.OnCancelStartingDay)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    )
                                ) {
                                    Text(text = stringResource(R.string.cancel))
                                }
                                Button(
                                    onClick = {
                                        isPickerOpened = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(text = stringResource(R.string.ok))
                                }
                            }
                        }
                    }
                }
            }
            SwitchButtonRow(
                text = R.string.notify_me_when_i_exceed_my_budget,
                switchState = state.isExceedButtonPressed,
                onCheckedChange = { pressed ->
                    onEvent(
                        AddEditBudgetEvent.OnExceedButtonPress(
                            pressed
                        )
                    )
                }
            )
        }
    }
}