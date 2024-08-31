package com.example.spendee.feature_current_balance.data.data_source

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.spendee.feature_current_balance.domain.model.Balance
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {
    @Query("SELECT * FROM balance WHERE id = 1")
    fun getBalance(): Flow<Balance>

    @Upsert
    suspend fun upsertBalance(balance: Balance)
}