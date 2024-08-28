package com.example.spendee.ui.budget.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.repositories.BudgetRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
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
class BudgetViewModel @Inject constructor(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

    private val _budget = MutableStateFlow<Budget?>(null)
    val budget: StateFlow<Budget?> = _budget.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadBudget()
    }

    private fun loadBudget() {
        viewModelScope.launch {
            repository.getBudget().collect { fetchedBudget ->
                _budget.value = fetchedBudget
                _isLoading.value = false
            }
        }
    }

    fun onEvent(event: BudgetEvent) {
        when (event) {
            BudgetEvent.OnSetBudgetClick -> handleSetBudgetClick()
            BudgetEvent.OnDeleteBudget -> handleDeleteBudget()
        }
    }

    private fun handleSetBudgetClick() {
        val route = if (budget.value != null) {
            "${Routes.ADD_EDIT_BUDGET}?isCreated=true"
        } else {
            Routes.ADD_EDIT_BUDGET
        }
        sendUiEvent(UiEvent.Navigate(route))
    }

    private fun handleDeleteBudget() {
        viewModelScope.launch(Dispatchers.IO) {
            _budget.value?.let { budgetToDelete ->
                repository.deleteBudget(budgetToDelete)
                sendUiEvent(UiEvent.Navigate(Routes.BUDGET))
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}