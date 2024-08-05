package com.example.spendee.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.spendee.data.entities.Balance
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {
    @Query("SELECT * FROM balance WHERE id = 1")
    fun getBalance(): Flow<Balance>

    @Query("UPDATE balance SET amount = :amount WHERE id = 1")
    suspend fun updateBalance(amount: Double)

    @Upsert
    suspend fun upsertBalance(balance: Balance)
}