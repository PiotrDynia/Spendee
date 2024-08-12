package com.example.spendee.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.repositories.BalanceRepository
import com.example.spendee.data.repositories.GoalRepository
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
class GoalsViewModel @Inject constructor(
    goalRepository: GoalRepository,
    balanceRepository: BalanceRepository,
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    val goals = goalRepository.getAllGoals()
    var balance: Balance? = null

    init {
        viewModelScope.launch {
            balanceRepository.getBalance().collect { balance ->
                this@GoalsViewModel.balance = balance
            }

            goals.collect {
                _isLoading.value = false
            }
        }
    }

    fun onEvent(event: GoalsEvent) {
        when(event) {
            GoalsEvent.OnAddGoalClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_GOAL))
            }
            is GoalsEvent.OnGoalClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_GOAL + "?goalId=${event.goal.id}"))
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}