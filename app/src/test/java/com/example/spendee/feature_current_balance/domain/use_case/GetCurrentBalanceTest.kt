package com.example.spendee.feature_current_balance.domain.use_case

import com.example.spendee.feature_current_balance.data.repository.FakeBalanceRepository
import com.example.spendee.feature_current_balance.domain.model.Balance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCurrentBalanceTest {

    private lateinit var getCurrentBalance: GetCurrentBalance
    private lateinit var repository: FakeBalanceRepository

    @Before
    fun setUp() {
        repository = FakeBalanceRepository()
        getCurrentBalance = GetCurrentBalance(repository)
    }

    @Test
    fun `Retrieve starting balance, get 0`() = runBlocking {
        val expectedAmount = 0.0
        val delta = 0.0
        assertEquals(getCurrentBalance().first().amount, expectedAmount, delta)
    }

    @Test
    fun `Update balance, retrieve updated balance`() = runBlocking {
        val updatedBalance = Balance(
            amount = 30.0
        )
        repository.upsertBalance(updatedBalance)
        val expectedAmount = 30.0
        val delta = 0.0
        assertEquals(getCurrentBalance().first().amount, expectedAmount, delta)
    }
}