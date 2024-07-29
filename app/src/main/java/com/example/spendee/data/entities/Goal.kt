package com.example.spendee.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val targetAmount: Double
)
