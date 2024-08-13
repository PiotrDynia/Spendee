package com.example.spendee.ui.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Expense
import com.example.spendee.data.repositories.ExpenseRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    val expenses = repository.getAllExpenses()

    private var deletedExpense: Expense? = null

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
                    repository.deleteExpense(event.expense)
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = "Expense deleted",
                        action = "Undo"
                    ))
                }

            }
            ExpensesEvent.OnUndoDelete -> {
                deletedExpense?.let { expense ->
                    viewModelScope.launch {
                        repository.upsertExpense(expense)
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