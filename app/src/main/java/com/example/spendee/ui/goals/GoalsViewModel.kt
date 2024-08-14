package com.example.spendee.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Goal
import com.example.spendee.data.repositories.BalanceRepository
import com.example.spendee.data.repositories.GoalRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val balanceRepository: BalanceRepository,
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    val goals = goalRepository.getAllGoals()
    var balance: Balance? = null
    private var deletedGoal: Goal? = null

    init {
        viewModelScope.launch {
            balance = balanceRepository.getBalance().first()

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

            is GoalsEvent.OnDeleteGoal -> {
                viewModelScope.launch {
                    deletedGoal = event.goal
                    goalRepository.deleteGoal(event.goal)
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = "Goal deleted",
                        action = "Undo"
                    ))
                }
            }
            GoalsEvent.OnUndoDeleteGoal -> {
                deletedGoal?.let { goal ->
                    viewModelScope.launch {
                        goalRepository.upsertGoal(goal)
                    }
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