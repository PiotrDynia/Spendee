package com.example.spendee.ui.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.repositories.BudgetRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
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

    var budget: Budget? = null
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getBudget().collect { budget ->
                this@BudgetViewModel.budget = budget
                _isLoading.value = false
            }
        }
    }

    fun onEvent(event: BudgetEvent) {
        when(event) {
            BudgetEvent.OnSetBudgetClick -> {
                if (budget != null) {
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_BUDGET + "?isCreated=${true}"))
                } else {
                    sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_BUDGET))
                }
            }
            BudgetEvent.OnDeleteBudget -> {
                viewModelScope.launch {
                    repository.deleteBudget(budget!!)
                    sendUiEvent(UiEvent.Navigate(Routes.BUDGET))
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