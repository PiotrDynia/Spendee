package com.example.spendee.ui.current_balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.repositories.BalanceRepository
import com.example.spendee.data.repositories.ExpenseRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentBalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository,
    private val expensesRepository: ExpenseRepository
) : ViewModel() {
    private val _viewBalanceState = MutableStateFlow(
        CurrentBalanceState(
            balance = Balance(id = 1, amount = 0.0),
            currentAmount = 0.0,
            originalAmount = 0.0,
            isDialogOpen = false,
            latestExpenses = emptyList()
        )
    )
    val viewState = _viewBalanceState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                expensesRepository.getAllExpenses().take(3),
                balanceRepository.getBalance()
            ) { expenses, balance ->
                CurrentBalanceState(
                    balance = balance,
                    currentAmount = balance.amount,
                    originalAmount = balance.amount,
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
                    currentAmount = event.amount.toDouble()
                )
            }

            CurrentBalanceEvent.OnCancelSetBalanceClick -> {
                _viewBalanceState.value = _viewBalanceState.value.copy(
                    currentAmount = _viewBalanceState.value.originalAmount,
                    isDialogOpen = false)
            }

            CurrentBalanceEvent.OnConfirmSetBalanceClick -> {
                viewModelScope.launch {
                    balanceRepository.updateBalance(_viewBalanceState.value.currentAmount)
                    _viewBalanceState.value = _viewBalanceState.value.copy(
                        originalAmount = _viewBalanceState.value.currentAmount,
                        isDialogOpen = false)
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