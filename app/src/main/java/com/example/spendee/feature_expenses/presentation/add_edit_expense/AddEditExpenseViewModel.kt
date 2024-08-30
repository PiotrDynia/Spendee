package com.example.spendee.feature_expenses.presentation.add_edit_expense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.core.domain.util.NotificationService
import com.example.spendee.R
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val balanceRepository: BalanceRepository,
    private val budgetRepository: BudgetRepository,
    private val notificationService: NotificationService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddEditExpenseState())
    val state = _state.asStateFlow()

    private val _balance = MutableStateFlow<Balance?>(null)
    val balance = _balance.asStateFlow()

    private val _budget = MutableStateFlow<Budget?>(null)
    val budget = _budget.asStateFlow()

    private var isNewExpense: Boolean = true

    init {
        loadInitialData(savedStateHandle)
    }

    private fun loadInitialData(savedStateHandle: SavedStateHandle) {
        viewModelScope.launch(Dispatchers.IO) {
            _balance.value = balanceRepository.getBalance().first()
            _budget.value = budgetRepository.getBudget().firstOrNull()
            val expenseId = savedStateHandle.get<Int>("expenseId") ?: 0
            if (expenseId != 0) {
                isNewExpense = false
                repository.getExpenseById(expenseId)?.let { expense ->
                    _state.update {
                        it.copy(
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
    }

    fun onEvent(event: AddEditExpenseEvent) {
        when (event) {
            is AddEditExpenseEvent.OnAmountChange -> updateAmount(event.amount)
            is AddEditExpenseEvent.OnCategoryChange -> updateCategory(event.categoryId)
            is AddEditExpenseEvent.OnDescriptionChange -> updateDescription(event.description)
            AddEditExpenseEvent.OnSaveExpenseClick -> saveExpense()
        }
    }

    private fun updateAmount(amount: String) {
        _state.update { it.copy(amount = amount) }
    }

    private fun updateCategory(categoryId: Int) {
        _state.update { it.copy(categoryId = categoryId) }
    }

    private fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    private fun saveExpense() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!isInputValid()) return@launch

            val amount = _state.value.amount.toDouble()
            val expense = createExpenseFromState(amount)

            repository.upsertExpense(expense)

            if (isNewExpense) {
                updateBalanceForNewExpense(amount)
                updateBudgetForNewExpense(amount)
            } else {
                updateBalanceForExistingExpense(amount)
                updateBudgetForExistingExpense(amount)
            }

            sendUiEvent(UiEvent.Navigate(Routes.EXPENSES))
        }
    }

    private fun isInputValid(): Boolean {
        val amount = _state.value.amount
        return when {
            amount.isBlank() -> {
                sendUiEvent(UiEvent.ShowSnackbar(message = R.string.amount_cant_be_empty))
                false
            }
            _state.value.description.isBlank() -> {
                sendUiEvent(UiEvent.ShowSnackbar(message = R.string.description_cant_be_empty))
                false
            }
            isBalanceExceeded(amount.toDouble()) -> {
                sendUiEvent(UiEvent.ShowSnackbar(message = R.string.cant_add_expense_your_balance_is_too_low))
                false
            }
            else -> true
        }
    }

    private fun createExpenseFromState(amount: Double) = Expense(
        id = _state.value.expense?.id ?: 0,
        amount = amount,
        description = _state.value.description,
        categoryId = _state.value.categoryId.takeIf { it != 0 } ?: 8,
        date = LocalDate.now()
    )

    private suspend fun updateBalanceForNewExpense(amount: Double) {
        _balance.value?.let { balance ->
            balanceRepository.upsertBalance(balance.copy(amount = balance.amount - amount))
        }
    }

    private suspend fun updateBalanceForExistingExpense(newAmount: Double) {
        val originalAmount = _state.value.originalAmount.toDouble()
        _balance.value?.let { balance ->
            balanceRepository.upsertBalance(
                balance.copy(amount = balance.amount - (newAmount - originalAmount))
            )
        }
    }

    private fun updateBudgetForNewExpense(amount: Double) {
        _budget.value?.let { budget ->
            updateBudget(budget, amount)
        }
    }

    private fun updateBudgetForExistingExpense(newAmount: Double) {
        val originalAmount = _state.value.originalAmount.toDouble()
        _budget.value?.let { budget ->
            updateBudget(budget, newAmount - originalAmount)
        }
    }

    private fun updateBudget(budget: Budget, amountDifference: Double) {
        if (budget.leftToSpend < amountDifference) {
            budget.isExceeded = true
            budget.leftToSpend = 0.0
            if (budget.isExceedNotificationEnabled) {
                notificationService.showBudgetExceededNotification()
            }
        } else {
            budget.isExceeded = false
            budget.leftToSpend -= amountDifference
        }
        budget.totalSpent += amountDifference
        viewModelScope.launch(Dispatchers.IO) {
            budgetRepository.upsertBudget(budget)
        }
    }

    private fun isBalanceExceeded(amount: Double): Boolean {
        val balanceAmount = _balance.value?.amount ?: 0.0
        val originalAmount = _state.value.originalAmount.toDoubleOrNull() ?: 0.0
        return if (isNewExpense) amount > balanceAmount else (amount - originalAmount) > balanceAmount
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}