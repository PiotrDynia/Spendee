package com.example.spendee.core.presentation.util

import androidx.annotation.StringRes

sealed class UiEvent {
    data object PopBackStack : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data class ShowSnackbar(
        @StringRes val message: Int,
        @StringRes val action: Int? = null
    ): UiEvent()
}