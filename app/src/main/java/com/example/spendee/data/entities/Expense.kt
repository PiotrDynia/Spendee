package com.example.spendee.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String?,
    val date: Long,
    val categoryId: Int
)
