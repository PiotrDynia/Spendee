package com.example.spendee.feature_expenses.presentation.add_edit_expense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.feature_expenses.domain.model.Expense
import com.example.spendee.feature_expenses.domain.model.InvalidExpenseException
import com.example.spendee.feature_expenses.domain.use_case.ExpensesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val useCases: ExpensesUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddEditExpenseState())
    val state = _state.asStateFlow()

    private var isNewExpense: Boolean = true

    init {
        loadInitialData(savedStateHandle)
    }

    private fun loadInitialData(savedStateHandle: SavedStateHandle) {
        viewModelScope.launch {
            val expenseId = savedStateHandle.get<Int>("expenseId") ?: 0
            if (expenseId != 0) {
                isNewExpense = false
                useCases.getExpense(expenseId)?.let { expense ->
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
        viewModelScope.launch {
            try {
                useCases.addExpense(state = _state.value, isNewExpense = isNewExpense, expense = createExpenseFromState(_state.value.amount.toDouble()))
                sendUiEvent(UiEvent.Navigate(Routes.EXPENSES))
            } catch (e: InvalidExpenseException) {
                sendUiEvent(UiEvent.ShowSnackbar(
                    message = e.messageResId
                ))
            }
        }
    }

    private fun createExpenseFromState(amount: Double) = Expense(
        id = _state.value.expense?.id ?: 0,
        amount = amount,
        description = _state.value.description,
        categoryId = _state.value.categoryId.takeIf { it != 0 } ?: 8,
        date = LocalDate.now()
    )

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}