package com.example.spendee.feature_current_balance.presentation.current_balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.feature_current_balance.domain.model.InvalidBalanceException
import com.example.spendee.feature_current_balance.domain.use_case.BalanceUseCases
import com.example.spendee.feature_expenses.domain.use_case.ExpensesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CurrentBalanceViewModel @Inject constructor(
    private val expensesUseCases: ExpensesUseCases,
    private val balanceUseCases: BalanceUseCases
) : ViewModel() {

    private val _viewBalanceState = MutableStateFlow(CurrentBalanceState())
    val viewState: StateFlow<CurrentBalanceState> = _viewBalanceState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                expensesUseCases.getExpenses().take(3),
                balanceUseCases.getBalance()
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
            try {
                withContext(Dispatchers.IO) {
                    balanceUseCases.updateBalance(currentAmount = _viewBalanceState.value.currentAmount)
                }

                balanceUseCases.getBalance().firstOrNull()?.let { updatedBalance ->
                    _viewBalanceState.value = _viewBalanceState.value.copy(
                        balance = updatedBalance,
                        isDialogOpen = false,
                        currentAmount = updatedBalance.amount.toString()
                    )
                }
            } catch (e: InvalidBalanceException) {
                sendUiEvent(UiEvent.ShowSnackbar(message = e.messageResId))
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}