package com.example.spendee.feature_goals.presentation.add_edit_goal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.core.domain.util.dateToString
import com.example.spendee.core.domain.util.stringToDate
import com.example.spendee.core.presentation.util.Routes
import com.example.spendee.core.presentation.util.UiEvent
import com.example.spendee.feature_goals.domain.model.Goal
import com.example.spendee.feature_goals.domain.model.InvalidGoalException
import com.example.spendee.feature_goals.domain.use_case.GoalsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditGoalViewModel @Inject constructor(
    private val useCases: GoalsUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddEditGoalState())
    val state = _state.asStateFlow()

    init {
        loadInitialData(savedStateHandle)
    }

    private fun loadInitialData(savedStateHandle: SavedStateHandle) {
        viewModelScope.launch {
            val goalId = savedStateHandle.get<Int>("goalId") ?: 0
            if (goalId != 0) {
                useCases.getGoal(goalId)?.let { goal ->
                    updateStateWithGoal(goal)
                }
            }
        }
    }

    private fun updateStateWithGoal(goal: Goal) {
        _state.update {
            it.copy(
                targetAmount = goal.targetAmount.toString(),
                deadline = dateToString(goal.deadline),
                description = goal.description.trim(),
                isReachedButtonPressed = goal.isReachedNotificationEnabled,
                goal = goal
            )
        }
    }

    fun onEvent(event: AddEditGoalEvent) {
        when (event) {
            is AddEditGoalEvent.OnAmountChange -> updateTargetAmount(event.amount)
            AddEditGoalEvent.OnCloseDeadlineDatePicker -> closeDeadlineDatePicker()
            is AddEditGoalEvent.OnDeadlineChange -> updateDeadline(event.deadline)
            is AddEditGoalEvent.OnDescriptionChange -> updateDescription(event.description)
            AddEditGoalEvent.OnOpenDeadlineDatePicker -> openDeadlineDatePicker()
            is AddEditGoalEvent.OnReachedButtonPress -> updateReachedButtonState(event.isPressed)
            AddEditGoalEvent.OnSaveGoalClick -> saveGoal()
        }
    }

    private fun updateTargetAmount(amount: String) {
        _state.update { it.copy(targetAmount = amount) }
    }

    private fun closeDeadlineDatePicker() {
        _state.update { it.copy(deadline = "", isDeadlineDatePickerOpened = false) }
    }

    private fun updateDeadline(deadline: String) {
        _state.update { it.copy(deadline = deadline, isDeadlineDatePickerOpened = false) }
    }

    private fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    private fun openDeadlineDatePicker() {
        _state.update { it.copy(isDeadlineDatePickerOpened = true) }
    }

    private fun updateReachedButtonState(isPressed: Boolean) {
        _state.update { it.copy(isReachedButtonPressed = isPressed) }
    }

    private fun saveGoal() {
        viewModelScope.launch {
            try {
                useCases.addGoal(createGoalFromState())
                closeDeadlineDatePicker()
                sendUiEvent(UiEvent.Navigate(Routes.GOALS))
            } catch (e: InvalidGoalException) {
                sendUiEvent(UiEvent.ShowSnackbar(
                    message = e.messageResId
                ))
            }
        }
    }

    private fun createGoalFromState(): Goal {
        val targetAmount = _state.value.targetAmount.toDoubleOrNull() ?: 0.0
        val deadline = stringToDate(_state.value.deadline) ?: LocalDate.now()
        return Goal(
            id = _state.value.goal?.id ?: 0,
            targetAmount = targetAmount,
            deadline = deadline,
            description = _state.value.description,
            isReached = false,
            isReachedNotificationEnabled = _state.value.isReachedButtonPressed
        )
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}