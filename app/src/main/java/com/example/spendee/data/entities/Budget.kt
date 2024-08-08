package com.example.spendee.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Budget(
    @PrimaryKey val id: Int = 1,
    val totalAmount: Double,
    val currentAmount: Double,
    val startDate: Date,
    val endDate: Date,
    val isExceedNotificationEnabled: Boolean,
    val isReach80PercentNotificationEnabled: Boolean
)
