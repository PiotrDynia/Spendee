package com.example.spendee.ui.expenses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Expense
import com.example.spendee.data.repositories.BalanceRepository
import com.example.spendee.data.repositories.ExpenseRepository
import com.example.spendee.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    private val balanceRepository: BalanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddEditExpenseState())
    val state = _state.asStateFlow()

    private var balance: Balance? = null

    init {
        viewModelScope.launch {
            balanceRepository.getBalance().collect { balance ->
                this@AddEditExpenseViewModel.balance = balance
            }
        }
        val expenseId = savedStateHandle.get<Int>("expenseId")!!
        if (expenseId != 0) {
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
                    repository.upsertExpense(
                        Expense(
                            id = _state.value.expense?.id ?: 0,
                            amount = _state.value.amount.toDouble(),
                            description = _state.value.description,
                            categoryId = _state.value.categoryId,
                            date = Date()
                        )
                    )
                    if (isNewExpense()) {
                        val difference = balance!!.amount - _state.value.amount.toDouble()
                        balanceRepository.updateBalance(difference)
                    }
                    if (isEditedExpense()) {
                        val difference = balance!!.amount - (_state.value.amount.toDouble() - _state.value.originalAmount.toDouble())
                        balanceRepository.updateBalance(difference)
                    }
                    sendUiEvent(UiEvent.PopBackStack)
                }
            }
        }
    }

    private fun isNewExpense(): Boolean = _state.value.originalAmount.isBlank()
    private fun isEditedExpense() : Boolean = _state.value.originalAmount.isNotBlank() && _state.value.originalAmount != _state.value.amount

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}