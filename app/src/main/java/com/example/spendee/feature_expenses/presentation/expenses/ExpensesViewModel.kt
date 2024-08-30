package com.example.spendee.feature_expenses.presentation.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.R
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.feature_budget.domain.model.Budget
import com.example.spendee.feature_budget.domain.repository.BudgetRepository
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.repository.ExpenseRepository
import com.example.spendee.feature_expenses.domain.use_case.ExpensesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val balanceRepository: BalanceRepository,
    private val budgetRepository: BudgetRepository,
    private val useCases: ExpensesUseCases
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    val expenses = useCases.getExpenses()

    private val _balance = MutableStateFlow<Balance?>(null)
    val balance = _balance.asStateFlow()

    private val _budget = MutableStateFlow<Budget?>(null)
    val budget = _budget.asStateFlow()

    private val _deletedExpense = MutableStateFlow<Expense?>(null)

    init {
        fetchInitialData()
    }

    // TODO delete
    private fun fetchInitialData() {
        viewModelScope.launch {
            _balance.value = balanceRepository.getBalance().firstOrNull()
            _budget.value = budgetRepository.getBudget().firstOrNull()
        }
    }

    fun onEvent(event: ExpensesEvent) {
        when (event) {
            ExpensesEvent.OnAddExpenseClick -> navigateToAddExpense()
            is ExpensesEvent.OnExpenseClick -> navigateToEditExpense(event.expense.id)
            is ExpensesEvent.OnDeleteExpense -> deleteExpense(event.expense)
            ExpensesEvent.OnUndoDelete -> undoDeleteExpense()
        }
    }

    private fun navigateToAddExpense() {
        sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_EXPENSE))
    }

    private fun navigateToEditExpense(expenseId: Int) {
        sendUiEvent(UiEvent.Navigate("${Routes.ADD_EDIT_EXPENSE}?expenseId=$expenseId"))
    }

    private fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            _deletedExpense.value = expense
            useCases.deleteExpense(expense)
            sendUiEvent(
                UiEvent.ShowSnackbar(
                message = R.string.expense_deleted,
                action = R.string.undo
            ))
        }
    }

    // TODO change to useCases.addNote (I hope)
    private fun undoDeleteExpense() {
        _deletedExpense.value?.let { expense ->
            viewModelScope.launch(Dispatchers.IO) {
                expenseRepository.upsertExpense(expense)
                updateBalanceOnExpenseUndoDeletion(expense)
                updateBudgetOnExpenseUndoDeletion(expense)
            }
        }
    }

    private suspend fun updateBalanceOnExpenseUndoDeletion(expense: Expense) {
        _balance.value?.let {
            val updatedBalance = it.copy(amount = it.amount - expense.amount)
            _balance.value = updatedBalance
            balanceRepository.upsertBalance(updatedBalance)
        }
    }

    private suspend fun updateBudgetOnExpenseUndoDeletion(expense: Expense) {
        _budget.value?.let {
            val updatedBudget = it.copy(
                leftToSpend = it.leftToSpend - expense.amount,
                totalSpent = it.totalSpent + expense.amount
            )
            _budget.value = updatedBudget
            budgetRepository.upsertBudget(updatedBudget)
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}