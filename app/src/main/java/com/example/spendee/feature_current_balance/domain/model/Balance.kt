package com.example.spendee.feature_current_balance.domain.model

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Balance(
    @PrimaryKey val id: Int = 1,
    val amount: Double
)

class InvalidBalanceException(@StringRes val messageResId: Int) : Exception()