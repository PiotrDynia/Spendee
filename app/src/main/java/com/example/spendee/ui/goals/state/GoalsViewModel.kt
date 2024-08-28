package com.example.spendee.ui.goals.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.R
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Goal
import com.example.spendee.data.repositories.BalanceRepository
import com.example.spendee.data.repositories.GoalRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val balanceRepository: BalanceRepository
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent: Flow<UiEvent> = _uiEvent.receiveAsFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _goalsState = MutableStateFlow<List<Goal>>(emptyList())
    val goalsState: StateFlow<List<Goal>> = _goalsState.asStateFlow()

    private val _balanceState = MutableStateFlow<Balance?>(null)
    val balanceState: StateFlow<Balance?> = _balanceState.asStateFlow()

    private var deletedGoal: Goal? = null

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                balanceRepository.getBalance(),
                goalRepository.getAllGoals()
            ) { balance, goals ->
                _balanceState.value = balance
                _goalsState.value = goals
                _isLoading.value = false
            }.collect()
        }
    }

    fun onEvent(event: GoalsEvent) {
        when (event) {
            GoalsEvent.OnAddGoalClick -> navigateToAddEditGoal()
            is GoalsEvent.OnGoalClick -> navigateToEditGoal(event.goal.id)
            is GoalsEvent.OnDeleteGoal -> handleDeleteGoal(event.goal)
            GoalsEvent.OnUndoDeleteGoal -> undoDeleteGoal()
        }
    }

    private fun navigateToAddEditGoal() {
        sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_GOAL))
    }

    private fun navigateToEditGoal(goalId: Int) {
        sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_GOAL + "?goalId=$goalId"))
    }

    private fun handleDeleteGoal(goal: Goal) {
        viewModelScope.launch(Dispatchers.IO) {
            deletedGoal = goal
            goalRepository.deleteGoal(goal)
            sendUiEvent(UiEvent.ShowSnackbar(
                message = R.string.goal_deleted,
                action = R.string.undo
            ))
        }
    }

    private fun undoDeleteGoal() {
        deletedGoal?.let { goal ->
            viewModelScope.launch(Dispatchers.IO) {
                goalRepository.upsertGoal(goal)
                deletedGoal = null
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}