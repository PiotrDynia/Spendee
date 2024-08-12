package com.example.spendee.ui.budget

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Budget
import com.example.spendee.data.repositories.BudgetRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val repository: BudgetRepository
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var budget by mutableStateOf<Budget?>(null)
        private set

    init {
        runBlocking {
            repository.getBudget().collect { budget ->
                this@BudgetViewModel.budget = budget
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
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}