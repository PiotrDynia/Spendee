package com.example.spendee.data.entities

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