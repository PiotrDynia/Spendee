package com.example.spendee.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val targetAmount: Double,
    val deadline: Date,
    val imagePath: String? = null
)
