package com.example.spendee.data.repositories

import com.example.spendee.data.dao.BalanceDao
import com.example.spendee.data.entities.Balance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class BalanceRepositoryImpl(
    private val balanceDao: BalanceDao
) : BalanceRepository {
    override fun getBalance(): Flow<Balance> = flow {
        val balanceFlow = balanceDao.getBalance()
        val balance = balanceFlow.firstOrNull()
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

    override suspend fun updateBalance(amount: Double) {
        balanceDao.updateBalance(amount)
    }
}