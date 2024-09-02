package com.example.spendee.feature_current_balance.data.repository

import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeBalanceRepository : BalanceRepository {

    private var balance: Balance = Balance(
        amount = 0.0
    )
    override fun getBalance(): Flow<Balance> {
        return flow { emit(balance) }
    }

    override suspend fun upsertBalance(balance: Balance) {
        this.balance = balance
    }
}