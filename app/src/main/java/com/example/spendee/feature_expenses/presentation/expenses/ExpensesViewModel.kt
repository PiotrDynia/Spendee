package com.example.spendee.feature_expenses.presentation.expenses

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.R
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.model.InvalidExpenseException
import com.example.spendee.feature_expenses.domain.use_case.ExpensesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val useCases: ExpensesUseCases
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()
    val expenses = useCases.getExpenses()

    private val _deletedExpense = MutableStateFlow<Expense?>(null)

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

    private fun undoDeleteExpense() {
        _deletedExpense.value?.let { expense ->
            viewModelScope.launch {
                try {
                    useCases.addExpense(isNewExpense = true, originalAmount = expense.amount, expense = expense)
                } catch (e: InvalidExpenseException) {
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = e.messageResId
                    ))
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