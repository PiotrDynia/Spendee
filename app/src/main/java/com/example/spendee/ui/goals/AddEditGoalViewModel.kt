package com.example.spendee.ui.goals

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.data.entities.Goal
import com.example.spendee.data.repositories.GoalRepository
import com.example.spendee.util.Routes
import com.example.spendee.util.UiEvent
import com.example.spendee.util.dateToString
import com.example.spendee.util.stringToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditGoalViewModel @Inject constructor(
    private val repository: GoalRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddEditGoalState())
    val state = _state.asStateFlow()

    init {
        val goalId = savedStateHandle.get<Int>("goalId")!!
        if (goalId != 0) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.getGoalById(goalId)?.let { goal ->
                    _state.value = _state.value.copy(
                        targetAmount = goal.targetAmount.toString(),
                        deadline = dateToString(goal.deadline),
                        description = goal.description,
                        isReachedButtonPressed = goal.isReachedNotificationEnabled,
                        goal = goal
                    )
                }
            }
        }
    }

    fun onEvent(event: AddEditGoalEvent) {
        when (event) {
            is AddEditGoalEvent.OnAmountChange -> {
                _state.value = _state.value.copy(
                    targetAmount = event.amount
                )
            }
            AddEditGoalEvent.OnCloseDeadlineDatePicker -> {
                _state.value = _state.value.copy(
                    deadline = "",
                    isDeadlineDatePickerOpened = false
                )
            }
            is AddEditGoalEvent.OnDeadlineChange -> {
                _state.value = _state.value.copy(
                    deadline = event.deadline,
                    isDeadlineDatePickerOpened = false
                )
            }
            is AddEditGoalEvent.OnDescriptionChange -> {
                _state.value = _state.value.copy(
                    description = event.description
                )
            }
            AddEditGoalEvent.OnOpenDeadlineDatePicker -> {
                _state.value = _state.value.copy(
                    isDeadlineDatePickerOpened = true
                )
            }
            is AddEditGoalEvent.OnReachedButtonPress -> {
                _state.value = _state.value.copy(
                    isReachedButtonPressed = event.isPressed
                )
            }
            AddEditGoalEvent.OnSaveGoalClick -> {
                viewModelScope.launch(Dispatchers.IO) {
                    if (_state.value.targetAmount.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar("Target amount can't be empty!"))
                        return@launch
                    }
                    if (_state.value.description.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar("Description can't be empty!"))
                        return@launch
                    }
                    if (_state.value.deadline.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar("Deadline can't be empty!"))
                        return@launch
                    }
                    if (stringToDate(_state.value.deadline)!!.isBefore(LocalDate.now())) {
                        sendUiEvent(UiEvent.ShowSnackbar("Deadline should be after today's date!"))
                        return@launch
                    }
                    repository.upsertGoal(
                        Goal(
                            id = _state.value.goal?.id ?: 0,
                            targetAmount = _state.value.targetAmount.toDoubleOrNull() ?: 0.0,
                            deadline = stringToDate(_state.value.deadline)!!,
                            description = _state.value.description,
                            isReachedNotificationEnabled = _state.value.isReachedButtonPressed
                        )
                    )
                    sendUiEvent(UiEvent.Navigate(Routes.GOALS))
                    _state.value = _state.value.copy(
                        isDeadlineDatePickerOpened = false
                    )
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