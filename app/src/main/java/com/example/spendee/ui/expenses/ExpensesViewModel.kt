package com.example.spendee.ui.expenses

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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val balanceRepository: BalanceRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    val expenses = expenseRepository.getAllExpenses()

    private var deletedExpense: Expense? = null
    private var balance: Balance? = null
    private var budget: Budget? = null

    init {
        viewModelScope.launch {
            balance = balanceRepository.getBalance().first()
            budget = budgetRepository.getBudget().firstOrNull()
        }
    }

    fun onEvent(event: ExpensesEvent) {
        when(event) {
            ExpensesEvent.OnAddExpenseClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_EXPENSE))
            }
            is ExpensesEvent.OnExpenseClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_EXPENSE + "?expenseId=${event.expense.id}"))
            }
            is ExpensesEvent.OnDeleteExpense -> {
                viewModelScope.launch {
                    deletedExpense = event.expense
                    expenseRepository.deleteExpense(event.expense)
                    balanceRepository.upsertBalance(
                        Balance(
                            amount = balance!!.amount + event.expense.amount
                        )
                    )
                    if (budget != null) {
                        budget!!.leftToSpend += event.expense.amount
                        budget!!.totalSpent += event.expense.amount
                        budgetRepository.upsertBudget(budget!!)
                    }
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = R.string.expense_deleted,
                        action = R.string.undo
                    ))
                }

            }
            ExpensesEvent.OnUndoDelete -> {
                deletedExpense?.let { expense ->
                    viewModelScope.launch {
                        this@ExpensesViewModel.expenseRepository.upsertExpense(expense)
                        balanceRepository.upsertBalance(
                            Balance(
                                amount = balance!!.amount - expense.amount
                            )
                        )
                        if (budget != null) {
                            budget!!.leftToSpend -= expense.amount
                            budget!!.totalSpent -= expense.amount
                            budgetRepository.upsertBudget(budget!!)
                        }
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