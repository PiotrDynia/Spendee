package com.example.spendee.feature_budget.presentation.add_edit_budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.feature_budget.domain.model.InvalidBudgetException
import com.example.spendee.feature_budget.domain.use_case.BudgetUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditBudgetViewModel @Inject constructor(
    private val budgetUseCases: BudgetUseCases,
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
            budgetUseCases.getBudget().collect { budget ->
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
            try {
                budgetUseCases.addBudget(state = stateValue)
                sendUiEvent(UiEvent.PopBackStack)
            } catch(e: InvalidBudgetException) {
                sendUiEvent(UiEvent.ShowSnackbar(
                    message = e.messageResId
                ))
            }
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

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}