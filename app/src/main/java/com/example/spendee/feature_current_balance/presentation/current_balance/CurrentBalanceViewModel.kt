package com.example.spendee.feature_current_balance.presentation.current_balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.core.domain.util.NotificationService
import com.example.spendee.R
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import com.example.spendee.feature_goals.domain.repository.GoalRepository
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val notificationService: NotificationService
) : ViewModel() {

    private val _viewBalanceState = MutableStateFlow(CurrentBalanceState())
    val viewState: StateFlow<CurrentBalanceState> = _viewBalanceState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

    private var goals: List<Goal> = emptyList()

    init {
        viewModelScope.launch {
            goals = goalsRepository.getAllGoals().first()
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
        }
    }

    fun onEvent(event: CurrentBalanceEvent) {
        when (event) {
            CurrentBalanceEvent.OnSetBalanceClick -> setBalanceDialogOpen()
            CurrentBalanceEvent.OnShowMoreClick -> sendUiEvent(UiEvent.Navigate(Routes.EXPENSES))
            is CurrentBalanceEvent.OnExpenseClick -> sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_EXPENSE + "?expenseId=${event.expense.id}"))
            is CurrentBalanceEvent.OnAmountChange -> updateCurrentAmount(event.amount)
            CurrentBalanceEvent.OnCancelSetBalanceClick -> resetBalanceDialog()
            CurrentBalanceEvent.OnConfirmSetBalanceClick -> confirmSetBalance()
        }
    }

    private fun setBalanceDialogOpen() {
        _viewBalanceState.value = _viewBalanceState.value.copy(isDialogOpen = true)
    }

    private fun updateCurrentAmount(amount: String) {
        _viewBalanceState.value = _viewBalanceState.value.copy(currentAmount = amount)
    }

    private fun resetBalanceDialog() {
        _viewBalanceState.value = _viewBalanceState.value.copy(
            currentAmount = _viewBalanceState.value.balance.amount.toString(),
            isDialogOpen = false
        )
    }

    private fun confirmSetBalance() {
        viewModelScope.launch {
            val currentAmountStr = _viewBalanceState.value.currentAmount
            if (currentAmountStr.isBlank()) {
                sendUiEvent(UiEvent.ShowSnackbar(R.string.amount_cant_be_empty))
                return@launch
            }

            val currentAmount = currentAmountStr.toDouble()
            balanceRepository.upsertBalance(Balance(amount = currentAmount))
            updateGoalsIfNeeded(currentAmount)

            balanceRepository.getBalance().collect { updatedBalance ->
                _viewBalanceState.value = _viewBalanceState.value.copy(
                    balance = updatedBalance,
                    isDialogOpen = false,
                    currentAmount = updatedBalance.amount.toString()
                )
            }
        }
    }

    private suspend fun updateGoalsIfNeeded(currentAmount: Double) {
        goals.forEach { goal ->
            if (currentAmount >= goal.targetAmount && !goal.isReached) {
                goal.isReached = true
                if (goal.isReachedNotificationEnabled) {
                    goal.isReachedNotificationEnabled = false
                    notificationService.showGoalReachedNotification()
                }
                goalsRepository.upsertGoal(goal)
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}