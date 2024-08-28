package com.example.spendee.ui.budget

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class AddEditBudgetViewModel @Inject constructor(
    private val repository: BudgetRepository,
    private val expensesRepository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    private val _state = MutableStateFlow(AddEditBudgetState())
    val state = _state.asStateFlow()

    init {
        val isCreated = savedStateHandle.get<Boolean>("isCreated")!!
        if (isCreated) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.getBudget().collect { budget ->
                    _state.value = _state.value.copy(
                        amount = budget.totalAmount.toString(),
                        startingDay = budget.startDate.dayOfMonth,
                        isExceedButtonPressed = budget.isExceedNotificationEnabled,
                    )
                }
            }
        }
    }

    fun onEvent(event: AddEditBudgetEvent) {
        when(event) {
            is AddEditBudgetEvent.OnAmountChange -> {
                _state.value = _state.value.copy(
                    amount = event.amount
                )
            }
            AddEditBudgetEvent.OnSaveBudgetClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (_state.value.amount.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(R.string.amount_cant_be_empty))
                        return@launch
                    }
                    if (_state.value.startingDay == null) {
                        sendUiEvent(UiEvent.ShowSnackbar(R.string.please_select_a_starting_day))
                        return@launch
                    }
                    val startDate = calculateStartDate(_state.value.startingDay!!)
                    val endDate = startDate.plusMonths(1).minusDays(1)
                    val filteredExpenses = expensesRepository.getAllExpenses().first().filter { expense ->
                        expense.date >= startDate
                    }
                    val expensesTotalAmount = filteredExpenses.sumOf { it.amount }
                    val currentAmount = _state.value.amount.toDouble() - expensesTotalAmount
                    repository.upsertBudget(
                        Budget(
                            totalAmount = _state.value.amount.toDouble(),
                            leftToSpend = if (currentAmount < 0) 0.0 else currentAmount,
                            totalSpent = expensesTotalAmount,
                            startDate = startDate,
                            endDate = endDate,
                            isExceeded = if (currentAmount > 0) false else true,
                            isExceedNotificationEnabled = _state.value.isExceedButtonPressed
                        )
                    )
                    sendUiEvent(UiEvent.PopBackStack)
                }
            }
            is AddEditBudgetEvent.OnExceedButtonPress -> {
                _state.value = _state.value.copy(
                    isExceedButtonPressed = event.isPressed
                )
            }
            AddEditBudgetEvent.OnCancelStartingDay -> {
                _state.value = _state.value.copy(
                    startingDay = null
                )
            }
            is AddEditBudgetEvent.OnChangeStartingDay -> {
                _state.value = _state.value.copy(
                    startingDay = event.newDay
                )
            }
        }
    }

    private fun calculateStartDate(startingDay: Int): LocalDate {
        val today = LocalDate.now()
        val currentYear = today.year
        val currentMonth = today.monthValue
        val currentDayOfMonth = today.dayOfMonth

        fun getValidDayForMonth(yearMonth: YearMonth, day: Int): Int {
            return if (day > yearMonth.lengthOfMonth()) {
                yearMonth.lengthOfMonth()
            } else {
                day
            }
        }

        val validStartingDay = getValidDayForMonth(YearMonth.of(currentYear, currentMonth), startingDay)

        return if (startingDay < currentDayOfMonth) {
            LocalDate.of(currentYear, currentMonth, validStartingDay)
        } else {
            val previousMonth = YearMonth.of(currentYear, currentMonth).minusMonths(1)
            LocalDate.of(previousMonth.year, previousMonth.monthValue, validStartingDay)
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}