package com.example.spendee.data.repositories

import com.example.spendee.data.dao.BalanceDao
import com.example.spendee.data.entities.Balance
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BalanceRepositoryImpl @Inject constructor(
    private val balanceDao: BalanceDao
) : BalanceRepository {
    override fun getBalance(): Flow<Balance> {
        return balanceDao.getBalance()
    }

    override suspend fun upsertBalance(balance: Balance) {
        balanceDao.upsertBalance(balance)
    }
}