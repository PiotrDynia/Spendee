package com.example.spendee.feature_current_balance.data.repository

import com.example.spendee.feature_current_balance.data.data_source.BalanceDao
import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class BalanceRepositoryImpl(
    private val balanceDao: BalanceDao
) : BalanceRepository {
    override fun getBalance(): Flow<Balance> = flow {
        val balance = balanceDao.getBalance().firstOrNull()
        if (balance == null) {
            balanceDao.upsertBalance(Balance(id = 1, amount = 0.0))
            emit(Balance(id = 1, amount = 0.0))
        } else {
            emit(balance)
        }
    }

    override suspend fun upsertBalance(balance: Balance) {
        balanceDao.upsertBalance(balance)
    }
}