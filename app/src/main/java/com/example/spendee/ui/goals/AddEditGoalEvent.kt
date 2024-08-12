package com.example.spendee.ui.goals

sealed class AddEditGoalEvent {
    data class OnDescriptionChange(val description: String) : AddEditGoalEvent()
    data class OnAmountChange(val amount: String) : AddEditGoalEvent()
    data class OnDeadlineChange(val deadline: String) : AddEditGoalEvent()
    data object OnOpenDeadlineDatePicker : AddEditGoalEvent()
    data object OnCloseDeadlineDatePicker : AddEditGoalEvent()
    data class OnReachedButtonPress(val isPressed: Boolean) : AddEditGoalEvent()
    data object OnSaveGoalClick : AddEditGoalEvent()
}