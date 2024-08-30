package com.example.spendee.ui.budget.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.R
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.repositories.BudgetRepository
import com.example.spendee.data.repositories.ExpenseRepository
import com.example.spendee.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class AddEditBudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddEditBudgetState())
    val state: StateFlow<AddEditBudgetState> = _state.asStateFlow()

    init {
        if (savedStateHandle.get<Boolean>("isCreated") == true) {
            loadBudget()
        }
    }

    private fun loadBudget() {
        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.getBudget().collect { budget ->
                _state.value = _state.value.copy(
                    amount = budget.totalAmount.toString(),
                    startingDay = budget.startDate.dayOfMonth,
                    isExceedButtonPressed = budget.isExceedNotificationEnabled
                )
            }
        }
    }

    fun onEvent(event: AddEditBudgetEvent) {
        when (event) {
            is AddEditBudgetEvent.OnAmountChange -> updateAmount(event.amount)
            AddEditBudgetEvent.OnSaveBudgetClick -> saveBudget()
            is AddEditBudgetEvent.OnExceedButtonPress -> updateExceedButtonState(event.isPressed)
            AddEditBudgetEvent.OnCancelStartingDay -> cancelStartingDay()
            is AddEditBudgetEvent.OnChangeStartingDay -> changeStartingDay(event.newDay)
        }
    }

    private fun updateAmount(amount: String) {
        _state.value = _state.value.copy(amount = amount)
    }

    private fun saveBudget() {
        viewModelScope.launch(Dispatchers.IO) {
            val stateValue = _state.value
            val amount = stateValue.amount
            val startingDay = stateValue.startingDay

            if (amount.isBlank()) {
                sendUiEvent(UiEvent.ShowSnackbar(R.string.amount_cant_be_empty))
                return@launch
            }
            if (startingDay == null) {
                sendUiEvent(UiEvent.ShowSnackbar(R.string.please_select_a_starting_day))
                return@launch
            }

            val startDate = calculateStartDate(startingDay)
            val endDate = startDate.plusMonths(1).minusDays(1)
            val expenses = expenseRepository.getAllExpenses().first()
            val filteredExpenses = expenses.filter { it.date >= startDate }
            val expensesTotalAmount = filteredExpenses.sumOf { it.amount }
            val currentAmount = amount.toDouble() - expensesTotalAmount
            val budget = Budget(
                totalAmount = amount.toDouble(),
                leftToSpend = maxOf(currentAmount, 0.0),
                totalSpent = expensesTotalAmount,
                startDate = startDate,
                endDate = endDate,
                isExceeded = currentAmount < 0,
                isExceedNotificationEnabled = stateValue.isExceedButtonPressed
            )

            budgetRepository.upsertBudget(budget)
            sendUiEvent(UiEvent.PopBackStack)
        }
    }

    private fun updateExceedButtonState(isPressed: Boolean) {
        _state.value = _state.value.copy(isExceedButtonPressed = isPressed)
    }

    private fun cancelStartingDay() {
        _state.value = _state.value.copy(startingDay = null)
    }

    private fun changeStartingDay(newDay: Int) {
        _state.value = _state.value.copy(startingDay = newDay)
    }

    private fun calculateStartDate(startingDay: Int): LocalDate {
        val today = LocalDate.now()
        val currentYearMonth = YearMonth.of(today.year, today.month)
        val validStartingDay = minOf(startingDay, currentYearMonth.lengthOfMonth())

        return if (startingDay < today.dayOfMonth) {
            LocalDate.of(today.year, today.month, validStartingDay)
        } else {
            val previousMonth = currentYearMonth.minusMonths(1)
            LocalDate.of(previousMonth.year, previousMonth.monthValue, validStartingDay)
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}