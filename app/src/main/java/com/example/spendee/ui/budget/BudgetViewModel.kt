package com.example.spendee.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.repositories.BudgetRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
import com.example.spendee.util.dateToString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: BudgetRepository
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(BudgetState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getBudget().collect { budget ->
                _state.value = _state.value.copy(
                    budget = budget
                )
            }
            if (_state.value.budget != null) {
                _state.value = _state.value.copy(
                    totalAmount = _state.value.budget?.totalAmount.toString(),
                    currentAmount = _state.value.budget?.currentAmount.toString(),
                    startDate = dateToString(_state.value.budget!!.startDate),
                    endDate = dateToString(_state.value.budget!!.endDate)
                )
            }
        }
    }

    fun onEvent(event: BudgetEvent) {
        when(event) {
            BudgetEvent.OnSetBudgetClick -> {
                if (_state.value.budget != null) {
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_BUDGET + "?isCreated=${true}"))
                } else {
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_BUDGET))
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