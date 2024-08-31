package com.example.spendee.feature_current_balance.domain.repository

import com.example.spendee.feature_current_balance.domain.model.Balance
import kotlinx.coroutines.flow.Flow

interface BalanceRepository {
    fun getBalance() : Flow<Balance>
    suspend fun upsertBalance(balance: Balance)
}