package com.example.spendee.feature_current_balance.domain.use_case

import com.example.spendee.feature_current_balance.domain.model.Balance
import com.example.spendee.feature_current_balance.domain.repository.BalanceRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentBalance(
    private val repository: BalanceRepository
) {
    operator fun invoke() : Flow<Balance> {
        return repository.getBalance()
    }
}