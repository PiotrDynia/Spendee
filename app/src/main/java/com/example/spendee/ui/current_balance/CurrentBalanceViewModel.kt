package com.example.spendee.ui.current_balance

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.R
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Goal
import com.example.spendee.data.repositories.BalanceRepository
import com.example.spendee.data.repositories.ExpenseRepository
import com.example.spendee.data.repositories.GoalRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentBalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository,
    private val expensesRepository: ExpenseRepository,
    private val goalsRepository: GoalRepository,
    private val notificationBuilder: NotificationCompat.Builder,
    private val notificationManager: NotificationManagerCompat
) : ViewModel() {
    private val _viewBalanceState = MutableStateFlow(
        CurrentBalanceState()
    )
    val viewState = _viewBalanceState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var goals: List<Goal> = emptyList()

    init {
        viewModelScope.launch {
            combine(
                expensesRepository.getAllExpenses().take(3),
                balanceRepository.getBalance()
            ) { expenses, balance ->
                CurrentBalanceState(
                    balance = balance,
                    currentAmount = balance.amount.toString(),
                    isDialogOpen = _viewBalanceState.value.isDialogOpen,
                    latestExpenses = expenses
                )
            }.collect { newState ->
                _viewBalanceState.value = newState
            }
            goals = goalsRepository.getAllGoals().first()
        }
    }

    fun onEvent(event: CurrentBalanceEvent) {
        when (event) {
            CurrentBalanceEvent.OnSetBalanceClick -> {
                _viewBalanceState.value = _viewBalanceState.value.copy(isDialogOpen = true)
            }

            CurrentBalanceEvent.OnShowMoreClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.EXPENSES))
            }

            is CurrentBalanceEvent.OnExpenseClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_EXPENSE + "?expenseId=${event.expense.id}"))
            }

            is CurrentBalanceEvent.OnAmountChange -> {
                _viewBalanceState.value = _viewBalanceState.value.copy(
                    currentAmount = event.amount
                )
            }

            CurrentBalanceEvent.OnCancelSetBalanceClick -> {
                _viewBalanceState.value = _viewBalanceState.value.copy(
                    currentAmount = _viewBalanceState.value.balance.amount.toString(),
                    isDialogOpen = false)
            }

            CurrentBalanceEvent.OnConfirmSetBalanceClick -> {
                viewModelScope.launch {
                    if (_viewBalanceState.value.currentAmount.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = R.string.amount_cant_be_empty
                        ))
                        return@launch
                    }
                    balanceRepository.upsertBalance(
                        Balance(
                            amount = _viewBalanceState.value.currentAmount.toDouble()
                        )
                    )
                    if (notificationManager.areNotificationsEnabled()) {
                        goals.forEach { goal ->
                            if (_viewBalanceState.value.currentAmount.toDouble() >= goal.targetAmount) {
                                goal.isReached = true
                                goalsRepository.upsertGoal(goal)
                                notificationManager.notify(1, notificationBuilder
                                    .setContentTitle("You've reached your goal!")
                                    .build())
                            }
                        }
                    }
                    balanceRepository.getBalance().collect { updatedBalance ->
                        _viewBalanceState.value = _viewBalanceState.value.copy(
                            balance = updatedBalance,
                            isDialogOpen = false,
                            currentAmount = updatedBalance.amount.toString()
                        )
                    }
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}