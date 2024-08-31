package com.example.spendee.feature_goals.domain.model

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val targetAmount: Double,
    val deadline: LocalDate,
    var isReached: Boolean,
    var isReachedNotificationEnabled: Boolean,
)

class InvalidGoalException(@StringRes val messageResId: Int) : Exception()