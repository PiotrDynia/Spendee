package com.example.spendee.data.repositories

import com.example.spendee.data.entities.Balance
import kotlinx.coroutines.flow.Flow

interface BalanceRepository {
    fun getBalance() : Flow<Balance>
    suspend fun upsertBalance(balance: Balance)
    suspend fun updateBalance(amount: Double)
}