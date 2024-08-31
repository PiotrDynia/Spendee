package com.example.spendee.feature_budget.domain.model

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Budget(
    @PrimaryKey val id: Int = 1,
    val totalAmount: Double,
    var leftToSpend: Double,
    var totalSpent: Double,
    val startDate: LocalDate,
    val endDate: LocalDate,
    var isExceeded: Boolean,
    var isExceedNotificationEnabled: Boolean
)

class InvalidBudgetException(@StringRes val messageResId: Int) : Exception()