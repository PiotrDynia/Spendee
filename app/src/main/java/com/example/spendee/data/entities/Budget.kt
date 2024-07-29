package com.example.spendee.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val totalAmount: Double,
    val startDate: Date,
    val endDate: Date
)
