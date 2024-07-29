package com.example.spendee.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Balance(
    @PrimaryKey val id: Int = 1,
    val amount: Double
)