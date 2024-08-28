package com.example.spendee.ui.goals.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendee.R
import com.example.spendee.data.entities.Balance
import com.example.spendee.data.entities.Goal
import com.example.spendee.data.repositories.BalanceRepository
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditGoalViewModel @Inject constructor(
    private val repository: GoalRepository,
    private val balanceRepository: BalanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(AddEditGoalState())
    val state = _state.asStateFlow()

    private var balance: Balance? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            balance = balanceRepository.getBalance().first()
        }
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
                        sendUiEvent(UiEvent.ShowSnackbar(R.string.target_amount_cant_be_empty))
                        return@launch
                    }
                    if (_state.value.description.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(R.string.description_cant_be_empty))
                        return@launch
                    }
                    if (_state.value.deadline.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(R.string.deadline_cant_be_empty))
                        return@launch
                    }
                    if (stringToDate(_state.value.deadline)!!.isBefore(LocalDate.now())) {
                        sendUiEvent(UiEvent.ShowSnackbar(R.string.deadline_should_be_after_todays_date))
                        return@launch
                    }
                    repository.upsertGoal(
                        Goal(
                            id = _state.value.goal?.id ?: 0,
                            targetAmount = _state.value.targetAmount.toDouble(),
                            deadline = stringToDate(_state.value.deadline)!!,
                            description = _state.value.description,
                            isReached = if (balance!!.amount >= _state.value.targetAmount.toDouble()) true else false,
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