package com.example.spendee.ui.budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.repositories.BudgetRepository
import com.example.spendee.util.UiEvent
import com.example.spendee.util.stringToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditBudgetViewModel @Inject constructor(
    private val repository: BudgetRepository,
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
                        startDate = budget.startDate.toString(),
                        endDate = budget.endDate.toString(),
                        budget = budget
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
                    repository.upsertBudget(
                        Budget(
                            totalAmount = _state.value.amount.toDoubleOrNull() ?: 0.0,
                            currentAmount = _state.value.amount.toDoubleOrNull() ?: 0.0,
                            startDate = stringToDate(_state.value.startDate)!!,
                            endDate = stringToDate(_state.value.endDate)!!,
                            isExceedNotificationEnabled = _state.value.isExceedButtonPressed,
                            isReach80PercentNotificationEnabled = _state.value.isReach80PercentButtonPressed
                        )
                    )
                    delay(2000)
                    sendUiEvent(UiEvent.PopBackStack)
                }
                _state.value = _state.value.copy(
                    isStartDatePickerOpened = false,
                    isEndDatePickerOpened = false
                )
            }
            is AddEditBudgetEvent.OnStartDateChange -> {
                _state.value = _state.value.copy(
                    startDate = event.startDate,
                    isStartDatePickerOpened = false
                )
            }
            is AddEditBudgetEvent.OnEndDateChange -> {
                _state.value = _state.value.copy(
                    endDate = event.endDate,
                    isEndDatePickerOpened = false
                )
            }
            is AddEditBudgetEvent.OnExceedButtonPress -> {
                _state.value = _state.value.copy(
                    isExceedButtonPressed = event.isPressed
                )
            }
            is AddEditBudgetEvent.OnReach80PercentButtonPress -> {
                _state.value = _state.value.copy(
                    isReach80PercentButtonPressed = event.isPressed
                )
            }
            AddEditBudgetEvent.OnCloseEndDatePicker -> {
                _state.value = _state.value.copy(
                    isEndDatePickerOpened = false,
                    endDate = ""
                )
            }
            AddEditBudgetEvent.OnCloseStartDatePicker -> {
                _state.value = _state.value.copy(
                    isStartDatePickerOpened = false,
                    startDate = ""
                )
            }
            AddEditBudgetEvent.OnOpenEndDatePicker -> {
                _state.value = _state.value.copy(
                    isEndDatePickerOpened = true
                )
            }
            AddEditBudgetEvent.OnOpenStartDatePicker -> {
                _state.value = _state.value.copy(
                    isStartDatePickerOpened = true
                )
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}