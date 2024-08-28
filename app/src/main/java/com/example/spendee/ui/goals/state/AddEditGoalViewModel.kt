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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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
        loadInitialData(savedStateHandle)
    }

    private fun loadInitialData(savedStateHandle: SavedStateHandle) {
        viewModelScope.launch(Dispatchers.IO) {
            balance = balanceRepository.getBalance().firstOrNull()
            val goalId = savedStateHandle.get<Int>("goalId") ?: 0
            if (goalId != 0) {
                repository.getGoalById(goalId)?.let { goal ->
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
                description = goal.description,
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
        viewModelScope.launch(Dispatchers.IO) {
            if (isInputValid()) {
                val goal = createGoalFromState()
                repository.upsertGoal(goal)
                sendUiEvent(UiEvent.Navigate(Routes.GOALS))
                closeDeadlineDatePicker()
            }
        }
    }

    private fun isInputValid(): Boolean {
        return when {
            _state.value.targetAmount.isBlank() -> {
                sendUiEvent(UiEvent.ShowSnackbar(R.string.target_amount_cant_be_empty))
                false
            }
            _state.value.description.isBlank() -> {
                sendUiEvent(UiEvent.ShowSnackbar(R.string.description_cant_be_empty))
                false
            }
            _state.value.deadline.isBlank() -> {
                sendUiEvent(UiEvent.ShowSnackbar(R.string.deadline_cant_be_empty))
                false
            }
            stringToDate(_state.value.deadline)?.isBefore(LocalDate.now()) == true -> {
                sendUiEvent(UiEvent.ShowSnackbar(R.string.deadline_should_be_after_todays_date))
                false
            }
            else -> true
        }
    }

    private fun createGoalFromState(): Goal {
        val targetAmount = _state.value.targetAmount.toDouble()
        val deadline = stringToDate(_state.value.deadline)!!
        return Goal(
            id = _state.value.goal?.id ?: 0,
            targetAmount = targetAmount,
            deadline = deadline,
            description = _state.value.description,
            isReached = (balance?.amount ?: 0.0) >= targetAmount,
            isReachedNotificationEnabled = _state.value.isReachedButtonPressed
        )
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}