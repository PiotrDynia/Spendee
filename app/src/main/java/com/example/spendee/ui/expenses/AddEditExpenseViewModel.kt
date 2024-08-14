package com.example.spendee.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.R
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.entities.Expense
import com.example.spendee.data.repositories.BalanceRepository
import com.example.spendee.data.repositories.BudgetRepository
import com.example.spendee.data.repositories.ExpenseRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val balanceRepository: BalanceRepository,
    private val budgetRepository: BudgetRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddEditExpenseState())
    val state = _state.asStateFlow()

    private var balance: Balance? = null
    private var budget: Budget? = null

    private var isNewExpense: Boolean = true

    init {
        viewModelScope.launch {
            balance = balanceRepository.getBalance().first()
            budget = budgetRepository.getBudget().firstOrNull()
        }
        val expenseId = savedStateHandle.get<Int>("expenseId")!!
        if (expenseId != 0) {
            isNewExpense = false
            viewModelScope.launch(Dispatchers.IO) {
                repository.getExpenseById(expenseId)?.let { expense ->
                    _state.value = _state.value.copy(
                        amount = expense.amount.toString(),
                        originalAmount = expense.amount.toString(),
                        description = expense.description,
                        categoryId = expense.categoryId,
                        expense = expense
                    )
                }
            }
        }
    }

    fun onEvent(event: AddEditExpenseEvent) {
        when(event) {
            is AddEditExpenseEvent.OnAmountChange -> {
                _state.value = _state.value.copy(
                    amount = event.amount
                )
            }
            is AddEditExpenseEvent.OnCategoryChange -> {
                _state.value = _state.value.copy(
                    categoryId = event.categoryId
                )
            }
            is AddEditExpenseEvent.OnDescriptionChange -> {
                _state.value = _state.value.copy(
                    description = event.description
                )
            }
            AddEditExpenseEvent.OnSaveExpenseClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (_state.value.amount.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(message = R.string.amount_cant_be_empty))
                        return@launch
                    }
                    if (_state.value.description.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(message = R.string.description_cant_be_empty))
                        return@launch
                    }
                    if (_state.value.amount.toDouble() > balance!!.amount) {
                        sendUiEvent(UiEvent.ShowSnackbar(message = R.string.cant_add_expense_your_balance_is_too_low))
                        return@launch
                    }
                    if (_state.value.categoryId == 0) {
                        _state.value = _state.value.copy(
                            categoryId = 8
                        )
                    }
                    repository.upsertExpense(
                        Expense(
                            id = _state.value.expense?.id ?: 0,
                            amount = _state.value.amount.toDouble(),
                            description = _state.value.description,
                            categoryId = _state.value.categoryId,
                            date = LocalDate.now()
                        )
                    )
                    if (isNewExpense) {
                        balanceRepository.upsertBalance(
                            Balance(
                                amount = balance!!.amount - (_state.value.amount.toDouble())
                            )
                        )
                        if (isBudgetSet()) {
                            budgetRepository.updateBudget(budget!!.currentAmount - _state.value.amount.toDouble())
                        }
                    } else {
                        balanceRepository.upsertBalance(
                            Balance(
                                amount = balance!!.amount - ((_state.value.amount.toDouble()) - (_state.value.originalAmount.toDouble()))
                            )
                        )
                        if (isBudgetSet()) {
                            budgetRepository.updateBudget(
                                budget!!.currentAmount - ((_state.value.amount.toDouble()) - (_state.value.originalAmount.toDouble())))
                        }
                    }
                    sendUiEvent(UiEvent.Navigate(Routes.EXPENSES))
                }
            }
        }
    }

    private fun isBudgetSet(): Boolean {
        return budget != null
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}