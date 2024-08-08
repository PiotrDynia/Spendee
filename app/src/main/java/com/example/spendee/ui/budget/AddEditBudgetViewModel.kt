package com.example.spendee.ui.budget

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.repositories.BudgetRepository
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
            is AddEditBudgetEvent.OnEndDateChange -> {
                _state.value = _state.value.copy(
                    endDate = event.endDate
                )
            }
            AddEditBudgetEvent.OnSaveBudgetClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.upsertBudget(
                        Budget(
                            totalAmount = _state.value.amount.toDouble(),
                            startDate = Date(_state.value.startDate),
                            endDate = Date(_state.value.endDate),
                            isExceedNotificationEnabled = _state.value.isExceedButtonPressed,
                            isReach80PercentNotificationEnabled = _state.value.isReach80PercentButtonPressed
                        )
                    )
                    sendUiEvent(UiEvent.PopBackStack)
                }
            }
            is AddEditBudgetEvent.OnStartDateChange -> {
                _state.value = _state.value.copy(
                    startDate = event.startDate
                )
            }

            AddEditBudgetEvent.OnExceedButtonPress -> {
                _state.value = _state.value.copy(
                    isExceedButtonPressed = !_state.value.isExceedButtonPressed
                )
            }
            AddEditBudgetEvent.OnReach80PercentButtonPress -> {
                _state.value = _state.value.copy(
                    isReach80PercentButtonPressed = !_state.value.isReach80PercentButtonPressed
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